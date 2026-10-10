import apiClient from './apiClient';

// Matches AdminDashboardController exactly (@RequestMapping("/api/v1/admin/dashboard")).
// NOTE: backend's @PreAuthorize is "hasAnyRole('SUPER_ADMIN','ADMIN')" — but
// RoleType enum has no 'ADMIN' value (only INSTITUTE_ADMIN), which likely
// means INSTITUTE_ADMIN users get a 403 from the backend despite the
// frontend showing this page to them. This is a backend-side issue,
// flagged for the backend team — not something the frontend can fix.
const ADMIN_DASHBOARD_ENDPOINTS = {
  MAIN: '/admin/dashboard',
  ANALYTICS: '/admin/dashboard/analytics',
};

export const adminDashboardApi = {
  // Returns AdminDashboardResponse: the 8 top-level KPI metrics.
  getDashboard: () => apiClient.get(ADMIN_DASHBOARD_ENDPOINTS.MAIN),

  // Returns AdminAnalyticsResponse — a SINGLE call that bundles
  // studentGrowth[], attendanceTrend[], feeCollectionTrend[], and
  // coursePerformance[] together. Deliberately used INSTEAD OF the four
  // separate /student-growth, /attendance-trend, /fee-collection-trend,
  // /course-performance endpoints, since /analytics is a superset —
  // one network call instead of four for the same data.
  getAnalytics: () => apiClient.get(ADMIN_DASHBOARD_ENDPOINTS.ANALYTICS),
};