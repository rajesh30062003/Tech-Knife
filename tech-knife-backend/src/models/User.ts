import mongoose, { Schema, Document } from 'mongoose';

export interface IUser extends Document {
  _id: mongoose.Types.ObjectId;
  userId: string;
  email: string;
  username: string;
  passwordHash: string;
  firstName: string;
  lastName: string;
  role: string;
  roles: string[];
  department?: string;
  designation?: string;
  phoneNumber?: string;
  avatarUrl?: string;
  enabled: boolean;
  accountNonLocked: boolean;
  emailVerified: boolean;
  permissions: string[];
  lastLoginAt?: Date;
  loginHistory: Array<{ timestamp: Date; ip?: string; userAgent?: string }>;
  failedLoginAttempts: number;
  passwordChangedAt?: Date;
  twoFactorEnabled: boolean;
  refreshToken?: string;
  createdAt: Date;
  updatedAt: Date;
}

const UserSchema = new Schema<IUser>(
  {
    userId: { type: String, required: true, unique: true, index: true },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true, index: true },
    username: { type: String, required: true, unique: true, lowercase: true, trim: true, index: true },
    passwordHash: { type: String, required: true },
    firstName: { type: String, required: true, trim: true },
    lastName: { type: String, required: true, trim: true },
    role: { type: String, required: true, default: 'ROLE_EMPLOYEE', index: true },
    roles: { type: [String], default: ['ROLE_EMPLOYEE'] },
    department: { type: String, default: 'General' },
    designation: { type: String, default: 'Team Member' },
    phoneNumber: { type: String, default: '' },
    avatarUrl: { type: String, default: '' },
    enabled: { type: Boolean, default: true },
    accountNonLocked: { type: Boolean, default: true },
    emailVerified: { type: Boolean, default: true },
    permissions: { type: [String], default: [] },
    lastLoginAt: { type: Date },
    loginHistory: [
      {
        timestamp: { type: Date, default: Date.now },
        ip: { type: String },
        userAgent: { type: String },
      },
    ],
    failedLoginAttempts: { type: Number, default: 0 },
    passwordChangedAt: { type: Date },
    twoFactorEnabled: { type: Boolean, default: false },
    refreshToken: { type: String },
  },
  {
    timestamps: true,
    collection: 'users',
  }
);

export const User = mongoose.model<IUser>('User', UserSchema);
