import React, { useState, useEffect, useRef } from 'react';
import { employeesApi, EmployeeData } from '../../api/employees';
import { Search, Check, ChevronDown, X, User } from 'lucide-react';

interface EmployeeSelectProps {
  label: string;
  placeholder?: string;
  isMulti?: boolean;
  value: string | string[];
  onChange: (value: any) => void;
  required?: boolean;
}

export const EmployeeSelect: React.FC<EmployeeSelectProps> = ({
  label,
  placeholder = 'Select employee...',
  isMulti = false,
  value,
  onChange,
  required = false,
}) => {
  const [employees, setEmployees] = useState<EmployeeData[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const fetchEmployees = async () => {
      setLoading(true);
      try {
        const res = await employeesApi.getEmployees();
        const list = Array.isArray(res?.employees) ? res.employees : [];
        setEmployees(list);
      } catch (err) {
        console.error('Failed to load employees for dropdown:', err);
        setEmployees([]);
      } finally {
        setLoading(false);
      }
    };

    fetchEmployees();
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const filteredEmployees = (Array.isArray(employees) ? employees : []).filter((emp) => {
    if (!emp) return false;
    const isIntern = (emp.status === 'INTERN') ||
      (emp.role === 'ROLE_INTERN') ||
      String(emp.employeeId || emp.employeeCode || emp.id || '').toUpperCase().startsWith('INT-');
    if (isIntern) return false;

    const query = (search ?? '').toLowerCase().trim();
    if (!query) return true;
    const name = `${emp.firstName ?? ''} ${emp.lastName ?? ''}`.toLowerCase();
    const code = (emp.employeeId ?? emp.employeeCode ?? emp.id ?? '').toLowerCase();
    const dept = (emp.department ?? '').toLowerCase();
    const desig = (emp.designation ?? '').toLowerCase();

    return (
      name.includes(query) ||
      code.includes(query) ||
      dept.includes(query) ||
      desig.includes(query)
    );
  });

  const getEmpCode = (emp: EmployeeData) => emp.employeeId || emp.employeeCode || emp.id || '';
  const getEmpName = (emp: EmployeeData) => `${emp.firstName || ''} ${emp.lastName || ''}`.trim() || 'Employee';

  const selectedValues: string[] = isMulti
    ? Array.isArray(value)
      ? value
      : []
    : typeof value === 'string' && value
    ? [value]
    : [];

  const handleSelect = (empCode: string) => {
    if (isMulti) {
      const current = Array.isArray(value) ? value : [];
      if (current.includes(empCode)) {
        onChange(current.filter((id) => id !== empCode));
      } else {
        onChange([...current, empCode]);
      }
    } else {
      onChange(empCode);
      setIsOpen(false);
    }
  };

  const handleRemove = (empCode: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (isMulti) {
      const current = Array.isArray(value) ? value : [];
      onChange(current.filter((id) => id !== empCode));
    } else {
      onChange('');
    }
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <label className="text-xs font-bold text-slate-500 block mb-1">
        {label} {required && <span className="text-rose-500">*</span>}
      </label>

      {/* Select Control Button */}
      <div
        onClick={() => setIsOpen(!isOpen)}
        className="w-full min-h-[42px] p-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-medium cursor-pointer flex items-center justify-between gap-2 flex-wrap transition-all focus-within:ring-2 focus-within:ring-cyan-500/20"
      >
        <div className="flex items-center gap-1.5 flex-wrap flex-1">
          {selectedValues.length === 0 ? (
            <span className="text-slate-400 pl-1">{placeholder}</span>
          ) : (
            selectedValues.map((val) => {
              const matched = employees.find((e) => getEmpCode(e) === val);
              const labelText = matched
                ? `${getEmpName(matched)} (${getEmpCode(matched)})`
                : val;
              const avatar = matched?.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(labelText)}`;

              return (
                <span
                  key={val}
                  className="inline-flex items-center gap-1.5 px-2.5 py-1 bg-cyan-500/10 text-cyan-700 dark:text-cyan-300 font-semibold rounded-lg border border-cyan-500/20 text-[11px]"
                >
                  <img
                    src={avatar}
                    alt={labelText}
                    className="w-4 h-4 rounded-full object-cover shrink-0"
                  />
                  <span>{labelText}</span>
                  <X
                    className="w-3 h-3 hover:text-rose-500 cursor-pointer"
                    onClick={(e) => handleRemove(val, e)}
                  />
                </span>
              );
            })
          )}
        </div>
        <ChevronDown className="w-4 h-4 text-slate-400 shrink-0" />
      </div>

      {/* Dropdown Menu */}
      {isOpen && (
        <div className="absolute z-50 left-0 right-0 mt-1 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl overflow-hidden max-h-64 flex flex-col">
          {/* Search Input */}
          <div className="p-2 border-b border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50">
            <div className="relative">
              <Search className="w-3.5 h-3.5 absolute left-2.5 top-2.5 text-slate-400" />
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search by employee ID, name, department..."
                className="w-full pl-8 pr-3 py-1.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs focus:outline-none focus:border-cyan-500"
                autoFocus
              />
            </div>
          </div>

          {/* Employee List */}
          <div className="overflow-y-auto custom-scrollbar flex-1 p-1 space-y-0.5">
            {loading ? (
              <div className="p-4 text-center text-slate-400 text-xs font-medium">
                Loading employees...
              </div>
            ) : filteredEmployees.length === 0 ? (
              <div className="p-4 text-center text-slate-400 text-xs font-medium">
                No employees found matching &quot;{search}&quot;
              </div>
            ) : (
              filteredEmployees.map((emp) => {
                const code = getEmpCode(emp);
                const name = getEmpName(emp);
                const isSelected = selectedValues.includes(code);
                const avatar = emp.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}`;

                return (
                  <div
                    key={code}
                    onClick={() => handleSelect(code)}
                    className={`p-2.5 rounded-xl cursor-pointer transition-colors flex items-center justify-between ${
                      isSelected
                        ? 'bg-cyan-500/10 text-cyan-700 dark:text-cyan-300 font-semibold'
                        : 'hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-200'
                    }`}
                  >
                    <div className="flex items-center gap-2.5">
                      <img
                        src={avatar}
                        alt={name}
                        className="w-7 h-7 rounded-full object-cover shrink-0 border border-slate-200 dark:border-slate-700"
                      />
                      <div className="flex flex-col">
                        <div className="text-xs font-bold flex items-center gap-1.5">
                          <span>{name}</span>
                          <span className="text-cyan-600 dark:text-cyan-400 font-mono text-[10px]">
                            ({code})
                          </span>
                        </div>
                        <div className="text-[10px] text-slate-400 font-medium">
                          {emp.designation || 'Staff'} • {emp.department || 'General'}
                        </div>
                      </div>
                    </div>
                    {isSelected && <Check className="w-4 h-4 text-cyan-500 shrink-0" />}
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}
    </div>
  );
};
