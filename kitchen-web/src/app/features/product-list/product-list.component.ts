import { CommonModule } from '@angular/common';
import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Product } from '../../core/model/product.model';
import { ProductService } from '../../core/service/product.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit, AfterViewInit, OnChanges {
  @Input() catalogSlug?: string;
  @Input() catalogs?: any[];

  products: Product[] = [];
  currentPage = 0;
  size = 12;
  totalPages = 0;
  loading = false;
  allLoaded = false;

  @ViewChild('anchor', { static: false }) anchor!: ElementRef<HTMLElement>;

  private observer!: IntersectionObserver;

  constructor(private productService: ProductService,
              private router: Router) {}

  ngOnInit() {
    console.log(this.catalogs);
    this.loadProducts();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['catalogSlug'] && !changes['catalogSlug'].firstChange) {
      this.products = [];
      this.currentPage = 0;
      this.allLoaded = false;
      this.loadProducts();
    }
  }

  ngAfterViewInit() {
    this.observer = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && !this.loading && !this.allLoaded) {
        this.loadProducts();
      }
    }, { threshold: 1 });

    if (this.anchor) {
      this.observer.observe(this.anchor.nativeElement);
    }
  }

  loadProducts() {
    if (this.allLoaded) return;

    this.loading = true;
    this.productService.getProducts(this.currentPage, this.size, this.catalogSlug).subscribe({
      next: (res) => {
        if (!res.data || res.data.data.length === 0) {
          this.allLoaded = true;
        } else {
          this.products = [...this.products, ...res.data.data];
          this.currentPage++;
          this.totalPages = res.data.totalPages;
        }
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
  viewDetails(product: Product) {
    this.router.navigate([`/product/${product.id}`]);
  }
  
}
