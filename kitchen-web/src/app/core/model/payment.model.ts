export interface Payment {
    id: number;
    method: string;
    status: string;
    amount: number;
    createdAt: Date;
  }
