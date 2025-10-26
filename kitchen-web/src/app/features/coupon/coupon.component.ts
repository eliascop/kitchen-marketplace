import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Coupon } from '../../core/model/coupon.model';
import { CouponService } from '../../core/service/coupon.service';

@Component({
  selector: 'app-coupon',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './coupon.component.html',
  styleUrls: ['./coupon.component.css']
})
export class CouponComponent {
  @Input() coupon?: Coupon;
  @Output() saved = new EventEmitter<void>();

  form: Coupon = {
    code: '',
    type: 'PERCENTUAL',
    amount: 0,
    scope: 'SELLER',
    visibility: 'PUBLIC',
    active: true
  };

  constructor(private couponService: CouponService) {}

  ngOnChanges() {
    if (this.coupon) {
      this.form = { ...this.coupon };
    }
  }

  save() {
    const request = this.form.id
      ? this.couponService.updateCoupon(this.form.id!, this.form)
      : this.couponService.createCoupon(this.form);

    request.subscribe(() => this.saved.emit());
  }
}
