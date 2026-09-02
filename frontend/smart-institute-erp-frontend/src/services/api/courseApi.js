import apiClient from './apiClient';

// Matches CourseController exactly (@RequestMapping("/api/v1/courses")).
// NOTE: every write operation requires SUPER_ADMIN specifically
// (not INSTITUTE_ADMIN) per the backend's @PreAuthorize annotations —
// stricter than Student, which allows both roles.
const COURSE_ENDPOINTS = {
  BASE: '/courses',
  BY_ID: (id) => `/courses/${id}`,
  STATUS: (id) => `/courses/${id}/status`,
};

export const courseApi = {
  getCourses: (params) => apiClient.get(COURSE_ENDPOINTS.BASE, { params }),
  getCourseById: (id) => apiClient.get(COURSE_ENDPOINTS.BY_ID(id)),
  createCourse: (payload) => apiClient.post(COURSE_ENDPOINTS.BASE, payload),
  updateCourse: (id, payload) => apiClient.put(COURSE_ENDPOINTS.BY_ID(id), payload),
  updateStatus: (id, status) => apiClient.patch(COURSE_ENDPOINTS.STATUS(id), { status }),
  deleteCourse: (id) => apiClient.delete(COURSE_ENDPOINTS.BY_ID(id)),
};