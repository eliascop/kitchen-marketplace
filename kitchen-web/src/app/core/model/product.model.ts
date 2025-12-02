import { Seller } from "./user.model";

export class Product {
  public id: number | null = null;
  public name: string = '';
  public description: string = '';
  public imageUrl: string = '';
  public price: number = 0;
  public catalog: string = '';
  public category: string = '';
  public productStatus: string = '';
  public skus: Sku[] = [];
  public seller: Seller = new Seller;

  constructor(init?: Partial<Product>) {
    if (init) {
      Object.assign(this, init);
    }
  }
}

export class Attribute {
  public attributeName: string = '';
  public attributeValue: string = '';
}

export class Stock {
  public totalQuantity: number = 0;
  public reservedQuantity: number = 0;
  public soldQuantity: number = 0;
  public availableQuantity: number = 0;
}

export interface Sku {
  id: number;
  sku: string;
  price: number;
  stock: Stock;
  attributes: Attribute[];
  stockHistory: StockHistory[];
  expanded?: boolean;
}

export interface StockHistory{
  id: number;
  createdAt: string;
  eventType: string;
  sku: string;
  soldQuantity: number;
  reservedQuantity: number;
  totalQuantity: number;
}

export interface SellerStore {
  id: number;
  storeName: string;
  shippingId: number;
}
