import mongoose, { Schema, Document } from 'mongoose';

export interface IRole extends Document {
  name: string;
  code: string;
  description: string;
  permissions: string[];
  menuPermissions: string[];
  featureFlags: Record<string, boolean>;
  hierarchyLevel: number;
  status: 'Active' | 'Inactive';
  updatedBy?: string;
  createdAt: Date;
  updatedAt: Date;
}

const RoleSchema = new Schema<IRole>(
  {
    name: { type: String, required: true },
    code: { type: String, required: true, unique: true, index: true },
    description: { type: String, default: '' },
    permissions: { type: [String], default: [] },
    menuPermissions: { type: [String], default: [] },
    featureFlags: { type: Schema.Types.Mixed, default: {} },
    hierarchyLevel: { type: Number, default: 30 },
    status: { type: String, default: 'Active', enum: ['Active', 'Inactive'] },
    updatedBy: { type: String, default: 'System' },
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
  category?: string;
}

const PermissionSchema = new Schema<IPermission>(
  {
    code: { type: String, required: true, unique: true },
    name: { type: String, required: true },
    module: { type: String, required: true },
    description: { type: String, default: '' },
    category: { type: String, default: 'General' },
  },
  { timestamps: true, collection: 'permissions' }
);

export const PermissionModel = mongoose.model<IPermission>('PermissionModel', PermissionSchema);

export interface IFeatureFlag extends Document {
  key: string;
  title: string;
  description: string;
  enabled: boolean;
  module?: string;
  updatedBy?: string;
}

const FeatureFlagSchema = new Schema<IFeatureFlag>(
  {
    key: { type: String, required: true, unique: true },
    title: { type: String, required: true },
    description: { type: String, default: '' },
    enabled: { type: Boolean, default: true },
    module: { type: String, default: 'General' },
    updatedBy: { type: String, default: 'System' },
  },
  { timestamps: true, collection: 'feature_flags' }
);

export const FeatureFlagModel = mongoose.model<IFeatureFlag>('FeatureFlagModel', FeatureFlagSchema);

export interface IRoutePermission extends Document {
  path: string;
  title: string;
  allowedRoles: string[];
  requiredPermission?: string;
  enabled: boolean;
}

const RoutePermissionSchema = new Schema<IRoutePermission>(
  {
    path: { type: String, required: true, unique: true },
    title: { type: String, required: true },
    allowedRoles: { type: [String], default: [] },
    requiredPermission: { type: String, default: '' },
    enabled: { type: Boolean, default: true },
  },
  { timestamps: true, collection: 'route_permissions' }
);

export const RoutePermissionModel = mongoose.model<IRoutePermission>('RoutePermissionModel', RoutePermissionSchema);
