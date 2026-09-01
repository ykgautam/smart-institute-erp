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

    // Normalize error shape for all other cases (400/403/404/409/422/500).
    return Promise.reject({
      status,
      message: data?.message || 'Something went wrong. Please try again.',
      errors: data?.errors || null,
    });
  },
);

export default apiClient;



// import axios from 'axios';

// import { normalizeApiError } from './apiError';

// const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// const apiClient = axios.create({
//   baseURL: API_BASE_URL,
//   timeout: 15000,
//   headers: {
//     'Content-Type': 'application/json',
//   },
// });

// /**
//  * Request interceptor.
//  *
//  * Authentication will be added in the authentication Part.
//  * Keeping the interceptor now gives us a single location for
//  * request-wide configuration later.
//  */
// apiClient.interceptors.request.use(
//   (config) => config,
//   (error) => Promise.reject(error),
// );

// /**
//  * Response interceptor.
//  *
//  * Feature services should receive a normalized application error
//  * instead of having to understand Axios' internal error structure.
//  */
// apiClient.interceptors.response.use(
//   (response) => response,
//   (error) => {
//     const normalizedError = normalizeApiError(error);

//     return Promise.reject(normalizedError);
//   },
// );

// export default apiClient;