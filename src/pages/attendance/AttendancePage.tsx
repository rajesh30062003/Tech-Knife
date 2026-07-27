import React, { useState, useEffect } from 'react';
import {
  Clock, MapPin, CheckCircle2, AlertCircle, Calendar, Coffee, Home,
  Edit3, Plus, UploadCloud, Download, BarChart3, Filter, Search,
  Users, Check, X, ChevronLeft, ChevronRight, Info, Sparkles, User, Briefcase, RefreshCw
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { StatusBadge } from '../../components/common/StatusBadge';
import { attendanceApi } from '../../api/attendance';
import {
  AttendanceRecord,
  AttendanceSummary,
  AttendanceStatusType,
  PunchType
} from '../../types';

export const AttendancePage: React.FC = () => {
  const { user } = useAuth();
  const isAdmin = user?.roles?.some(r => ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_MANAGER'].includes(r)) || false;

  const [activeTab, setActiveTab] = useState<'today' | 'history' | 'calendar' | 'admin' | 'monthly_summary' | 'yearly_summary'>('today');

  // Today state
  const [todayRecord, setTodayRecord] = useState<AttendanceRecord | null>(null);
  const [isWfh, setIsWfh] = useState(false);
  const [punchNotes, setPunchNotes] = useState('');
  const [punchLocation, setPunchLocation] = useState('San Jose HQ (37.3382, -121.8863)');
  const [isOnBreak, setIsOnBreak] = useState(false);
  const [loading, setLoading] = useState(false);

  // History & Calendar state
  const [historyList, setHistoryList] = useState<AttendanceRecord[]>([]);
  const [historySearch, setHistorySearch] = useState('');
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [calendarRecords, setCalendarRecords] = useState<AttendanceRecord[]>([]);

  // Summaries
  const [monthlySummaries, setMonthlySummaries] = useState<AttendanceSummary[]>([]);
  const [yearlySummary, setYearlySummary] = useState<AttendanceSummary | null>(null);
  const [departmentFilter, setDepartmentFilter] = useState('');

  // Modals
  const [showCorrectionModal, setShowCorrectionModal] = useState(false);
  const [selectedCorrectionRecord, setSelectedCorrectionRecord] = useState<AttendanceRecord | null>(null);
  const [correctionForm, setCorrectionForm] = useState({
    checkInTime: '',
    checkOutTime: '',
    status: 'PRESENT' as AttendanceStatusType,
    totalBreakMinutes: 30,
    remarks: '',
    reason: '',
  });

  const [showManualModal, setShowManualModal] = useState(false);
  const [manualForm, setManualForm] = useState({
    userId: 'EMP-2026-005',
    userName: 'David Vance',
    userEmail: 'david.vance@techknife.io',
    department: 'Engineering',
    date: new Date().toISOString().split('T')[0],
    status: 'PRESENT' as AttendanceStatusType,
    checkInTime: `${new Date().toISOString().split('T')[0]}T09:00`,
    checkOutTime: `${new Date().toISOString().split('T')[0]}T18:00`,
    totalBreakMinutes: 60,
    isWfh: false,
    remarks: 'Approved manual attendance entry',
  });

  const [showBulkImportModal, setShowBulkImportModal] = useState(false);
  const [bulkDataText, setBulkDataText] = useState(`EMP-2026-001,Sarah Connor,Engineering,2026-07-23,PRESENT,09:00,18:00
EMP-2026-002,Marcus Brody,Management,2026-07-23,WFH,08:30,17:30
EMP-2026-003,Elena Rostova,Engineering,2026-07-23,LATE,09:45,18:15`);

  // Fetch initial data
  useEffect(() => {
    loadData();
  }, [user, selectedMonth, selectedYear, activeTab]);

  const loadData = async () => {
    setLoading(true);
    const userId = user?.id || 'EMP-2026-001';

    try {
      const today = await attendanceApi.getTodayAttendance(userId);
      setTodayRecord(today);

      if (today && today.punches && today.punches.length > 0) {
        const lastPunch = today.punches[today.punches.length - 1];
        setIsOnBreak(lastPunch.punchType === 'BREAK_START');
      }

      const history = await attendanceApi.getUserHistory(isAdmin ? 'all' : userId);
      setHistoryList(history);

      const calendar = await attendanceApi.getMonthlyCalendar(userId, selectedYear, selectedMonth);
      setCalendarRecords(calendar);

      if (activeTab === 'monthly_summary' || isAdmin) {
        const monthly = await attendanceApi.getMonthlySummary(selectedYear, selectedMonth, departmentFilter);
        setMonthlySummaries(monthly);
      }

      if (activeTab === 'yearly_summary') {
        const yearly = await attendanceApi.getYearlySummary(selectedYear, userId);
        setYearlySummary(yearly);
      }
    } catch (error) {
      console.error('Error loading attendance data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCheckIn = async () => {
    setLoading(true);
    try {
      const rec = await attendanceApi.checkIn({
        userId: user?.id || 'EMP-2026-001',
        userName: user?.name || 'Current User',
        userEmail: user?.email || 'user@techknife.io',
        department: 'Engineering',
        location: punchLocation,
        ipAddress: '192.168.1.104',
        notes: punchNotes,
        isWfh,
      });
      setTodayRecord(rec);
      setPunchNotes('');
      await loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCheckOut = async () => {
    if (!todayRecord) return;
    setLoading(true);
    try {
      const rec = await attendanceApi.checkOut(todayRecord.id, {
        location: punchLocation,
        ipAddress: '192.168.1.104',
        notes: punchNotes || 'Daily check-out completed',
      });
      setTodayRecord(rec);
      setPunchNotes('');
      await loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleBreak = async () => {
    if (!todayRecord) return;
    setLoading(true);
    try {
      const pType: PunchType = isOnBreak ? 'BREAK_END' : 'BREAK_START';
      const rec = await attendanceApi.toggleBreak(todayRecord.id, {
        punchType: pType,
        location: punchLocation,
        notes: isOnBreak ? 'Ended break' : 'Started break',
      });
      setTodayRecord(rec);
      setIsOnBreak(!isOnBreak);
      await loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const openCorrection = (record: AttendanceRecord) => {
    setSelectedCorrectionRecord(record);
    setCorrectionForm({
      checkInTime: record.checkInTime ? new Date(record.checkInTime).toISOString().slice(0, 16) : '',
      checkOutTime: record.checkOutTime ? new Date(record.checkOutTime).toISOString().slice(0, 16) : '',
      status: record.status,
      totalBreakMinutes: record.totalBreakMinutes || 0,
      remarks: record.remarks || '',
      reason: '',
    });
    setShowCorrectionModal(true);
  };

  const handleSaveCorrection = async () => {
    if (!selectedCorrectionRecord || !correctionForm.reason.trim()) {
      alert('Mandatory correction reason is required for administrative audit trail.');
      return;
    }
    setLoading(true);
    try {
      await attendanceApi.correctAttendance(
        selectedCorrectionRecord.id,
        {
          checkInTime: correctionForm.checkInTime ? new Date(correctionForm.checkInTime).toISOString() : undefined,
          checkOutTime: correctionForm.checkOutTime ? new Date(correctionForm.checkOutTime).toISOString() : undefined,
          status: correctionForm.status,
          totalBreakMinutes: correctionForm.totalBreakMinutes,
          remarks: correctionForm.remarks,
          reason: correctionForm.reason,
        },
        user?.email || 'admin@techknife.io'
      );
      setShowCorrectionModal(false);
      await loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveManual = async () => {
    setLoading(true);
    try {
      await attendanceApi.createManualAttendance(
        {
          userId: manualForm.userId,
          userName: manualForm.userName,
          userEmail: manualForm.userEmail,
          department: manualForm.department,
          date: manualForm.date,
          status: manualForm.status,
          checkInTime: new Date(manualForm.checkInTime).toISOString(),
          checkOutTime: new Date(manualForm.checkOutTime).toISOString(),
          totalBreakMinutes: manualForm.totalBreakMinutes,
          isWfh: manualForm.isWfh,
          remarks: manualForm.remarks,
        },
        user?.email || 'admin@techknife.io'
      );
      setShowManualModal(false);
      await loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleBulkImport = async () => {
    setLoading(true);
    try {
      const lines = bulkDataText.split('\n').filter((l) => l.trim().length > 0);
      const parsedRecords = lines.map((line) => {
        const parts = line.split(',');
        return {
          userId: parts[0]?.trim() || 'EMP-100',
          userName: parts[1]?.trim() || 'Staff',
          department: parts[2]?.trim() || 'Engineering',
          date: parts[3]?.trim() || new Date().toISOString().split('T')[0],
          status: (parts[4]?.trim().toUpperCase() as AttendanceStatusType) || 'PRESENT',
          checkInTime: `${parts[3]?.trim()}T${parts[5]?.trim() || '09:00'}:00Z`,
          checkOutTime: `${parts[3]?.trim()}T${parts[6]?.trim() || '18:00'}:00Z`,
          totalBreakMinutes: 60,
          remarks: 'Bulk CSV imported entry',
        };
      });

      await attendanceApi.bulkImportAttendance(parsedRecords, user?.email || 'admin@techknife.io');
      setShowBulkImportModal(false);
      await loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Status color helper
  const getStatusBadgeColor = (status: AttendanceStatusType) => {
    switch (status) {
      case 'PRESENT': return 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/20';
      case 'ABSENT': return 'bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-500/20';
      case 'LATE': return 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-500/20';
      case 'HALF_DAY': return 'bg-orange-500/10 text-orange-600 dark:text-orange-400 border-orange-500/20';
      case 'WFH': return 'bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 border-cyan-500/20';
      case 'HOLIDAY': return 'bg-purple-500/10 text-purple-600 dark:text-purple-400 border-purple-500/20';
      case 'WEEKEND': return 'bg-slate-500/10 text-slate-600 dark:text-slate-400 border-slate-500/20';
      case 'LEAVE': return 'bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border-indigo-500/20';
      default: return 'bg-slate-500/10 text-slate-500 border-slate-500/20';
    }
  };

  // Filtered history
  const filteredHistory = historyList.filter((r) =>
    r.userName.toLowerCase().includes(historySearch.toLowerCase()) ||
    r.userId.toLowerCase().includes(historySearch.toLowerCase()) ||
    r.department.toLowerCase().includes(historySearch.toLowerCase()) ||
    r.status.toLowerCase().includes(historySearch.toLowerCase())
  );

  return (
    <div className="space-y-8">
      {/* Top Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-xs uppercase tracking-wider mb-1">
            <Clock className="w-4 h-4" />
            <span>Attendance & Time Intelligence Platform</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">
            Daily Attendance & Geo Punch Log
          </h1>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Geo-tagged punch engine, break management, overtime calculations, WFH tracking, and admin audit correction.
          </p>
        </div>

        {/* Action Controls for Admin */}
        {isAdmin && (
          <div className="flex items-center gap-2 flex-wrap">
            <button
              onClick={() => setShowManualModal(true)}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl transition shadow-md shadow-indigo-600/20"
            >
              <Plus className="w-4 h-4" />
              <span>Manual Entry</span>
            </button>
            <button
              onClick={() => setShowBulkImportModal(true)}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold text-slate-700 dark:text-slate-200 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 rounded-xl transition"
            >
              <UploadCloud className="w-4 h-4" />
              <span>Bulk Import</span>
            </button>
          </div>
        )}
      </div>

      {/* Navigation Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 dark:border-slate-800 pb-1 overflow-x-auto">
        {[
          { id: 'today', label: "Today's Punch Widget", icon: Clock },
          { id: 'history', label: 'Punch History & Audit', icon: Filter },
          { id: 'calendar', label: 'Monthly Calendar View', icon: Calendar },
          { id: 'monthly_summary', label: 'Monthly Analytics', icon: BarChart3 },
          { id: 'yearly_summary', label: 'Yearly Summary', icon: AwardIcon },
        ].map((tab) => {
          const Icon = tab.icon;
          const active = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`inline-flex items-center gap-2 px-4 py-2.5 font-bold text-xs rounded-xl transition-all whitespace-nowrap ${
                active
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/20'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* TAB 1: TODAY'S ATTENDANCE WIDGET */}
      {activeTab === 'today' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Interactive Check-In/Out Card */}
          <div className="lg:col-span-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <h3 className="text-lg font-bold text-slate-900 dark:text-white">
                  Real-time Punch Console
                </h3>
                <p className="text-xs text-slate-500">
                  Date: {new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
                </p>
              </div>

              {todayRecord ? (
                <span className={`px-3 py-1 rounded-full text-xs font-bold border ${getStatusBadgeColor(todayRecord.status)}`}>
                  {todayRecord.status}
                </span>
              ) : (
                <span className="px-3 py-1 rounded-full text-xs font-bold border bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400">
                  Not Checked In
                </span>
              )}
            </div>

            {/* Check-In / Check-Out Form Controls */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Geo / Network Location
                </label>
                <div className="relative">
                  <MapPin className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type="text"
                    value={punchLocation}
                    onChange={(e) => setPunchLocation(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    placeholder="Enter location or IP"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Work Mode & WFH
                </label>
                <label className="inline-flex items-center gap-2 mt-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={isWfh}
                    onChange={(e) => setIsWfh(e.target.checked)}
                    disabled={!!todayRecord}
                    className="w-4 h-4 text-indigo-600 rounded border-slate-300 focus:ring-indigo-500"
                  />
                  <span className="text-xs font-semibold text-slate-700 dark:text-slate-300 flex items-center gap-1">
                    <Home className="w-3.5 h-3.5 text-indigo-500" />
                    Remote / Work From Home (WFH)
                  </span>
                </label>
              </div>

              <div className="md:col-span-2">
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Punch Notes / Activity Brief
                </label>
                <input
                  type="text"
                  value={punchNotes}
                  onChange={(e) => setPunchNotes(e.target.value)}
                  className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  placeholder="E.g. Working on enterprise attendance module sprint"
                />
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex flex-wrap items-center gap-3 pt-2">
              {!todayRecord ? (
                <button
                  onClick={handleCheckIn}
                  disabled={loading}
                  className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-xs rounded-xl shadow-lg shadow-emerald-600/30 inline-flex items-center gap-2 transition disabled:opacity-50"
                >
                  <Clock className="w-4 h-4" />
                  <span>Punch Check-In Now</span>
                </button>
              ) : (
                <>
                  {!todayRecord.checkOutTime ? (
                    <button
                      onClick={handleCheckOut}
                      disabled={loading}
                      className="px-6 py-3 bg-rose-600 hover:bg-rose-700 text-white font-extrabold text-xs rounded-xl shadow-lg shadow-rose-600/30 inline-flex items-center gap-2 transition disabled:opacity-50"
                    >
                      <Clock className="w-4 h-4" />
                      <span>Punch Check-Out</span>
                    </button>
                  ) : (
                    <div className="px-4 py-2 bg-emerald-500/10 border border-emerald-500/20 text-emerald-600 dark:text-emerald-400 font-bold text-xs rounded-xl inline-flex items-center gap-2">
                      <CheckCircle2 className="w-4 h-4" />
                      <span>Attendance completed for today!</span>
                    </div>
                  )}

                  {todayRecord.checkInTime && !todayRecord.checkOutTime && (
                    <button
                      onClick={handleToggleBreak}
                      disabled={loading}
                      className={`px-5 py-3 font-bold text-xs rounded-xl border transition inline-flex items-center gap-2 ${
                        isOnBreak
                          ? 'bg-amber-600 text-white border-amber-600 hover:bg-amber-700 shadow-md shadow-amber-600/20'
                          : 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 border-slate-200 dark:border-slate-700 hover:bg-slate-200'
                      }`}
                    >
                      <Coffee className="w-4 h-4" />
                      <span>{isOnBreak ? 'End Break Punch' : 'Start Break Punch'}</span>
                    </button>
                  )}
                </>
              )}
            </div>

            {/* Today's Punch History Stream */}
            {todayRecord && todayRecord.punches && todayRecord.punches.length > 0 && (
              <div className="pt-4 border-t border-slate-100 dark:border-slate-800">
                <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3">
                  Today's Punch Audit Trail
                </h4>
                <div className="space-y-2">
                  {todayRecord.punches.map((p, idx) => (
                    <div
                      key={idx}
                      className="flex items-center justify-between p-3 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-100 dark:border-slate-800 text-xs"
                    >
                      <div className="flex items-center gap-3">
                        <span className="p-1.5 rounded-lg bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
                          <Clock className="w-3.5 h-3.5" />
                        </span>
                        <div>
                          <p className="font-bold text-slate-900 dark:text-white">{p.punchType}</p>
                          <p className="text-[11px] text-slate-400">{p.notes || p.location}</p>
                        </div>
                      </div>
                      <span className="font-mono font-bold text-slate-600 dark:text-slate-300">
                        {new Date(p.timestamp).toLocaleTimeString()}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Today's KPI Metrics Sidebar */}
          <div className="space-y-4">
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
              <h3 className="font-bold text-sm text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2">
                Shift & Time Summary
              </h3>

              <div className="space-y-3">
                <div className="flex items-center justify-between text-xs">
                  <span className="text-slate-500">Check-In Time:</span>
                  <span className="font-mono font-bold text-slate-900 dark:text-white">
                    {todayRecord?.checkInTime ? new Date(todayRecord.checkInTime).toLocaleTimeString() : '--:--'}
                  </span>
                </div>

                <div className="flex items-center justify-between text-xs">
                  <span className="text-slate-500">Check-Out Time:</span>
                  <span className="font-mono font-bold text-slate-900 dark:text-white">
                    {todayRecord?.checkOutTime ? new Date(todayRecord.checkOutTime).toLocaleTimeString() : '--:--'}
                  </span>
                </div>

                <div className="flex items-center justify-between text-xs">
                  <span className="text-slate-500">Work Duration:</span>
                  <span className="font-mono font-bold text-indigo-600 dark:text-indigo-400">
                    {todayRecord ? `${Math.floor(todayRecord.totalWorkMinutes / 60)}h ${todayRecord.totalWorkMinutes % 60}m` : '0h 0m'}
                  </span>
                </div>

                <div className="flex items-center justify-between text-xs">
                  <span className="text-slate-500">Break Duration:</span>
                  <span className="font-mono font-bold text-amber-600 dark:text-amber-400">
                    {todayRecord ? `${todayRecord.totalBreakMinutes} mins` : '0 mins'}
                  </span>
                </div>

                <div className="flex items-center justify-between text-xs">
                  <span className="text-slate-500">Overtime Balance:</span>
                  <span className="font-mono font-bold text-emerald-600 dark:text-emerald-400">
                    {todayRecord ? `${Math.floor(todayRecord.overtimeMinutes / 60)}h ${todayRecord.overtimeMinutes % 60}m` : '0h 0m'}
                  </span>
                </div>
              </div>
            </div>

            {/* Enterprise Policy Notice */}
            <div className="bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-900/50 rounded-2xl p-5 text-xs text-indigo-900 dark:text-indigo-200 space-y-2">
              <div className="flex items-center gap-2 font-bold">
                <Info className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />
                <span>Attendance Policy Rules</span>
              </div>
              <p className="text-[11px] leading-relaxed opacity-90">
                Grace period is 15 minutes (09:15 AM). Arrival past 09:15 AM triggers LATE flag. Work under 4 hours counts as HALF DAY. Overtime is calculated on net hours exceeding 8 hours per day.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: PUNCH HISTORY & AUDIT */}
      {activeTab === 'history' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white">
                Detailed Punch Log & Attendance History
              </h3>
              <p className="text-xs text-slate-500">Searchable ledger across employees and departments</p>
            </div>

            <div className="relative w-full sm:w-64">
              <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                value={historySearch}
                onChange={(e) => setHistorySearch(e.target.value)}
                placeholder="Search staff, ID or status..."
                className="w-full pl-9 pr-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
              <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-semibold text-slate-500">
                <tr>
                  <th className="py-3 px-4">Employee</th>
                  <th className="py-3 px-4">Date</th>
                  <th className="py-3 px-4">Clock In</th>
                  <th className="py-3 px-4">Clock Out</th>
                  <th className="py-3 px-4">Work / Break</th>
                  <th className="py-3 px-4">Location</th>
                  <th className="py-3 px-4">Status</th>
                  {isAdmin && <th className="py-3 px-4 text-right">Action</th>}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredHistory.map((row) => (
                  <tr key={row.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/30 transition">
                    <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-slate-100">
                      <div>{row.userName}</div>
                      <div className="text-[10px] text-slate-400 font-normal">{row.userId} • {row.department}</div>
                    </td>
                    <td className="py-3.5 px-4 font-mono">{row.date}</td>
                    <td className="py-3.5 px-4 font-mono text-emerald-600 dark:text-emerald-400">
                      {row.checkInTime ? new Date(row.checkInTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '--:--'}
                    </td>
                    <td className="py-3.5 px-4 font-mono text-rose-600 dark:text-rose-400">
                      {row.checkOutTime ? new Date(row.checkOutTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '--:--'}
                    </td>
                    <td className="py-3.5 px-4 font-semibold text-indigo-600 dark:text-indigo-400">
                      {Math.floor(row.totalWorkMinutes / 60)}h {row.totalWorkMinutes % 60}m
                      <span className="text-[10px] text-slate-400 block font-normal">Break: {row.totalBreakMinutes}m</span>
                    </td>
                    <td className="py-3.5 px-4 text-[11px] text-slate-400 max-w-[150px] truncate">
                      {row.locationIn || row.ipAddress || 'HQ Main'}
                    </td>
                    <td className="py-3.5 px-4">
                      <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold border ${getStatusBadgeColor(row.status)}`}>
                        {row.status}
                      </span>
                    </td>
                    {isAdmin && (
                      <td className="py-3.5 px-4 text-right">
                        <button
                          onClick={() => openCorrection(row)}
                          className="p-1.5 rounded-lg bg-slate-100 dark:bg-slate-800 hover:bg-indigo-50 dark:hover:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400 transition"
                          title="Admin Correction"
                        >
                          <Edit3 className="w-3.5 h-3.5" />
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: MONTHLY CALENDAR VIEW */}
      {activeTab === 'calendar' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm space-y-6">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
            <h3 className="text-base font-bold text-slate-900 dark:text-white">
              Monthly Attendance Visual Calendar
            </h3>

            <div className="flex items-center gap-2">
              <select
                value={selectedMonth}
                onChange={(e) => setSelectedMonth(Number(e.target.value))}
                className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                {[1,2,3,4,5,6,7,8,9,10,11,12].map((m) => (
                  <option key={m} value={m}>
                    {new Date(2026, m - 1).toLocaleString('default', { month: 'long' })}
                  </option>
                ))}
              </select>
              <select
                value={selectedYear}
                onChange={(e) => setSelectedYear(Number(e.target.value))}
                className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                <option value={2026}>2026</option>
                <option value={2025}>2025</option>
              </select>
            </div>
          </div>

          {/* Calendar Grid */}
          <div className="grid grid-cols-7 gap-2 text-center text-xs font-bold text-slate-500 uppercase">
            <div>Sun</div><div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div>
          </div>

          <div className="grid grid-cols-7 gap-2">
            {Array.from({ length: 31 }, (_, i) => {
              const dayNum = i + 1;
              const dateStr = `2026-${String(selectedMonth).padStart(2, '0')}-${String(dayNum).padStart(2, '0')}`;
              const match = calendarRecords.find((r) => r.date === dateStr);

              return (
                <div
                  key={i}
                  className="min-h-[80px] p-2 bg-slate-50 dark:bg-slate-800/40 border border-slate-100 dark:border-slate-800 rounded-xl flex flex-col justify-between"
                >
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-slate-700 dark:text-slate-300 text-xs">{dayNum}</span>
                    {match && (
                      <span className={`text-[9px] font-extrabold px-1.5 py-0.5 rounded ${getStatusBadgeColor(match.status)}`}>
                        {match.status}
                      </span>
                    )}
                  </div>

                  {match && (
                    <div className="text-[10px] font-mono text-slate-500 space-y-0.5">
                      {match.checkInTime && <div>In: {new Date(match.checkInTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>}
                      {match.checkOutTime && <div>Out: {new Date(match.checkOutTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* TAB 4: MONTHLY ANALYTICS SUMMARY */}
      {activeTab === 'monthly_summary' && (
        <div className="space-y-6">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <h3 className="text-base font-bold text-slate-900 dark:text-white">
                  Monthly Departmental Attendance Matrix
                </h3>
                <p className="text-xs text-slate-500">Aggregated work hours, percentage compliance, and overtime</p>
              </div>

              <div className="flex items-center gap-2">
                <input
                  type="text"
                  placeholder="Filter department..."
                  value={departmentFilter}
                  onChange={(e) => setDepartmentFilter(e.target.value)}
                  className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
                />
                <button
                  onClick={loadData}
                  className="px-3 py-1.5 bg-indigo-600 text-white font-bold text-xs rounded-xl"
                >
                  Refresh
                </button>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
                <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-semibold text-slate-500">
                  <tr>
                    <th className="py-3 px-4">Employee</th>
                    <th className="py-3 px-4">Department</th>
                    <th className="py-3 px-4">Present / WFH</th>
                    <th className="py-3 px-4">Absent / Leave</th>
                    <th className="py-3 px-4">Late Days</th>
                    <th className="py-3 px-4">Work Hours</th>
                    <th className="py-3 px-4">Overtime</th>
                    <th className="py-3 px-4">Attendance Rate</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                  {monthlySummaries.map((s, idx) => (
                    <tr key={idx} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/30">
                      <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-slate-100">
                        {s.userName}
                      </td>
                      <td className="py-3.5 px-4 text-slate-500">{s.department}</td>
                      <td className="py-3.5 px-4 font-semibold text-emerald-600 dark:text-emerald-400">
                        {s.presentDays} Days ({s.wfhDays} WFH)
                      </td>
                      <td className="py-3.5 px-4 font-semibold text-rose-600 dark:text-rose-400">
                        {s.absentDays} Abs / {s.leaveDays} Lve
                      </td>
                      <td className="py-3.5 px-4 font-mono text-amber-600">{s.lateDays}</td>
                      <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-white">{s.totalWorkingHours} hrs</td>
                      <td className="py-3.5 px-4 font-bold text-indigo-600 dark:text-indigo-400">{s.totalOvertimeHours} hrs</td>
                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-2">
                          <div className="w-20 bg-slate-200 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                            <div
                              className="bg-emerald-500 h-full rounded-full"
                              style={{ width: `${s.attendancePercentage}%` }}
                            />
                          </div>
                          <span className="font-bold">{s.attendancePercentage}%</span>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TAB 5: YEARLY SUMMARY */}
      {activeTab === 'yearly_summary' && yearlySummary && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm space-y-6">
          <div className="border-b border-slate-100 dark:border-slate-800 pb-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">
              Annual Employee Attendance & Time Summary ({selectedYear})
            </h3>
            <p className="text-xs text-slate-500">Yearly attendance metrics for {yearlySummary.userName || 'Employee'}</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="p-4 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-100 dark:border-slate-800">
              <span className="text-xs text-slate-500 uppercase font-bold">Total Working Days</span>
              <p className="text-2xl font-black text-slate-900 dark:text-white mt-1">{yearlySummary.totalDays} Days</p>
            </div>

            <div className="p-4 bg-emerald-50/50 dark:bg-emerald-950/20 rounded-xl border border-emerald-100 dark:border-emerald-900/30">
              <span className="text-xs text-emerald-600 dark:text-emerald-400 uppercase font-bold">Present & WFH Days</span>
              <p className="text-2xl font-black text-emerald-600 dark:text-emerald-400 mt-1">
                {yearlySummary.presentDays + yearlySummary.wfhDays} Days
              </p>
            </div>

            <div className="p-4 bg-indigo-50/50 dark:bg-indigo-950/20 rounded-xl border border-indigo-100 dark:border-indigo-900/30">
              <span className="text-xs text-indigo-600 dark:text-indigo-400 uppercase font-bold">Annual Working Hours</span>
              <p className="text-2xl font-black text-indigo-600 dark:text-indigo-400 mt-1">{yearlySummary.totalWorkingHours} hrs</p>
            </div>

            <div className="p-4 bg-amber-50/50 dark:bg-amber-950/20 rounded-xl border border-amber-100 dark:border-amber-900/30">
              <span className="text-xs text-amber-600 dark:text-amber-400 uppercase font-bold">Total Overtime Hours</span>
              <p className="text-2xl font-black text-amber-600 dark:text-amber-400 mt-1">{yearlySummary.totalOvertimeHours} hrs</p>
            </div>
          </div>
        </div>
      )}

      {/* CORRECTION MODAL */}
      {showCorrectionModal && selectedCorrectionRecord && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">
                Admin Attendance Correction
              </h3>
              <button onClick={() => setShowCorrectionModal(false)}>
                <X className="w-4 h-4 text-slate-400 hover:text-slate-600" />
              </button>
            </div>

            <div className="space-y-3 text-xs">
              <div>
                <label className="block font-bold mb-1">Check-In Timestamp</label>
                <input
                  type="datetime-local"
                  value={correctionForm.checkInTime}
                  onChange={(e) => setCorrectionForm({ ...correctionForm, checkInTime: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border rounded-xl"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Check-Out Timestamp</label>
                <input
                  type="datetime-local"
                  value={correctionForm.checkOutTime}
                  onChange={(e) => setCorrectionForm({ ...correctionForm, checkOutTime: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border rounded-xl"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Status Override</label>
                <select
                  value={correctionForm.status}
                  onChange={(e) => setCorrectionForm({ ...correctionForm, status: e.target.value as any })}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border rounded-xl"
                >
                  <option value="PRESENT">PRESENT</option>
                  <option value="ABSENT">ABSENT</option>
                  <option value="LATE">LATE</option>
                  <option value="HALF_DAY">HALF_DAY</option>
                  <option value="WFH">WFH</option>
                  <option value="HOLIDAY">HOLIDAY</option>
                  <option value="LEAVE">LEAVE</option>
                </select>
              </div>

              <div>
                <label className="block font-bold mb-1">Mandatory Audit Reason</label>
                <textarea
                  value={correctionForm.reason}
                  onChange={(e) => setCorrectionForm({ ...correctionForm, reason: e.target.value })}
                  rows={2}
                  placeholder="State official reason for correction..."
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border rounded-xl"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
              <button
                onClick={() => setShowCorrectionModal(false)}
                className="px-4 py-2 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={handleSaveCorrection}
                className="px-4 py-2 text-xs font-bold bg-indigo-600 text-white rounded-xl shadow"
              >
                Save Correction
              </button>
            </div>
          </div>
        </div>
      )}

      {/* MANUAL ENTRY MODAL */}
      {showManualModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">
                Create Manual Attendance Entry
              </h3>
              <button onClick={() => setShowManualModal(false)}>
                <X className="w-4 h-4 text-slate-400 hover:text-slate-600" />
              </button>
            </div>

            <div className="space-y-3 text-xs">
              <div>
                <label className="block font-bold mb-1">User ID / Staff Code</label>
                <input
                  type="text"
                  value={manualForm.userId}
                  onChange={(e) => setManualForm({ ...manualForm, userId: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border rounded-xl"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Date</label>
                <input
                  type="date"
                  value={manualForm.date}
                  onChange={(e) => setManualForm({ ...manualForm, date: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border rounded-xl"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Status</label>
                <select
                  value={manualForm.status}
                  onChange={(e) => setManualForm({ ...manualForm, status: e.target.value as any })}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border rounded-xl"
                >
                  <option value="PRESENT">PRESENT</option>
                  <option value="ABSENT">ABSENT</option>
                  <option value="LATE">LATE</option>
                  <option value="HALF_DAY">HALF_DAY</option>
                  <option value="WFH">WFH</option>
                  <option value="HOLIDAY">HOLIDAY</option>
                  <option value="LEAVE">LEAVE</option>
                </select>
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
              <button
                onClick={() => setShowManualModal(false)}
                className="px-4 py-2 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={handleSaveManual}
                className="px-4 py-2 text-xs font-bold bg-indigo-600 text-white rounded-xl shadow"
              >
                Create Entry
              </button>
            </div>
          </div>
        </div>
      )}

      {/* BULK IMPORT MODAL */}
      {showBulkImportModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">
                Bulk Attendance CSV Import
              </h3>
              <button onClick={() => setShowBulkImportModal(false)}>
                <X className="w-4 h-4 text-slate-400 hover:text-slate-600" />
              </button>
            </div>

            <div className="space-y-3 text-xs">
              <p className="text-slate-500">
                Format: <code className="bg-slate-100 dark:bg-slate-800 px-1 py-0.5 rounded">UserId, Name, Dept, Date(YYYY-MM-DD), Status, InTime(HH:MM), OutTime(HH:MM)</code>
              </p>

              <textarea
                value={bulkDataText}
                onChange={(e) => setBulkDataText(e.target.value)}
                rows={6}
                className="w-full font-mono text-[11px] p-3 bg-slate-50 dark:bg-slate-800 border rounded-xl"
              />
            </div>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
              <button
                onClick={() => setShowBulkImportModal(false)}
                className="px-4 py-2 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={handleBulkImport}
                className="px-4 py-2 text-xs font-bold bg-indigo-600 text-white rounded-xl shadow"
              >
                Process Bulk Import
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// Helper Award Icon wrapper
const AwardIcon: React.FC<{ className?: string }> = ({ className }) => (
  <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15l-2 5l2-1l2 1l-2-5m0 0a7 7 0 100-14a7 7 0 000 14z" />
  </svg>
);
