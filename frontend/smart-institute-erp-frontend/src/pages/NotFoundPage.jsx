import { Button, Container, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';

function NotFoundPage() {
  return (
    <Container maxWidth="md">
      <Stack
        spacing={2}
        alignItems="center"
        justifyContent="center"
        sx={{ minHeight: '100vh', textAlign: 'center' }}
      >
        <Typography variant="h1" component="h1">
          404
        </Typography>

        <Typography variant="h5">
          Page not found
        </Typography>

        <Typography color="text.secondary">
          The page you are looking for does not exist.
        </Typography>

        <Button component={Link} to="/" variant="contained">
          Go to Home
        </Button>
      </Stack>
    </Container>
  );
}

export default NotFoundPage;