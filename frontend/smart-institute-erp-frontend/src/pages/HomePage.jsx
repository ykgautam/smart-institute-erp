import { Box, Container, Stack, Typography } from '@mui/material';

function HomePage() {
  return (
    <Container maxWidth="lg">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Stack spacing={1} alignItems="center">
          <Typography variant="h3" component="h1">
            Smart Institute ERP
          </Typography>

          <Typography variant="body1" color="text.secondary">
            Application routing is working.
          </Typography>
        </Stack>
      </Box>
    </Container>
  );
}

export default HomePage;