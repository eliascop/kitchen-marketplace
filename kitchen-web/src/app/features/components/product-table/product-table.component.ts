import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Product } from '../../../core/model/product.model';
import { CurrencyFormatterPipe } from "../../../core/pipes/currency-input.pipe";

@Component({
  selector: 'app-product-table',
  standalone: true,
  imports: [CommonModule, CurrencyFormatterPipe],
  templateUrl: './product-table.component.html',
  styleUrls: ['./product-table.component.scss']
})
export class ProductTableComponent {
  @Input() products: Product[] = [];
  @Output() remove = new EventEmitter<number>();
  @Output() detail = new EventEmitter<Product>();
  @Output() add = new EventEmitter<void>();

  sortField: keyof Product | '' = '';
  sortDirection: 'asc' | 'desc' = 'asc';

  currentPage = 1;
  itemsPerPage = 10;

  get sortedProducts(): Product[] {
    const productsCopy = [...this.products];
    const field = this.sortField;
  
    if (!field) return productsCopy;
  
    return productsCopy.sort((a, b) => {
      const valueA = a[field] as string | number;
      const valueB = b[field] as string | number;
  
      let compare = 0;
  
      if (typeof valueA === 'string' && typeof valueB === 'string') {
        compare = valueA.localeCompare(valueB);
      } else if (typeof valueA === 'number' && typeof valueB === 'number') {
        compare = valueA - valueB;
      }
  
      return this.sortDirection === 'asc' ? compare : -compare;
    });
  }

  sortBy(field: keyof Product) {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
  }

  get totalPages(): number {
    return Math.ceil(this.sortedProducts.length / this.itemsPerPage);
  }

  get paginatedProducts(): Product[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.sortedProducts.slice(start, end);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  detailProduct(product: Product) {
    this.detail.emit(product);
  }

}
