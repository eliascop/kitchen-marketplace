import { Injectable } from '@angular/core';
import { ServiceResponse } from './model/http-options-request.model';
import { DataService } from './data.service';
import { environment } from '../../../environments/environment.dev';
import { Product } from '../model/product.model';

export const PRODUCT_REST_SERVICE = environment.PRODUCT_REST_SERVICE;

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private dataService: DataService) {}   

  getMyProducts(): ServiceResponse<Product[]> {
    return this.dataService.get<Product[]>({
      url: `${PRODUCT_REST_SERVICE}/seller`
    });
  }

  getProducts(): ServiceResponse<Product[]> {
    return this.dataService.get<Product[]>({
      url: `${PRODUCT_REST_SERVICE}`
    });
  }

  getProductById(id: number) {
    return this.dataService.get<Product>({url: `${PRODUCT_REST_SERVICE}/${id}`});
  }

  createProduct(productData: any): ServiceResponse<Product> {
    return this.dataService.post<Product>({
      url: PRODUCT_REST_SERVICE,
      body: productData,
    });
  }

  deleteProduct(id: number): ServiceResponse<Product>{
    return this.dataService.delete<Product>({
      url: `${PRODUCT_REST_SERVICE}/${id}`
    });
  }
}
