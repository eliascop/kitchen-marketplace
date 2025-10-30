import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CatalogService } from '../../core/service/catalog.service';
import { SearchService } from '../../core/service/search.service';
import { ProductListComponent } from "../product-list/product-list.component";
import { distinctUntilChanged, map, of, Subject, switchMap, takeUntil } from 'rxjs';
import { ProductService } from '../../core/service/product.service';
import { Product } from '../../core/model/product.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ProductListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit, OnDestroy {
  catalogs: any[] = [];
  products: Product[] = [];
  total = 0;
  page = 0;
  size = 20;
  isSearchMode = false;

  private destroy$ = new Subject<void>();
  selectedCatalogSlug = '';

  constructor(
    private catalogService: CatalogService,
    private productService: ProductService,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.loadCatalogs();
    this.setupSearchListener();
  }

  loadCatalogs() {
    this.catalogService.getCatalogs().subscribe(resp => {
      this.catalogs = resp.data ?? [];
      if (this.catalogs.length > 0) {
        this.selectCatalog(this.catalogs[0]);
      }
    });
  }

  selectCatalog(catalog: { name: string; slug: string }) {
    this.selectedCatalogSlug = catalog.slug;
    this.isSearchMode = false;
    this.loadProductsByCatalog(catalog.slug);
  }

  loadProductsByCatalog(slug: string) {
    this.productService.getProductsByCatalogSlug(this.page, this.size, slug)
      .subscribe(resp => {
        this.products = resp.data?.data ?? [];
        this.total = resp.data?.totalElements ?? 0;
      });
  }

  setupSearchListener() {
    this.searchService.query$
      .pipe(
        map(q => q?.term?.trim() ?? ''),
        distinctUntilChanged(),
        switchMap(term => {
          if (term.length === 0) {
            this.isSearchMode = false;
            this.loadCatalogs();
            return of({ data: { data: [] } });
          }
          this.isSearchMode = true;
          return this.productService.searchProducts(this.page, this.size, term);
        }),
  
        takeUntil(this.destroy$)
      )
      .subscribe(resp => {
        if (this.isSearchMode) {
          this.products = resp.data?.data ?? [];
        }
      });
  }
      

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
