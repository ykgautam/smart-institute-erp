import { Chip } from '@mui/material';
import { getAttendanceStatusColor } from '@constants/enums';

// Used by Part 2 (batch+date records) — matches AttendanceResponse fields.
export function getAttendanceRecordColumns() {
  return [
    { field: 'studentName', headerName: 'Student' },
    {
      field: 'status',
      headerName: 'Status',
      render: (row) => <Chip label={row.status} size="small" color={getAttendanceStatusColor(row.status)} />,
    },
    { field: 'remarks', headerName: 'Remarks', render: (row) => row.remarks || '—' },
    { field: 'markedByName', headerName: 'Marked By', render: (row) => row.markedByName || '—' },
  ];
}

// Used by Part 3 (student history) — includes batch name since a
// student's history may span multiple batches over time.
export function getAttendanceHistoryColumns() {
  return [
    { field: 'attendanceDate', headerName: 'Date' },
    { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
    {
      field: 'status',
      headerName: 'Status',
      render: (row) => <Chip label={row.status} size="small" color={getAttendanceStatusColor(row.status)} />,
    },
    { field: 'remarks', headerName: 'Remarks', render: (row) => row.remarks || '—' },
  ];
}