import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function HomePage() {
  const { user, loading } = useAuth();

  if (loading) {
    return <p className="text-center mt-10">Validando sessão...</p>;
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100">
      <h1 className="text-4xl font-bold mb-6">
        Painel de notificação
      </h1>

      <p className="mb-4">
        Olá, <strong>{user?.name ?? user?.user}</strong>
      </p>

      <Link to="/products" className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition">
        Gerenciar Produtos
      </Link>
    </div>
  );
}