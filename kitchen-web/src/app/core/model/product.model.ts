import { Seller } from "./user.model";
import { Category } from "./category.model";
import { Catalog } from "./catalog.model";
import { Stock, StockHistory } from "./stock.model";

export class Product {
  public id: number | null = null;
  public name: string = '';
  public description: string = '';
  public imageUrl: string = '';
  public basePrice: number = 0;
  public catalog: Catalog = new Catalog;
  public category: Category = new Category;
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

export interface Sku {
  id: number;
  sku: string;
  price: number;
  stock: Stock;
  attributes: Attribute[];
  stockHistory: StockHistory[];
  expanded?: boolean;
}