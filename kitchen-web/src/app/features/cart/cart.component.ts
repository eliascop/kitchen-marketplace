import { Component, OnInit } from '@angular/core';
import { Cart, CartItem } from '../../core/model/cart.model';
import { CartService } from '../../core/service/cart.service';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/service/product.service';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";

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

  constructor(private cartService: CartService, private productService: ProductService) {}

  ngOnInit(): void {
    this.getCartDetails();
  }

  getCartDetails(): void {
    this.isLoading = true;
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cart = data.data!;
        this.cartItems = data.data!.items || []; 
        this.cartTotal = data.data!.cartTotal;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar o carrinho', error);
        this.isLoading = false;
      },
    });
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

  onRemoveItem(productId: number): void {
    console.warn('Lógica de remover item precisa ser implementada.');
  }

  onClearCart(): void {
    console.warn('Lógica de limpar carrinho precisa ser implementada.');
  }
}