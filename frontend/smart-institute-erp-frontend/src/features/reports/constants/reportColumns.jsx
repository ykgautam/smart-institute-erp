import { Chip } from '@mui/material';
import { formatCurrency } from '@features/dashboard/utils/dashboardUtils';
import { STUDENT_STATUS, FEE_STATUS } from '@constants/enums';

const pct = (v) => `${Number(v ?? 0).toFixed(1)}%`;

export const studentReportColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'email', headerName: 'Email' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'status', headerName: 'Status', render: (row) => <Chip label={row.status} size="small" variant="outlined" /> },
];

export const studentPerformanceColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'totalAttempts', headerName: 'Attempts' },
  { field: 'passedAttempts', headerName: 'Passed' },
  { field: 'failedAttempts', headerName: 'Failed' },
  { field: 'averagePercentage', headerName: 'Avg %', render: (row) => pct(row.averagePercentage) },
  { field: 'highestPercentage', headerName: 'Highest %', render: (row) => pct(row.highestPercentage) },
];

export const studentAttendanceReportColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'totalClasses', headerName: 'Total Classes' },
  { field: 'presentClasses', headerName: 'Present' },
  { field: 'absentClasses', headerName: 'Absent' },
  { field: 'attendancePercentage', headerName: 'Attendance %', render: (row) => pct(row.attendancePercentage) },
];

export const studentAcademicReportColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'attendancePercentage', headerName: 'Attendance %', render: (row) => pct(row.attendancePercentage) },
  { field: 'totalTests', headerName: 'Total Tests' },
  { field: 'passedTests', headerName: 'Passed' },
  { field: 'failedTests', headerName: 'Failed' },
  { field: 'averageTestPercentage', headerName: 'Avg Test %', render: (row) => pct(row.averageTestPercentage) },
];

export const feeCollectionColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'finalFee', headerName: 'Final Fee', render: (row) => formatCurrency(row.finalFee) },
  { field: 'paidAmount', headerName: 'Paid', render: (row) => formatCurrency(row.paidAmount) },
  { field: 'pendingAmount', headerName: 'Pending', render: (row) => formatCurrency(row.pendingAmount) },
  { field: 'status', headerName: 'Status', render: (row) => <Chip label={row.status} size="small" variant="outlined" /> },
];

export const feeOutstandingColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'finalFee', headerName: 'Final Fee', render: (row) => formatCurrency(row.finalFee) },
  { field: 'pendingAmount', headerName: 'Pending', render: (row) => formatCurrency(row.pendingAmount) },
  { field: 'feeStatus', headerName: 'Status', render: (row) => <Chip label={row.feeStatus} size="small" variant="outlined" /> },
  { field: 'feeDueDate', headerName: 'Due Date', render: (row) => row.feeDueDate || '—' },
];

// StudentFeeCollectionReportResponse has the identical shape as
// StudentFeeOutstandingReportResponse — reused rather than duplicated.
export const studentFeeCollectionColumns = feeOutstandingColumns;

export const studentFeeAcademicColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'attendancePercentage', headerName: 'Attendance %', render: (row) => pct(row.attendancePercentage) },
  { field: 'averageTestPercentage', headerName: 'Avg Test %', render: (row) => pct(row.averageTestPercentage) },
  { field: 'finalFee', headerName: 'Final Fee', render: (row) => formatCurrency(row.finalFee) },
  { field: 'pendingAmount', headerName: 'Pending', render: (row) => formatCurrency(row.pendingAmount) },
  { field: 'feeStatus', headerName: 'Fee Status', render: (row) => <Chip label={row.feeStatus} size="small" variant="outlined" /> },
];

export const coursePerformanceColumns = [
  { field: 'courseName', headerName: 'Course' },
  { field: 'totalStudents', headerName: 'Total Students' },
  { field: 'studentsAttempted', headerName: 'Attempted' },
  { field: 'passedAttempts', headerName: 'Passed' },
  { field: 'failedAttempts', headerName: 'Failed' },
  { field: 'averagePercentage', headerName: 'Avg %', render: (row) => pct(row.averagePercentage) },
];

export const batchPerformanceColumns = [
  { field: 'batchName', headerName: 'Batch' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'totalStudents', headerName: 'Total Students' },
  { field: 'studentsAttempted', headerName: 'Attempted' },
  { field: 'passedAttempts', headerName: 'Passed' },
  { field: 'failedAttempts', headerName: 'Failed' },
  { field: 'averagePercentage', headerName: 'Avg %', render: (row) => pct(row.averagePercentage) },
];

export const attendanceReportColumns = [
  { field: 'studentName', headerName: 'Student' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
  { field: 'totalClasses', headerName: 'Total Classes' },
  { field: 'presentClasses', headerName: 'Present' },
  { field: 'absentClasses', headerName: 'Absent' },
  { field: 'attendancePercentage', headerName: 'Attendance %', render: (row) => pct(row.attendancePercentage) },
];

export const testWiseColumns = [
  { field: 'testName', headerName: 'Test' },
  { field: 'courseName', headerName: 'Course' },
  { field: 'topicName', headerName: 'Topic', render: (row) => row.topicName || '—' },
  { field: 'totalAttempts', headerName: 'Attempts' },
  { field: 'passedAttempts', headerName: 'Passed' },
  { field: 'failedAttempts', headerName: 'Failed' },
  { field: 'averagePercentage', headerName: 'Avg %', render: (row) => pct(row.averagePercentage) },
  { field: 'highestPercentage', headerName: 'Highest %', render: (row) => pct(row.highestPercentage) },
];

// Re-exported here so pages don't need a separate enums import for
// status filter dropdown options.
export { STUDENT_STATUS, FEE_STATUS };