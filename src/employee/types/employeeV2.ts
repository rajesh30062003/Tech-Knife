export type EmployeeStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'TERMINATED' | 'RESIGNED';

export type EmploymentType = 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'PROBATION' | 'TEMPORARY';

export type Gender = 'MALE' | 'FEMALE' | 'OTHER' | 'PREFER_NOT_TO_SAY';

export type BloodGroup =
  | 'A_POSITIVE'
  | 'A_NEGATIVE'
  | 'B_POSITIVE'
  | 'B_NEGATIVE'
  | 'AB_POSITIVE'
  | 'AB_NEGATIVE'
  | 'O_POSITIVE'
  | 'O_NEGATIVE';

export interface CreateEmployeeRequest {
  employeeId: string;
  officialEmail: string;
  personalEmail?: string;
  primaryMobile: string;
  alternateMobile?: string;
  firstName: string;
  lastName: string;
  gender?: Gender;
  dob?: string;
  bloodGroup?: BloodGroup;
  departmentId: string;
  designationId: string;
  managerId?: string;
  joiningDate: string;
  employmentType: EmploymentType;
  salary: number;
  skills?: string[];
  githubUsername?: string;
  profileImage?: string;
  status?: EmployeeStatus;
}

export interface UpdateEmployeeRequest {
  personalEmail?: string;
  primaryMobile?: string;
  alternateMobile?: string;
  firstName?: string;
  lastName?: string;
  gender?: Gender;
  dob?: string;
  bloodGroup?: BloodGroup;
  departmentId?: string;
  designationId?: string;
  managerId?: string;
  joiningDate?: string;
  employmentType?: EmploymentType;
  salary?: number;
  skills?: string[];
  githubUsername?: string;
  profileImage?: string;
  status?: EmployeeStatus;
}

export interface UpdateEmployeeStatusRequest {
  status: EmployeeStatus;
  statusReason?: string;
}

export interface EmployeeSearchFilter {
  searchTerm?: string;
  departmentId?: string;
  designationId?: string;
  managerId?: string;
  status?: EmployeeStatus;
  employmentType?: EmploymentType;
  bloodGroup?: BloodGroup;
  skills?: string[];
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export interface EmployeeResponse {
  id: string;
  employeeId: string;
  officialEmail: string;
  personalEmail?: string;
  primaryMobile: string;
  alternateMobile?: string;
  firstName: string;
  lastName: string;
  fullName: string;
  gender?: Gender;
  dob?: string;
  bloodGroup?: BloodGroup;
  departmentId: string;
  designationId: string;
  managerId?: string;
  joiningDate: string;
  employmentType: EmploymentType;
  salary: number;
  skills: string[];
  githubUsername?: string;
  profileImage?: string;
  status: EmployeeStatus;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface EmployeeSummaryResponse {
  id: string;
  employeeId: string;
  fullName: string;
  officialEmail: string;
  primaryMobile: string;
  departmentId: string;
  designationId: string;
  employmentType: EmploymentType;
  status: EmployeeStatus;
  profileImage?: string;
  joiningDate: string;
}

export interface EmployeeDTO {
  id: string;
  employeeId: string;
  officialEmail: string;
  personalEmail?: string;
  primaryMobile: string;
  alternateMobile?: string;
  firstName: string;
  lastName: string;
  gender?: Gender;
  dob?: string;
  bloodGroup?: BloodGroup;
  departmentId: string;
  designationId: string;
  managerId?: string;
  joiningDate: string;
  employmentType: EmploymentType;
  salary: number;
  skills: string[];
  githubUsername?: string;
  profileImage?: string;
  status: EmployeeStatus;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
