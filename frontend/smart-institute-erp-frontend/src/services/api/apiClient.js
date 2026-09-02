import axios from 'axios';
import { HTTP_STATUS } from '@constants/httpStatus';

// Centralized Axios instance. Every feature API service imports THIS
// client — JWT attachment, refresh, and error handling live here only.
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

// Separate plain instance (no interceptors) used ONLY for the refresh
// call itself — prevents a failed refresh from recursively re-triggering
// this same response interceptor.
const refreshClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// Tracks a single in-flight refresh call so multiple simultaneous 401s
// (e.g. several widgets loading at once) share one refresh, not one each.
let refreshPromise = null;

apiClient.interceptors.response.use(
  (response) => {
    // Unwrap ApiResponse { success, message, data, timestamp } so
    // feature services/components work with `data` directly.
    if (response.data && typeof response.data.success !== 'undefined') {
      return response.data.data;
    }
    return response.data;
  },
  async (error) => {
    if (!error.response) {
      return Promise.reject({
        isNetworkError: true,
        message: 'Unable to reach the server. Please check your connection.',
      });
    }

    const { status, data } = error.response;
    const originalRequest = error.config;
    const isRefreshCall = originalRequest.url?.includes('/auth/refresh');

    // Attempt exactly one silent refresh per failed request on a 401,
    // unless it's the refresh call itself that failed (avoid infinite loop).
    if (status === HTTP_STATUS.UNAUTHORIZED && !originalRequest._retry && !isRefreshCall) {
      originalRequest._retry = true;
      const refreshToken = localStorage.getItem('refreshToken');

      if (!refreshToken) {
        localStorage.removeItem('accessToken');
        window.dispatchEvent(new CustomEvent('auth:unauthorized'));
        return Promise.reject({ status, message: data?.message || 'Session expired.' });
      }

      try {
        if (!refreshPromise) {
          refreshPromise = refreshClient
            .post('/auth/refresh', { refreshToken })
            .then((res) => res.data.data);
        }
        const refreshed = await refreshPromise;
        refreshPromise = null;

        localStorage.setItem('accessToken', refreshed.accessToken);
        localStorage.setItem('refreshToken', refreshed.refreshToken);

        // Retry the original failed request with the newly issued token.
        originalRequest.headers.Authorization = `Bearer ${refreshed.accessToken}`;
        return apiClient(originalRequest);
      } catch {
        refreshPromise = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.dispatchEvent(new CustomEvent('auth:unauthorized'));
        return Promise.reject({
          status: HTTP_STATUS.UNAUTHORIZED,
          message: 'Session expired. Please log in again.',
        });
      }
    }

    // Normalize error shape so components/hooks handle one structure
    // regardless of which endpoint failed. `errors` may come back under
    // different keys depending on the backend's GlobalExceptionHandler
    // (commonly `errors` or `validationErrors` for Bean Validation
    // failures) — shape not yet fully confirmed, so we check both.
    // Consumed by normalizeFieldErrors() in studentSchemas.js to map
    // field-level messages onto form inputs.
    return Promise.reject({
      status,
      message: data?.message || 'Something went wrong. Please try again.',
      errors: data?.errors || data?.validationErrors || null,
    });
  },
);

export default apiClient;