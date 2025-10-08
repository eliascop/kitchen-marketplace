import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.dev";
import { DataService } from "./data.service";
import { AuthService } from "./auth.service";
import { Cart } from "../model/cart.model";
import { BehaviorSubject, map, Observable, of, tap } from "rxjs";
import { HttpParams } from "@angular/common/http";

export const CART_SERVICE_REST = environment.CART_REST_SERVICE;
export const PAYMENT_SERVICE_URL = environment.PAYMENT_REST_SERVICE;

@Injectable({
  providedIn: 'root',
})
export class CartService {

  private cartSubject = new BehaviorSubject<Cart | null>(null);
  private cartItemsCount = new BehaviorSubject<number>(0);

  cart$ = this.cartSubject.asObservable();
  cartItemsCount$ = this.cartItemsCount.asObservable();

  constructor(
    private dataService: DataService,
    private authService: AuthService
  ) {
    this.authService.user$.subscribe(user => {
      if (user) {
        this.loadCartFromBackend(user.id);
      } else {
        this.clearCartFromLocalStorage();
      }
    });
  }

  private loadCartFromBackend(userId: number) {
    this.dataService.get<Cart>({
      url: `${CART_SERVICE_REST}`,
      params: { userId }
    }).subscribe({
      next: response => this.saveCartToLocalStorage(response.data),
      error: () => this.clearCartFromLocalStorage()
    });
  }

  getCart(): Observable<{ data: Cart | null; error?: any }> {
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    const cachedCart = this.getCartFromLocalStorage();
    if (cachedCart) {
      return of({ data: cachedCart });
    }

    const params = { userId };
    return this.dataService.get<Cart>({
      url: `${CART_SERVICE_REST}`,
      params
    }).pipe(
      tap(response => this.saveCartToLocalStorage(response.data))
    );
  }

  updateCartAddresses(cart: Cart): Observable<{ data: Cart | null; error?: any }> { 
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.put<Cart>({
      url: `${CART_SERVICE_REST}`,
      body: cart
    }).pipe(
      tap(response => this.saveCartToLocalStorage(response.data))
    );
  }
 
  addToCart(skuId: number, quantity: number) {
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.patch<Cart>({
      url: `${CART_SERVICE_REST}/skuId/${skuId}/quantity/${quantity}`
    }).pipe(
      tap(response => this.saveCartToLocalStorage(response.data))
    );
  }

  removeItem(itemId: number) {
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.delete<Cart>({
      url: `${CART_SERVICE_REST}/remove/${itemId}`
    }).pipe(
      tap(response => this.saveCartToLocalStorage(response.data))
    );
  }

  clearCart() {
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.delete({
      url: `${CART_SERVICE_REST}/clear`,
    }).pipe(
      tap(() => {
        this.clearCartFromLocalStorage();
      })
    );
  }

  choosePaymentMethod(paymentType: string) {
    return this.dataService.post<{ redirectUrl: string }>({
      url: `${PAYMENT_SERVICE_URL}/${paymentType}`
    });
  }

  validadePaymentMethod(paymentType: string, paymentToken: string, secureToken: string, cartId: string) {
    const params = new HttpParams()
      .set('token', paymentToken)
      .set('secureToken', secureToken)
      .set('cartId', cartId);

    return this.dataService.get<{ message: string }>({
      url: `${PAYMENT_SERVICE_URL}/${paymentType}/success`,
      params
    });
  }

  getCartTotalItems(): Observable<number> {
    return this.cartItemsCount$;
  }

  private saveCartToLocalStorage(cart: Cart | null): void {
    if (!cart) return;
    localStorage.setItem('cartData', JSON.stringify(cart));
    this.cartSubject.next(cart);
    this.updateCartCountFromCart(cart);
  }

  private getCartFromLocalStorage(): Cart | null {
    const data = localStorage.getItem('cartData');
    return data ? JSON.parse(data) : null;
  }

  private clearCartFromLocalStorage(): void {
    localStorage.removeItem('cartData');
    this.cartSubject.next(null);
    this.cartItemsCount.next(0);
  }

  private updateCartCountFromCart(cart: Cart | null) {
    const total = cart?.totalItems ?? 0;
    this.cartItemsCount.next(total);
  }
}
