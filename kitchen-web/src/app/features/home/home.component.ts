import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCarouselComponent } from '../product-carousel/product-carousel.component';
import { Product } from '../../core/model/product.model';
import { ProductService } from '../../core/service/product.service';
import { CatalogService } from '../../core/service/catalog.service';
import { SearchService } from '../../core/service/search.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ProductCarouselComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  catalogs: any[] = [];
  catalogedProducts: Product[] = [];
  allProducts: Product[] = [];
  selectedCatalogSlug: string = '';

  constructor(
    private catalogService: CatalogService,
    private searchService: SearchService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.loadAllProducts();
    this.loadCatalogs();

    this.searchService.searchTerm$.subscribe(term => {
      if (term) {
        this.filterProducts(term);
      } else {
        if (this.selectedCatalogSlug) {
          this.selectCatalogBySlug(this.selectedCatalogSlug);
        }
      }
    });
  }

  loadCatalogs() {
    this.catalogService.getCatalogs().subscribe(resp => {
      this.catalogs = resp.data ?? [];
      if (this.catalogs.length > 0) {
        this.selectCatalog(this.catalogs[0]);
      }
    });
  }

  loadAllProducts() {
    this.productService.getProducts().subscribe(resp => {
      this.allProducts = resp.data ?? [];
    });
  }

  selectCatalog(catalog: { name: string; slug: string }) {
    this.selectedCatalogSlug = catalog.slug;
    this.catalogService.getProductsByCatalog(catalog.slug).subscribe(resp => {
      this.catalogedProducts = resp.data ?? [];
    });
  }

  selectCatalogBySlug(slug: string) {
    this.catalogService.getProductsByCatalog(slug).subscribe(resp => {
      this.catalogedProducts = resp.data ?? [];
    });
  }

  filterProducts(term: string) {
    this.catalogedProducts = this.allProducts.filter(p =>
      p.name.toLowerCase().includes(term.toLowerCase())
    );
  }
}
