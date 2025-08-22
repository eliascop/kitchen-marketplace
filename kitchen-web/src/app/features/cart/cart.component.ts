import { Component, OnInit } from '@angular/core';
import { Cart, CartItem } from '../../core/model/cart.model';
import { CartService } from '../../core/service/cart.service';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/service/product.service';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";
import { ToastService } from '../../core/service/toast.service';
import { Location } from '@angular/common';
import { OrderService } from '../../core/service/order.service';
import { Order } from '../../core/model/order.model';
import { User } from '../../core/model/user.model';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Address } from '../../core/model/address.model';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, CurrencyFormatterPipe, FormsModule],
  standalone: true,
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cart!: Cart;
  order: Order = new Order;
  user: User = new User({ addresses: [] });
  cartItems: CartItem[] = [];
  cartTotal: number = 0;
  isLoading = false;

  currentStep = 1;

  paymentMethod: string = '';
  shippingMethod: string = '';
  selectedAddress: Address | null = null;

  constructor(private cartService: CartService, 
    private productService: ProductService,
    private readonly location: Location,
    private orderService: OrderService,
    private route: ActivatedRoute,
    private router: Router,
    private toast: ToastService) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadOrderAddress();
    this.getCartDetails();
    this.checkPaymentStatus();
  }

  loadUserData(){
    const userDataString = localStorage.getItem('userData');
    if(userDataString)
      this.user = userDataString ? JSON.parse(userDataString) : null;
  }

  checkPaymentStatus() {
    const params = this.route.snapshot.queryParamMap;
    const cartId = params.get('cartId');
    const status = params.get('status');
    const paymentToken = params.get('token');
    const secureToken = params.get('secureToken');
  
    if (status) {
      sessionStorage.setItem('checkoutStatus', JSON.stringify({ status: status, paymentToken: paymentToken, secureToken: secureToken, cartId: cartId}));
    }

    const saved = sessionStorage.getItem('checkoutStatus');
    if (saved) {
      const { status, paymentToken, secureToken } = JSON.parse(saved);
      if (status === 'success' && cartId) {
          this.validatePayment(paymentToken, secureToken, cartId);
      } else if (status === 'cancelled') {
        this.currentStep = 2;
        console.log('Pagamento cancelado');
      }
    }
  }

  validatePayment(paymentToken: string, secureToken: string, cartId: string): void {
    this.cartService.validadePaymentMethod('paypal', paymentToken, secureToken, cartId).subscribe({
      next: (data) => {
        const paymentStatusStored = data.data?.message;
        if (paymentStatusStored === 'SUCCESS') {
          this.toast.show('Pagamento aprovado com sucesso!');
          this.currentStep = 3;
        } else {
          this.toast.show('Pagamento não aprovado.');
          this.currentStep = 2;
        }
      },
      error: (error) => {
        console.error("Erro ao validar pagamento:", error);
        this.toast.show("Erro ao validar pagamento.");
        this.currentStep = 2;
      }
    });
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

  loadOrderAddress(){
    if(this.user && this.user.addresses){
      this.user.addresses.forEach(addr=>{
        if(addr.type=='SHIPPING'){
          this.order.shippingAddressId = addr.id;
        }
        if(addr.type=='BILLING'){
          this.order.billingAddressId = addr.id;
        }
      });
    }
  }

  onCheckout(): void{
    this.orderService.checkout(this.order).subscribe({
      next: (data) => {
        const orderId = data.data!.orderId;
        this.router.navigate([`/tracking/${orderId}`]);
        this.toast.show(`Compra realizada com sucesso!`);
      },
      error: (error) => {
        this.toast.show("Ocorreu um erro: "+error)
      }
    })
  }

  onPaymentSelected():void{
    let paymentMethodSelected = this.paymentMethod;
    if(paymentMethodSelected){
      this.cartService.choosePaymentMethod(paymentMethodSelected).subscribe({
        next: response => {
          window.location.href = response.data!.redirectUrl;
        },
        error: err => {
          this.toast.show('Ocorreu um erro ao selecionar pagamento.');
          console.error(err);
        }
      });
    }
  }

  goBack(): void {
    this.location.back();
  }

  nextStep() {
    if (this.currentStep < 3) {
      this.currentStep++;
    }
  }

  prevStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }
}