import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.dev";
import { DataService } from "./data.service";
import { AuthService } from "./auth.service";
import { Cart } from "../model/cart.model";



export const CART_SERVICE_REST = environment.CART_REST_SERVICE;

@Injectable({
  providedIn: 'root',
})
export class CartService {

  constructor(private dataService: DataService, private authService: AuthService) {}
        
  getCart() {
    const params = {userId: this.authService.currentUserId!};

    return this.dataService.get<Cart>({
      url: `${CART_SERVICE_REST}`,
      params
    });
  }

  addToCart(productId: number, sku: string, quantity: number){
    const userId = this.authService.currentUserId;

    if (!userId) {
      throw new Error('Usuário não autenticado.');
    }

    return this.dataService.patch<Cart>({
      url: `${CART_SERVICE_REST}/product/${productId}/sku/${sku}/quantity/${quantity}`
    });
  };
}
