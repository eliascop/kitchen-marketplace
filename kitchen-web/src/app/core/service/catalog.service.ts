import { Injectable } from "@angular/core";
import { DataService } from "./data.service";
import { AuthService } from "./auth.service";
import { environment } from "../../../environments/environment.dev";
import { Product } from "../model/product.model";

export const CATALOG_SERVICE_REST = environment.CATALOG_REST_SERVICE;

@Injectable({
  providedIn: 'root',
})
export class CatalogService {

  constructor(private dataService: DataService, private authService: AuthService) {}
        
  getCatalogs() {
    const params = { userId: this.authService.currentUserId! };
    return this.dataService.get<Array<{slug?: string;name: string }>>({
      url: `${CATALOG_SERVICE_REST}`,
      params
    });
  }

  getProductsByCatalog(catalogSlug: String) {
    return this.dataService.get<Product[]>({
      url: `${CATALOG_SERVICE_REST}/${catalogSlug}/products`
    });
  }
}