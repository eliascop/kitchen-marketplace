import { Address } from "./address.model";

export class User{
    public id: number | null = null;
    public login: string = '';
    public name: string = '';
    public phone: string = '';
    public email: string = '';
    public roles: string[] = [];
    public addresses: Address[] = [];

    constructor(init?: Partial<User>) {
        Object.assign(this, init);
    }

    get shippingAddress(): Address | undefined {
      return this.addresses.find(addr => addr.type === 'SHIPPING');
    }
  
    get billingAddress(): Address | undefined {
      return this.addresses.find(addr => addr.type === 'BILLING');
    }

    get isSeller(): boolean {
      return this.roles.includes('ROLE_SELLER');
    }
}
