import { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { environment } from "../config/environment";

interface DecodedToken {
  sub: string;
  name?: string;
  email?: string;
  roles: string[];
  exp: number;
}

const ANGULAR_LOGIN_URL = environment.apiBaseUrl;

export function useAuth() {
  const [user, setUser] = useState<{
    user: string;
    name?: string;
    email?: string;
    roles?: string[];
  } | null>(null);

  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    if (!storedToken) {
      redirectToAngular();
      return;
    }

    try {
      const decoded = jwtDecode<DecodedToken>(storedToken);
      const now = Date.now() / 1000;

      if (decoded.exp < now) {
        logoutAndRedirect();
        return;
      }

      setUser({
        user: decoded.sub,
        name: decoded.name,
        email: decoded.email,
        roles: decoded.roles,
      });

      setToken(storedToken);
    } catch {
      logoutAndRedirect();
    } finally {
      setLoading(false);
    }
  }, []);

  function logoutAndRedirect() {
    localStorage.removeItem("token");
    redirectToAngular();
  }

  function redirectToAngular() {
    window.location.href = ANGULAR_LOGIN_URL;
  }

  return {
    user,
    token,
    loading,
    isAuthenticated: !!user,
  };
}