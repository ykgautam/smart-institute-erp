import apiClient from './apiClient';

// Matches Topic Management tag exactly (@RequestMapping("/api/v1/topics")).
// GET /topics is UNPAGED — same client-filter pattern as Fee Structures.
const TOPIC_ENDPOINTS = {
  BASE: '/topics',
  BY_ID: (id) => `/topics/${id}`,
  BY_COURSE: (courseId) => `/topics/course/${courseId}`,
};

export const topicApi = {
  getAll: () => apiClient.get(TOPIC_ENDPOINTS.BASE),
  getByCourse: (courseId) => apiClient.get(TOPIC_ENDPOINTS.BY_COURSE(courseId)),
  create: (payload) => apiClient.post(TOPIC_ENDPOINTS.BASE, payload),
  update: (id, payload) => apiClient.put(TOPIC_ENDPOINTS.BY_ID(id), payload),
  remove: (id) => apiClient.delete(TOPIC_ENDPOINTS.BY_ID(id)),
};