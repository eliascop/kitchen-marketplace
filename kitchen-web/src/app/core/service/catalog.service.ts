import { Injectable } from "@angular/core";
import { DataService } from "./data.service";
import { AuthService } from "./auth.service";
import { environment } from "../../../environments/environment.dev";
import { Product } from "../model/product.model";
import { Catalog } from "../model/catalog.model";
import { ServiceResponse } from "./model/http-options-request.model";

export const CATALOG_SERVICE_REST = environment.CATALOG_REST_SERVICE;

@Injectable({
  providedIn: 'root',
})
export class CatalogService {

  constructor(private dataService: DataService, private authService: AuthService) {}
        
  getCatalogs():ServiceResponse<Catalog[]> {
    const params = { userId: this.authService.currentUserId! };
    return this.dataService.get<Catalog[]>({
      url: `${CATALOG_SERVICE_REST}`,
      params
    });
  }

}