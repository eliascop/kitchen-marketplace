import { AxiosError } from "axios";
import apiClient from "./apiClient";

let isInterceptorRegistered = false;

export function setupAuthInterceptor(onUnauthorized: () => void) {

  if (isInterceptorRegistered) return;

  isInterceptorRegistered = true;

  apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  apiClient.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      if (error.response?.status === 401) {
        localStorage.removeItem("token");
        onUnauthorized();
      }
      return Promise.reject(error);
    }
  );
}
