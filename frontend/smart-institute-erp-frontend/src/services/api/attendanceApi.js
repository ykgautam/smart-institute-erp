import apiClient from './apiClient';

// Matches attendance-controller exactly (per openapi.json).
const ATTENDANCE_ENDPOINTS = {
  BASE: '/attendance',
  BY_ID: (id) => `/attendance/${id}`,
  BY_BATCH: (batchId) => `/attendance/batch/${batchId}`,
  BY_STUDENT: (studentId) => `/attendance/student/${studentId}`,
  STUDENT_SUMMARY: (studentId) => `/attendance/student/${studentId}/summary`,
};

export const attendanceApi = {
  // payload: { batchId, attendanceDate, attendanceList: [{studentId, status, remarks}] }
  // — matches MarkAttendanceRequest exactly.
  markAttendance: (payload) => apiClient.post(ATTENDANCE_ENDPOINTS.BASE, payload),

  // Fetches existing attendance records for a batch on a specific date —
  // used both to check "already marked?" before showing the mark form,
  // and to power the view/edit records screen (Part 2).
  getBatchAttendance: (batchId, attendanceDate) =>
    apiClient.get(ATTENDANCE_ENDPOINTS.BY_BATCH(batchId), { params: { attendanceDate } }),

  // payload: { status, remarks } — matches UpdateAttendanceRequest.
  updateAttendance: (attendanceId, payload) => apiClient.put(ATTENDANCE_ENDPOINTS.BY_ID(attendanceId), payload),

  getStudentHistory: (studentId) => apiClient.get(ATTENDANCE_ENDPOINTS.BY_STUDENT(studentId)),

  getStudentSummary: (studentId) => apiClient.get(ATTENDANCE_ENDPOINTS.STUDENT_SUMMARY(studentId)),
};