import { Response } from 'express';
import { Attendance } from '../models/EnterpriseModels';
import { AuthRequest } from '../middleware/auth';
import { generateDatabaseBackups } from '../services/backupService';

export const getAttendance = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { date, department, userId } = req.query;
    const filter: any = {};

    if (date) filter.date = String(date);
    if (department && department !== 'All') filter.department = String(department);
    if (userId) filter.userId = String(userId);

    const records = await Attendance.find(filter).sort({ date: -1 }).lean();
    res.json({
      success: true,
      message: 'Attendance fetched from MongoDB Atlas',
      data: records,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const clockIn = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ success: false, message: 'Unauthorized' });
      return;
    }

    const todayStr = new Date().toISOString().split('T')[0];
    const timeStr = new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

    let record = await Attendance.findOne({ userId: req.user.userId, date: todayStr });

    if (!record) {
      record = new Attendance({
        attendanceId: `ATT-${req.user.userId}-${todayStr}`,
        userId: req.user.userId,
        userEmail: req.user.email,
        userName: `${req.user.email.split('@')[0]}`,
        department: req.user.department || 'General',
        date: todayStr,
        status: 'PRESENT',
        checkInTime: timeStr,
        clockIn: timeStr,
        totalHours: '0.0 hrs',
        punches: [{ punchType: 'CHECK_IN', timestamp: new Date().toISOString() }],
      });
    } else {
      record.status = 'PRESENT';
      record.checkInTime = timeStr;
      record.clockIn = timeStr;
      record.punches.push({ punchType: 'CHECK_IN', timestamp: new Date().toISOString() });
    }

    await record.save();
    await generateDatabaseBackups();

    res.json({ success: true, message: 'Clock-in successful', data: record });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const clockOut = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ success: false, message: 'Unauthorized' });
      return;
    }

    const todayStr = new Date().toISOString().split('T')[0];
    const timeStr = new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

    const record = await Attendance.findOne({ userId: req.user.userId, date: todayStr });
    if (!record) {
      res.status(404).json({ success: false, message: 'No active clock-in record found for today.' });
      return;
    }

    record.checkOutTime = timeStr;
    record.clockOut = timeStr;
    record.totalHours = '8.0 hrs';
    record.punches.push({ punchType: 'CHECK_OUT', timestamp: new Date().toISOString() });

    await record.save();
    await generateDatabaseBackups();

    res.json({ success: true, message: 'Clock-out successful', data: record });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};
