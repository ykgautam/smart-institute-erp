import { Chip } from '@mui/material';
import { getBatchStatusColor } from '@constants/enums'; // reused: same 4-state shape works visually

function testStatusColor(status) {
  switch (status) {
    case 'PUBLISHED': return 'success';
    case 'COMPLETED': return 'info';
    case 'CANCELLED': return 'error';
    case 'DRAFT':
    default: return 'default';
  }
}

export function getTestColumns() {
  return [
    { field: 'title', headerName: 'Title' },
    { field: 'courseName', headerName: 'Course' },
    { field: 'topicName', headerName: 'Topic' },
    { field: 'testType', headerName: 'Type', render: (row) => <Chip label={row.testType} size="small" variant="outlined" /> },
    { field: 'questionCount', headerName: 'Questions' },
    { field: 'passingPercentage', headerName: 'Passing %', render: (row) => `${row.passingPercentage}%` },
    { field: 'status', headerName: 'Status', render: (row) => <Chip label={row.status} size="small" color={testStatusColor(row.status)} /> },
  ];
}