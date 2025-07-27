import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Sku, Product } from '../../core/model/product.model';
import { ProductService } from '../../core/service/product.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css'
})
export class ProductDetailComponent implements OnInit {
  product: Product | undefined;
  selectedSku: Sku | undefined;

  constructor(private route: ActivatedRoute,
              private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const productId = params.get('id');
      if (productId) {
        this.loadProductDetails(+productId);
      }
    });
  }

  loadProductDetails(id: number): void {
    this.productService.getProductById(id).subscribe(data => {
       this.product = data.data!;
       if (this.product && this.product.skus.length > 0) {
         this.selectedSku = this.product.skus[0];
       }
     });
  }

  getAttributeValue(sku: Sku, attributeName: string): string | undefined {
    const attribute = sku.attributes.find(attr => attr.name === attributeName);
    return attribute ? attribute.attributeValue : undefined;
  }

  selectSku(sku: Sku): void {
    this.selectedSku = sku;
  }

  addToCart(): void {
    if (this.selectedSku) {
      console.log(`Adicionar ao carrinho: SKU ${this.selectedSku.id} (${this.selectedSku.sku})`);
    } else {
      console.warn('Nenhum SKU selecionado para adicionar ao carrinho.');
    }
  }

  goBack(): void {
    window.history.back();
  }
}