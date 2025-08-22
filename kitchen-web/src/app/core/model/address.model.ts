export class Address {
  id: number = 0;
  type?: 'SHIPPING' | 'BILLING';
  zipCode: string = '';
  street: string = '';
  number?: string;
  complement?: string;
  district: string = '';
  city: string = '';
  state: string = '';
  country: string = 'Brazil';

  constructor(init?: Partial<Address>) {
    Object.assign(this, init);
  }
}