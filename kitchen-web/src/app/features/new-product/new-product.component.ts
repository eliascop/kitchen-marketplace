import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../core/service/product.service';
import { ToastService } from '../../core/service/toast.service';
import { CommonModule } from '@angular/common';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';
import { Catalog } from '../../core/model/catalog.model';
import { CatalogService } from '../../core/service/catalog.service';

@Component({
  selector: 'app-new-product',
  standalone: true,
  templateUrl: './new-product.component.html',
  styleUrls: ['./new-product.component.css'],
  imports: [CommonModule,ReactiveFormsModule, CurrencyInputComponent] 
})
export class NewProductComponent implements OnInit {

  catalogs: Catalog[] = [];
  productForm!: FormGroup;
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private catalogService: CatalogService,
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute,
    private toast: ToastService
  ) {}

  ngOnInit(): void {

    this.createForm();

    const id = this.route.snapshot.queryParams['id']; 
    if(id){
      this.isEditing = true;
      this.loadProduct(id);
    }

    this.loadCatalogs();
  }

  loadProduct(id: number) {
    this.productService.getProductById(id).subscribe({
      next: (res) => {
        const p = res.data!;
        this.productForm.patchValue({
          id: p.id,
          name: p.name,
          description: p.description,
          price: p.price,
          imageUrl: p.imageUrl,
          catalog: p.catalog
        });
      },
      error: () => {
        this.toast.show("Erro ao carregar produto.");
      }
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
      price: [0.0, Validators.required],
      imageUrl: ['', Validators.required],
      catalogName: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    const product: any = this.productForm.value;
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

}
