import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CouponComponent } from '../coupon/coupon.component';
import { Coupon } from '../../core/model/coupon.model';
import { CouponService } from '../../core/service/coupon.service';

@Component({
  selector: 'app-manage-coupons',
  standalone: true,
  imports: [CommonModule, CouponComponent],
  templateUrl: './manage-coupons.component.html',
  styleUrls: ['./manage-coupons.component.css']
})
export class ManageCouponsComponent implements OnInit {
  coupons: Coupon[] = [];
  loading = false;
  selectedCoupon?: Coupon;

  constructor(private couponService: CouponService) {}

  ngOnInit() {
    this.loadCoupons();
  }

  desactiveCopupon(couponId: string | undefined) {
    this.couponService.deactivateCoupon(couponId!).subscribe({
      next:(data) => {
        this.loadCoupons();
      }
    });
  }

  loadCoupons() {
    this.loading = true;
    this.couponService.getSellerCoupons().subscribe({
      next: (data) => {
        this.coupons = data.data!;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }

  editCoupon(coupon: Coupon) {
    this.selectedCoupon = coupon;
  }

  onSaved() {
    this.selectedCoupon = undefined;
    this.loadCoupons();
  }
}
