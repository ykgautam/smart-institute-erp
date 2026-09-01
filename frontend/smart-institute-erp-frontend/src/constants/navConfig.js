import DashboardIcon from '@mui/icons-material/DashboardOutlined';
import SchoolIcon from '@mui/icons-material/SchoolOutlined';
import ClassIcon from '@mui/icons-material/ClassOutlined';
import GroupsIcon from '@mui/icons-material/GroupsOutlined';
import PaymentsIcon from '@mui/icons-material/PaymentsOutlined';
import EventAvailableIcon from '@mui/icons-material/EventAvailableOutlined';

// Declarative navigation source of truth. Each item's `roles` array
// controls visibility — Sidebar filters against the current user's role
// rather than hard-coding role checks inside JSX. Add new modules here
// only; do not scatter route/role logic into layout components.
//
// NOTE: `path` values for unbuilt modules (students, courses, etc.) are
// placeholders and will 404 until those features are implemented in
// later Sprints — they exist now so the nav shape doesn't need revisiting.
export const NAV_ITEMS = [
  {
    label: 'Dashboard',
    path: '/dashboard',
    icon: DashboardIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY', 'ACCOUNTANT', 'STUDENT'],
  },
  {
    label: 'Students',
    path: '/students',
    icon: SchoolIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY'],
  },
  {
    label: 'Courses',
    path: '/courses',
    icon: ClassIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF'],
  },
  {
    label: 'Batches',
    path: '/batches',
    icon: GroupsIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY'],
  },
  {
    label: 'Fees',
    path: '/fees',
    icon: PaymentsIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'ACCOUNTANT'],
  },
  {
    label: 'Attendance',
    path: '/attendance',
    icon: EventAvailableIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY'],
  },
];

export const SIDEBAR_WIDTH_EXPANDED = 240;
export const SIDEBAR_WIDTH_COLLAPSED = 72;