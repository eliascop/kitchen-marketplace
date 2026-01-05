import {
  createContext,
  useCallback,
  useEffect,
  useMemo,
  useState,
  ReactNode,
} from "react";
import { Notification } from "../models/Notification";
import {
  fetchNotifications,
  markNotificationAsRead,
} from "../services/notificationService";
import { NotificationContextType } from "./NotificationContextType";

export const NotificationContext =
  createContext<NotificationContextType | null>(null);

interface Props {
  children: ReactNode;
}

export function NotificationProvider({ children }: Props) {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const loadNotifications = useCallback(async () => {
    try {
      setLoading(true);
      const { data } = await fetchNotifications();
      setNotifications(data);
    } finally {
      setLoading(false);
    }
  }, []);

  const markAsRead = useCallback(async (id: string) => {
    await markNotificationAsRead(id);
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  }, []);

  const unreadCount = useMemo(
    () => notifications.filter((n) => !n.read).length,
    [notifications]
  );

  useEffect(() => {
    loadNotifications();

    const interval = setInterval(loadNotifications, 30_000);
    return () => clearInterval(interval);
  }, [loadNotifications]);

  const value: NotificationContextType = {
    notifications,
    unreadCount,
    loading,
    loadNotifications,
    markAsRead,
  };

  return (
    <NotificationContext.Provider value={value}>
      {children}
    </NotificationContext.Provider>
  );
}