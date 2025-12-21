import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { select } from '@ngneat/elf';

import { Product } from '../../core/model/product.model';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";
import { FormatDateTimePipe } from "../../core/pipes/format-date-time.pipe";
import { StockStatusPipe } from "../../core/pipes/stock-status.pipe";
import { sellerProductDetailStore } from '../../core/state/seller-product-detail.store';

@Component({
  selector: 'app-seller-product-details',
  standalone: true,
  imports: [CommonModule, CurrencyFormatterPipe, FormatDateTimePipe, StockStatusPipe],
  templateUrl: './seller-product-details.component.html',
  styleUrls: ['./seller-product-details.component.css']
})
export class SellerProductDetailsComponent implements OnInit {

  product$!: Observable<Product | null>;
  loading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.product$ = sellerProductDetailStore.pipe(
      select(state => state.product)
    );
    this.loading = false;
  }

  toggleHistory(sku: any) {
    sku.expanded = !sku.expanded;
  }

  goBack() {
    const page = this.route.snapshot.queryParams['page'] ?? 0;
    const size = this.route.snapshot.queryParams['pageSize'] ?? 10;
    this.router.navigate(['/seller-products'],{ queryParams: {page, size }});
  }

  editProduct() {
    this.product$.pipe(take(1)).subscribe(product => {
      this.router.navigate(['/new-product'], { queryParams: { id: product?.id } });
    });
  }

  manageSkus() {
    this.product$.pipe(take(1)).subscribe(product => {
      this.router.navigate([`/seller-products/${product?.id}/skus`]);
    });
  }
}