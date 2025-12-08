import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../core/service/product.service';
import { CommonModule } from '@angular/common';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from '@angular/material/tooltip';
import { ToastService } from '../../core/service/toast.service';

@Component({
  selector: 'app-sku-manager',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    CurrencyInputComponent, 
    MatIconModule,
    MatTooltipModule],
  templateUrl: './sku-manager.component.html',
  styleUrl: './sku-manager.component.css'
})
export class SkuManagerComponent implements OnInit {

  form!: FormGroup;
  product!: any;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private productService: ProductService,
    private toast: ToastService
  ) {}

  ngOnInit() {
    const id = +this.route.snapshot.params['id'];

    this.form = this.fb.group({
      skus: this.fb.array([])
    });

    this.loadProduct(id);
  }

  loadProduct(id: number) {
    this.productService.getProductById(id).subscribe({
      next: (res) => {
  
        this.product = res.data;
  
        this.product.skus.forEach((sku: any) => {
  
          const skuForm = this.fb.group({
            id: [sku.id],
            sku: [sku.sku],
            price: [sku.price, Validators.required],
  
            stock: this.fb.group({
              totalQuantity: [sku.stock.totalQuantity]
            }),
  
            attributes: this.fb.array([])
          });
  
          sku.attributes.forEach((attr: any) => {
            (skuForm.get('attributes') as FormArray).push(
              this.fb.group({
                attributeName: [attr.attributeName, Validators.required],
                attributeValue: [attr.attributeValue, Validators.required]
              })
            );
          });
  
          skuForm.get('attributes')?.valueChanges.subscribe(() => {
            const index = this.skus.controls.indexOf(skuForm);
            this.updateSku(index);
          });
  
          this.skus.push(skuForm);
        });
  
      }
    });
  }

  get skus(): FormArray {
    return this.form.get('skus') as FormArray;
  }

  addSku() {
    const skuForm = this.fb.group({
      id: [null],
      sku: [''],
      price: [0, Validators.required],
      stock: this.fb.group({ totalQuantity: [0] }),
      attributes: this.fb.array([])
    });

    this.skus.push(skuForm);
    skuForm.get('attributes')?.valueChanges.subscribe(() => {
      const index = this.skus.controls.indexOf(skuForm);
      this.updateSku(index);
    });
  }

  removeSku(index: number) {
    this.skus.removeAt(index);
  }

  addAttribute(i: number) {
    const attrs = this.attributes(i);
    attrs.push(this.createAttributeForm());
    this.updateSku(i);
  }

  attributes(i: number): FormArray {
    return this.skus.at(i).get('attributes') as FormArray;
  }

  removeAttribute(skuIndex: number, attrIndex: number) {
    const attrs = this.skus.at(skuIndex).get('attributes') as FormArray;
    attrs.removeAt(attrIndex);
    this.updateSku(skuIndex);
  }

  getAttributesControls(i: number) {
    return this.attributes(i).controls;
  }

  createAttributeForm(): FormGroup {
    return this.fb.group({
      attributeName: ['', Validators.required],
      attributeValue: ['', Validators.required]
    });
  }

  updateSku(i: number) {
    const skuGroup = this.skus.at(i) as FormGroup;
    const attributes = skuGroup.get('attributes')?.value;

    const sku = this.generateSku(this.product, attributes);
    skuGroup.patchValue({ sku });
  }

  generateSku(product: any, attributes: any[]): string {
    return (
      `PROD-${product.seller.id}-${product.id}` + attributes
        .slice()
        .sort((a, b) => a.attributeName.localeCompare(b.attributeName))
        .map(attr => '-' + this.normalize(attr.attributeValue))
        .join('')
    );
  }

  normalize(value: string): string {
    return value
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/\s+/g, "")
      .replace(/[^a-zA-Z0-9]/g, "")
      .toLowerCase();
  }

  onSubmit() {
    const productId = this.product.id;
    const skuToUpdate = this.form.value.skus;

    this.productService.saveSkus(productId, skuToUpdate).subscribe({
      next: () => this.toast.show("SKUs salvos!"),
      error: () => this.toast.show("Erro ao salvar SKUs")
    });
  }
}