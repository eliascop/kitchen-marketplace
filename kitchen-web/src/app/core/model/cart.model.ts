import { Product } from "./product.model";

export class CartItem {
    public id: number | null = null;
    public product: Product = new Product();
    public quantity: number = 0;
    public value: number = 0;
  
    constructor(init?: Partial<CartItem>) {
      if (init) {
        Object.assign(this, init);
        if (init.product && !(init.product instanceof Product)) {
          this.product = new Product(init.product);
        }
      }
    }
}

export class Cart {
    id: number = 0;
    userId: number = 0;
    items: CartItem[] = [];
    creation: Date = new Date();
    cartTotal: number = 0;
  
    constructor(init?: Partial<Cart>) {
        if (init) {
          Object.assign(this, init);
          if (init.items) {
            this.items = init.items.map(item => item instanceof CartItem ? item : new CartItem(item));
          }
        }
      }


  calculateTotal(): void {
    this.cartTotal = this.items.reduce((acc, item) => acc + item.value, 0);
  }

  addItem(product: Product, quantity: number): void {
    const existing = this.items.find(i => i.product.id === product.id);
    if (existing) {
      existing.quantity += Number(quantity);
      existing.value = existing.product.price * existing.quantity;
    } else {
      this.items.push(new CartItem({ product, quantity, value: product.price * quantity }));
    }
    this.calculateTotal();
  }

  removeItem(index: number): void {
    this.items.splice(index, 1);
    this.calculateTotal();
  }
}


