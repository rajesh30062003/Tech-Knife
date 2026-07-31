import { Response } from 'express';
import { RoleModel, DepartmentModel, PermissionModel } from '../models/RoleAndDept';
import { AuthRequest } from '../middleware/auth';

export const getRoles = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const roles = await RoleModel.find({}).lean();
    res.json({ success: true, data: roles });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getDepartments = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const depts = await DepartmentModel.find({}).lean();
    res.json({ success: true, data: depts });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getPermissions = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const perms = await PermissionModel.find({}).lean();
    res.json({ success: true, data: perms });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};
