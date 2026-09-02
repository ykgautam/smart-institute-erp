import { Box, Typography, Button } from '@mui/material';
import ErrorOutlinedIcon from '@mui/icons-material/ErrorOutlined';

function ErrorState({ message = 'Something went wrong. Please try again.', onRetry }) {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        py: 6,
        color: 'error.main',
      }}
    >
      <ErrorOutlinedIcon sx={{ fontSize: 48, mb: 1 }} />
      <Typography variant="body2" sx={{ mb: onRetry ? 2 : 0 }}>
        {message}
      </Typography>
      {onRetry && (
        <Button variant="outlined" size="small" onClick={onRetry}>
          Retry
        </Button>
      )}
    </Box>
  );
}

export default ErrorState;