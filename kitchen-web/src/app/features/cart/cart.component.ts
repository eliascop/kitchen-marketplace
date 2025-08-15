import { Component, OnInit } from '@angular/core';
import { Cart, CartItem } from '../../core/model/cart.model';
import { CartService } from '../../core/service/cart.service';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/service/product.service';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";
import { ToastService } from '../../core/service/toast.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, CurrencyFormatterPipe],
  standalone: true,
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cart: Cart | undefined;
  cartItems: CartItem[] = [];
  cartTotal: number = 0;
  isLoading = false;

  constructor(private cartService: CartService, 
    private productService: ProductService,
    private readonly location: Location,
    private toast: ToastService) {}

  ngOnInit(): void {
    this.getCartDetails();
  }

  getCartDetails(): void {
    this.isLoading = true;
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.updateCartFromResponse(data.data!);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar o carrinho', error);
        this.isLoading = false;
      },
    });
  }

  updateCartFromResponse(data: any){
    this.cart = data;
    this.cartItems = data.items || []; 
    this.cartTotal = data.cartTotal;
  }

  loadProductDetails(productId: number){
    this.productService.getProductById(productId).subscribe({
      next: (data) => {
        return data.data! 
      }
    });
  }

  calculateTotal(items: CartItem[]): number {
    return items.reduce((total, item) => total + item.value, 0);
  }

  onRemoveItem(itemid: number): void {
    this.cartService.removeItem(itemid).subscribe({
      next: (data) => {
        this.updateCartFromResponse(data.data!);
        this.toast.show("Item removido com sucesso.");        
      },
      error: (error) => {
        console.error('Erro ao remover item', error);
      }
    });
  }
  
  onClearCart(): void {
    this.cartService.clearCart().subscribe({
      next: () => {
        this.cartItems = [];
        this.calculateTotal(this.cartItems);
        this.toast.show("Todos os items foram removidos com sucesso.");
      },
      error: (error) => {
        console.error('Erro ao remover item', error);
      }
    });
  }

  goBack(): void {
    this.location.back();
  }
}