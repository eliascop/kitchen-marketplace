import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App";
import { setupAuthInterceptor } from "./api/authInterceptor";
import { environment } from "./config/environment";

setupAuthInterceptor(() => {
  window.location.href = environment.apiBaseUrl;
});

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);