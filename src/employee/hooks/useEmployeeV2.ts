import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { employeeV2Api } from '../api/employeeV2Api';
import {
  CreateEmployeeRequest,
  EmployeeSearchFilter,
  UpdateEmployeeRequest,
  UpdateEmployeeStatusRequest,
} from '../types/employeeV2';

export const EMPLOYEE_V2_KEYS = {
  all: ['employeesV2'] as const,
  list: (params?: Record<string, any>) => [...EMPLOYEE_V2_KEYS.all, 'list', params] as const,
  search: (filter?: EmployeeSearchFilter) => [...EMPLOYEE_V2_KEYS.all, 'search', filter] as const,
  detail: (id?: string) => [...EMPLOYEE_V2_KEYS.all, 'detail', id] as const,
  department: (deptId?: string) => [...EMPLOYEE_V2_KEYS.all, 'department', deptId] as const,
  reports: (managerId?: string) => [...EMPLOYEE_V2_KEYS.all, 'reports', managerId] as const,
};

/**
 * Fetch paginated employees list with basic filters
 */
export function useEmployeesQuery(params?: {
  page?: number;
  size?: number;
  search?: string;
  departmentId?: string;
  managerId?: string;
  status?: string;
}) {
  return useQuery({
    queryKey: EMPLOYEE_V2_KEYS.list(params),
    queryFn: () => employeeV2Api.getAllEmployees(params),
  });
}

/**
 * Perform dynamic multi-criteria search
 */
export function useEmployeeSearchQuery(filter: EmployeeSearchFilter) {
  return useQuery({
    queryKey: EMPLOYEE_V2_KEYS.search(filter),
    queryFn: () => employeeV2Api.searchEmployees(filter),
  });
}

/**
 * Fetch single employee details by ID
 */
export function useEmployeeDetailQuery(id?: string) {
  return useQuery({
    queryKey: EMPLOYEE_V2_KEYS.detail(id),
    queryFn: () => (id ? employeeV2Api.getEmployeeById(id) : Promise.reject('No ID provided')),
    enabled: Boolean(id),
  });
}

/**
 * Fetch department employees
 */
export function useDepartmentEmployeesQuery(departmentId?: string) {
  return useQuery({
    queryKey: EMPLOYEE_V2_KEYS.department(departmentId),
    queryFn: () => (departmentId ? employeeV2Api.getEmployeesByDepartment(departmentId) : Promise.resolve([])),
    enabled: Boolean(departmentId),
  });
}

/**
 * Fetch direct reports for manager
 */
export function useDirectReportsQuery(managerId?: string) {
  return useQuery({
    queryKey: EMPLOYEE_V2_KEYS.reports(managerId),
    queryFn: () => (managerId ? employeeV2Api.getDirectReports(managerId) : Promise.resolve([])),
    enabled: Boolean(managerId),
  });
}

/**
 * Mutation hook to create/onboard new employee
 */
export function useCreateEmployeeMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateEmployeeRequest) => employeeV2Api.createEmployee(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_V2_KEYS.all });
    },
  });
}

/**
 * Mutation hook to update existing employee record
 */
export function useUpdateEmployeeMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, request }: { id: string; request: UpdateEmployeeRequest }) =>
      employeeV2Api.updateEmployee(id, request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_V2_KEYS.all });
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_V2_KEYS.detail(variables.id) });
    },
  });
}

/**
 * Mutation hook to update employee status
 */
export function useUpdateEmployeeStatusMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, request }: { id: string; request: UpdateEmployeeStatusRequest }) =>
      employeeV2Api.updateEmployeeStatus(id, request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_V2_KEYS.all });
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_V2_KEYS.detail(variables.id) });
    },
  });
}

/**
 * Mutation hook to delete employee
 */
export function useDeleteEmployeeMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => employeeV2Api.deleteEmployee(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: EMPLOYEE_V2_KEYS.all });
    },
  });
}
