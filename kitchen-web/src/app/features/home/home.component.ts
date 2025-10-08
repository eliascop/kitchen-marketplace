import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CatalogService } from '../../core/service/catalog.service';
import { SearchService } from '../../core/service/search.service';
import { ProductListComponent } from "../product-list/product-list.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ProductListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  catalogs: any[] = [];
  selectedCatalogSlug: string = '';

  constructor(
    private catalogService: CatalogService,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.loadCatalogs();

    this.searchService.searchTerm$.subscribe(term => {
      if (term) {
        this.selectedCatalogSlug = `search:${term}`;
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

  selectCatalog(catalog: { name: string; slug: string }) {
    this.selectedCatalogSlug = catalog.slug;
  }
}
