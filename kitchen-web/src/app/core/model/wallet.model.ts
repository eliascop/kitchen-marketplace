export interface Wallet {
    id: number;
    balance: number;
    updatedAt: string;
  }
  
  export interface WalletTransaction {
    id: number;
    amount: number;
    type: 'CREDIT' | 'DEBIT';
    description: string;
    createdAt: string;
    status: string;
  }
  
  export interface CreditRequest {
    amount: number;
    description: string;
  }
  
  export interface DebitRequest {
    amount: number;
    description: string;
  }
  