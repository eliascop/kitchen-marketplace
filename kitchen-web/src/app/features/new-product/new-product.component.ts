import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../core/service/product.service';
import { ToastService } from '../../core/service/toast.service';
import { CommonModule } from '@angular/common';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';

@Component({
  selector: 'app-new-product',
  standalone: true,
  templateUrl: './new-product.component.html',
  styleUrls: ['./new-product.component.css'],
  imports: [CommonModule,ReactiveFormsModule, CurrencyInputComponent] 
})
export class NewProductComponent implements OnInit {

  productForm!: FormGroup;
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.createForm();
  }

  createForm() {
    this.productForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      description: ['', Validators.required],
      imageUrl: [''],
      category: ['', Validators.required],
      catalog: [''],
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

      attributes: this.fb.array([])
    });
  }

  addSku() {
    this.skus.push(this.createSkuForm());
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
  }

  removeAttribute(skuIndex: number, attrIndex: number) {
    const attrs = this.skus.at(skuIndex).get('attributes') as FormArray;
    attrs.removeAt(attrIndex);
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
        this.router.navigate(['/products']);
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
}
