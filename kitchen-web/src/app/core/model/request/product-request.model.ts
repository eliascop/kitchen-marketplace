export interface ProductRequest {
    id?: number;
    name: string;
    description: string;
    basePrice: number;
    imageUrl?: string;
    catalog: {
      id: number;
      name: string;
    };
  }