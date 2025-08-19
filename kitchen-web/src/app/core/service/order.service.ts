import { Injectable } from '@angular/core';
import { DataService } from './data.service';
import { environment } from '../../../environments/environment.dev';
import { Order } from '../model/order.model';
import { AuthService } from './auth.service';
import { ServiceResponse } from './model/http-options-request.model';
import { OrderTracking } from '../model/order-tracking.model';
import { HttpParams } from '@angular/common/http';

export const ORDER_SERVICE_REST = environment.ORDER_REST_SERVICE;

@Injectable({
  providedIn: 'root',
})
export class OrderService {

  constructor(private dataService: DataService, private authService: AuthService) {}
        
  getOrders() {
    const params = {userId: this.authService.currentUserId!};

    return this.dataService.get<Order[]>({
      url: `${ORDER_SERVICE_REST}/search`,
      params
    });
  }

  getOrderById(orderId: number): ServiceResponse<Order> {
    return this.dataService.get<Order>({
      url: `${ORDER_SERVICE_REST}/${orderId}`
    });
  }

  checkout(order: Order){
    const params = new HttpParams()
    .set('shippingAddressId', order.shippingAddressId)
    .set('billingAddressId', order.billingAddressId);

    return this.dataService.post<OrderTracking>({
      url: `${ORDER_SERVICE_REST}/checkout`,
      params
    }); 
  }
  
}
