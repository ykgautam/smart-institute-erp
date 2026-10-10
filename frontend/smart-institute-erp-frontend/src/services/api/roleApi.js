import apiClient from './apiClient';

// New endpoint (GET /api/v1/roles) — returns the Role lookup table used
// to populate the Role dropdown in User create/edit forms. UserResponse
// exposes a role NAME (string), but CreateUserRequest/UpdateUserRequest
// require a roleId (Long) — this endpoint bridges that gap.
const ROLE_ENDPOINTS = {
  BASE: '/roles',
};

export const roleApi = {
  getAll: () => apiClient.get(ROLE_ENDPOINTS.BASE),
};