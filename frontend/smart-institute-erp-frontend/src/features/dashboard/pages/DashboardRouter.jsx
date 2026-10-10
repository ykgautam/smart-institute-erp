import { useAuth } from '@hooks/useAuth';
import AdminDashboardPage from './AdminDashboardPage';
import StudentDashboardPage from './StudentDashboardPage';
import { ROLE_TYPE } from '@constants/enums';

// Single place where "which dashboard does this role see" is decided —
// keeps role-branching out of App.jsx and out of the dashboard pages
// themselves. If the backend adds role-specific dashboards for STAFF/
// FACULTY/ACCOUNTANT in the future, extend the switch here only.
function DashboardRouter() {
  const { user } = useAuth();

  if (user?.role === ROLE_TYPE.STUDENT) {
    return <StudentDashboardPage />;
  }

  // SUPER_ADMIN, INSTITUTE_ADMIN, and (for now, as a fallback) STAFF/
  // FACULTY/ACCOUNTANT all see the Admin dashboard — no dedicated
  // dashboard exists for those roles on the backend yet.
  return <AdminDashboardPage />;
}

export default DashboardRouter;