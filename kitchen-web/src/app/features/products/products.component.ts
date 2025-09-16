import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/service/product.service';
import { Product } from '../../core/model/product.model';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { ToastService } from '../../core/service/toast.service';
import { ProductTableComponent } from "../components/product-table/product-table.component";
import { ProductDetailsModalComponent } from "../components/product-details-modal/product-details-modal.component";

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, ProductTableComponent, ProductDetailsModalComponent],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {

  userId: number = 1;
  products: Product[] = [];
  selectedOrder: any = null;
  selectedProduct: Product | null = null;
  histories: History[] = [];

  constructor(private productService: ProductService, private router: Router, private toast: ToastService) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.ngOnInit();
    });
  }
  
  ngOnInit() {
    this.getProducts();
  }

  getProducts() {
    this.productService.getMyProducts().subscribe(data => {
      this.products = data.data!;
    });
  }

  goToNewProduct(){
    this.router.navigate(['/new-product']);
  }

  closeModal() {
    this.selectedOrder = null;
  }
  
  removeProduct(productId: number | null): void {
    if (productId === null) return;
    
    const confirmed = window.confirm('Tem certeza de que quer excluir esse Produto ?');
    if(confirmed){
      this.productService.deleteProduct(productId).subscribe({
        next: (response) => {
          this.products = this.products.filter(product => product.id !== productId);
          this.toast.show("Produto removido com sucesso!");
        },
        error: (err) => {
          this.toast.show("Ocorreu um erro ao excluir o produto.");
          console.error('Erro ao deletar produto:', err);
        }
      });
    }
  }

  onShowDetails(product: Product) {
    this.selectedProduct = product;
  }
} 
