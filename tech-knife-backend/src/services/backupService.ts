import fs from 'fs';
import path from 'path';
import mongoose from 'mongoose';
import { User } from '../models/User';
import { Employee } from '../models/Employee';
import { Customer } from '../models/Customer';
import { RoleModel, DepartmentModel, PermissionModel } from '../models/RoleAndDept';
import {
  Attendance,
  Salary,
  BankAccount,
  LeaveRequest,
  Project,
  Task,
  Announcement,
  Notification,
  Setting,
} from '../models/EnterpriseModels';

export const generateDatabaseBackups = async (): Promise<void> => {
  try {
    const rootBackupDir = path.resolve(__dirname, '../../../backend/database');
    const localBackupDir = path.resolve(__dirname, '../../database');

    [rootBackupDir, localBackupDir].forEach((dir) => {
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }
    });

    const collections: { name: string; model: mongoose.Model<any> }[] = [
      { name: 'users.txt', model: User as any },
      { name: 'employees.txt', model: Employee as any },
      { name: 'customers.txt', model: Customer as any },
      { name: 'roles.txt', model: RoleModel as any },
      { name: 'departments.txt', model: DepartmentModel as any },
      { name: 'attendance.txt', model: Attendance as any },
      { name: 'salary.txt', model: Salary as any },
      { name: 'bankAccounts.txt', model: BankAccount as any },
      { name: 'leaveRequests.txt', model: LeaveRequest as any },
      { name: 'projects.txt', model: Project as any },
      { name: 'tasks.txt', model: Task as any },
      { name: 'announcements.txt', model: Announcement as any },
      { name: 'notifications.txt', model: Notification as any },
      { name: 'permissions.txt', model: PermissionModel as any },
      { name: 'settings.txt', model: Setting as any },
    ];

    for (const item of collections) {
      const docs = await item.model.find({}).lean();
      const formattedJson = JSON.stringify(docs, null, 2);

      const rootPath = path.join(rootBackupDir, item.name);
      const localPath = path.join(localBackupDir, item.name);

      fs.writeFileSync(rootPath, formattedJson, 'utf-8');
      fs.writeFileSync(localPath, formattedJson, 'utf-8');
    }

    console.log('[Backup Service] Backup txt files successfully created in backend/database/');
  } catch (error) {
    console.error('[Backup Service Error]', error);
  }
};
