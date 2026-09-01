import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  IconButton,
  Tooltip,
  useMediaQuery,
} from '@mui/material';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { useTheme } from '@mui/material/styles';
import { NAV_ITEMS, SIDEBAR_WIDTH_EXPANDED, SIDEBAR_WIDTH_COLLAPSED } from '@constants/navConfig';
import { useAuth } from '@hooks/useAuth';

// Persistent sidebar navigation. Collapses to icon-only rather than
// hiding entirely on tablets — ERP users typically keep navigation
// visible while working, per Section 17 (desktop-first, usable smaller).
function Sidebar() {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const { hasRole } = useAuth();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('md'));

  // Auto-collapse on smaller screens by default; user can still toggle manually.
  const [collapsed, setCollapsed] = useState(isSmallScreen);

  const width = collapsed ? SIDEBAR_WIDTH_COLLAPSED : SIDEBAR_WIDTH_EXPANDED;

  // Filter nav items by role here — the ONLY place this filtering happens,
  // so navConfig stays pure data and role logic stays in useAuth.
  const visibleItems = NAV_ITEMS.filter((item) => hasRole(item.roles));

  return (
    <Drawer
      variant="permanent"
      sx={{
        width,
        flexShrink: 0,
        whiteSpace: 'nowrap',
        transition: theme.transitions.create('width', {
          easing: theme.transitions.easing.sharp,
          duration: theme.transitions.duration.enteringScreen,
        }),
        '& .MuiDrawer-paper': {
          width,
          overflowX: 'hidden',
          transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
          }),
          boxSizing: 'border-box',
        },
      }}
    >
      <Toolbar sx={{ display: 'flex', justifyContent: collapsed ? 'center' : 'flex-end' }}>
        <IconButton onClick={() => setCollapsed((prev) => !prev)} size="small">
          {collapsed ? <ChevronRightIcon /> : <ChevronLeftIcon />}
        </IconButton>
      </Toolbar>

      <List>
        {visibleItems.map(({ label, path, icon: Icon }) => {
          const isActive = location.pathname.startsWith(path);
          const button = (
            <ListItemButton
              key={path}
              selected={isActive}
              onClick={() => navigate(path)}
              sx={{
                minHeight: 48,
                justifyContent: collapsed ? 'center' : 'initial',
                px: 2.5,
              }}
            >
              <ListItemIcon sx={{ minWidth: 0, mr: collapsed ? 0 : 2, justifyContent: 'center' }}>
                <Icon color={isActive ? 'primary' : 'inherit'} />
              </ListItemIcon>
              {!collapsed && <ListItemText primary={label} />}
            </ListItemButton>
          );

          // Tooltip only needed when collapsed — label is hidden, icon alone is ambiguous.
          return collapsed ? (
            <Tooltip title={label} placement="right" key={path}>
              {button}
            </Tooltip>
          ) : (
            button
          );
        })}
      </List>
    </Drawer>
  );
}

export default Sidebar;