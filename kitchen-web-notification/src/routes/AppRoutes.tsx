import { Routes, Route } from "react-router-dom";
import HomePage from "../pages/HomePage";
import Navbar from "../components/Navbar";
import ProductsPage from "../pages/ProductsPage";
import NotificationsPage from "../pages/NotificationsPage";

export default function AppRoutes() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/details" element={<NotificationsPage />} />
      </Routes>
    </>
  );
}