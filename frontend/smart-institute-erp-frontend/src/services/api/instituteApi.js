import apiClient from './apiClient';

// Matches institute-controller (per openapi.json). No "create" is
// exposed here deliberately — institute creation happens via the
// /institutes/onboard flow (institute + owner together), which is a
// separate, larger onboarding feature outside this Sprint's scope.
// This module covers viewing/editing the CURRENT institute only.
const INSTITUTE_ENDPOINTS = {
  BY_ID: (id) => `/institutes/${id}`,
  STATUS: (id) => `/institutes/${id}/status`,
};

export const instituteApi = {
  getById: (id) => apiClient.get(INSTITUTE_ENDPOINTS.BY_ID(id)),
  update: (id, payload) => apiClient.put(INSTITUTE_ENDPOINTS.BY_ID(id), payload),
  // NOTE: body is { active: boolean } — UpdateInstituteStatusRequest,
  // unlike User/Batch/Course status which take an enum string.
  updateStatus: (id, active) => apiClient.patch(INSTITUTE_ENDPOINTS.STATUS(id), { active }),
};