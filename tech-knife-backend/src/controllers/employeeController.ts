import { Response } from 'express';
import bcrypt from 'bcryptjs';
import { Employee } from '../models/Employee';
import { User } from '../models/User';
import { AuthRequest } from '../middleware/auth';
import { generateDatabaseBackups } from '../services/backupService';

export const getAllEmployees = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { department, role, search } = req.query;
    const filter: any = {};

    if (department && department !== 'All') {
      filter.department = String(department);
    }
    if (role) {
      filter.role = String(role);
    }
    if (search) {
      const regex = new RegExp(String(search), 'i');
      filter.$or = [{ fullName: regex }, { officialEmail: regex }, { employeeCode: regex }, { designation: regex }];
    }

    const employees = await Employee.find(filter).lean();
    res.json({
      success: true,
      message: 'Employees retrieved from MongoDB Atlas',
      data: employees,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getEmployeeById = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { id } = req.params;
    const employee = await Employee.findOne({ $or: [{ employeeId: id }, { officialEmail: id }] }).lean();

    if (!employee) {
      res.status(404).json({ success: false, message: 'Employee not found' });
      return;
    }

    res.json({
      success: true,
      data: employee,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const createEmployee = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const {
      firstName,
      lastName,
      officialEmail,
      password,
      role,
      designation,
      department,
      mobileNumber,
      joiningDate,
      skills,
    } = req.body;

    if (!officialEmail || !firstName || !lastName || !role || !designation || !department) {
      res.status(400).json({ success: false, message: 'Missing required employee fields.' });
      return;
    }

    const existing = await Employee.findOne({ officialEmail: officialEmail.toLowerCase().trim() });
    if (existing) {
      res.status(400).json({ success: false, message: 'Employee with this email already exists.' });
      return;
    }

    const passwordHash = await bcrypt.hash(password || 'TechKnife@2026', 10);
    const empId = role === 'ROLE_INTERN' ? `INT-${Date.now().toString().slice(-4)}` : `EMP-${Date.now().toString().slice(-4)}`;
    const empCode = role === 'ROLE_INTERN' ? `TK-INT-${Date.now().toString().slice(-3)}` : `TK-${Date.now().toString().slice(-3)}`;

    const newEmp = await Employee.create({
      employeeId: empId,
      employeeCode: empCode,
      fullName: `${firstName} ${lastName}`,
      firstName,
      lastName,
      username: officialEmail.split('@')[0],
      passwordHash,
      officialEmail: officialEmail.toLowerCase().trim(),
      email: officialEmail.toLowerCase().trim(),
      personalEmail: req.body.personalEmail || officialEmail,
      mobileNumber: mobileNumber || '+91 90000 00000',
      role,
      designation,
      department,
      joiningDate: joiningDate || new Date().toISOString().split('T')[0],
      skills: skills || ['Enterprise Management'],
      employmentStatus: 'Active',
      employmentType: role === 'ROLE_INTERN' ? 'Intern' : 'Full-Time',
      hierarchyLevel: role === 'ROLE_INTERN' ? 4 : 3,
    });

    // Also create User document
    await User.create({
      userId: empId,
      email: officialEmail.toLowerCase().trim(),
      username: officialEmail.split('@')[0],
      passwordHash,
      firstName,
      lastName,
      role,
      roles: [role],
      department,
      designation,
      phoneNumber: mobileNumber || '',
      enabled: true,
      accountNonLocked: true,
      emailVerified: true,
      permissions: ['USER_READ', 'PROJECT_READ'],
    });

    // Refresh backups
    await generateDatabaseBackups();

    res.status(201).json({
      success: true,
      message: 'Employee created in MongoDB Atlas',
      data: newEmp,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const updateEmployee = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { id } = req.params;
    const updates = req.body;

    const updated = await Employee.findOneAndUpdate(
      { $or: [{ employeeId: id }, { officialEmail: id }] },
      { $set: updates },
      { new: true }
    );

    if (!updated) {
      res.status(404).json({ success: false, message: 'Employee record not found.' });
      return;
    }

    await generateDatabaseBackups();

    res.json({
      success: true,
      message: 'Employee updated',
      data: updated,
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const deleteEmployee = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { id } = req.params;
    await Employee.deleteOne({ $or: [{ employeeId: id }, { officialEmail: id }] });
    await User.deleteOne({ userId: id });

    await generateDatabaseBackups();

    res.json({ success: true, message: 'Employee deleted.' });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getInterns = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const interns = await Employee.find({ role: 'ROLE_INTERN' }).lean();
    res.json({
      success: true,
      data: interns,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getOrgChart = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const allEmployees = await Employee.find({}).sort({ hierarchyLevel: 1 }).lean();

    res.json({
      success: true,
      data: allEmployees,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getOrganizationChartV1 = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const rawEmployees = await Employee.find({ employmentStatus: { $ne: 'Terminated' } }).lean();

    // Filter out interns
    const activeStaff = rawEmployees.filter((e) => e.role !== 'ROLE_INTERN');

    const nodeMap = new Map<string, any>();
    const existingIds = new Set<string>();

    activeStaff.forEach((e) => {
      existingIds.add(e.employeeId);
      nodeMap.set(e.employeeId, {
        employeeId: e.employeeId,
        fullName: e.fullName,
        firstName: e.firstName,
        lastName: e.lastName,
        profilePhoto: e.profilePhoto || `https://ui-avatars.com/api/?name=${encodeURIComponent(e.fullName)}&background=0D8ABC&color=fff`,
        designation: e.designation,
        department: e.department,
        role: e.role,
        email: e.officialEmail || e.email,
        mobileNumber: e.mobileNumber,
        status: e.employmentStatus || 'Active',
        company: e.companyName || 'Tech Knife Enterprises',
        reportingManagerId: (e as any).reportingManagerId || e.managerId || '',
        hierarchyLevel: e.hierarchyLevel || 3,
        directReportsCount: 0,
        subordinates: [],
      });
    });

    const allNodes = Array.from(nodeMap.values());

    // Identify Root Nodes (CEO / MD)
    const ceoNode = allNodes.find((n) => n.role === 'ROLE_CEO' || n.designation.toLowerCase().includes('ceo'));
    const mdNode = allNodes.find((n) => n.role === 'ROLE_MD' || n.designation.toLowerCase().includes('managing director'));
    
    const rootNodes: any[] = [];
    if (mdNode) rootNodes.push(mdNode);
    if (ceoNode && ceoNode !== mdNode) rootNodes.push(ceoNode);

    const primaryRoot = ceoNode || mdNode || (rootNodes.length > 0 ? rootNodes[0] : null);

    allNodes.forEach((node) => {
      if (node.role === 'ROLE_CEO' || node.role === 'ROLE_MD') {
        return; // Top roots already collected
      }

      let parentId = node.reportingManagerId;

      // Reconnect if reporting manager is not in database
      if (parentId && !existingIds.has(parentId)) {
        parentId = '';
      }

      if (parentId && nodeMap.has(parentId) && parentId !== node.employeeId) {
        const parentNode = nodeMap.get(parentId);
        if (!parentNode.subordinates.some((s: any) => s.employeeId === node.employeeId)) {
          parentNode.subordinates.push(node);
          parentNode.directReportsCount += 1;
        }
      } else {
        // Connect to primary top root if missing direct manager
        if (primaryRoot && primaryRoot.employeeId !== node.employeeId) {
          if (!primaryRoot.subordinates.some((s: any) => s.employeeId === node.employeeId)) {
            primaryRoot.subordinates.push(node);
            primaryRoot.directReportsCount += 1;
          }
        } else if (!rootNodes.some((r) => r.employeeId === node.employeeId)) {
          rootNodes.push(node);
        }
      }
    });

    res.json({
      success: true,
      message: 'Organization chart retrieved dynamically from MongoDB Atlas',
      data: rootNodes,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

