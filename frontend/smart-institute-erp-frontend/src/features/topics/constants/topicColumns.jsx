import { Chip } from '@mui/material';

export function getTopicColumns() {
  return [
    { field: 'displayOrder', headerName: 'Order' },
    { field: 'name', headerName: 'Topic' },
    { field: 'courseName', headerName: 'Course', render: (row) => `${row.courseCode} — ${row.courseName}` },
    { field: 'description', headerName: 'Description', render: (row) => row.description || '—' },
    {
      field: 'active',
      headerName: 'Status',
      render: (row) => <Chip label={row.active ? 'Active' : 'Inactive'} size="small" color={row.active ? 'success' : 'default'} />,
    },
  ];
}