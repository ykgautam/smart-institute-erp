import apiClient from './apiClient';

// Matches BatchController exactly (@RequestMapping("/api/v1/batches")).
// NOTE: unlike Student/Course, this controller has NO @PreAuthorize
// annotations — any authenticated user can technically call these.
// The frontend still gates write-action UI to SUPER_ADMIN/INSTITUTE_ADMIN
// for consistency (see Sprint 4 assumption flag), but this is UX only.
const BATCH_ENDPOINTS = {
  BASE: '/batches',
  BY_ID: (id) => `/batches/${id}`,
  STATUS: (id) => `/batches/${id}/status`,
};

export const batchApi = {
  getBatches: (params) => apiClient.get(BATCH_ENDPOINTS.BASE, { params }),
  getBatchById: (id) => apiClient.get(BATCH_ENDPOINTS.BY_ID(id)),
  createBatch: (payload) => apiClient.post(BATCH_ENDPOINTS.BASE, payload),
  updateBatch: (id, payload) => apiClient.put(BATCH_ENDPOINTS.BY_ID(id), payload),
  updateStatus: (id, status) => apiClient.patch(BATCH_ENDPOINTS.STATUS(id), { status }),
  deleteBatch: (id) => apiClient.delete(BATCH_ENDPOINTS.BY_ID(id)),
};