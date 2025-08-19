import { Product } from "./product.model";

export class CartItem {
  public id: number | null = null;
  public product: Product = new Product();
  public quantity: number = 0;

  constructor(init?: Partial<CartItem>) {
    if (init) {
      Object.assign(this, init);
      if (init.product && !(init.product instanceof Product)) {
        this.product = new Product(init.product);
      }
    }
  }

  get value(): number {
    const price = Number(this.product?.price ?? 0);
    const qty = Number(this.quantity ?? 0);
    return price * qty;
  }
}

export class Cart {
  id: number = 0;
  userId: number = 0;
  items: CartItem[] = [];
  totalItems: number = 0; 
  creation: Date = new Date();
  cartTotal: number = 0;
  shippingAddressId: number = 0;
  billingAddressId: number = 0;

  constructor(init?: Partial<Cart>) {
    if (init) {
      Object.assign(this, init);

      if (init.items) {
        this.items = init.items.map(i => i instanceof CartItem ? i : new CartItem(i));
      }

      if (init.creation && !(init.creation instanceof Date)) {
        this.creation = new Date(init.creation as any);
      }
    }

    this.recalculateTotals();
  }

  recalculateTotals(): void {
    this.cartTotal = this.items.reduce((acc, item) => acc + item.value, 0);
    this.totalItems = this.items.reduce((acc, item) => acc + Number(item.quantity ?? 0), 0);
  }

  addItem(product: Product, quantity: number): void {
    const qty = Number(quantity) || 0;
    if (!product || qty <= 0) return;

    const existing = this.items.find(i => i.product?.id === product.id);
    if (existing) {
      existing.quantity += qty;
    } else {
      this.items.push(new CartItem({ product, quantity: qty }));
    }
    this.recalculateTotals();
  }

  setItemQuantity(productId: number, quantity: number): void {
    const item = this.items.find(i => i.product?.id === productId);
    if (!item) return;

    const qty = Math.max(0, Number(quantity) || 0);
    item.quantity = qty;

    if (item.quantity === 0) {
      this.items = this.items.filter(i => i !== item);
    }
    this.recalculateTotals();
  }

  removeItem(index: number): void {
    if (index < 0 || index >= this.items.length) return;
    this.items.splice(index, 1);
    this.recalculateTotals();
  }

  removeItemByProductId(productId: number): void {
    this.items = this.items.filter(i => i.product?.id !== productId);
    this.recalculateTotals();
  }

  clear(): void {
    this.items = [];
    this.recalculateTotals();
  }
}
