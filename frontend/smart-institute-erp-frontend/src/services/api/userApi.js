import apiClient from './apiClient';

// Matches user-controller exactly (per openapi.json).
// NOTE: GET /users is UNPAGED (ApiResponseListUserResponse), same
// constraint as Fee Structure and Student Fees — client-side search only.
const USER_ENDPOINTS = {
  BASE: '/users',
  BY_ID: (id) => `/users/${id}`,
  BY_EMAIL: (email) => `/users/email/${email}`,
  STATUS: (id) => `/users/${id}/status`,
};

export const userApi = {
  getAllUsers: () => apiClient.get(USER_ENDPOINTS.BASE),
  getUserById: (id) => apiClient.get(USER_ENDPOINTS.BY_ID(id)),
  getUserByEmail: (email) => apiClient.get(USER_ENDPOINTS.BY_EMAIL(email)),
  // payload: { firstName, lastName, email, mobile, password, gender, roleId }
  // — matches CreateUserRequest exactly.
  createUser: (payload) => apiClient.post(USER_ENDPOINTS.BASE, payload),
  // payload: { firstName, lastName, mobile, gender, roleId } — matches
  // UpdateUserRequest (no email/password on update).
  updateUser: (id, payload) => apiClient.put(USER_ENDPOINTS.BY_ID(id), payload),
  updateStatus: (id, status) => apiClient.patch(USER_ENDPOINTS.STATUS(id), { status }),
  deleteUser: (id) => apiClient.delete(USER_ENDPOINTS.BY_ID(id)),
};