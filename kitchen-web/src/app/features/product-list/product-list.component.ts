import { CommonModule } from '@angular/common';
import { Component, AfterViewInit, ViewChild, ElementRef, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { Product } from '../../core/model/product.model';
import { Router } from '@angular/router';
import { Catalog } from '../../core/model/catalog.model';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, CurrencyFormatterPipe],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements AfterViewInit, OnDestroy {

  @Input() catalog: Catalog | null = null;
  @Input() products: Product[] = [];
  @Input() loading = false;
  @Input() canLoadMore = false;

  @Output() loadMore = new EventEmitter<void>();

  @ViewChild('anchor', { static: false }) anchor!: ElementRef<HTMLElement>;

  private observer?: IntersectionObserver;

  constructor(private router: Router) {}

  ngAfterViewInit() {
    this.observer = new IntersectionObserver(
      entries => {
        const entry = entries[0];

        if (entry.isIntersecting && this.canLoadMore && !this.loading) {
          this.loadMore.emit();
        }
      },
      {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
      }
    );

    if (this.anchor?.nativeElement) {
      this.observer.observe(this.anchor.nativeElement);
    }
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }

  viewDetails(product: Product) {
    this.router.navigate([`/product/${product.id}`]);
  }
}