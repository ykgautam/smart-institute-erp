import apiClient from './apiClient';

// All 11 report endpoints (excluding Test Performance Report, already
// implemented in Sprint 13 Part 6 as testReportApi.js).
const REPORT_ENDPOINTS = {
  STUDENTS: '/admin/reports/students',
  STUDENT_PERFORMANCE: '/admin/reports/students/performance',
  STUDENT_ATTENDANCE: '/admin/reports/students/attendance',
  STUDENT_ACADEMIC: '/admin/reports/students/academic',
  FEE_COLLECTION: '/admin/reports/fees',
  FEE_OUTSTANDING: '/admin/reports/fees/outstanding',
  STUDENT_FEE_COLLECTION: '/admin/reports/fees/collection',
  STUDENT_FEE_ACADEMIC: '/admin/reports/fees/academic',
  COURSE_PERFORMANCE: '/admin/reports/courses/performance',
  BATCH_PERFORMANCE: '/admin/reports/batches/performance',
  ATTENDANCE: '/admin/reports/attendance',
  TEST_WISE_PERFORMANCE: '/admin/reports/tests/summary',
};

export const reportApi = {
  getStudentReport: (params) => apiClient.get(REPORT_ENDPOINTS.STUDENTS, { params }),
  getStudentPerformanceReport: (params) => apiClient.get(REPORT_ENDPOINTS.STUDENT_PERFORMANCE, { params }),
  getStudentAttendanceReport: (params) => apiClient.get(REPORT_ENDPOINTS.STUDENT_ATTENDANCE, { params }),
  getStudentAcademicReport: (params) => apiClient.get(REPORT_ENDPOINTS.STUDENT_ACADEMIC, { params }),
  getFeeCollectionReport: (params) => apiClient.get(REPORT_ENDPOINTS.FEE_COLLECTION, { params }),
  getFeeOutstandingReport: (params) => apiClient.get(REPORT_ENDPOINTS.FEE_OUTSTANDING, { params }),
  getStudentFeeCollectionReport: (params) => apiClient.get(REPORT_ENDPOINTS.STUDENT_FEE_COLLECTION, { params }),
  getStudentFeeAcademicReport: (params) => apiClient.get(REPORT_ENDPOINTS.STUDENT_FEE_ACADEMIC, { params }),
  getCoursePerformanceReport: (params) => apiClient.get(REPORT_ENDPOINTS.COURSE_PERFORMANCE, { params }),
  getBatchPerformanceReport: (params) => apiClient.get(REPORT_ENDPOINTS.BATCH_PERFORMANCE, { params }),
  getAttendanceReport: (params) => apiClient.get(REPORT_ENDPOINTS.ATTENDANCE, { params }),
  getTestWisePerformanceReport: (params) => apiClient.get(REPORT_ENDPOINTS.TEST_WISE_PERFORMANCE, { params }),
};