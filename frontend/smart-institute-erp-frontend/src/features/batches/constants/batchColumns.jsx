import { Chip, TableSortLabel } from '@mui/material';
import { getBatchStatusColor } from '@constants/enums';

export function getBatchColumns({ sortBy, direction, onSort }) {
  const sortableHeader = (field, label) => (
    <TableSortLabel
      active={sortBy === field}
      direction={sortBy === field ? direction.toLowerCase() : 'asc'}
      onClick={() => onSort(field)}
    >
      {label}
    </TableSortLabel>
  );

  return [
    { field: 'batchCode', headerName: sortableHeader('batchCode', 'Code') },
    { field: 'batchName', headerName: sortableHeader('batchName', 'Name') },
    { field: 'courseName', headerName: 'Course', render: (row) => row.courseName || '—' },
    { field: 'facultyName', headerName: 'Faculty', render: (row) => row.facultyName || '—' },
    {
      field: 'capacity',
      headerName: sortableHeader('capacity', 'Strength'),
      // Backend-provided studentCount, displayed as-is (Section 12).
      render: (row) => `${row.studentCount ?? 0} / ${row.capacity}`,
    },
    { field: 'startDate', headerName: sortableHeader('startDate', 'Start Date') },
    { field: 'endDate', headerName: sortableHeader('endDate', 'End Date') },
    {
      field: 'status',
      headerName: sortableHeader('status', 'Status'),
      render: (row) => <Chip label={row.status} size="small" color={getBatchStatusColor(row.status)} />,
    },
  ];
}