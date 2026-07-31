import { Response } from 'express';
import { Customer } from '../models/Customer';
import { AuthRequest } from '../middleware/auth';

export const getCustomers = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const customers = await Customer.find({}).lean();
    res.json({
      success: true,
      message: 'Customers retrieved from MongoDB Atlas',
      data: customers,
      timestamp: new Date().toISOString(),
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const getCustomerById = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { id } = req.params;
    const customer = await Customer.findOne({ $or: [{ customerId: id }, { email: id }] }).lean();

    if (!customer) {
      res.status(404).json({ success: false, message: 'Customer not found' });
      return;
    }

    res.json({ success: true, data: customer });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};
