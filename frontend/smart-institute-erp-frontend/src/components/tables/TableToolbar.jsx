import { Box, TextField, InputAdornment, Button } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';

// Generic toolbar sitting above any data table: search input (debounced
// by the caller, not here — see note below) plus an optional primary
// action button ("Add Student", "Add Course", etc.) and a slot for
// module-specific filter controls (dropdowns, date pickers).
//
// Debouncing lives in the FEATURE hook (e.g. useStudents), not here,
// per Section 29 ("do not add unnecessary debounce complexity to simple
// forms") — this component just reports raw input changes upward.
function TableToolbar({
  searchValue,
  onSearchChange,
  searchPlaceholder = 'Search...',
  onAddClick,
  addLabel = 'Add',
  filters, // optional node: module-specific filter controls
}) {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2, flexWrap: 'wrap' }}>
      <TextField
        size="small"
        placeholder={searchPlaceholder}
        value={searchValue}
        onChange={(e) => onSearchChange(e.target.value)}
        sx={{ minWidth: 240 }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon fontSize="small" />
            </InputAdornment>
          ),
        }}
      />
      {filters}
      <Box sx={{ flexGrow: 1 }} />
      {onAddClick && (
        <Button variant="contained" startIcon={<AddIcon />} onClick={onAddClick}>
          {addLabel}
        </Button>
      )}
    </Box>
  );
}

export default TableToolbar;