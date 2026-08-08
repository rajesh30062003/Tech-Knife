import { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { User } from '../models/User';
import { Employee } from '../models/Employee';
import { Customer } from '../models/Customer';
import { AuthRequest } from '../middleware/auth';

const JWT_SECRET = process.env.JWT_SECRET || '9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b';
const JWT_REFRESH_SECRET = process.env.JWT_REFRESH_SECRET || '8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b';

import { AUTHORITATIVE_STAFF_EMAILS } from '../services/seedService';

export const login = async (req: Request, res: Response): Promise<void> => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      res.status(400).json({ success: false, message: 'Email and password are required.' });
      return;
    }

    const queryEmail = String(email).toLowerCase().trim();
    let user = await User.findOne({ email: queryEmail });

    if (!user) {
      // Check in Employee or Customer
      const emp = await Employee.findOne({ officialEmail: queryEmail });
      if (emp) {
        user = await User.findOne({ userId: emp.employeeId });
      }
    }

    if (!user) {
      res.status(401).json({ success: false, message: 'Invalid credentials. Account not found.' });
      return;
    }

    // Enforce strict staff email authorization check
    if (user.role !== 'ROLE_CUSTOMER' && !AUTHORITATIVE_STAFF_EMAILS.includes(queryEmail)) {
      res.status(401).json({ success: false, message: 'Invalid credentials. Non-authoritative account disabled.' });
      return;
    }

    const isMatch = await bcrypt.compare(password, user.passwordHash);
    if (!isMatch) {
      user.failedLoginAttempts += 1;
      await user.save();
      res.status(401).json({ success: false, message: 'Invalid email or password.' });
      return;
    }

    // Generate Tokens
    const payload = {
      id: user._id.toString(),
      userId: user.userId,
      email: user.email,
      role: user.role,
      roles: user.roles,
      department: user.department,
    };

    const accessToken = jwt.sign(payload, JWT_SECRET, { expiresIn: '8h' });
    const refreshToken = jwt.sign(payload, JWT_REFRESH_SECRET, { expiresIn: '7d' });

    user.lastLoginAt = new Date();
    user.refreshToken = refreshToken;
    user.failedLoginAttempts = 0;
    user.loginHistory.push({ timestamp: new Date(), ip: req.ip, userAgent: req.headers['user-agent'] });
    await user.save();

    res.json({
      success: true,
      message: 'Login successful',
      data: {
        accessToken,
        refreshToken,
        userId: user.userId,
        employeeId: user.userId,
        customerId: user.userId,
        fullName: `${user.firstName} ${user.lastName}`,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        mobile: user.phoneNumber || '',
        profilePhoto: user.avatarUrl || '',
        githubUrl: user.githubUrl || '',
        role: user.role,
        roles: user.roles,
        designation: user.designation || 'Enterprise Specialist',
        department: user.department || 'General',
        permissions: user.permissions || ['USER_READ', 'PROJECT_READ'],
        organizationId: 'TECH-KNIFE-ORG-001',
        lastLogin: user.lastLoginAt ? user.lastLoginAt.toISOString() : new Date().toISOString(),
        tokenType: 'Bearer',
        expiresInMs: 28800000,
      },
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message || 'Internal Server Error during login.' });
  }
};

