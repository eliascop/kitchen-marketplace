import { Injectable } from '@angular/core';
import { Coupon } from '../model/coupon.model';
import { environment } from '../../../environments/environment.dev';
import { DataService } from './data.service';
import { ServiceResponse } from './model/http-options-request.model';

export const COUPON_SERVICE_REST = environment.COUPON_REST_SERVICE;

@Injectable({
  providedIn: 'root'
})
export class CouponService {

  constructor(private dataService: DataService) {}   

  getSellerCoupons(): ServiceResponse<Coupon[]> {
    return this.dataService.get<Coupon[]>({
        url: `${COUPON_SERVICE_REST}/seller`
    });
  }

  createCoupon(coupon: Coupon): ServiceResponse<Coupon> {
    return this.dataService.post<Coupon>({
        url: COUPON_SERVICE_REST,
        body: coupon,
    });
  }

  updateCoupon(id: string, coupon: Coupon): ServiceResponse<Coupon> {
    return this.dataService.put<Coupon>({
        url: `${COUPON_SERVICE_REST}/${id}`,
        body: coupon,
    });
  }

  deactivateCoupon(id: string):ServiceResponse<Coupon> {
    return this.dataService.patch<Coupon>({
      url: `${COUPON_SERVICE_REST}/${id}/deactivate`
    });
  }
}
