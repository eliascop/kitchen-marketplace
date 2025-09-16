import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.dev";
import { DataService } from "./data.service";
import { AuthService } from "./auth.service";
import { Cart } from "../model/cart.model";
import { BehaviorSubject, map, Observable } from "rxjs";
import { HttpParams } from "@angular/common/http";

export const CART_SERVICE_REST = environment.CART_REST_SERVICE;
export const PAYMENT_SERVICE_URL = environment.PAYMENT_REST_SERVICE;

@Injectable({
  providedIn: 'root',
})
export class CartService {

  private cartItemsCount = new BehaviorSubject<number>(0);
  cartItemsCount$ = this.cartItemsCount.asObservable();

  constructor(
    private dataService: DataService,
    private authService: AuthService
  ) {}

  getCart(): Observable<{ data: Cart | null; error?: any }> {
    const params = { userId: this.authService.currentUserId! };
    return this.dataService.get<Cart>({
      url: `${CART_SERVICE_REST}`,
      params
    });
  }

  private updateCartCountFromCart(cart: Cart | null) {
    const total = cart?.totalItems ?? 0;
    this.cartItemsCount.next(total);
  }

  updateCartAddresses(cart: Cart): Observable<{ data: Cart | null; error?: any }> { 
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.put<Cart>({
      url: `${CART_SERVICE_REST}`,
      body: cart
    });
  }

  getCartTotalItems(): Observable<number> {
    return this.getCart().pipe(
      map(response => {
        this.updateCartCountFromCart(response.data);
        return this.cartItemsCount.value;
      })
    );
  }

  addToCart(sku: string, quantity: number) {
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.patch<Cart>({
      url: `${CART_SERVICE_REST}/productSku/${sku}/quantity/${quantity}`
    }).pipe(
      map(response => {
        this.updateCartCountFromCart(response.data);
        return response;
      })
    );
  }

  removeItem(itemId: number) {
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.delete<Cart>({
      url: `${CART_SERVICE_REST}/remove/${itemId}`
    }).pipe(
      map(response => {
        this.updateCartCountFromCart(response.data);
        return response;
      })
    );
  }

  clearCart(){
    const userId = this.authService.currentUserId;
    if (!userId) throw new Error('Usuário não autenticado.');

    return this.dataService.delete({
      url: `${CART_SERVICE_REST}/clear`,
    }).pipe(
      map(response => {
        this.cartItemsCount.next(0);
        return response;
      })
    );
  }

  choosePaymentMethod(paymentType: string){
    return this.dataService.post<{redirectUrl: string}>({
      url: `${PAYMENT_SERVICE_URL}/${paymentType}`
    });
  }

  validadePaymentMethod(paymentType: string, paymentToken: string, secureToken: string, cartId: string){
    const params = new HttpParams()
      .set('token', paymentToken)
      .set('secureToken', secureToken)
      .set('cartId', cartId);

    return this.dataService.get<{message:string}>({
      url: `${PAYMENT_SERVICE_URL}/${paymentType}/success`,
      params: params
    });
  }
}
