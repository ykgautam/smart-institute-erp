import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  IconButton,
  Badge,
  Menu,
  Box,
  Typography,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Button,
} from '@mui/material';
import NotificationsOutlinedIcon from '@mui/icons-material/NotificationsOutlined';
import { useNotifications } from '../hooks/useNotifications';
import { getNotificationIcon } from '../constants/notificationIcons';

// Lives in Topbar — shows unread count as a badge, and a dropdown of
// the most recent notifications on click. "View all" navigates to the
// full NotificationListPage.
function NotificationBell() {
  const navigate = useNavigate();
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications();
  const [anchorEl, setAnchorEl] = useState(null);

  const recent = notifications.slice(0, 5);

  const handleItemClick = (notification) => {
    if (!notification.read) markAsRead(notification.id);
    setAnchorEl(null);
  };

  return (
    <>
      <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small">
        <Badge badgeContent={unreadCount} color="error" max={99}>
          <NotificationsOutlinedIcon />
        </Badge>
      </IconButton>

      <Menu anchorEl={anchorEl} open={!!anchorEl} onClose={() => setAnchorEl(null)} PaperProps={{ sx: { width: 360 } }}>
        <Box sx={{ px: 2, py: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="body1" fontWeight={600}>
            Notifications
          </Typography>
          {unreadCount > 0 && (
            <Button size="small" onClick={markAllAsRead}>
              Mark all read
            </Button>
          )}
        </Box>
        <Divider />

        {recent.length === 0 ? (
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              No notifications yet.
            </Typography>
          </Box>
        ) : (
          <List dense sx={{ maxHeight: 360, overflowY: 'auto' }}>
            {recent.map((n) => {
              const Icon = getNotificationIcon(n.type);
              return (
                <ListItemButton key={n.id} onClick={() => handleItemClick(n)} sx={{ opacity: n.read ? 0.6 : 1 }}>
                  <ListItemIcon sx={{ minWidth: 36 }}>
                    <Icon fontSize="small" />
                  </ListItemIcon>
                  <ListItemText
                    primary={n.title}
                    secondary={n.message}
                    primaryTypographyProps={{ fontWeight: n.read ? 400 : 600 }}
                  />
                </ListItemButton>
              );
            })}
          </List>
        )}

        <Divider />
        <Box sx={{ p: 1 }}>
          <Button
            fullWidth
            size="small"
            onClick={() => {
              setAnchorEl(null);
              navigate('/notifications');
            }}
          >
            View all
          </Button>
        </Box>
      </Menu>
    </>
  );
}

export default NotificationBell;