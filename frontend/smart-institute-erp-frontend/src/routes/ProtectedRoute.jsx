import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '@hooks/useAuth';

// Guards routes requiring authentication. Preserves the attempted
// location so LoginPage can send the user back after signing in.
//
// This is a UX convenience only — the backend remains the actual
// authorization boundary (Section 6/10 of project instructions).
function ProtectedRoute() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;