import { Chip } from '@mui/material';

export function getFeeStructureColumns() {
  return [
    { field: 'courseName', headerName: 'Course' },
    {
      field: 'amount',
      headerName: 'Amount',
      render: (row) => `₹${Number(row.amount).toLocaleString('en-IN')}`,
    },
    { field: 'description', headerName: 'Description', render: (row) => row.description || '—' },
    {
      field: 'active',
      headerName: 'Status',
      render: (row) => (
        <Chip label={row.active ? 'Active' : 'Inactive'} size="small" color={row.active ? 'success' : 'default'} />
      ),
    },
  ];
}