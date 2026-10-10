import apiClient from './apiClient';

const NOTIFICATION_ENDPOINTS = {
  BASE: '/notifications',
  UNREAD_COUNT: '/notifications/unread-count',
  MARK_READ: (id) => `/notifications/${id}/read`,
  MARK_ALL_READ: '/notifications/read-all',
};

export const notificationApi = {
  // params: { read?: boolean, type?: string } — matches NotificationFilterRequest.
  getNotifications: (params) => apiClient.get(NOTIFICATION_ENDPOINTS.BASE, { params }),
  getUnreadCount: () => apiClient.get(NOTIFICATION_ENDPOINTS.UNREAD_COUNT),
  markAsRead: (id) => apiClient.patch(NOTIFICATION_ENDPOINTS.MARK_READ(id)),
  markAllAsRead: () => apiClient.patch(NOTIFICATION_ENDPOINTS.MARK_ALL_READ),
};