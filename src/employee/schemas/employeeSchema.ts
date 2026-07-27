import { z } from 'zod';

export const employeeStatusEnum = z.enum([
  'ACTIVE',
  'INACTIVE',
  'SUSPENDED',
  'TERMINATED',
  'RESIGNED',
]);

export const employmentTypeEnum = z.enum([
  'FULL_TIME',
  'PART_TIME',
  'CONTRACT',
  'PROBATION',
  'TEMPORARY',
]);

export const genderEnum = z.enum([
  'MALE',
  'FEMALE',
  'OTHER',
  'PREFER_NOT_TO_SAY',
]);

export const bloodGroupEnum = z.enum([
  'A_POSITIVE',
  'A_NEGATIVE',
  'B_POSITIVE',
  'B_NEGATIVE',
  'AB_POSITIVE',
  'AB_NEGATIVE',
  'O_POSITIVE',
  'O_NEGATIVE',
]);

export const createEmployeeSchema = z.object({
  employeeId: z
    .string()
    .min(2, 'Employee Code must be at least 2 characters')
    .max(50, 'Employee Code cannot exceed 50 characters'),
  officialEmail: z
    .string()
    .email('Must be a valid official corporate email address'),
  personalEmail: z
    .string()
    .email('Must be a valid email address')
    .optional()
    .or(z.literal('')),
  primaryMobile: z
    .string()
    .min(10, 'Primary mobile number must be at least 10 digits')
    .max(20, 'Primary mobile number cannot exceed 20 characters'),
  alternateMobile: z
    .string()
    .max(20, 'Alternate mobile number cannot exceed 20 characters')
    .optional()
    .or(z.literal('')),
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(50, 'First name cannot exceed 50 characters'),
  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(50, 'Last name cannot exceed 50 characters'),
  gender: genderEnum.optional(),
  dob: z.string().optional().or(z.literal('')),
  bloodGroup: bloodGroupEnum.optional(),
  departmentId: z.string().min(1, 'Department is required'),
  designationId: z.string().min(1, 'Designation is required'),
  managerId: z.string().optional().or(z.literal('')),
  joiningDate: z.string().min(1, 'Joining date is required'),
  employmentType: employmentTypeEnum,
  salary: z.coerce.number().min(0, 'Salary cannot be negative'),
  skills: z.array(z.string()).optional(),
  githubUsername: z.string().optional().or(z.literal('')),
  profileImage: z.string().optional().or(z.literal('')),
  status: employeeStatusEnum.default('ACTIVE'),
});

export const updateEmployeeSchema = z.object({
  personalEmail: z
    .string()
    .email('Must be a valid email address')
    .optional()
    .or(z.literal('')),
  primaryMobile: z
    .string()
    .min(10, 'Primary mobile number must be at least 10 digits')
    .optional()
    .or(z.literal('')),
  alternateMobile: z.string().optional().or(z.literal('')),
  firstName: z.string().min(2, 'First name must be at least 2 characters').optional(),
  lastName: z.string().min(1, 'Last name is required').optional(),
  gender: genderEnum.optional(),
  dob: z.string().optional().or(z.literal('')),
  bloodGroup: bloodGroupEnum.optional(),
  departmentId: z.string().optional(),
  designationId: z.string().optional(),
  managerId: z.string().optional().or(z.literal('')),
  joiningDate: z.string().optional(),
  employmentType: employmentTypeEnum.optional(),
  salary: z.coerce.number().min(0, 'Salary cannot be negative').optional(),
  skills: z.array(z.string()).optional(),
  githubUsername: z.string().optional().or(z.literal('')),
  profileImage: z.string().optional().or(z.literal('')),
  status: employeeStatusEnum.optional(),
});

export const updateEmployeeStatusSchema = z.object({
  status: employeeStatusEnum,
  statusReason: z.string().max(250, 'Reason cannot exceed 250 characters').optional(),
});

export type CreateEmployeeFormValues = z.infer<typeof createEmployeeSchema>;
export type UpdateEmployeeFormValues = z.infer<typeof updateEmployeeSchema>;
export type UpdateEmployeeStatusFormValues = z.infer<typeof updateEmployeeStatusSchema>;
