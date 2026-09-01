import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Skeleton,
} from '@mui/material';
import EmptyState from './EmptyState';
import ErrorState from './ErrorState';
import TablePaginationBar from './TablePaginationBar';

// The single reusable table component for the entire ERP (Section 13/40).
// Every module wraps this with its own `columns` definition instead of
// hand-building a <Table> per feature.
//
// columns: [{ field: 'name', headerName: 'Name', render?: (row) => node }]
// `render` is optional — when omitted, DataTable displays row[field] directly.
function DataTable({
  columns,
  rows,
  loading,
  error,
  onRetry,
  emptyMessage,
  page,
  size,
  totalElements,
  onPageChange,
  onSizeChange,
  getRowId = (row) => row.id,
}) {
  // Loading: show skeleton rows matching the column count, so the table
  // shape doesn't jump/reflow once real data arrives.
  if (loading) {
    return (
      <TableContainer component={Paper} variant="outlined">
        <Table>
          <TableHead>
            <TableRow>
              {columns.map((col) => (
                <TableCell key={col.field}>{col.headerName}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {Array.from({ length: 5 }).map((_, rowIndex) => (
              <TableRow key={rowIndex}>
                {columns.map((col) => (
                  <TableCell key={col.field}>
                    <Skeleton variant="text" />
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  }

  if (error) {
    return (
      <Paper variant="outlined">
        <ErrorState message={error} onRetry={onRetry} />
      </Paper>
    );
  }

  if (!rows || rows.length === 0) {
    return (
      <Paper variant="outlined">
        <EmptyState message={emptyMessage} />
      </Paper>
    );
  }

  return (
    <Paper variant="outlined">
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              {columns.map((col) => (
                <TableCell key={col.field}>{col.headerName}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={getRowId(row)} hover>
                {columns.map((col) => (
                  <TableCell key={col.field}>
                    {col.render ? col.render(row) : row[col.field]}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePaginationBar
        page={page}
        size={size}
        totalElements={totalElements}
        onPageChange={onPageChange}
        onSizeChange={onSizeChange}
      />
    </Paper>
  );
}

export default DataTable;