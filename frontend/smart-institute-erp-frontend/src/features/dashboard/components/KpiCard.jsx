import { Card, CardContent, Box, Typography, Avatar } from '@mui/material';

// Reusable KPI tile — used by BOTH AdminDashboardPage and
// StudentDashboardPage, so spacing/typography/color stay consistent
// across dashboard variants (Section 15/38: reusable components).
function KpiCard({ icon: Icon, label, value, color = 'primary' }) {
  return (
    <Card variant="outlined">
      <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Avatar sx={{ bgcolor: `${color}.main`, width: 44, height: 44 }}>
          <Icon fontSize="small" />
        </Avatar>
        <Box>
          <Typography variant="h3">{value}</Typography>
          <Typography variant="body2" color="text.secondary">
            {label}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
}

export default KpiCard;