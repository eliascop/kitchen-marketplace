import { Link } from "react-router-dom";
import { useNotifications } from "../hooks/useNotifications";

export default function NotificationBell() {
  const { unreadCount } = useNotifications();

  return (
    <Link
      to="/details"
      className="relative hover:text-green-400 transition"
      title="Notificações"
    >
      🔔

      {unreadCount > 0 && (
        <span className="absolute -top-2 -right-3 bg-red-600 text-white text-xs px-1.5 rounded-full">
          {unreadCount}
        </span>
      )}
    </Link>
  );
}