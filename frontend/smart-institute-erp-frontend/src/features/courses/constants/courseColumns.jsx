import { Chip, TableSortLabel } from '@mui/material';
import { getCourseStatusColor } from '@constants/enums';

export function getCourseColumns({ sortBy, direction, onSort }) {
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
    { field: 'courseCode', headerName: sortableHeader('courseCode', 'Code') },
    { field: 'courseName', headerName: sortableHeader('courseName', 'Name') },
    {
      field: 'duration',
      headerName: sortableHeader('duration', 'Duration'),
      render: (row) => `${row.duration} ${row.durationType?.toLowerCase()}`,
    },
    {
      field: 'fee',
      headerName: sortableHeader('fee', 'Fee'),
      // Backend-calculated value displayed as-is per Section 12 —
      // no frontend fee recalculation.
      render: (row) => `₹${Number(row.fee).toLocaleString('en-IN')}`,
    },
    {
      field: 'status',
      headerName: sortableHeader('status', 'Status'),
      render: (row) => <Chip label={row.status} size="small" color={getCourseStatusColor(row.status)} />,
    },
  ];
}