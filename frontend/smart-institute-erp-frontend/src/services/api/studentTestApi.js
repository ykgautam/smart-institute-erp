import apiClient from './apiClient';

const STUDENT_TEST_ENDPOINTS = {
  START: '/student-tests/start',
  BY_ID: (id) => `/student-tests/${id}`,
  QUESTIONS: (id) => `/student-tests/${id}/questions`,
  QUESTION_BY_ID: (id, questionId) => `/student-tests/${id}/questions/${questionId}`,
  ANSWERS: (id) => `/student-tests/${id}/answers`,
  SUBMIT: (id) => `/student-tests/${id}/submit`,
  RESULT: (id) => `/student-tests/${id}/result`,
  MY_TESTS: '/student-tests/my-tests',
  // NOTE: this is a DIFFERENT endpoint from MY_TESTS above. my-tests
  // returns a simple, unpaged summary list (used for quick display).
  // history returns a properly PAGINATED, FILTERABLE list — with
  // status/date-range/search filters — meant for a dedicated
  // "browse my past attempts" screen. Different backend controllers,
  // different response DTOs (StudentTestSummaryResponse vs
  // StudentTestHistoryResponse), so we keep them as two separate
  // API methods rather than trying to unify them.
  HISTORY: '/student/tests/history',
};

export const studentTestApi = {
  startTest: (testId) => apiClient.post(STUDENT_TEST_ENDPOINTS.START, { testId }),
  getStudentTest: (id) => apiClient.get(STUDENT_TEST_ENDPOINTS.BY_ID(id)),
  // QuestionForStudentResponse — deliberately has NO `correct` flag on
  // options (backend hides the answer key from the student until submission).
  getQuestions: (id) => apiClient.get(STUDENT_TEST_ENDPOINTS.QUESTIONS(id)),
  getSavedAnswers: (id) => apiClient.get(STUDENT_TEST_ENDPOINTS.ANSWERS(id)),
  // payload: { questionId, selectedOptionId } — matches SaveAnswerRequest.
  saveAnswer: (id, payload) => apiClient.post(STUDENT_TEST_ENDPOINTS.ANSWERS(id), payload),
  submitTest: (id) => apiClient.post(STUDENT_TEST_ENDPOINTS.SUBMIT(id)),
  getResult: (id) => apiClient.get(STUDENT_TEST_ENDPOINTS.RESULT(id)),
  getMyTests: () => apiClient.get(STUDENT_TEST_ENDPOINTS.MY_TESTS),

  // params: { page, size, status, fromDate, toDate, search, sort, direction }
  // IMPORTANT: this endpoint's query param names are "sort" and
  // "direction" — NOT "sortBy"/"direction" (used by Student/Course/Batch)
  // and NOT "sortBy"/"sortDirection" (used by admin Reports). Every
  // paginated endpoint in this backend uses slightly different param
  // naming, so always double-check the actual controller/openapi spec
  // before assuming a pattern carries over.
  getHistory: (params) => apiClient.get(STUDENT_TEST_ENDPOINTS.HISTORY, { params }),
};