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
  Collapse,
  useMediaQuery,
} from '@mui/material';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { useTheme } from '@mui/material/styles';
import { NAV_ITEMS, SIDEBAR_WIDTH_EXPANDED, SIDEBAR_WIDTH_COLLAPSED } from '@constants/navConfig';
import { useAuth } from '@hooks/useAuth';

function Sidebar() {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const { hasRole } = useAuth();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('md'));

  const [collapsed, setCollapsed] = useState(isSmallScreen);
  // Tracks which parent items (e.g. "Fees") are expanded, by label.
  const [expandedItems, setExpandedItems] = useState({});

  const width = collapsed ? SIDEBAR_WIDTH_COLLAPSED : SIDEBAR_WIDTH_EXPANDED;

  const visibleItems = NAV_ITEMS.filter((item) => hasRole(item.roles));

  const toggleExpand = (label) => {
    setExpandedItems((prev) => ({ ...prev, [label]: !prev[label] }));
  };

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
        {visibleItems.map((item) => {
          const { label, path, icon: Icon, children } = item;
          const hasChildren = Array.isArray(children) && children.length > 0;
          const visibleChildren = hasChildren ? children.filter((c) => hasRole(c.roles)) : [];
          const isActive = path ? location.pathname.startsWith(path) : false;
          const isParentActive = visibleChildren.some((c) => location.pathname.startsWith(c.path));
          const isExpanded = expandedItems[label] || isParentActive;

          // Parent item with children: clicking toggles expand/collapse
          // instead of navigating (it has no `path` of its own).
          if (hasChildren) {
            const parentButton = (
              <ListItemButton
                key={label}
                selected={isParentActive}
                onClick={() => (collapsed ? setCollapsed(false) : toggleExpand(label))}
                sx={{ minHeight: 48, justifyContent: collapsed ? 'center' : 'initial', px: 2.5 }}
              >
                <ListItemIcon sx={{ minWidth: 0, mr: collapsed ? 0 : 2, justifyContent: 'center' }}>
                  <Icon color={isParentActive ? 'primary' : 'inherit'} />
                </ListItemIcon>
                {!collapsed && (
                  <>
                    <ListItemText primary={label} />
                    {isExpanded ? <ExpandLessIcon fontSize="small" /> : <ExpandMoreIcon fontSize="small" />}
                  </>
                )}
              </ListItemButton>
            );

            return (
              <div key={label}>
                {collapsed ? (
                  <Tooltip title={label} placement="right">
                    {parentButton}
                  </Tooltip>
                ) : (
                  parentButton
                )}
                {!collapsed && (
                  <Collapse in={isExpanded} timeout="auto" unmountOnExit>
                    <List component="div" disablePadding>
                      {visibleChildren.map((child) => {
                        const childActive = location.pathname.startsWith(child.path);
                        return (
                          <ListItemButton
                            key={child.path}
                            selected={childActive}
                            onClick={() => navigate(child.path)}
                            sx={{ pl: 6, minHeight: 40 }}
                          >
                            <ListItemText
                              primary={child.label}
                              primaryTypographyProps={{ fontSize: '0.875rem' }}
                            />
                          </ListItemButton>
                        );
                      })}
                    </List>
                  </Collapse>
                )}
              </div>
            );
          }

          // Regular leaf item (no children) — same as before.
          const button = (
            <ListItemButton
              key={path}
              selected={isActive}
              onClick={() => navigate(path)}
              sx={{ minHeight: 48, justifyContent: collapsed ? 'center' : 'initial', px: 2.5 }}
            >
              <ListItemIcon sx={{ minWidth: 0, mr: collapsed ? 0 : 2, justifyContent: 'center' }}>
                <Icon color={isActive ? 'primary' : 'inherit'} />
              </ListItemIcon>
              {!collapsed && <ListItemText primary={label} />}
            </ListItemButton>
          );

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