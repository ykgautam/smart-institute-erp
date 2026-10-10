import apiClient from './apiClient';

// Matches Student Dashboard tag exactly (@GetMapping "/api/v1/student/dashboard").
const STUDENT_DASHBOARD_ENDPOINTS = {
  BASE: '/student/dashboard',
};

export const studentDashboardApi = {
  getDashboard: () => apiClient.get(STUDENT_DASHBOARD_ENDPOINTS.BASE),
};