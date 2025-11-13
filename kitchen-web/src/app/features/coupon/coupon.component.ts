import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Coupon } from '../../core/model/coupon.model';
import { CouponService } from '../../core/service/coupon.service';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';
import { Router } from '@angular/router';
import { ToastService } from '../../core/service/toast.service';

@Component({
  selector: 'app-coupon',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CurrencyInputComponent],
  templateUrl: './coupon.component.html',
  styleUrls: ['./coupon.component.css']
})
export class CouponComponent implements OnInit{
  @Input() coupon?: Coupon;
  today = new Date();

  couponForm!: FormGroup;
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private couponService: CouponService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit():void {
    this.createForm();
  }

  createForm() {
    this.couponForm = this.fb.group({
      id: [null],
      code: ['', Validators.required],
      couponType: [''],
      visibility: [''],
      scope: [''],
      amount: [null, Validators.required],
      minOrderAmount: [0, Validators.required],
      maxDiscountAmount: [0, Validators.required],
      usageLimitTotal: [''],
      usageCountTotal: [''],
      startsAt: ['', Validators.required],
      expiresAt: ['', Validators.required]
    });
  }

  onSubmit() {
    if(this.couponForm.invalid){
      this.couponForm.markAllAsTouched();
      return;
    }
    const coupon: any = this.couponForm.value;
    const request = this.isEditing
      ? this.couponService.updateCoupon(coupon.id, coupon)
      : this.couponService.createCoupon(coupon);

    request.subscribe({
      next: () => {
        this.toast.show(
          this.isEditing
            ? 'Coupon atualizado com sucesso!'
            : 'Coupon cadastrado com sucesso.'
        );
        this.router.navigate(['/manage-coupons']);
      },
      error: (err) => {
        this.toast.show('Ocorreu um erro ao salvar o coupon.');
        console.error(err);
      }
    });
  }

  cancel() {
    this.router.navigate(['/manage-coupons']);
  }
}
