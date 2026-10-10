import apiClient from './apiClient';

const TEST_REPORT_ENDPOINTS = {
  BASE: '/admin/reports/tests',
};

export const testReportApi = {
  // params match TestPerformanceReportRequest: testId, courseId, batchId,
  // studentId, status, submittedFrom, submittedTo, page, size, sortBy, sortDirection.
  getReport: (params) => apiClient.get(TEST_REPORT_ENDPOINTS.BASE, { params }),
};