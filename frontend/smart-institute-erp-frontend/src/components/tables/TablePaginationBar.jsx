import { TablePagination } from '@mui/material';

// Thin wrapper around MUI's TablePagination, bound to the exact
// page/size vocabulary usePagination.js manages. Kept separate so
// DataTable doesn't need to know pagination is even MUI-based —
// swapping the pagination UI later touches only this file.
function TablePaginationBar({ page, size, totalElements, onPageChange, onSizeChange }) {
  return (
    <TablePagination
      component="div"
      count={totalElements}
      page={page}
      rowsPerPage={size}
      onPageChange={(_, newPage) => onPageChange(newPage)}
      onRowsPerPageChange={(e) => onSizeChange(parseInt(e.target.value, 10))}
      rowsPerPageOptions={[10, 25, 50]}
    />
  );
}

export default TablePaginationBar;