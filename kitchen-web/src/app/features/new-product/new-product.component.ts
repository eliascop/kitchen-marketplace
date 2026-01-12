import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../core/service/product.service';
import { ToastService } from '../../core/service/toast.service';
import { CommonModule } from '@angular/common';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';
import { Catalog } from '../../core/model/catalog.model';
import { CatalogService } from '../../core/service/catalog.service';
import { ProductRequest } from '../../core/model/request/product-request.model';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { Product } from '../../core/model/product.model';
import { sellerProductDetailStore } from '../../core/state/seller-product-detail.store';
import { select } from '@ngneat/elf';

@Component({
  selector: 'app-new-product',
  standalone: true,
  templateUrl: './new-product.component.html',
  styleUrls: ['./new-product.component.css'],
  imports: [CommonModule,ReactiveFormsModule, CurrencyInputComponent] 
})
export class NewProductComponent implements OnInit, OnDestroy {

  catalogs: Catalog[] = [];
  productForm!: FormGroup;
  isEditing = false;
  productId: number | null = null;
  product$!: Observable<Product>;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private catalogService: CatalogService,
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute,
    private toast: ToastService
  ) {}

  ngOnInit(): void {

    this.loadCatalogs();
    this.createForm();

    this.productId = this.route.snapshot.queryParams['id']; 
    if(this.productId){
      this.isEditing = true;
      this.loadProduct(this.productId );
    }
  }

  loadProduct(id: number) {
    this.product$ = sellerProductDetailStore.pipe(
      select(state => state.product),
      filter((product): product is Product => !!product)
    );

    this.product$
      .pipe(takeUntil(this.destroy$))
      .subscribe(product => {
        this.productForm.patchValue({
          id: product.id,
          name: product.name,
          description: product.description,
          basePrice: product.basePrice,
          imageUrl: product.imageUrl,
          catalog: product.catalog
        });
      });
  }

  loadCatalogs() {
    this.catalogService.getCatalogs().subscribe({
      next: (data) => {
        this.catalogs = data.data || [];
      },
      error: (err) => {
        this.toast.show('Erro ao carregar catálogos.');
        console.error(err);
      }
    });
  }

  createForm() {
    this.productForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      description: ['', Validators.required],
      basePrice: [0.0, Validators.required],
      imageUrl: ['', Validators.required],
      catalog: [null, Validators.required]
    });
  }

  compareCatalogs(c1: Catalog, c2: Catalog): boolean {
    return c1 && c2 ? c1.id === c2.id : c1 === c2;
  }

  onSubmit() {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    const productform = this.productForm.value;
    const selectedCatalog = this.productForm.get('catalog')?.value;

    if (!selectedCatalog) {
      this.toast.show('Catálogo inválido.');
      return;
    }

    const product: ProductRequest = { ...productform, catalog: { ... selectedCatalog } };

    const request$ = this.isEditing
      ? this.productService.updateProduct(product)
      : this.productService.createProduct(product);

    request$.subscribe({
      next: () => {
        this.toast.show(
          this.isEditing
            ? 'Produto atualizado com sucesso!'
            : 'Produto cadastrado com sucesso.'
        );
        this.router.navigate(['/seller-products']);
      },
      error: (err) => {
        this.toast.show('Erro ao salvar o produto.');
        console.error(err);
      }
    });
  }

  goBack() {
    this.router.navigate(['/seller-products/'+this.productId]);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
