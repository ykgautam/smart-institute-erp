import apiClient from './apiClient';

// Matches Test Question Management tag exactly — note this IS on
// /api/v1 (unlike testApi.js), so no baseURL override needed here.
const TEST_QUESTION_ENDPOINTS = {
  BASE: (testId) => `/tests/${testId}/questions`,
  ORDER: (testId) => `/tests/${testId}/questions/order`,
  BY_QUESTION: (testId, questionId) => `/tests/${testId}/questions/${questionId}`,
};

export const testQuestionApi = {
  getQuestions: (testId) => apiClient.get(TEST_QUESTION_ENDPOINTS.BASE(testId)),
  // payload: { questionIds: [] } — matches AddQuestionToTestRequest.
  addQuestions: (testId, questionIds) => apiClient.post(TEST_QUESTION_ENDPOINTS.BASE(testId), { questionIds }),
  // payload: [{ questionId, displayOrder }] — matches
  // UpdateTestQuestionOrderRequest[] (array body, not wrapped in an object).
  updateOrder: (testId, orderList) => apiClient.put(TEST_QUESTION_ENDPOINTS.ORDER(testId), orderList),
  removeQuestion: (testId, questionId) => apiClient.delete(TEST_QUESTION_ENDPOINTS.BY_QUESTION(testId, questionId)),
};