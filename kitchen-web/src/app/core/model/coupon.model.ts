export interface Coupon {
    id?: string;
    code: string;
    type: 'PERCENTUAL' | 'VALOR_FIXO';
    amount: number;
    scope: 'SELLER' | 'MARKETPLACE';
    visibility: 'PUBLIC' | 'PRIVATE';
    sellerId?: number;
    issuerId?: number;
    minOrderAmount?: number;
    maxDiscountAmount?: number;
    usageLimitTotal?: number;
    usageLimitPerBuyer?: number;
    usageCountTotal?: number;
    startsAt?: string;
    expiresAt?: string;
    active?: boolean;
    createdAt?: string;
    updatedAt?: string;
  }
  