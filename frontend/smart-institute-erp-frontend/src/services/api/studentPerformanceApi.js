import apiClient from './apiClient';

// Matches Student Performance tag exactly (GET /api/v1/student/performance).
// No params needed — backend resolves the current authenticated student.
export const studentPerformanceApi = {
  getPerformance: () => apiClient.get('/student/performance'),
};