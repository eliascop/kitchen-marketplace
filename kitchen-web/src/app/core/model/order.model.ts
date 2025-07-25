import { Product } from './product.model';
import { User } from './user.model';

export class OrderItem {
  public id: number | null = null;
  public product: Product = new Product();
  public quantity: number = 0;
  public value: number = 0;

  constructor(init?: Partial<OrderItem>) {
    if (init) {
      Object.assign(this, init);
      if (init.product && !(init.product instanceof Product)) {
        this.product = new Product(init.product);
      }
    }
  }
}

export class Order {
  public id: number | null = null;
  public user: User = new User();
  public blink: boolean = false;
  public items: OrderItem[] = [];
  public total: number = 0;
  public status: string = 'PENDING';
  public creation: Date = new Date();
  public paymentId?: string;

  constructor(init?: Partial<Order>) {
    if (init) {
      Object.assign(this, init);
      if (init.user && !(init.user instanceof User)) {
        this.user = new User(init.user);
      }
      if (init.items) {
        this.items = init.items.map(item => item instanceof OrderItem ? item : new OrderItem(item));
      }
    }
  }

  calculateTotal(): void {
    this.total = this.items.reduce((acc, item) => acc + item.value, 0);
  }

  addItem(product: Product, quantity: number): void {
    const existing = this.items.find(i => i.product.id === product.id);
    if (existing) {
      existing.quantity += Number(quantity);
      existing.value = existing.product.price * existing.quantity;
    } else {
      this.items.push(new OrderItem({ product, quantity, value: product.price * quantity }));
    }
    this.calculateTotal();
  }

  removeItem(index: number): void {
    this.items.splice(index, 1);
    this.calculateTotal();
  }
}
