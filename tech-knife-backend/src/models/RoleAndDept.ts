import mongoose, { Schema, Document } from 'mongoose';

export interface IRole extends Document {
  name: string;
  code: string;
  description: string;
  permissions: string[];
}

const RoleSchema = new Schema<IRole>(
  {
    name: { type: String, required: true, unique: true },
    code: { type: String, required: true, unique: true, index: true },
    description: { type: String, default: '' },
    permissions: { type: [String], default: [] },
  },
  { timestamps: true, collection: 'roles' }
);

export const RoleModel = mongoose.model<IRole>('RoleModel', RoleSchema);

export interface IDepartment extends Document {
  name: string;
  code: string;
  headName?: string;
  description?: string;
  employeeCount: number;
}

const DepartmentSchema = new Schema<IDepartment>(
  {
    name: { type: String, required: true, unique: true },
    code: { type: String, required: true, unique: true },
    headName: { type: String, default: '' },
    description: { type: String, default: '' },
    employeeCount: { type: Number, default: 0 },
  },
  { timestamps: true, collection: 'departments' }
);

export const DepartmentModel = mongoose.model<IDepartment>('DepartmentModel', DepartmentSchema);

export interface IPermission extends Document {
  code: string;
  name: string;
  module: string;
  description?: string;
}

const PermissionSchema = new Schema<IPermission>(
  {
    code: { type: String, required: true, unique: true },
    name: { type: String, required: true },
    module: { type: String, required: true },
    description: { type: String, default: '' },
  },
  { timestamps: true, collection: 'permissions' }
);

export const PermissionModel = mongoose.model<IPermission>('PermissionModel', PermissionSchema);
