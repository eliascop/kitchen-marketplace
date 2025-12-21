import { Product } from "../model/product.model";
import { sellerProductDetailStore } from "./seller-product-detail.store";

export const setSellerProductDetail = (product: Product) =>
    sellerProductDetailStore.update(state => ({
      ...state, product
    }));
  
export const clearSellerProductDetail = () => sellerProductDetailStore.reset();