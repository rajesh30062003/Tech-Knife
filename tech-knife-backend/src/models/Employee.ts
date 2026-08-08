import mongoose, { Schema, Document } from 'mongoose';

export interface IEmployee extends Document {
  _id: mongoose.Types.ObjectId;
  employeeId: string;
  employeeCode: string;
  fullName: string;
  firstName: string;
  lastName: string;
  username: string;
  passwordHash: string;
  profilePhoto?: string;
  gender: string;
  dateOfBirth?: string;
  bloodGroup?: string;
  maritalStatus?: string;
  nationality?: string;
  aadhaarNumber?: string;
  panNumber?: string;
  passportNumber?: string;
  drivingLicenseNumber?: string;
  personalEmail: string;
  officialEmail: string;
  githubUrl?: string;
  mobileNumber: string;
  alternateMobileNumber?: string;
  emergencyContactName?: string;
  emergencyContactNumber?: string;
  emergencyContactRelation?: string;
  presentAddress?: string;
  permanentAddress?: string;
  
  designation: string;
  role: string;
  department: string;
  reportingManager?: string;
  employmentType: string;
  workLocation: string;
  officeBranch: string;
  joiningDate: string;
  confirmationDate?: string;
  probationStatus?: string;
  experience?: string;
  previousCompany?: string;
  skills: string[];
  certifications: string[];
  education: Array<{ degree: string; institution: string; year: string; grade?: string }>;
  salaryGrade?: string;
  shift?: string;
  workingHours?: string;
  weeklyOff?: string;
  attendanceStatus?: string;
  leaveBalance?: { annual: number; sick: number; casual: number };
  promotionHistory: any[];
  transferHistory: any[];
  employmentStatus: 'Active' | 'On Leave' | 'Terminated' | 'Resigned' | 'Probation';

  companyName: string;
  businessUnit?: string;
  team?: string;
  division?: string;
  managerId?: string;
  managerName?: string;
  hierarchyLevel: number;

  email: string;
  lastLogin?: Date;
  loginHistory?: any[];
  failedLoginAttempts: number;
  passwordChangedDate?: Date;
  twoFactorEnabled: boolean;
  refreshToken?: string;
  accountLocked: boolean;
  activeStatus: boolean;

  bankDetails?: {
    bankName: string;
    branchName: string;
    accountHolderName: string;
    accountNumber: string;
    ifscCode: string;
    upiId?: string;
    salaryAccount: boolean;
    pfNumber?: string;
    uanNumber?: string;
    esiNumber?: string;
  };

  payroll?: {
    basicSalary: number;
    hra: number;
    bonus: number;
    incentives: number;
    tax: number;
    professionalTax: number;
    netSalary: number;
    salarySlipHistory: any[];
  };

  documents?: {
    aadhaarUrl?: string;
    panUrl?: string;
    passportUrl?: string;
    resumeUrl?: string;
    offerLetterUrl?: string;
    appointmentLetterUrl?: string;
    relievingLetterUrl?: string;
    experienceCertUrl?: string;
    educationalCertsUrl?: string;
    joiningDocsUrl?: string;
    ndaUrl?: string;
    policeVerificationUrl?: string;
    medicalCertificateUrl?: string;
  };

  companyAssets?: any[];

  performance?: {
    kpis: string[];
    goals: string[];
    reviews: any[];
    rating: number;
    achievements: string[];
    awards: string[];
    warningLetters: any[];
    trainingHistory: any[];
  };

  systemPermissions?: {
    role: string;
    permissions: string[];
    accessibleModules: string[];
    approvalLevel: number;
    menuVisibility: string[];
    featureAccess: string[];
  };

  createdAt: Date;
  updatedAt: Date;
}

