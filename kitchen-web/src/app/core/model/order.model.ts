import { Payment } from './payment.model';
import { Product } from './product.model';
import { Seller, User } from './user.model';

export class OrderItem {
  public id: number | null = null;
  public product: Product = new Product();
  public seller: Seller | null = null;
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
  public payment: Payment | null = null;
  public shippingId: number = 0;
  
  public shippingAddressId: number = 0;
  public billingAddressId: number = 0;

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
}
