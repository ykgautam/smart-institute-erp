import { Chip, TableSortLabel } from '@mui/material';

// Column definitions consumed by DataTable. Separated from the page
// component so adding/removing a column doesn't require touching
// StudentListPage's data-fetching logic.
//
// `sortBy`/`direction`/`onSort` are passed in so header cells can show
// the active sort arrow — only for fields we're confident are sortable
// (see Part 2 assumption flag re: StudentSortableFields).
export function getStudentColumns({ sortBy, direction, onSort }) {
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
    {
      field: 'admissionNumber',
      headerName: sortableHeader('admissionNumber', 'Admission No.'),
    },
    {
      field: 'fullName',
      headerName: sortableHeader('firstName', 'Name'),
      // Backend already computes fullName (StudentResponse.fullName) —
      // we display it directly rather than concatenating first/last
      // ourselves, per Section 12's "display backend-calculated values" spirit.
      render: (row) => row.fullName,
    },
    {
      field: 'mobile',
      headerName: 'Mobile',
      render: (row) => row.mobile || '—',
    },
    {
      field: 'email',
      headerName: 'Email',
      render: (row) => row.email || '—',
    },
    {
      field: 'admissionDate',
      headerName: 'Admission Date',
      render: (row) => row.admissionDate || '—',
    },
    {
      field: 'status',
      headerName: 'Status',
      // Status/Gender enum values are unconfirmed (see assumption flag) —
      // rendering the raw backend string in a Chip is safe regardless
      // of what the actual enum values turn out to be.
      render: (row) => (
        <Chip
          label={row.status}
          size="small"
          color={row.active ? 'success' : 'default'}
          variant="outlined"
        />
      ),
    },
  ];
}