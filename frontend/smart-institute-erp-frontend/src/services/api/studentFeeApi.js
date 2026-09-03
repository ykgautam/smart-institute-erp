import apiClient from './apiClient';

// Matches student-fee-controller exactly (per openapi.json).
// NOTE: GET /student-fees is UNPAGED, same constraint as fee-structures —
// client-side search only, no server-side pagination available.
const STUDENT_FEE_ENDPOINTS = {
  BASE: '/student-fees',
  ASSIGN: '/student-fees/assign',
  BY_STUDENT_ID: (studentId) => `/student-fees/${studentId}`,
};

export const studentFeeApi = {
  getAll: () => apiClient.get(STUDENT_FEE_ENDPOINTS.BASE),
  getByStudentId: (studentId) => apiClient.get(STUDENT_FEE_ENDPOINTS.BY_STUDENT_ID(studentId)),
  // payload: { studentId, feeStructureId, discount? } — matches
  // AssignStudentFeeRequest exactly.
  assign: (payload) => apiClient.post(STUDENT_FEE_ENDPOINTS.ASSIGN, payload),
};