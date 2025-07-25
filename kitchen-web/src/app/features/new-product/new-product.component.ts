import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/service/product.service';
import { Router } from '@angular/router';
import { ToastService } from '../../core/service/toast.service';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';

@Component({
  selector: 'app-new-product',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, CurrencyInputComponent],
  templateUrl: './new-product.component.html',
  styleUrl: './new-product.component.css'
})
export class NewProductComponent {
  productForm: FormGroup;

  constructor(private fb: FormBuilder, private productService: ProductService, private router: Router, private toast: ToastService) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      type: ['', Validators.required],
      price: [null, Validators.required],
    });
  } 

  onSubmit() {
    if (this.productForm.valid) {
      const formData = { ...this.productForm.value };
      
      this.productService.createProduct(formData).subscribe({
        next: (res: any) => {
          this.router.navigate(['/products']);
          this.productForm.reset();
          this.toast.show('Produto cadastrado com sucesso.');
        },
        error: (err) => {
          this.toast.show('Ocorreu um erro ao salvar o produto.');
          console.error('Erro ao salvar o produto:', err);
        }
      });
    } else {
      Object.keys(this.productForm.controls).forEach(key => {
        this.productForm.controls[key].markAsTouched();
      });
    }
  }
}



