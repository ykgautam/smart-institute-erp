import apiClient from './apiClient';

const QUESTION_ENDPOINTS = {
  BASE: '/questions',
  BY_ID: (id) => `/questions/${id}`,
  STATUS: (id) => `/questions/${id}/status`,
  DUPLICATE: (id) => `/questions/${id}/duplicate`,
  SEARCH: '/questions/search',
};

export const questionApi = {
  getAll: () => apiClient.get(QUESTION_ENDPOINTS.BASE),
  // params: { courseId, topicId, difficulty, questionType, keyword } — all
  // optional per QuestionSearchRequest. Always used instead of getAll()
  // for the list page so filters go server-side (Section 28).
  search: (params) => apiClient.get(QUESTION_ENDPOINTS.SEARCH, { params }),
  create: (payload) => apiClient.post(QUESTION_ENDPOINTS.BASE, payload),
  update: (id, payload) => apiClient.put(QUESTION_ENDPOINTS.BY_ID(id), payload),
  updateStatus: (id, active) => apiClient.patch(QUESTION_ENDPOINTS.STATUS(id), { active }),
  remove: (id) => apiClient.delete(QUESTION_ENDPOINTS.BY_ID(id)),
  duplicate: (id) => apiClient.post(QUESTION_ENDPOINTS.DUPLICATE(id)),
};