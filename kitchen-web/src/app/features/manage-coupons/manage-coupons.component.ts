import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Coupon } from '../../core/model/coupon.model';
import { CouponService } from '../../core/service/coupon.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { FormatDateTimePipe } from "../../core/pipes/format-date-time.pipe";
import { GenericTableComponent, TableColumn } from "../../shared/components/generic-table/generic-table.component";

@Component({
  selector: 'app-manage-coupons',
  standalone: true,
  imports: [CommonModule, FormatDateTimePipe, GenericTableComponent],
  templateUrl: './manage-coupons.component.html',
  styleUrls: ['./manage-coupons.component.css']
})
export class ManageCouponsComponent implements OnInit {
  coupons: Coupon[] = [];
  loading = false;
  selectedCoupon?: Coupon;

  totalItems = 0;
  totalPages = 0;
  pageSize = 10;
  page = 1; 

  sortField: keyof Coupon | '' = '';
  sortDir: 'asc' | 'desc' = 'asc';

  columns: TableColumn<Coupon>[] = [
    { key: 'code', label: 'Código', sortable: true },
    { key: 'couponType', label: 'Tipo', sortable: true },
    { key: 'visibility', label: 'Visibilidade' },
    {
      key: 'amount',
      label: 'Valor',
      formatter: (value, row) =>
        row.couponType === 'FIXED' ? `R$ ${value.toFixed(2)}` : `${value}%` 
    },
    { key: 'usageCountTotal', label: 'Limite de Utilização' },
    { key: 'expiresAt', label: 'Válido até', formatter: (v) => v ? new Date(v).toLocaleDateString() : 'Indeterminado' },
    { key: 'active', label: 'Status', formatter: v => v ? 'Ativo' : 'Inativo' }
  ];

  constructor(private couponService: CouponService,
    private router: Router) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.ngOnInit());
    }

  ngOnInit() {
    this.loadCoupons();
  }

  loadCoupons() {
    this.loading = true;
    const page0 = this.page - 1;
    this.couponService.getSellerCoupons(page0, this.pageSize, this.sortField, this.sortDir)
    .subscribe({
      next: (resp) => {
        this.coupons = resp.data?.data ?? [];

        this.totalItems = resp?.data?.totalElements ?? 0;
        this.totalPages = resp?.data?.totalPages ?? 0;

        const backendPage = resp?.data?.page ?? page0;
        this.page = backendPage + 1;
      },
      error: () => {
        this.coupons = [];
        this.totalItems = 0;
        this.totalPages = 0;
      },
      complete: () => this.loading = false
    });
  }

  newCoupon() {
    this.router.navigate(['/coupons']);
  }

  onPaginate({ page, pageSize }: { page: number; pageSize: number }) {
    this.page = page;
    this.pageSize = pageSize;
    this.loadCoupons();
  }

  onSort(e: { field: keyof Coupon | null; direction: 'asc' | 'desc' | null }) {
    if (!e.field || !e.direction) {
      this.sortField = '' as any;
      this.sortDir = 'asc';
    } else {
      this.sortField = e.field;
      this.sortDir = e.direction;
    }
    this.page = 1;
    this.loadCoupons();
  }

  onEdit(coupon: Coupon) {
    this.selectedCoupon = coupon;
  }
  onActivate(coupon: Coupon) {
    this.couponService.deactivateCoupon(coupon.id!).subscribe({
      next:(data) => {
        this.loadCoupons();
      }
    });
  }
}
