import { Typography, Box, Paper, List, ListItemButton, ListItemIcon, ListItemText, Button, CircularProgress } from '@mui/material';
import { useNotifications } from '../hooks/useNotifications';
import { getNotificationIcon } from '../constants/notificationIcons';
import EmptyState from '@components/tables/EmptyState';

function NotificationListPage() {
  const { notifications, unreadCount, loading, markAsRead, markAllAsRead } = useNotifications();

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h2">Notifications</Typography>
        {unreadCount > 0 && (
          <Button variant="outlined" onClick={markAllAsRead}>
            Mark all as read
          </Button>
        )}
      </Box>

      <Paper variant="outlined">
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
            <CircularProgress />
          </Box>
        ) : notifications.length === 0 ? (
          <EmptyState message="No notifications yet." />
        ) : (
          <List>
            {notifications.map((n) => {
              const Icon = getNotificationIcon(n.type);
              return (
                <ListItemButton key={n.id} onClick={() => !n.read && markAsRead(n.id)} sx={{ opacity: n.read ? 0.6 : 1 }}>
                  <ListItemIcon>
                    <Icon />
                  </ListItemIcon>
                  <ListItemText
                    primary={n.title}
                    secondary={`${n.message} — ${n.createdAt ? new Date(n.createdAt).toLocaleString('en-IN') : ''}`}
                    primaryTypographyProps={{ fontWeight: n.read ? 400 : 600 }}
                  />
                </ListItemButton>
              );
            })}
          </List>
        )}
      </Paper>
    </Box>
  );
}

export default NotificationListPage;