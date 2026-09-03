import { useState } from 'react';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import PaymentIcon from '@mui/icons-material/Payment';
import HistoryIcon from '@mui/icons-material/History';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import AssignFeeDialog from '../components/AssignFeeDialog';
import CollectPaymentDialog from '../components/CollectPaymentDialog';
import PaymentHistoryDialog from '../components/PaymentHistoryDialog';
import { useStudentFees } from '../hooks/useStudentFees';
import { getStudentFeeColumns } from '../constants/studentFeeColumns';
import { useAuth } from '@hooks/useAuth';

function StudentFeeListPage() {
  const { rows, loading, error, searchInput, setSearchInput, refetch } = useStudentFees();
  const { hasRole } = useAuth();
  const canAssign = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'ACCOUNTANT']);
  const canCollect = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'ACCOUNTANT']);

  const [assignDialogOpen, setAssignDialogOpen] = useState(false);
  const [collectTarget, setCollectTarget] = useState(null); // studentFee row, or null
  const [historyTarget, setHistoryTarget] = useState(null); // studentFee row, or null

  const columns = [
    ...getStudentFeeColumns(),
    ...(canCollect
      ? [
          {
            field: 'actions',
            headerName: 'Actions',
            render: (row) => (
              <Box sx={{ display: 'flex', gap: 0.5 }}>
                <Tooltip title="Collect Payment">
                  {/* Disabled once fully paid — nothing left to collect,
                      matches backend's FEE_STATUS.PAID semantics. */}
                  <span>
                    <IconButton
                      size="small"
                      disabled={row.pendingAmount <= 0}
                      onClick={() => setCollectTarget(row)}
                    >
                      <PaymentIcon fontSize="small" />
                    </IconButton>
                  </span>
                </Tooltip>
                <Tooltip title="View Payment History">
                  <IconButton size="small" onClick={() => setHistoryTarget(row)}>
                    <HistoryIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
              </Box>
            ),
          },
        ]
      : []),
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Student Fees
      </Typography>

      <TableToolbar
        searchValue={searchInput}
        onSearchChange={setSearchInput}
        searchPlaceholder="Search by student name..."
        addLabel="Assign Fee"
        onAddClick={canAssign ? () => setAssignDialogOpen(true) : undefined}
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No fee records found."
        page={0}
        size={rows.length || 10}
        totalElements={rows.length}
        onPageChange={() => {}}
        onSizeChange={() => {}}
        getRowId={(row) => row.id}
      />

      {assignDialogOpen && (
        <AssignFeeDialog open={assignDialogOpen} onClose={() => setAssignDialogOpen(false)} onSuccess={refetch} />
      )}

      {collectTarget && (
        <CollectPaymentDialog
          open={!!collectTarget}
          studentFee={collectTarget}
          onClose={() => setCollectTarget(null)}
          onSuccess={refetch}
        />
      )}

      {historyTarget && (
        <PaymentHistoryDialog
          open={!!historyTarget}
          studentFee={historyTarget}
          onClose={() => setHistoryTarget(null)}
        />
      )}
    </Box>
  );
}

export default StudentFeeListPage;