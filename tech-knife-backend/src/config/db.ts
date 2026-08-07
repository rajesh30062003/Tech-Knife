import mongoose from 'mongoose';
import dotenv from 'dotenv';
import dns from 'dns';

dotenv.config();

// Ensure SRV records and IPv4 resolution work seamlessly across network environments
try {
  dns.setServers(['8.8.8.8', '1.1.1.1', '8.8.4.4']);
  if (dns.setDefaultResultOrder) {
    dns.setDefaultResultOrder('ipv4first');
  }
} catch (e) {
  // Ignore DNS config warnings if restricted
}

const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/techknife';

export const connectDB = async (): Promise<void> => {
  try {
    const conn = await mongoose.connect(MONGODB_URI, {
      dbName: process.env.MONGODB_DATABASE || 'techknife',
      serverSelectionTimeoutMS: 15000,
    });
    console.log(`[MongoDB] Connected to Host: ${conn.connection.host}, Database: ${conn.connection.name}`);
  } catch (error) {
    console.error('[MongoDB Connection Error]', error);
    process.exit(1);
  }
};
