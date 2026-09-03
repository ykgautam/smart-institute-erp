import apiClient from './apiClient';

// Minimal User API — only what's needed right now: fetching the faculty
// list for Batch's faculty-picker dropdown. Full User management
// (create/update/status) will be ADDED to this same file when a
// dedicated User Management Sprint begins.
const USER_ENDPOINTS = {
  BASE: '/users',
};

export const userApi = {
  // Returns an unpaged List<UserResponse> per the backend (confirmed
  // via openapi.json — no pagination params on GET /users).
  getAllUsers: () => apiClient.get(USER_ENDPOINTS.BASE),
};