import apiClient from './apiClient';

// IMPORTANT: test-controller is mapped at "/api/tests" — NOT "/api/v1/tests"
// like every other controller. Our apiClient's baseURL already includes
// "/api/v1", so we compute the root ("http://host/api") and pass FULL
// absolute URLs for these specific calls. Axios ignores baseURL when the
// URL passed is already absolute (starts with http). Interceptors
// (auth token, response unwrapping) still apply normally since they
// operate on the request/response regardless of URL shape.
const API_ROOT = import.meta.env.VITE_API_BASE_URL.replace(/\/api\/v1\/?$/, '/api');

const TEST_ENDPOINTS = {
  BASE: `${API_ROOT}/tests`,
  BY_ID: (id) => `${API_ROOT}/tests/${id}`,
  PUBLISH: (id) => `${API_ROOT}/tests/${id}/publish`,
  DRAFT: (id) => `${API_ROOT}/tests/${id}/draft`,
};

export const testApi = {
  getAll: () => apiClient.get(TEST_ENDPOINTS.BASE),
  getById: (id) => apiClient.get(TEST_ENDPOINTS.BY_ID(id)),
  create: (payload) => apiClient.post(TEST_ENDPOINTS.BASE, payload),
  update: (id, payload) => apiClient.put(TEST_ENDPOINTS.BY_ID(id), payload),
  remove: (id) => apiClient.delete(TEST_ENDPOINTS.BY_ID(id)),
  publish: (id) => apiClient.put(TEST_ENDPOINTS.PUBLISH(id)),
  setDraft: (id) => apiClient.put(TEST_ENDPOINTS.DRAFT(id)),
};