import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
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
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.createForm();
    this.loadCatalogs();
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
      imageUrl: [''],
      catalog: ['', Validators.required],
      skus: this.fb.array([this.createSkuForm()])
    });
  }

  get skus(): FormArray {
    return this.productForm.get('skus') as FormArray;
  }

  createSkuForm(): FormGroup {
    return this.fb.group({
      id: [null],
      sku: ['', Validators.required],
      price: [0, Validators.required],

      stock: this.fb.group({
        totalQuantity: [0]
      }),

      attributes: this.fb.array([this.createAttributeForm()])
    });
  }
  

  addSku() {
    this.skus.push(this.createSkuForm());
  }

  updateSku(i: number) {
    const skuGroup = this.skus.at(i) as FormGroup;
    const product = this.productForm.value;
    const attributes = skuGroup.get('attributes')?.value;
  
    const sku = this.generateSku(product, attributes);
  
    skuGroup.patchValue({ sku });
  }

  generateSku(product: any, attributes: any[]): string {
    return "PROD-" 
        + product.seller.id 
        + "-" 
        + "{ID}"
        + attributes
            .slice()
            .sort((a, b) => a.attributeName.localeCompare(b.attributeName))
            .map(attr => "-" + this.normalize(attr.attributeValue))
            .join("");
  }

  normalize(value: string): string {
    return value
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/\s+/g, "")
      .replace(/[^a-zA-Z0-9]/g, "")
      .toLowerCase();
  }

  removeSku(index: number) {
    this.skus.removeAt(index);
  }

  createAttributeForm(): FormGroup {
    return this.fb.group({
      attributeName: ['', Validators.required],
      attributeValue: ['', Validators.required]
    });
  }

  addAttribute(skuIndex: number) {
    const attrs = this.skus.at(skuIndex).get('attributes') as FormArray;
    attrs.push(this.createAttributeForm());
    this.updateSku(skuIndex);
  }

  removeAttribute(skuIndex: number, attrIndex: number) {
    const attrs = this.skus.at(skuIndex).get('attributes') as FormArray;
    attrs.removeAt(attrIndex);
    this.updateSku(skuIndex);
  }

  onSubmit() {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    const product: any = this.productForm.value;

//Todo: Enable update product functionality
//    const request$ = this.isEditing
//      ? this.productService.updateProduct(product.id, product)
//      : this.productService.createProduct(product);

    const request$ = this.productService.createProduct(product);

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

  attributes(i: number): FormArray {
    return this.skus.at(i).get('attributes') as FormArray;
  }

  getAttributesControls(i: number) {
    return (this.skus.at(i).get('attributes') as FormArray).controls;
  }
}
