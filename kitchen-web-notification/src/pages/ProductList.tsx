import { useEffect, useState } from "react";
import Pagination from "./Pagination";
import { Product } from "../models/Product";
import { getProducts } from "../services/productsService";
import ProductCard from "./ProductCard";

export default function ProductList() {
  const [products, setProducts] = useState<Product[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const loadProducts = async (page: number) => {
    const response = await getProducts(page, 5);
    setProducts(response.data);
    setTotalPages(response.totalPages);
  };

  useEffect(() => {
    loadProducts(page);
  }, [page]);

  return (
    <div>
      <div className="products-container">
        {products.map((p) => (
          <ProductCard key={p.id} product={p} />
        ))}
      </div>

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}