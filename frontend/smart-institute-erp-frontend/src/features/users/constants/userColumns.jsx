import { Chip } from '@mui/material';
import { getUserStatusColor } from '@constants/enums';

export function getUserColumns() {
  return [
    {
      field: 'name',
      headerName: 'Name',
      render: (row) => `${row.firstName} ${row.lastName || ''}`.trim(),
    },
    { field: 'email', headerName: 'Email' },
    { field: 'mobile', headerName: 'Mobile', render: (row) => row.mobile || '—' },
    { field: 'role', headerName: 'Role', render: (row) => <Chip label={row.role} size="small" variant="outlined" /> },
    {
      field: 'status',
      headerName: 'Status',
      render: (row) => <Chip label={row.status} size="small" color={getUserStatusColor(row.status)} />,
    },
  ];
}