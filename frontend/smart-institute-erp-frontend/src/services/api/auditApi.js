import apiClient from './apiClient';

// Matches audit-log-controller exactly (@RequestMapping("/api/v1/admin/audit-logs")).
// This is a READ-ONLY module — no create/update/delete, only viewing
// historical system activity (Section 23/24: audit logs are for
// historical record, not editable).
const AUDIT_ENDPOINTS = {
  BASE: '/admin/audit-logs',
};

export const auditApi = {
  // params: { userId, action, entityType, entityId, dateFrom, dateTo,
  // page, size, sortBy, sortDirection } — matches AuditLogSearchRequest.
  getAuditLogs: (params) => apiClient.get(AUDIT_ENDPOINTS.BASE, { params }),
};