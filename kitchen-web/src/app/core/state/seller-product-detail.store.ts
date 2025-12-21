import { createStore, withProps } from '@ngneat/elf';
import { Product } from '../model/product.model';

export interface SellerProductDetailState {
  product: Product | null;
}

export const sellerProductDetailStore = createStore(
  { name: 'seller-product-detail' },withProps<SellerProductDetailState>({product: null})
);