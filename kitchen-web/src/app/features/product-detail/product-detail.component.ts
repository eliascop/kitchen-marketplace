import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { take, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

import { Sku, Product } from '../../core/model/product.model';
import { Cart } from '../../core/model/cart.model';
import { ProductService } from '../../core/service/product.service';
import { CartService } from '../../core/service/cart.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css'
})
export class ProductDetailComponent implements OnInit {

  product: Product| null = null;
  selectedSku!: Sku;
  selectedQuantity: number = 1;
  cart!: Cart;
  loading = false;
  errorMessage = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly location: Location,
    private readonly productService: ProductService,
    private readonly cartService: CartService
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
        next: response => {
          if (response?.data) {
            this.product = response.data;
            if (this.product.skus.length > 0) {
              this.selectedSku = this.product.skus[0];
            }
          } else {
            this.errorMessage = 'Produto não encontrado.';
          }
          this.loading = false;
        },
        error: err => {
          console.error('Erro ao carregar produto:', err);
          this.errorMessage = 'Erro ao carregar os detalhes do produto.';
          this.loading = false;
        }
      });
  }

  getAttributeValue(sku: Sku, attributeName: string): string {
    return sku.attributes.find(attr => attr.name === attributeName)?.attributeValue ?? '';
  }

  selectSku(sku: Sku): void {
    this.selectedSku = sku;
  }

  addToCart(productId: number): void {
    if (!this.selectedSku) {
      console.warn('Nenhum SKU selecionado para adicionar ao carrinho.');
      return;
    }

    if (this.selectedQuantity == 0) {
      alert('Selecione a quantidade');
    }
    this.cartService.addToCart(productId, this.selectedSku.sku, this.selectedQuantity)
      .pipe(take(1))
      .subscribe({
        next: data => {
          this.cart = data.data!;
          console.log('Produto adicionado ao carrinho:', this.selectedSku);
        },
        error: err => {
          console.error('Erro ao adicionar produto ao carrinho:', err);
        }
      });
  }

  goBack(): void {
    this.location.back();
  }
}
