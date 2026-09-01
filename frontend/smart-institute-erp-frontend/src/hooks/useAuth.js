import { useSelector, useDispatch } from 'react-redux';
import { logout as logoutAction } from '@features/auth/authSlice';

// Thin wrapper around the auth slice so components consume auth state
// without knowing it's backed by Redux.
export function useAuth() {
  const dispatch = useDispatch();
  const { user, isAuthenticated, status, error } = useSelector((state) => state.auth);

  const logout = () => dispatch(logoutAction());

  // Centralized role check — every role-aware component (Sidebar,
  // future RoleGuard) should call this rather than reading
  // `user.role` directly, so a backend contract change (e.g. to
  // `roles: []`) only requires updating this one function.
  const hasRole = (allowedRoles = []) => {
    if (!user?.role) return false;
    return allowedRoles.includes(user.role);
  };

  return { user, isAuthenticated, status, error, logout, hasRole };
}