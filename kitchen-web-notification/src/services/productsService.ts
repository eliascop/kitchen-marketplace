import apiClient from "../api/apiClient";
import { API_BASE } from "../config/api";
import { PaginatedResponse } from "../models/PaginatedResponse";
import { Product } from "../models/Product";

export async function getProducts(page: number = 0, size: number = 5) {
  const response = await apiClient.get<PaginatedResponse<Product>>(
    `${API_BASE.PRODUCT}?page=${page}&size=${size}`
  );
  return response.data;
}