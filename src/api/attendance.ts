import { apiClient } from './client';
import {
  AttendanceRecord,
  AttendanceSummary,
  PunchType,
} from '../types';
import { logActivityAction } from './coreServices';

const INITIAL_ATTENDANCE_RECORDS: AttendanceRecord[] = [
  {
    id: 'att-101',
    userId: 'EMP-2026-001',
    userName: 'Sarah Connor',
    userEmail: 'sarah.connor@techknife.io',
    department: 'Engineering',
    date: new Date().toISOString().split('T')[0],
    status: 'PRESENT',
    checkInTime: new Date(new Date().setHours(8, 55, 0)).toISOString(),
    checkOutTime: undefined,
    clockIn: '08:55 AM',
    clockOut: '--:--',
    totalHours: '4h 00m',
    location: 'San Jose HQ',
    totalWorkMinutes: 240,
    totalBreakMinutes: 15,
    overtimeMinutes: 0,
    isLateArrival: false,
    isEarlyLeaving: false,
    isHalfDay: false,
    isWorkFromHome: false,
    isHoliday: false,
    isWeekend: false,
    locationIn: 'San Jose HQ (37.3382, -121.8863)',
    ipAddress: '192.168.1.104',
    punches: [
      {
        punchType: 'CHECK_IN',
        timestamp: new Date(new Date().setHours(8, 55, 0)).toISOString(),
        location: 'San Jose HQ',
        notes: 'Morning Check-In',
      },
      {
        punchType: 'BREAK_START',
        timestamp: new Date(new Date().setHours(12, 0, 0)).toISOString(),
        location: 'San Jose HQ Cafeteria',
        notes: 'Lunch Break',
      },
      {
        punchType: 'BREAK_END',
        timestamp: new Date(new Date().setHours(12, 15, 0)).toISOString(),
        location: 'San Jose HQ Cafeteria',
        notes: 'Back from lunch',
      },
    ],
  },
  {
    id: 'att-102',
    userId: 'EMP-2026-002',
    userName: 'Marcus Brody',
    userEmail: 'marcus.brody@techknife.io',
    department: 'Management',
    date: new Date(Date.now() - 86400000).toISOString().split('T')[0],
    status: 'WFH',
    checkInTime: new Date(Date.now() - 86400000 + 32400000).toISOString(),
    checkOutTime: new Date(Date.now() - 86400000 + 64800000).toISOString(),
    clockIn: '09:00 AM',
    clockOut: '06:00 PM',
    totalHours: '8h 30m',
    location: 'Remote Home Network',
    totalWorkMinutes: 510,
    totalBreakMinutes: 30,
    overtimeMinutes: 30,
    isLateArrival: false,
    isEarlyLeaving: false,
    isHalfDay: false,
    isWorkFromHome: true,
    isHoliday: false,
    isWeekend: false,
    locationIn: 'Remote Home Network (IP 73.189.20.12)',
    locationOut: 'Remote Home Network',
    ipAddress: '73.189.20.12',
    punches: [
      { punchType: 'CHECK_IN', timestamp: new Date(Date.now() - 86400000 + 32400000).toISOString(), location: 'Home Network' },
      { punchType: 'CHECK_OUT', timestamp: new Date(Date.now() - 86400000 + 64800000).toISOString(), location: 'Home Network' },
    ],
  },
  {
    id: 'att-103',
    userId: 'EMP-2026-003',
    userName: 'Elena Rostova',
    userEmail: 'elena.rostova@techknife.io',
    department: 'Engineering',
    date: new Date(Date.now() - 172800000).toISOString().split('T')[0],
    status: 'LATE',
    checkInTime: new Date(Date.now() - 172800000 + 35100000).toISOString(), // 09:45 AM
    checkOutTime: new Date(Date.now() - 172800000 + 64800000).toISOString(),
    clockIn: '09:45 AM',
    clockOut: '06:00 PM',
    totalHours: '7h 45m',
    location: 'San Jose HQ',
    totalWorkMinutes: 465,
    totalBreakMinutes: 30,
    overtimeMinutes: 0,
    isLateArrival: true,
    isEarlyLeaving: false,
    isHalfDay: false,
    isWorkFromHome: false,
    isHoliday: false,
    isWeekend: false,
    locationIn: 'San Jose HQ',
    locationOut: 'San Jose HQ',
    ipAddress: '192.168.1.115',
    punches: [],
  },
  {
    id: 'att-104',
    userId: 'EMP-2026-004',
    userName: 'Devon Vance',
    userEmail: 'devon.vance@techknife.io',
    department: 'DevOps',
    date: new Date(Date.now() - 259200000).toISOString().split('T')[0],
    status: 'HALF_DAY',
    checkInTime: new Date(Date.now() - 259200000 + 32400000).toISOString(),
    checkOutTime: new Date(Date.now() - 259200000 + 46800000).toISOString(),
    clockIn: '09:00 AM',
    clockOut: '01:00 PM',
    totalHours: '4h 00m',
    location: 'San Jose HQ',
    totalWorkMinutes: 240,
    totalBreakMinutes: 0,
    overtimeMinutes: 0,
    isLateArrival: false,
    isEarlyLeaving: true,
    isHalfDay: true,
    isWorkFromHome: false,
    isHoliday: false,
    isWeekend: false,
    locationIn: 'San Jose HQ',
    locationOut: 'San Jose HQ',
    ipAddress: '192.168.1.180',
    punches: [],
  },
];

