import apiClient from './apiClient';

// Matches fee-structure-controller exactly (per openapi.json).
// NOTE: this endpoint returns an UNPAGED list (ApiResponseListFeeStructureResponse),
// unlike Student/Course/Batch which are paginated — so there's no
// page/size/sort query params here. Search/filter happens client-side
// on the full list, since the backend doesn't offer server-side paging
// for this resource.
const FEE_STRUCTURE_ENDPOINTS = {
  BASE: '/fee-structures',
  BY_ID: (id) => `/fee-structures/${id}`,
};

export const feeStructureApi = {
  getAll: () => apiClient.get(FEE_STRUCTURE_ENDPOINTS.BASE),
  getById: (id) => apiClient.get(FEE_STRUCTURE_ENDPOINTS.BY_ID(id)),
  create: (payload) => apiClient.post(FEE_STRUCTURE_ENDPOINTS.BASE, payload),
  update: (id, payload) => apiClient.put(FEE_STRUCTURE_ENDPOINTS.BY_ID(id), payload),
  remove: (id) => apiClient.delete(FEE_STRUCTURE_ENDPOINTS.BY_ID(id)),
};