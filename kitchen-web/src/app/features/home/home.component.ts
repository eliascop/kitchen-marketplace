import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/service/auth.service';
import { CommonModule } from '@angular/common';

import { ProductCarouselComponent } from '../product-carousel/product-carousel.component';
import { Product } from '../../core/model/product.model';
import { ProductService } from '../../core/service/product.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ProductCarouselComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  userProfile: number | undefined;
  latestAutumnProducts: Product[] = [];
  summerProducts: Product[] = [];
  promotionProducts: Product[] = [];
  bargainProducts: Product[] = [];

  constructor(private authService: AuthService, 
              private productService: ProductService) {
    this.userProfile = authService.currentUserId!;
  }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe(data => {
      this.summerProducts = data.data!;
    });
  }
}