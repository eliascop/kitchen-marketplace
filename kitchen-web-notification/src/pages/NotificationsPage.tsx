import { useNotifications } from "../hooks/useNotifications";

export default function NotificationsPage() {
  const {
    notifications,
    loading,
    markAsRead,
  } = useNotifications();

  if (loading) {
    return <div className="p-6">Carregando notificações...</div>;
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Notificações</h1>

      {notifications.length === 0 && (
        <p className="text-gray-500">Nenhuma notificação encontrada.</p>
      )}

      <ul className="space-y-4">
        {notifications.map((n) => (
          <li
            key={n.id}
            className={`p-4 rounded border ${
              n.read ? "bg-gray-100" : "bg-white border-green-500"
            }`}
          >
            <div className="flex justify-between items-start">
              <div>
                <p className="font-semibold">{n.title}</p>
                <p className="text-sm text-gray-600">{n.message}</p>
                <p className="text-xs text-gray-400 mt-1">
                  {new Date(n.createdAt).toLocaleString()}
                </p>
              </div>

              {!n.read && (
                <button
                  onClick={() => markAsRead(n.id)}
                  className="text-sm text-green-600 hover:underline"
                >
                  Marcar como lida
                </button>
              )}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}