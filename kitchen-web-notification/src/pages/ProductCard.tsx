import { Product } from "../models/Product";

interface ProductCardProps {
  product: Product ;
}

export default function ProductCard({ product }: ProductCardProps) {
  return (
    <div className="product-card">
      <img src={product.imageUrl} alt={product.name} width={150} />
      <h3>{product.name}</h3>
      <p>{product.description}</p>
      <p>
        <strong>R$ {product.basePrice.toFixed(2)}</strong>
      </p>
    </div>
  );
}