import { Seller } from "./user.model";

export class Product {
  public id: number | null = null;
  public name: string = '';
  public description: string = '';
  public imageUrl: string = '';
  public price: number = 0;
  public catalog: string = '';
  public category: string = '';
  public active: boolean = false;
  public skus: Sku[] = [];
  public seller: Seller = new Seller;

  constructor(init?: Partial<Product>) {
    if (init) {
      Object.assign(this, init);
    }
  }
}

export interface Attribute {
  name: string;
  attributeValue: string;
}

export interface Stock {
  totalQuantity: number;
  reservedQuantity: number;
  soldQuantity: number;
  availableQuantity: number;
}

export interface Sku {
  id: number;
  sku: string;
  price: number;
  stock: Stock;
  attributes: Attribute[];
}

export interface SellerStore {
  id: number;
  storeName: string;
  shippingId: number;
}