const EmployeeSchema = new Schema<IEmployee>(
  {
    employeeId: { type: String, required: true, unique: true, index: true },
    employeeCode: { type: String, required: true, unique: true, index: true },
    fullName: { type: String, required: true, trim: true },
    firstName: { type: String, required: true, trim: true },
    lastName: { type: String, required: true, trim: true },
    username: { type: String, required: true, unique: true, lowercase: true, trim: true },
    passwordHash: { type: String, required: true },
    profilePhoto: { type: String, default: '' },
    gender: { type: String, default: 'Unspecified' },
    dateOfBirth: { type: String, default: '' },
    bloodGroup: { type: String, default: '' },
    maritalStatus: { type: String, default: '' },
    nationality: { type: String, default: 'Indian' },
    aadhaarNumber: { type: String, default: '' },
    panNumber: { type: String, default: '' },
    passportNumber: { type: String, default: '' },
    drivingLicenseNumber: { type: String, default: '' },
    personalEmail: { type: String, default: '' },
    officialEmail: { type: String, required: true, unique: true, lowercase: true, trim: true, index: true },
    githubUrl: { type: String, default: '' },
    mobileNumber: { type: String, default: '' },
    alternateMobileNumber: { type: String, default: '' },
    emergencyContactName: { type: String, default: '' },
    emergencyContactNumber: { type: String, default: '' },
    emergencyContactRelation: { type: String, default: '' },
    presentAddress: { type: String, default: '' },
    permanentAddress: { type: String, default: '' },

    designation: { type: String, required: true },
    role: { type: String, required: true, index: true },
    department: { type: String, required: true, index: true },
    reportingManager: { type: String, default: '' },
    employmentType: { type: String, default: 'Full-Time' },
    workLocation: { type: String, default: 'Headquarters' },
    officeBranch: { type: String, default: 'Kolkata Main' },
    joiningDate: { type: String, required: true },
    confirmationDate: { type: String, default: '' },
    probationStatus: { type: String, default: 'Passed' },
    experience: { type: String, default: '3+ Years' },
    previousCompany: { type: String, default: '' },
    skills: { type: [String], default: [] },
    certifications: { type: [String], default: [] },
    education: [
      {
        degree: String,
        institution: String,
        year: String,
        grade: String,
      },
    ],
    salaryGrade: { type: String, default: 'G4' },
    shift: { type: String, default: 'General Shift (9:00 AM - 6:00 PM)' },
    workingHours: { type: String, default: '8 Hours/Day' },
    weeklyOff: { type: String, default: 'Saturday, Sunday' },
    attendanceStatus: { type: String, default: 'PRESENT' },
    leaveBalance: {
      annual: { type: Number, default: 18 },
      sick: { type: Number, default: 12 },
      casual: { type: Number, default: 10 },
    },
    promotionHistory: [Schema.Types.Mixed],
    transferHistory: [Schema.Types.Mixed],
    employmentStatus: { type: String, default: 'Active', enum: ['Active', 'On Leave', 'Terminated', 'Resigned', 'Probation'] },

    companyName: { type: String, default: 'Tech Knife Enterprises' },
    businessUnit: { type: String, default: 'Core Operations' },
    team: { type: String, default: 'Enterprise Solutions' },
    division: { type: String, default: 'Technology' },
    managerId: { type: String, default: '' },
    managerName: { type: String, default: '' },
    hierarchyLevel: { type: Number, default: 3 },

    email: { type: String, required: true, lowercase: true, trim: true },
    lastLogin: { type: Date },
    loginHistory: [Schema.Types.Mixed],
    failedLoginAttempts: { type: Number, default: 0 },
    passwordChangedDate: { type: Date },
    twoFactorEnabled: { type: Boolean, default: false },
    refreshToken: { type: String, default: '' },
    accountLocked: { type: Boolean, default: false },
    activeStatus: { type: Boolean, default: true },

    bankDetails: {
      bankName: { type: String, default: 'HDFC Bank' },
      branchName: { type: String, default: 'Salt Lake Sector V' },
      accountHolderName: { type: String, default: '' },
      accountNumber: { type: String, default: '' },
      ifscCode: { type: String, default: 'HDFC0001234' },
      upiId: { type: String, default: '' },
      salaryAccount: { type: Boolean, default: true },
      pfNumber: { type: String, default: '' },
      uanNumber: { type: String, default: '' },
      esiNumber: { type: String, default: '' },
    },

    payroll: {
      basicSalary: { type: Number, default: 75000 },
      hra: { type: Number, default: 30000 },
      bonus: { type: Number, default: 10000 },
      incentives: { type: Number, default: 5000 },
      tax: { type: Number, default: 8000 },
      professionalTax: { type: Number, default: 200 },
      netSalary: { type: Number, default: 111800 },
      salarySlipHistory: [Schema.Types.Mixed],
    },

    documents: {
      aadhaarUrl: { type: String, default: '' },
      panUrl: { type: String, default: '' },
      passportUrl: { type: String, default: '' },
      resumeUrl: { type: String, default: '' },
      offerLetterUrl: { type: String, default: '' },
      appointmentLetterUrl: { type: String, default: '' },
      relievingLetterUrl: { type: String, default: '' },
      experienceCertUrl: { type: String, default: '' },
      educationalCertsUrl: { type: String, default: '' },
      joiningDocsUrl: { type: String, default: '' },
      ndaUrl: { type: String, default: '' },
      policeVerificationUrl: { type: String, default: '' },
      medicalCertificateUrl: { type: String, default: '' },
    },

    companyAssets: [Schema.Types.Mixed],
    performance: {
      kpis: { type: [String], default: ['System Reliability 99.9%', 'On-time Delivery'] },
      goals: { type: [String], default: ['Q3 Enterprise Refactor', 'Team Mentorship'] },
      reviews: [Schema.Types.Mixed],
      rating: { type: Number, default: 4.8 },
      achievements: { type: [String], default: ['Excellence Award 2025'] },
      awards: { type: [String], default: ['Employee of the Month'] },
      warningLetters: [Schema.Types.Mixed],
      trainingHistory: [Schema.Types.Mixed],
    },

    systemPermissions: {
      role: { type: String, default: 'ROLE_EMPLOYEE' },
      permissions: { type: [String], default: ['USER_READ', 'PROJECT_READ'] },
      accessibleModules: { type: [String], default: ['Dashboard', 'Profile', 'Attendance', 'Projects'] },
      approvalLevel: { type: Number, default: 1 },
      menuVisibility: { type: [String], default: ['Core', 'Operations'] },
      featureAccess: { type: [String], default: ['Standard'] },
    },
  },
  {
    timestamps: true,
    collection: 'employees',
  }
);

export const Employee = mongoose.model<IEmployee>('Employee', EmployeeSchema);
