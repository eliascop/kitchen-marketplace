import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/service/product.service';
import { Product } from '../../core/model/product.model';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { ToastService } from '../../core/service/toast.service';
import { ProductDetailsModalComponent } from "../../shared/components/product-details-modal/product-details-modal.component";
import { GenericTableComponent, TableColumn } from "../../shared/components/generic-table/generic-table.component";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: 'app-seller-products',
  standalone: true,
  imports: [CommonModule, ProductDetailsModalComponent, GenericTableComponent, MatIcon],
  templateUrl: './seller-products.component.html',
  styleUrl: './seller-products.component.css'
})
export class SellerProductsComponent implements OnInit {

  userId: number = 1;
  products: Product[] = [];
  selectedProduct: Product | null = null;
  histories: History[] = [];

  loading = false;
  totalItems = 0;
  totalPages = 0;
  pageSize = 10;
  page = 1; 

  sortField: keyof Product | '' = '';
  sortDir: 'asc' | 'desc' = 'asc';

  columns: TableColumn<Product>[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Nome', sortable: true },
    { key: 'catalog', label: 'Catálogo' },
    { key: 'category', label: 'Categoria' },
    {
      key: 'price',
      label: 'Preço',
      formatter: (value, row) =>
        row.price ? `R$ ${value.toFixed(2)}` : '—'
    },
    { key: 'active', label: 'Status', formatter: v => v ? 'Ativo' : 'Inativo' }
  ];

  constructor(
    private productService: ProductService,
    private router: Router,
    private toast: ToastService
  ) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.ngOnInit());
  }
  
  ngOnInit() {
    this.loadMyProducts();
  }

  loadMyProducts() {
    this.loading = true;
    const page0 = this.page - 1;

    this.productService.getMyProducts(page0, this.pageSize, this.sortField, this.sortDir)
      .subscribe({
        next: (resp) => {

          const data = resp?.data?.data ?? [];
          this.products = data;

          this.totalItems = resp?.data?.totalElements ?? 0;
          this.totalPages = resp?.data?.totalPages ?? 0;

          const backendPage = resp?.data?.page ?? page0;
          this.page = backendPage + 1;
        },
        error: () => {
          this.products = [];
          this.totalItems = 0;
          this.totalPages = 0;
        },
        complete: () => this.loading = false
      });
  }

  onAdd() {
    this.router.navigate(['/new-product']);
  }

  onRemove(product: Product | undefined) {
    if (product === undefined) return;
    
    const confirmed = window.confirm('Tem certeza que deseja excluir este produto?');
    if (!confirmed) return;

    this.productService.deleteProduct(product.id!).subscribe({
      next: () => {
        this.toast.show("Produto removido com sucesso!");
        this.loadMyProducts();
      },
      error: (err) => {
        this.toast.show("Erro ao excluir o produto.");
        console.error(err);
      }
    });
  }

  onView(product: Product) {
    this.selectedProduct = product;
  }

  onPaginate({ page, pageSize }: { page: number; pageSize: number }) {
    this.page = page;
    this.pageSize = pageSize;
    this.loadMyProducts();
  }

  onSort(e: { field: keyof Product | null; direction: 'asc' | 'desc' | null }) {
    if (!e.field || !e.direction) {
      this.sortField = '' as any;
      this.sortDir = 'asc';
    } else {
      this.sortField = e.field;
      this.sortDir = e.direction;
    }
    this.page = 1;
    this.loadMyProducts();
  }
  
}