import { Payment } from './payment.model';
import { Product } from './product.model';
import { Seller, User } from './user.model';

export class OrderItem {
  id: number | null = null;
  sku = '';
  price = 0;
  productName = '';
  storeName = '';
  quantity = 0;
  value = 0;

  constructor(init?: Partial<OrderItem>) {
    Object.assign(this, init);
  }
}

export class Order {
  id:number | null = null;
  user: User = new User();
  blink = false;
  items: OrderItem[] = [];
  total = 0;
  status = 'PENDING';
  creation: Date = new Date();
  payment: Payment | null = null;
  shippingId = 0;
  
  shippingAddressId = 0;
  billingAddressId = 0;

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
