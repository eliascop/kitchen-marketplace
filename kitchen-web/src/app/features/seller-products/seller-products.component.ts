import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription, take } from 'rxjs';
import { ProductService } from '../../core/service/product.service';
import { Product } from '../../core/model/product.model';
import { ToastService } from '../../core/service/toast.service';
import { GenericTableComponent, TableColumn } from "../../shared/components/generic-table/generic-table.component";

@Component({
  selector: 'app-seller-products',
  standalone: true,
  imports: [CommonModule, GenericTableComponent],
  templateUrl: './seller-products.component.html',
  styleUrl: './seller-products.component.css'
})
export class SellerProductsComponent implements OnInit, OnDestroy {

  products: Product[] = [];
  histories: History[] = [];

  loading = false;
  totalItems = 0;
  totalPages = 0;
  pageSize = 10;
  page = 1; 

  sortField: keyof Product | '' = '';
  sortDir: 'asc' | 'desc' = 'asc';

  private sub?: Subscription;

  columns: TableColumn<Product>[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Nome', sortable: true },
    { key: 'catalog', label: 'Catálogo', formatter: (value, row) => row.catalog ? row.catalog.name : '—', sortable: true },
    { key: 'category', label: 'Categoria', formatter: (value, row) => row.category ? row.category.name : '—', sortable: true },
    { key: 'price', label: 'Preço', formatter: (value, row) => row.price ? `R$ ${value.toFixed(2)}` : '—', sortable: true },
    { key: 'productStatus', label: 'Status', sortable: true }
  ];

  constructor(
    private productService: ProductService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.loadMyProducts();
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  loadMyProducts() {
    this.loading = true;
    const page0 = this.page - 1;

    this.sub = this.productService
      .getMyProducts(page0, this.pageSize, this.sortField, this.sortDir)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          const data = resp?.data?.data ?? [];
          this.products = data;

          this.totalItems = resp?.data?.totalElements ?? 0;
          this.totalPages = resp?.data?.totalPages ?? 0;

          const backendPage = resp?.data?.page ?? page0;
          this.page = backendPage + 1;

          this.loading = false;
        },
        error: () => {
          this.products = [];
          this.totalItems = 0;
          this.totalPages = 0;
          this.loading = false;
        }
      });
  }

  onAdd() {
    this.router.navigate(['/new-product']);
  }

  onRemove(product: Product | undefined) {
    if (!product) return;
    
    const confirmed = window.confirm('Tem certeza que deseja excluir este produto?');
    if (!confirmed) return;

    this.productService.deleteProduct(product.id!).pipe(take(1)).subscribe({
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
    this.router.navigate([`/seller-products/${product.id}`]);
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