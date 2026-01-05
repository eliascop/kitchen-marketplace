import { Notification } from "../models/Notification";

export interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  loading: boolean;

  loadNotifications: () => Promise<void>;
  markAsRead: (id: string) => Promise<void>;
}