export const attendanceApi = {
  getTodayAttendance: async (userId: string): Promise<AttendanceRecord | null> => {
    try {
      const res = await apiClient.get('/v1/attendance/today', { params: { userId } });
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null');
      const records: AttendanceRecord[] = stored || INITIAL_ATTENDANCE_RECORDS;
      const today = new Date().toISOString().split('T')[0];
      return records.find((r) => r.userId === userId && r.date === today) || null;
    }
  },

  checkIn: async (data: {
    userId: string;
    userName?: string;
    userEmail?: string;
    department?: string;
    location?: string;
    ipAddress?: string;
    notes?: string;
    isWfh?: boolean;
  }): Promise<AttendanceRecord> => {
    try {
      const res = await apiClient.post('/v1/attendance/check-in', data);
      return res.data.data;
    } catch {
      const records = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null') || INITIAL_ATTENDANCE_RECORDS;
      const today = new Date().toISOString().split('T')[0];
      const now = new Date();
      const isLate = now.getHours() > 9 || (now.getHours() === 9 && now.getMinutes() > 15);

      const newRecord: AttendanceRecord = {
        id: `att-${Date.now()}`,
        userId: data.userId,
        userName: data.userName || 'Current User',
        userEmail: data.userEmail || 'user@techknife.io',
        department: data.department || 'Engineering',
        date: today,
        status: data.isWfh ? 'WFH' : (isLate ? 'LATE' : 'PRESENT'),
        checkInTime: now.toISOString(),
        totalWorkMinutes: 0,
        totalBreakMinutes: 0,
        overtimeMinutes: 0,
        isLateArrival: isLate,
        isEarlyLeaving: false,
        isHalfDay: false,
        isWorkFromHome: !!data.isWfh,
        isHoliday: false,
        isWeekend: false,
        locationIn: data.location || 'HQ San Jose (37.3382, -121.8863)',
        ipAddress: data.ipAddress || '192.168.1.100',
        punches: [
          {
            punchType: 'CHECK_IN',
            timestamp: now.toISOString(),
            location: data.location || 'HQ San Jose',
            notes: data.notes || 'Punched Check-In via Web Portal',
          },
        ],
      };

      const updated = [newRecord, ...records];
      localStorage.setItem('techknife_attendance_records', JSON.stringify(updated));
      logActivityAction(data.userName || 'User', 'ROLE_EMPLOYEE', 'Attendance Management', 'CHECK_IN', `Check-In recorded at ${now.toLocaleTimeString()}`);
      return newRecord;
    }
  },

  checkOut: async (id: string, data: { location?: string; ipAddress?: string; notes?: string }): Promise<AttendanceRecord> => {
    try {
      const res = await apiClient.post(`/v1/attendance/${id}/check-out`, data);
      return res.data.data;
    } catch {
      const records: AttendanceRecord[] = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null') || INITIAL_ATTENDANCE_RECORDS;
      const now = new Date();

      const updatedList = records.map((r) => {
        if (r.id === id) {
          const checkIn = r.checkInTime ? new Date(r.checkInTime) : new Date();
          const grossMinutes = Math.floor((now.getTime() - checkIn.getTime()) / (1000 * 60));
          const netMinutes = Math.max(0, grossMinutes - r.totalBreakMinutes);
          const overtime = netMinutes > 480 ? netMinutes - 480 : 0;
          const isHalf = netMinutes < 240;

          return {
            ...r,
            checkOutTime: now.toISOString(),
            locationOut: data.location || 'HQ San Jose',
            totalWorkMinutes: netMinutes,
            overtimeMinutes: overtime,
            isHalfDay: isHalf,
            status: isHalf ? ('HALF_DAY' as const) : r.status,
            punches: [
              ...r.punches,
              {
                punchType: 'CHECK_OUT' as const,
                timestamp: now.toISOString(),
                location: data.location || 'HQ San Jose',
                notes: data.notes || 'Check-out completed',
              },
            ],
          };
        }
        return r;
      });

      localStorage.setItem('techknife_attendance_records', JSON.stringify(updatedList));
      const record = updatedList.find((r) => r.id === id)!;
      logActivityAction(record.userName, 'ROLE_EMPLOYEE', 'Attendance Management', 'CHECK_OUT', `Checked out. Duration: ${Math.floor(record.totalWorkMinutes / 60)}h ${record.totalWorkMinutes % 60}m`);
      return record;
    }
  },

  toggleBreak: async (id: string, data: { punchType: PunchType; location?: string; notes?: string }): Promise<AttendanceRecord> => {
    try {
      const res = await apiClient.post(`/v1/attendance/${id}/break`, data);
      return res.data.data;
    } catch {
      const records: AttendanceRecord[] = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null') || INITIAL_ATTENDANCE_RECORDS;
      const now = new Date().toISOString();

      const updatedList = records.map((r) => {
        if (r.id === id) {
          const newPunches = [
            ...r.punches,
            {
              punchType: data.punchType,
              timestamp: now,
              location: data.location || 'Office Desk',
              notes: data.notes || `Break ${data.punchType === 'BREAK_START' ? 'Started' : 'Ended'}`,
            },
          ];

          // Calculate cumulative break duration
          let breakTime = 0;
          let breakStart: Date | null = null;
          for (const p of newPunches) {
            if (p.punchType === 'BREAK_START') {
              breakStart = new Date(p.timestamp);
            } else if (p.punchType === 'BREAK_END' && breakStart) {
              breakTime += Math.floor((new Date(p.timestamp).getTime() - breakStart.getTime()) / (1000 * 60));
              breakStart = null;
            }
          }

          return {
            ...r,
            totalBreakMinutes: breakTime,
            punches: newPunches,
          };
        }
        return r;
      });

      localStorage.setItem('techknife_attendance_records', JSON.stringify(updatedList));
      return updatedList.find((r) => r.id === id)!;
    }
  },

  getUserHistory: async (userId: string, fromDate?: string, toDate?: string): Promise<AttendanceRecord[]> => {
    try {
      const res = await apiClient.get('/v1/attendance/history', { params: { userId, fromDate, toDate } });
      return res.data.data;
    } catch {
      const records: AttendanceRecord[] = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null') || INITIAL_ATTENDANCE_RECORDS;
      return records.filter((r) => r.userId === userId || userId === 'all');
    }
  },

  getMonthlyCalendar: async (userId: string, year: number, month: number): Promise<AttendanceRecord[]> => {
    try {
      const res = await apiClient.get('/v1/attendance/calendar', { params: { userId, year, month } });
      return res.data.data;
    } catch {
      const records: AttendanceRecord[] = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null') || INITIAL_ATTENDANCE_RECORDS;
      return records.filter((r) => r.userId === userId || userId === 'all');
    }
  },

  correctAttendance: async (
    id: string,
    data: {
      checkInTime?: string;
      checkOutTime?: string;
      status?: string;
      totalBreakMinutes?: number;
      remarks?: string;
      reason: string;
    },
    adminEmail: string
  ): Promise<AttendanceRecord> => {
    try {
      const res = await apiClient.put(`/v1/attendance/${id}/correct`, data);
      return res.data.data;
    } catch {
      const records: AttendanceRecord[] = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null') || INITIAL_ATTENDANCE_RECORDS;
      const updatedList = records.map((r) => {
        if (r.id === id) {
          return {
            ...r,
            checkInTime: data.checkInTime || r.checkInTime,
            checkOutTime: data.checkOutTime || r.checkOutTime,
            status: (data.status as any) || r.status,
            totalBreakMinutes: data.totalBreakMinutes ?? r.totalBreakMinutes,
            remarks: data.remarks || r.remarks,
            correctedByAdmin: true,
            correctionReason: data.reason,
          };
        }
        return r;
      });

      localStorage.setItem('techknife_attendance_records', JSON.stringify(updatedList));
      logActivityAction(adminEmail, 'ROLE_ADMIN', 'Attendance Management', 'ATTENDANCE_CORRECTED', `Corrected attendance ID ${id}. Reason: ${data.reason}`);
      return updatedList.find((r) => r.id === id)!;
    }
  },

  createManualAttendance: async (data: {
    userId: string;
    userName?: string;
    userEmail?: string;
    department?: string;
    date: string;
    status: string;
    checkInTime?: string;
    checkOutTime?: string;
    totalBreakMinutes?: number;
    isWfh?: boolean;
    remarks?: string;
  }, adminEmail: string): Promise<AttendanceRecord> => {
    try {
      const res = await apiClient.post('/v1/attendance/manual', data);
      return res.data.data;
    } catch {
      const records: AttendanceRecord[] = JSON.parse(localStorage.getItem('techknife_attendance_records') || 'null') || INITIAL_ATTENDANCE_RECORDS;
      const newRec: AttendanceRecord = {
        id: `att-manual-${Date.now()}`,
        userId: data.userId,
        userName: data.userName || `Staff (${data.userId})`,
        userEmail: data.userEmail || `${data.userId.toLowerCase()}@techknife.io`,
        department: data.department || 'Engineering',
        date: data.date,
        status: data.status as any,
        checkInTime: data.checkInTime,
        checkOutTime: data.checkOutTime,
        clockIn: data.checkInTime ? new Date(data.checkInTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '09:00 AM',
        clockOut: data.checkOutTime ? new Date(data.checkOutTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '06:00 PM',
        totalHours: '8h 00m',
        location: 'HQ San Jose',
        totalWorkMinutes: data.checkInTime && data.checkOutTime ? Math.floor((new Date(data.checkOutTime).getTime() - new Date(data.checkInTime).getTime()) / (1000 * 60)) : 480,
        totalBreakMinutes: data.totalBreakMinutes || 30,
        overtimeMinutes: 0,
        isLateArrival: false,
        isEarlyLeaving: false,
        isHalfDay: data.status === 'HALF_DAY',
        isWorkFromHome: !!data.isWfh,
        isHoliday: data.status === 'HOLIDAY',
        isWeekend: data.status === 'WEEKEND',
        remarks: data.remarks || 'Manual Entry by Admin',
        punches: [],
        correctedByAdmin: true,
        correctionReason: `Manual entry by ${adminEmail}`,
      };

      const updated = [newRec, ...records];
      localStorage.setItem('techknife_attendance_records', JSON.stringify(updated));
      logActivityAction(adminEmail, 'ROLE_ADMIN', 'Attendance Management', 'MANUAL_ATTENDANCE_CREATED', `Manual entry created for ${data.userId} on ${data.date}`);
      return newRec;
    }
  },

  bulkImportAttendance: async (recordsList: any[], adminEmail: string): Promise<AttendanceRecord[]> => {
    try {
      const res = await apiClient.post('/v1/attendance/bulk-import', { records: recordsList });
      return res.data.data;
    } catch {
      const createdRecords: AttendanceRecord[] = [];
      for (const item of recordsList) {
        const created = await attendanceApi.createManualAttendance(item, adminEmail);
        createdRecords.push(created);
      }
      logActivityAction(adminEmail, 'ROLE_ADMIN', 'Attendance Management', 'BULK_ATTENDANCE_IMPORT', `Imported ${recordsList.length} records in bulk`);
      return createdRecords;
    }
  },

  getMonthlySummary: async (year: number, month: number, department?: string): Promise<AttendanceSummary[]> => {
    try {
      const res = await apiClient.get('/v1/attendance/monthly-summary', { params: { year, month, department } });
      return res.data.data;
    } catch {
      // Mock aggregated monthly summaries for enterprise reporting
      return [
        {
          userId: 'EMP-2026-001',
          userName: 'Sarah Connor',
          department: 'Engineering',
          periodYear: year,
          periodMonth: month,
          totalDays: 22,
          presentDays: 18,
          absentDays: 0,
          lateDays: 1,
          halfDays: 0,
          wfhDays: 3,
          holidayDays: 1,
          weekendDays: 8,
          leaveDays: 0,
          totalWorkingHours: 168.5,
          totalOvertimeHours: 8.5,
          averageDailyHours: 8.2,
          attendancePercentage: 95.5,
          statusBreakdown: { PRESENT: 18, WFH: 3, LATE: 1, HOLIDAY: 1 },
        },
        {
          userId: 'EMP-2026-002',
          userName: 'Marcus Brody',
          department: 'Management',
          periodYear: year,
          periodMonth: month,
          totalDays: 22,
          presentDays: 15,
          absentDays: 1,
          lateDays: 0,
          halfDays: 0,
          wfhDays: 5,
          holidayDays: 1,
          weekendDays: 8,
          leaveDays: 1,
          totalWorkingHours: 160.0,
          totalOvertimeHours: 4.0,
          averageDailyHours: 8.0,
          attendancePercentage: 90.9,
          statusBreakdown: { PRESENT: 15, WFH: 5, ABSENT: 1, LEAVE: 1, HOLIDAY: 1 },
        },
        {
          userId: 'EMP-2026-003',
          userName: 'Elena Rostova',
          department: 'Engineering',
          periodYear: year,
          periodMonth: month,
          totalDays: 22,
          presentDays: 20,
          absentDays: 0,
          lateDays: 2,
          halfDays: 0,
          wfhDays: 1,
          holidayDays: 1,
          weekendDays: 8,
          leaveDays: 0,
          totalWorkingHours: 172.0,
          totalOvertimeHours: 12.0,
          averageDailyHours: 8.4,
          attendancePercentage: 98.0,
          statusBreakdown: { PRESENT: 20, LATE: 2, WFH: 1, HOLIDAY: 1 },
        },
      ];
    }
  },

  getYearlySummary: async (year: number, userId: string): Promise<AttendanceSummary> => {
    try {
      const res = await apiClient.get('/v1/attendance/yearly-summary', { params: { year, userId } });
      return res.data.data;
    } catch {
      return {
        userId,
        userName: 'Sarah Connor',
        department: 'Engineering',
        periodYear: year,
        totalDays: 252,
        presentDays: 210,
        absentDays: 2,
        lateDays: 5,
        halfDays: 1,
        wfhDays: 28,
        holidayDays: 10,
        weekendDays: 104,
        leaveDays: 6,
        totalWorkingHours: 1940.0,
        totalOvertimeHours: 85.0,
        averageDailyHours: 8.25,
        attendancePercentage: 96.8,
      };
    }
  },
};
