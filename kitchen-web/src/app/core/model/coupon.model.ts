export interface Coupon {
    id?: string;
    code: string;
    couponType: 'PERCENTUAL' | 'FIXED';
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
    createdAt?: string;
    updatedAt?: string;
    active?: boolean;
  }
  