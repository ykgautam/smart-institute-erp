import { Chip } from '@mui/material';
import { getFeeStatusColor } from '@constants/enums';

// All monetary values here are backend-calculated (totalFee, discount,
// finalFee, paidAmount, pendingAmount) — displayed exactly as returned,
// per Section 12's rule against frontend financial recalculation.
export function getStudentFeeColumns() {
  return [
    { field: 'studentName', headerName: 'Student' },
    {
      field: 'totalFee',
      headerName: 'Total Fee',
      render: (row) => `₹${Number(row.totalFee).toLocaleString('en-IN')}`,
    },
    {
      field: 'discount',
      headerName: 'Discount',
      render: (row) => `₹${Number(row.discount || 0).toLocaleString('en-IN')}`,
    },
    {
      field: 'finalFee',
      headerName: 'Final Fee',
      render: (row) => `₹${Number(row.finalFee).toLocaleString('en-IN')}`,
    },
    {
      field: 'paidAmount',
      headerName: 'Paid',
      render: (row) => `₹${Number(row.paidAmount).toLocaleString('en-IN')}`,
    },
    {
      field: 'pendingAmount',
      headerName: 'Pending',
      render: (row) => `₹${Number(row.pendingAmount).toLocaleString('en-IN')}`,
    },
    {
      field: 'status',
      headerName: 'Status',
      render: (row) => <Chip label={row.status} size="small" color={getFeeStatusColor(row.status)} />,
    },
  ];
}