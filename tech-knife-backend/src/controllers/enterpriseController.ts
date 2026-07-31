import { Response } from 'express';
import { Project, Task, Announcement, Notification, LeaveRequest, Salary } from '../models/EnterpriseModels';
import { AuthRequest } from '../middleware/auth';

export const getProjects = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const projects = await Project.find({}).lean();
    res.json({ success: true, data: projects });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getTasks = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const tasks = await Task.find({}).lean();
    res.json({ success: true, data: tasks });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getAnnouncements = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const announcements = await Announcement.find({}).sort({ createdAt: -1 }).lean();
    res.json({ success: true, data: announcements });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getNotifications = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ success: false, message: 'Unauthorized' });
      return;
    }
    const notifications = await Notification.find({ userId: req.user.userId }).sort({ createdAt: -1 }).lean();
    res.json({ success: true, data: notifications });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getLeaves = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const leaves = await LeaveRequest.find({}).sort({ createdAt: -1 }).lean();
    res.json({ success: true, data: leaves });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getSalarySlips = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const slips = await Salary.find({}).sort({ createdAt: -1 }).lean();
    res.json({ success: true, data: slips });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};
