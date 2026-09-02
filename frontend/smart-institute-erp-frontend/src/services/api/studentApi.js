import apiClient from './apiClient';

// Matches StudentController exactly (@RequestMapping("/api/v1/students")).
// Do not change these paths without re-checking the controller.
const STUDENT_ENDPOINTS = {
  BASE: '/students',
  BY_ID: (id) => `/students/${id}`,
  STATUS: (id) => `/students/${id}/status`,
  BATCH: (id) => `/students/${id}/batch`,
};

export const studentApi = {
  // params: { page, size, sortBy, direction, keyword } — matches
  // PaginationRequest exactly (@ModelAttribute binds query params to it).
  getStudents: (params) => apiClient.get(STUDENT_ENDPOINTS.BASE, { params }),

  getStudentById: (id) => apiClient.get(STUDENT_ENDPOINTS.BY_ID(id)),

  // payload: CreateStudentRequest shape (admissionNumber, firstName, etc.)
  createStudent: (payload) => apiClient.post(STUDENT_ENDPOINTS.BASE, payload),

  // payload: UpdateStudentRequest shape
  updateStudent: (id, payload) => apiClient.put(STUDENT_ENDPOINTS.BY_ID(id), payload),

  // status: one of StudentStatus enum values (exact values TBD)
  updateStatus: (id, status) =>
    apiClient.patch(STUDENT_ENDPOINTS.STATUS(id), { status }),

  // Backend performs a soft delete (active=false), not a physical delete —
  // see StudentServiceImpl.deleteStudent(). UI labels this "Deactivate".
  deactivateStudent: (id) => apiClient.delete(STUDENT_ENDPOINTS.BY_ID(id)),

  assignToBatch: (studentId, batchId) =>
    apiClient.put(STUDENT_ENDPOINTS.BATCH(studentId), { batchId }),

  removeFromBatch: (studentId) => apiClient.delete(STUDENT_ENDPOINTS.BATCH(studentId)),
};