export const register = async (req: Request, res: Response): Promise<void> => {
  try {
    const { email, password, firstName, lastName, companyName, mobile } = req.body;

    if (!email || !password || !firstName || !lastName) {
      res.status(400).json({ success: false, message: 'Required registration fields missing.' });
      return;
    }

    const existingUser = await User.findOne({ email: email.toLowerCase().trim() });
    if (existingUser) {
      res.status(400).json({ success: false, message: 'User with this email already exists.' });
      return;
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const custId = `CUST-${Date.now()}`;

    // Create Customer Document in Atlas
    const newCustomer = await Customer.create({
      customerId: custId,
      fullName: `${firstName} ${lastName}`,
      companyName: companyName || `${firstName}'s Enterprise`,
      email: email.toLowerCase().trim(),
      mobile: mobile || '+91 90000 00000',
      passwordHash,
      status: 'Active',
    });

    // Create User Account Document in Atlas
    const newUser = await User.create({
      userId: custId,
      email: email.toLowerCase().trim(),
      username: email.split('@')[0],
      passwordHash,
      firstName,
      lastName,
      role: 'ROLE_CUSTOMER',
      roles: ['ROLE_CUSTOMER'],
      department: newCustomer.companyName,
      designation: 'Client Representative',
      phoneNumber: mobile || '',
      enabled: true,
      accountNonLocked: true,
      emailVerified: true,
      permissions: ['CRM_READ'],
    });

    const payload = {
      id: newUser._id.toString(),
      userId: newUser.userId,
      email: newUser.email,
      role: 'ROLE_CUSTOMER',
      roles: ['ROLE_CUSTOMER'],
    };

    const accessToken = jwt.sign(payload, JWT_SECRET, { expiresIn: '8h' });
    const refreshToken = jwt.sign(payload, JWT_REFRESH_SECRET, { expiresIn: '7d' });

    newUser.refreshToken = refreshToken;
    await newUser.save();

    res.status(201).json({
      success: true,
      message: 'Customer registered successfully',
      data: {
        userId: newUser.userId,
        email: newUser.email,
        firstName: newUser.firstName,
        lastName: newUser.lastName,
        roles: newUser.roles,
        role: newUser.role,
        accessToken,
        refreshToken,
        tokenType: 'Bearer',
        expiresInMs: 28800000,
      },
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message || 'Error creating user account.' });
  }
};

export const getCurrentUser = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ success: false, message: 'Unauthorized' });
      return;
    }

    const user = await User.findOne({ userId: req.user.userId }).select('-passwordHash -refreshToken');
    if (!user) {
      res.status(404).json({ success: false, message: 'User profile not found in MongoDB.' });
      return;
    }

    res.json({
      success: true,
      message: 'User profile retrieved',
      data: {
        id: user.userId,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        role: user.role,
        roles: user.roles,
        department: user.department,
        designation: user.designation,
        phoneNumber: user.phoneNumber,
        avatarUrl: user.avatarUrl,
        githubUrl: user.githubUrl,
        enabled: user.enabled,
        accountNonLocked: user.accountNonLocked,
        emailVerified: user.emailVerified,
        permissions: user.permissions,
        lastLoginAt: user.lastLoginAt,
        createdAt: user.createdAt,
        updatedAt: user.updatedAt,
      },
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const refreshToken = async (req: Request, res: Response): Promise<void> => {
  try {
    const { refreshToken } = req.body;
    if (!refreshToken) {
      res.status(400).json({ success: false, message: 'Refresh token required.' });
      return;
    }

    const decoded = jwt.verify(refreshToken, JWT_REFRESH_SECRET) as any;
    const user = await User.findOne({ userId: decoded.userId });

    if (!user || user.refreshToken !== refreshToken) {
      res.status(401).json({ success: false, message: 'Invalid refresh token.' });
      return;
    }

    const payload = {
      id: user._id.toString(),
      userId: user.userId,
      email: user.email,
      role: user.role,
      roles: user.roles,
      department: user.department,
    };

    const newAccessToken = jwt.sign(payload, JWT_SECRET, { expiresIn: '8h' });
    const newRefreshToken = jwt.sign(payload, JWT_REFRESH_SECRET, { expiresIn: '7d' });

    user.refreshToken = newRefreshToken;
    await user.save();

    res.json({
      success: true,
      data: {
        accessToken: newAccessToken,
        refreshToken: newRefreshToken,
      },
    });
  } catch (error: any) {
    res.status(401).json({ success: false, message: 'Failed to refresh token.' });
  }
};

export const logout = async (req: Request, res: Response): Promise<void> => {
  try {
    const { refreshToken } = req.body;
    if (refreshToken) {
      await User.updateOne({ refreshToken }, { $unset: { refreshToken: 1 } });
    }
    res.json({ success: true, message: 'Logged out successfully.' });
  } catch (error: any) {
    res.json({ success: true, message: 'Logged out.' });
  }
};
