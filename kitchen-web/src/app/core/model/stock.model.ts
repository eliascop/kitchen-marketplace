export class Stock {
  public totalQuantity: number = 0;
  public reservedQuantity: number = 0;
  public soldQuantity: number = 0;
  public availableQuantity: number = 0;
}

export interface StockHistory{
  id: number;
  createdAt: string;
  eventType: string;
  sku: string;
  soldQuantity: number;
  reservedQuantity: number;
  totalQuantity: number;
}
