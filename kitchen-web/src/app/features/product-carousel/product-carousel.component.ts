import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { Product } from '../../core/model/product.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-carousel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-carousel.component.html',
  styleUrl: './product-carousel.component.css'
})
export class ProductCarouselComponent implements OnInit {
  @Input() title: string = '';
  @Input() products: Product[] = [];

  constructor(private router: Router) { }

  ngOnInit(): void { }

  viewProductDetails(productId: number): void {
    this.router.navigate([`/product/${productId}`]);
  }
}