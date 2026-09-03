import DashboardIcon from '@mui/icons-material/DashboardOutlined';
import SchoolIcon from '@mui/icons-material/SchoolOutlined';
import ClassIcon from '@mui/icons-material/ClassOutlined';
import GroupsIcon from '@mui/icons-material/GroupsOutlined';
import PaymentsIcon from '@mui/icons-material/PaymentsOutlined';
import EventAvailableIcon from '@mui/icons-material/EventAvailableOutlined';

// Declarative navigation source of truth. Items can now optionally have
// a `children` array — Sidebar renders these as an expandable sub-menu
// instead of a direct link. Used for modules with multiple sub-screens
// (e.g. Fees: Structures + Student Fees + Collection).
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
    icon: PaymentsIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'ACCOUNTANT'],
    // No `path` at this level — clicking "Fees" expands the sub-menu
    // instead of navigating directly (see Sidebar.jsx).
    children: [
      { label: 'Fee Structures', path: '/fees/structures', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
      { label: 'Student Fees', path: '/fees/students', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'ACCOUNTANT'] },
    ],
  },
  {
    label: 'Attendance',
    icon: EventAvailableIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY'],
    children: [
      { label: 'Mark Attendance', path: '/attendance/mark', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY'] },
      { label: 'Attendance Records', path: '/attendance/records', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY'] },
      { label: 'Student History', path: '/attendance/history', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY'] },
    ],
  },
];

export const SIDEBAR_WIDTH_EXPANDED = 240;
export const SIDEBAR_WIDTH_COLLAPSED = 72;