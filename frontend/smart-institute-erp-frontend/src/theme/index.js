import { createTheme } from '@mui/material/styles';
import { palette } from './palette';
import { typography } from './typography';

// Single source of truth for MUI theming across the app.
// Component-level style overrides go here as they're needed —
// avoid inline sx overrides for things that repeat across features.
export const theme = createTheme({
  palette,
  typography,
  shape: {
    borderRadius: 8,
  },
  components: {
    MuiButton: {
      defaultProps: {
        disableElevation: true, // Flat buttons suit a professional ERP look
      },
    },
    MuiCard: {
      defaultProps: {
        variant: 'outlined',
      },
    },
  },
});