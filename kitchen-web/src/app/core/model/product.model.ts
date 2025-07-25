  export class Product {
    public id: number | null = null;
    public name: string = '';
    public description: string = '';
    public type: string = '';
    public catalogName: string = '';
    public categoryName: string = '';
    public active: boolean = false;
    public price: number = 0;
  
    constructor(init?: Partial<Product>) {
      if (init) {
        Object.assign(this, init);
      }
    }
  }