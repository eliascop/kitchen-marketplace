import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductListComponent } from "../product-list/product-list.component";
import { SearchService } from '../../core/service/search.service';
import { CatalogService } from '../../core/service/catalog.service';
import { ProductService } from '../../core/service/product.service';
import { Product } from '../../core/model/product.model';
import { Subject, of, switchMap, takeUntil, map, distinctUntilChanged } from 'rxjs';
import { Catalog } from '../../core/model/catalog.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ProductListComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {

  selectedCatalog: Catalog | null = null;
  catalogs: Catalog[] = [];
  products: Product[] = [];

  loading = false;
  canLoadMore = false;

  page = 0;
  size = 10;
  totalPages = 0;

  isSearchMode = false;

  private destroy$ = new Subject<void>();

  constructor(
    private catalogService: CatalogService,
    private productService: ProductService,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.loadCatalogs();
    this.setupSearch();
  }

  loadCatalogs() {
    this.catalogService.getCatalogs().subscribe(resp => {
      this.catalogs = resp.data || [];
      if (this.catalogs.length > 0) {
        setTimeout(() => {
          this.selectCatalog(this.catalogs[0]);
        });
      }
    });
  }

  selectCatalog(catalog: Catalog) {
    if (!catalog || catalog === this.selectedCatalog) return;
  
    this.selectedCatalog = catalog;
    this.isSearchMode = false;
    this.canLoadMore = false;

    this.page = 0;
    this.products = [];
    this.loadProductsPage();
  }

  loadMore() {
    if (this.loading || !this.canLoadMore) return;
    this.page++;
    this.loadProductsPage();
  }

  loadProductsPage() {
    if (this.loading) return;
    if (!this.selectedCatalog) return;
  
    this.loading = true;

    this.productService
      .getProductsByCatalogSlug(this.page, this.size, this.selectedCatalog.slug)
      .subscribe({
        next: (resp) => {
          const pageData = resp.data;
          const items = pageData?.data ?? [];
          const totalPages = pageData?.totalPages ?? 0;
  
          if (this.page === 0) {
            this.products = items;
          } else {
            this.products = [...this.products, ...items];
          }
  
          this.totalPages = totalPages;
          this.canLoadMore = this.page < this.totalPages - 1;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }

  setupSearch() {
    this.searchService.query$
      .pipe(
        map(q => q?.term?.trim() ?? ''),
        distinctUntilChanged(),
        switchMap(term => {

          if (term === '') {
            this.isSearchMode = false;

            this.page = 0;
            this.products = [];
            this.selectCatalog(this.selectedCatalog!);

            return of(null);
          }

          this.isSearchMode = true;
          this.loading = true;

          return this.productService.searchProducts(this.page, this.size, term);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(resp => {
        if (!resp) return;

        const items = resp.data?.data ?? [];
        this.products = items;

        this.loading = false;
        this.canLoadMore = false;
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}