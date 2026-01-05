import AppRoutes from "./routes/AppRoutes";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import { NotificationProvider } from "./context/NotificationContext";

const BASENAME = "/web-notification";

export default function App() {
  return (
    <BrowserRouter basename={BASENAME}>
      <NotificationProvider>
        <AppRoutes />
      </NotificationProvider>
    </BrowserRouter>
  );
}