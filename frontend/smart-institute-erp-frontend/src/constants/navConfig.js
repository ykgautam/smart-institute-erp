import DashboardIcon from '@mui/icons-material/DashboardOutlined';
import SchoolIcon from '@mui/icons-material/SchoolOutlined';
import ClassIcon from '@mui/icons-material/ClassOutlined';
import GroupsIcon from '@mui/icons-material/GroupsOutlined';
import PaymentsIcon from '@mui/icons-material/PaymentsOutlined';
import EventAvailableIcon from '@mui/icons-material/EventAvailableOutlined';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import BusinessOutlinedIcon from '@mui/icons-material/BusinessOutlined';
import NotificationsOutlinedIcon from '@mui/icons-material/NotificationsOutlined';
import HistoryEduOutlinedIcon from '@mui/icons-material/HistoryEduOutlined';
import QuizOutlinedIcon from '@mui/icons-material/QuizOutlined';
import AssessmentOutlinedIcon from '@mui/icons-material/AssessmentOutlined';

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
  {
    label: 'Users',
    path: '/users',
    icon: PeopleAltOutlinedIcon,
    roles: ['SUPER_ADMIN'],
  },
  {
    label: 'Institute',
    path: '/institute',
    icon: BusinessOutlinedIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'],
  },
  {
    label: 'Notifications',
    path: '/notifications',
    icon: NotificationsOutlinedIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'STAFF', 'FACULTY', 'ACCOUNTANT', 'STUDENT'],
  },
  {
    label: 'Audit Logs',
    path: '/audit-logs',
    icon: HistoryEduOutlinedIcon,
    roles: ['SUPER_ADMIN'],
  },
  {
    label: 'Examinations',
    icon: QuizOutlinedIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'FACULTY', 'STUDENT'],
    children: [
      { label: 'Topics', path: '/exams/topics', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
      { label: 'Question Bank', path: '/exams/questions', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'FACULTY'] },
      { label: 'Tests', path: '/exams/tests', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'FACULTY'] },
      { label: 'Performance Report', path: '/exams/reports', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
      { label: 'My Tests', path: '/exams/my-tests', roles: ['STUDENT'] },
      { label: 'My Performance', path: '/exams/my-performance', roles: ['STUDENT'] },
    ],
  },
  {
    label: 'Reports',
    icon: AssessmentOutlinedIcon,
    roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'],
    children: [
      { label: 'Student Reports', path: '/reports/students', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
      { label: 'Attendance Reports', path: '/reports/attendance', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
      { label: 'Fee Reports', path: '/reports/fees', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
      { label: 'Course & Batch Performance', path: '/reports/performance', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
      { label: 'Test-Wise Report', path: '/reports/tests', roles: ['SUPER_ADMIN', 'INSTITUTE_ADMIN'] },
    ],
  },
];

export const SIDEBAR_WIDTH_EXPANDED = 240;
export const SIDEBAR_WIDTH_COLLAPSED = 72;