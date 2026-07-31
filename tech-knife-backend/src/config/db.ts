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

const MONGODB_URI = process.env.MONGODB_URI || 'mongodb+srv://techknife01_db_user:cgTVT9DFas3hoG3n@techknife.yotus1r.mongodb.net/techknife?retryWrites=true&w=majority&appName=TechKnife';

export const connectDB = async (): Promise<void> => {
  try {
    const conn = await mongoose.connect(MONGODB_URI, {
      dbName: process.env.MONGODB_DATABASE || 'techknife',
      serverSelectionTimeoutMS: 15000,
    });
    console.log(`[MongoDB Atlas] Connected to Host: ${conn.connection.host}, Database: ${conn.connection.name}`);
  } catch (error) {
    console.error('[MongoDB Atlas Connection Error]', error);
    // If SRV lookup fails under restrictive local DNS, attempt standard direct node connection fallback
    try {
      const fallbackUri = 'mongodb://techknife01_db_user:cgTVT9DFas3hoG3n@techknife-shard-00-00.yotus1r.mongodb.net:27017,techknife-shard-00-01.yotus1r.mongodb.net:27017,techknife-shard-00-02.yotus1r.mongodb.net:27017/techknife?replicaSet=atlas-13s63k-shard-0&ssl=true&authSource=admin';
      console.log('[MongoDB Atlas] Attempting Direct Seed Connection...');
      const conn = await mongoose.connect(fallbackUri, {
        dbName: 'techknife',
        serverSelectionTimeoutMS: 15000,
      });
      console.log(`[MongoDB Atlas] Connected via Direct Replica Set to Database: ${conn.connection.name}`);
    } catch (fallbackErr) {
      console.error('[MongoDB Atlas Fallback Connection Error]', fallbackErr);
      process.exit(1);
    }
  }
};
