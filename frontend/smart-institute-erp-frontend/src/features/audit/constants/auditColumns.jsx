import { Chip } from '@mui/material';
import { getAuditActionColor } from '@constants/enums';

export function getAuditColumns() {
  return [
    {
      field: 'createdAt',
      headerName: 'Timestamp',
      render: (row) => (row.createdAt ? new Date(row.createdAt).toLocaleString('en-IN') : '—'),
    },
    { field: 'userName', headerName: 'User', render: (row) => row.userName || 'System' },
    {
      field: 'action',
      headerName: 'Action',
      render: (row) => <Chip label={row.action} size="small" color={getAuditActionColor(row.action)} />,
    },
    { field: 'entityType', headerName: 'Entity', render: (row) => row.entityType || '—' },
    { field: 'description', headerName: 'Description', render: (row) => row.description || '—' },
  ];
}