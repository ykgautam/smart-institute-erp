import { useState, useEffect, useCallback } from 'react';
import { notificationApi } from '@services/api/notificationApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// Shared by both the Topbar bell (unread count + recent dropdown) and
// the full NotificationListPage — one hook, two consumers, so unread
// count logic isn't duplicated.
export function useNotifications() {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchAll = useCallback(async (filters = {}) => {
    setLoading(true);
    try {
      const [listRes, countRes] = await Promise.all([
        notificationApi.getNotifications(filters),
        notificationApi.getUnreadCount(),
      ]);
      setNotifications(listRes || []);
      setUnreadCount(countRes || 0);
    } catch {
      // Silent on notification-fetch failure — this runs in the Topbar
      // on every page, so a network hiccup shouldn't spam a snackbar
      // that interrupts unrelated work.
    } finally {
      setLoading(false);
    }
  }, []);

  const markAsRead = async (id) => {
    try {
      await notificationApi.markAsRead(id);
      fetchAll();
    } catch (err) {
      showSnackbar(err.message || 'Failed to mark notification as read.', 'error');
    }
  };

  const markAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      showSnackbar('All notifications marked as read.', 'success');
      fetchAll();
    } catch (err) {
      showSnackbar(err.message || 'Failed to mark all as read.', 'error');
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchAll();
  }, [fetchAll]);

  return { notifications, unreadCount, loading, refetch: fetchAll, markAsRead, markAllAsRead };
}