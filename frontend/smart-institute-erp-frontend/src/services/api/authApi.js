import apiClient from './apiClient';

// Matches AuthController exactly (@RequestMapping("/api/v1/auth")).
// Do not change these paths without re-checking the controller.
const AUTH_ENDPOINTS = {
  LOGIN: '/auth/login',
  REFRESH: '/auth/refresh',
  ME: '/auth/me',
  LOGOUT: '/auth/logout',
  CHANGE_PASSWORD: '/auth/change-password',
};

export const authApi = {
  // credentials: { email, password } — matches LoginRequest exactly.
  login: (credentials) => apiClient.post(AUTH_ENDPOINTS.LOGIN, credentials),

  refreshToken: (refreshToken) =>
    apiClient.post(AUTH_ENDPOINTS.REFRESH, { refreshToken }),

  // Returns UserResponse — shape not yet confirmed against backend DTO.
  getCurrentUser: () => apiClient.get(AUTH_ENDPOINTS.ME),

  // Stateless on the backend (AuthServiceImpl.logout() is a no-op) —
  // called for completeness/future-proofing, but client-side token
  // removal (in authSlice) is what actually ends the session.
  logout: () => apiClient.post(AUTH_ENDPOINTS.LOGOUT),

  changePassword: (payload) =>
    apiClient.patch(AUTH_ENDPOINTS.CHANGE_PASSWORD, payload),
};