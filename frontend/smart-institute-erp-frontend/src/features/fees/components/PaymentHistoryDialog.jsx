import { useEffect, useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Chip } from '@mui/material';
import DataTable from '@components/tables/DataTable';
import { feePaymentApi } from '@services/api/feePaymentApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// Read-only history view — fetches on open, no pagination needed since
// GET /fee-payments/history/{id} returns an unpaged list scoped to one
// student's fee record (naturally small — a handful of installments).
function PaymentHistoryDialog({ open, studentFee, onClose }) {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!open || !studentFee) return;

    async function loadHistory() {
      setLoading(true);
      setError(null);
      try {
        const response = await feePaymentApi.getHistory(studentFee.id);
        setPayments(response || []);
      } catch (err) {
        setError(err.message || 'Failed to load payment history.');
      } finally {
        setLoading(false);
      }
    }

    loadHistory();
  }, [open, studentFee]);

  const columns = [
    { field: 'receiptNumber', headerName: 'Receipt No.' },
    { field: 'paymentDate', headerName: 'Date' },
    {
      field: 'amount',
      headerName: 'Amount',
      render: (row) => `₹${Number(row.amount).toLocaleString('en-IN')}`,
    },
    {
      field: 'paymentMode',
      headerName: 'Mode',
      render: (row) => <Chip label={row.paymentMode} size="small" variant="outlined" />,
    },
    { field: 'transactionReference', headerName: 'Reference', render: (row) => row.transactionReference || '—' },
    { field: 'remarks', headerName: 'Remarks', render: (row) => row.remarks || '—' },
  ];

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>Payment History — {studentFee?.studentName}</DialogTitle>
      <DialogContent>
        <DataTable
          columns={columns}
          rows={payments}
          loading={loading}
          error={error}
          emptyMessage="No payments recorded yet."
          page={0}
          size={payments.length || 10}
          totalElements={payments.length}
          onPageChange={() => {}}
          onSizeChange={() => {}}
          getRowId={(row) => row.id}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
}

export default PaymentHistoryDialog;