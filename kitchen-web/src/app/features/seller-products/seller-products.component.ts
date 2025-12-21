import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, take } from 'rxjs';
import { ProductService } from '../../core/service/product.service';
import { Product } from '../../core/model/product.model';
import { ToastService } from '../../core/service/toast.service';
import { GenericTableComponent, TableColumn } from "../../shared/components/generic-table/generic-table.component";
import { setSellerProductDetail } from '../../core/state/seller-product-detail.actions';

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
  size = 10;
  page = 1; 

  sortField: keyof Product | '' = '';
  sortDir: 'asc' | 'desc' = 'asc';

  private sub?: Subscription;

  columns: TableColumn<Product>[] = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Nome', sortable: true },
    { key: 'catalog', label: 'Catálogo', formatter: (value, row) => row.catalog ? row.catalog.name : '—', sortable: true },
    { key: 'category', label: 'Categoria', formatter: (value, row) => row.category ? row.category.name : '—', sortable: true },
    { key: 'basePrice', label: 'Preço', formatter: (value, row) => row.basePrice ? `R$ ${value.toFixed(2)}` : '—', sortable: true },
    { key: 'productStatus', label: 'Status', sortable: true }
  ];

  constructor(
    private productService: ProductService,
    private router: Router,
    private readonly route: ActivatedRoute,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.page = +params['page'] || 1;
      this.size = +params['size'] || 10;
  
      this.loadMyProducts();
    });
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  loadMyProducts() {
    this.loading = true;
    const page0 = this.page - 1;
  
    this.sub = this.productService
      .getMyProducts(page0, this.size, this.sortField, this.sortDir)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.products = resp?.data?.data ?? [];
          this.totalItems = resp?.data?.totalElements ?? 0;
          this.totalPages = resp?.data?.totalPages ?? 0;
  
          this.loading = false;
        },
        error: () => {
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
    setSellerProductDetail(product);
    this.router.navigate([`/seller-products/${product.id}`],{ queryParams: { page: this.page, size: this.size } });
  }

  onPaginate({ page, pageSize }: { page: number; pageSize: number }) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page,
        size: pageSize
      },
      queryParamsHandling: 'merge'
    });
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