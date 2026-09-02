import apiClient from './apiClient';

// Minimal Batch API service — only what's needed right now for the
// Batch-picker dropdown in Student's "Assign to Batch" flow. Full CRUD
// (create/update/delete/status) will be ADDED to this same file when
// the dedicated Batch module Sprint begins — not duplicated elsewhere.
const BATCH_ENDPOINTS = {
  BASE: '/batches',
};

export const batchApi = {
  // params: { page, size, sortBy, direction, keyword } — matches
  // BatchController's GET / exactly (confirmed via openapi.json:
  // individual @RequestParam bindings, same field names as Student/Course).
  getBatches: (params) => apiClient.get(BATCH_ENDPOINTS.BASE, { params }),
};