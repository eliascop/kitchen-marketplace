import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';

import { Product } from '../../core/model/product.model';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";
import { FormatDateTimePipe } from "../../core/pipes/format-date-time.pipe";
import { StockStatusPipe } from "../../core/pipes/stock-status.pipe";
import { ProductService } from '../../core/service/product.service';

@Component({
  selector: 'app-seller-product-details',
  standalone: true,
  imports: [CommonModule, CurrencyFormatterPipe, FormatDateTimePipe, StockStatusPipe],
  templateUrl: './seller-product-details.component.html',
  styleUrl: './seller-product-details.component.css'
})
export class SellerProductDetailsComponent implements OnInit {

  product!: Product;
  loading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly productService: ProductService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.loading = true;

    this.route.paramMap
      .pipe(
        take(1),
        switchMap(params => {
          const productId = params.get('id');
          return productId ? this.productService.getProductById(+productId) : of(null);
        })
      )
      .subscribe({
        next: (res) => {
          this.product = res!.data!;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }

  toggleHistory(sku: any) {
    sku.expanded = !sku.expanded;
  }

  goBack() {
    this.router.navigate(['/seller-products']);
  }

  editProduct() {
    this.router.navigate(['/new-product'], { queryParams: { id: this.product?.id } });
  }

  manageSkus() {
    this.router.navigate([`/seller-products/${this.product?.id}/skus`]);
  }
}