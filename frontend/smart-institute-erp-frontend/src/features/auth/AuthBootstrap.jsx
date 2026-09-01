import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { fetchCurrentUser, logout } from '@features/auth/authSlice';

// Owns two cross-cutting auth concerns with no natural single-page home:
//  1. Session restore — if a token survived a page refresh, fetch the
//     user profile so isAuthenticated/user reflect actual session state.
//  2. Forced logout — listens for 'auth:unauthorized', dispatched by
//     apiClient.js when a silent token refresh fails, and redirects
//     to /login so the user isn't left on a broken authenticated screen.
function AuthBootstrap() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      dispatch(fetchCurrentUser());
    }
  }, [dispatch]);

  useEffect(() => {
    const handleUnauthorized = () => {
      dispatch(logout());
      navigate('/login', { replace: true });
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, [dispatch, navigate]);

  return null; // Logic-only component — nothing to render.
}

export default AuthBootstrap;