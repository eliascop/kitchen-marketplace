import { useContext } from "react";
import {
  NotificationContext,
} from "../context/NotificationContext";
import { NotificationContextType } from "../context/NotificationContextType";

export function useNotifications(): NotificationContextType {
  const context = useContext(NotificationContext);

  if (!context) {
    throw new Error(
      "useNotifications must be used inside a NotificationProvider"
    );
  }

  return context;
}
