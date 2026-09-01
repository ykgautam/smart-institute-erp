import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Menu,
  MenuItem,
  Avatar,
  Box,
  Divider,
  ListItemIcon,
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';
import LockResetIcon from '@mui/icons-material/LockReset';
import { useAuth } from '@hooks/useAuth';

// Topbar shows the authenticated user's identity and houses account
// actions (logout, change password). Kept separate from Sidebar since
// it has a distinct responsibility (session/account, not navigation).
function Topbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const menuOpen = Boolean(anchorEl);

  const handleLogout = () => {
    setAnchorEl(null);
    logout();
    navigate('/login', { replace: true });
  };

  // Falls back gracefully if `user` hasn't loaded yet (e.g. brief window
  // during AuthBootstrap's fetchCurrentUser call).
  const displayName = user?.fullName || user?.email || 'User';
  const initial = displayName.charAt(0).toUpperCase();

  return (
    <AppBar
      position="fixed"
      color="inherit"
      elevation={0}
      sx={{ borderBottom: '1px solid', borderColor: 'divider', zIndex: (theme) => theme.zIndex.drawer + 1 }}
    >
      <Toolbar sx={{ justifyContent: 'space-between' }}>
        <Typography variant="h3" noWrap>
          Smart Institute ERP
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {user?.instituteName && (
            <Typography variant="body2" color="text.secondary" sx={{ mr: 1 }}>
              {user.instituteName}
            </Typography>
          )}
          <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small">
            <Avatar sx={{ width: 34, height: 34, bgcolor: 'primary.main' }}>{initial}</Avatar>
          </IconButton>
        </Box>

        <Menu anchorEl={anchorEl} open={menuOpen} onClose={() => setAnchorEl(null)}>
          <Box sx={{ px: 2, py: 1 }}>
            <Typography variant="body2" fontWeight={600}>
              {displayName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {user?.role}
            </Typography>
          </Box>
          <Divider />
          <MenuItem onClick={() => { setAnchorEl(null); navigate('/change-password'); }}>
            <ListItemIcon><LockResetIcon fontSize="small" /></ListItemIcon>
            Change Password
          </MenuItem>
          <MenuItem onClick={handleLogout}>
            <ListItemIcon><LogoutIcon fontSize="small" /></ListItemIcon>
            Logout
          </MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
}

export default Topbar;