import mongoose, { Schema, Document } from 'mongoose';

export interface ICustomer extends Document {
  _id: mongoose.Types.ObjectId;
  customerId: string;
  fullName: string;
  companyName: string;
  gstNumber?: string;
  pan?: string;
  email: string;
  mobile: string;
  alternateMobile?: string;
  address?: string;
  state?: string;
  city?: string;
  pin?: string;
  passwordHash: string;
  orders: any[];
  projects: any[];
  invoices: any[];
  payments: any[];
  supportTickets: any[];
  notifications: any[];
  loginHistory: any[];
  status: 'Active' | 'Inactive' | 'Suspended';
  createdAt: Date;
  updatedAt: Date;
}

const CustomerSchema = new Schema<ICustomer>(
  {
    customerId: { type: String, required: true, unique: true, index: true },
    fullName: { type: String, required: true, trim: true },
    companyName: { type: String, required: true, trim: true },
    gstNumber: { type: String, default: '' },
    pan: { type: String, default: '' },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true, index: true },
    mobile: { type: String, required: true },
    alternateMobile: { type: String, default: '' },
    address: { type: String, default: '' },
    state: { type: String, default: '' },
    city: { type: String, default: '' },
    pin: { type: String, default: '' },
    passwordHash: { type: String, required: true },
    orders: [Schema.Types.Mixed],
    projects: [Schema.Types.Mixed],
    invoices: [Schema.Types.Mixed],
    payments: [Schema.Types.Mixed],
    supportTickets: [Schema.Types.Mixed],
    notifications: [Schema.Types.Mixed],
    loginHistory: [Schema.Types.Mixed],
    status: { type: String, default: 'Active', enum: ['Active', 'Inactive', 'Suspended'] },
  },
  {
    timestamps: true,
    collection: 'customers',
  }
);

export const Customer = mongoose.model<ICustomer>('Customer', CustomerSchema);
