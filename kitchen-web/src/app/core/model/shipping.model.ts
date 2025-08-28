import { Seller } from "./user.model";

export class Shipping {
    public id: number = 0;
    public carrier: string = '';
    public method: string = '';
    public cost: number = 0.0;
    public estimatedDays: number = 0;
    public seller: Seller = new Seller;
}
