import apiClient from "../api/apiClient";
import { API_BASE } from "../config/api";
import { Notification } from "../models/Notification";

export async function fetchNotifications() {
  return apiClient.get<Notification[]>(`${API_BASE.KNS}`);
}

export async function markNotificationAsRead(id: string) {
  return apiClient.patch(`${API_BASE.KNS}/${id}/read`);
}