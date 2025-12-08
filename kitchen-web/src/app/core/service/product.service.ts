import { Injectable } from '@angular/core';
import { ServiceResponse } from './model/http-options-request.model';
import { DataService } from './data.service';
import { environment } from '../../../environments/environment.dev';
import { Product } from '../model/product.model';
import { PaginatedResponse } from './model/paginated-response';

export const PRODUCT_REST_SERVICE = environment.PRODUCT_REST_SERVICE;

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private dataService: DataService) {}   

  getMyProducts(page: number, size: number,sortField?: string, sortDir?: 'asc'|'desc'): ServiceResponse<PaginatedResponse<Product>> {

    const params: any = { page, size };
    if (sortField) params.sort = `${sortField},${sortDir || 'asc'}`;
    return this.dataService.get<PaginatedResponse<Product>>({
      url: `${PRODUCT_REST_SERVICE}/seller`,
      params: params
    });
  }

  getProductsByCatalogSlug(page: number, size: number, catalogSlug: string | undefined): ServiceResponse<PaginatedResponse<Product>> {
    return this.dataService.get<PaginatedResponse<Product>>({
      url: `${PRODUCT_REST_SERVICE}?page=${page}&size=${size}&catalog=${catalogSlug}`
    });
  }

  searchProducts(page: number,size: number, productName: string): ServiceResponse<PaginatedResponse<Product>> {
    const queryParam = productName ? `&query=${encodeURIComponent(productName)}` : '';
    return this.dataService.get<PaginatedResponse<Product>>({
      url: `${PRODUCT_REST_SERVICE}/search?page=${page}&size=${size}&query=${encodeURIComponent(productName)}`,
    });
    
  }

  getProductById(id: number): ServiceResponse<Product> {
    return this.dataService.get<Product>({url: `${PRODUCT_REST_SERVICE}/${id}`});
  }

  createProduct(productData: any): ServiceResponse<Product> {
    return this.dataService.post<Product>({
      url: PRODUCT_REST_SERVICE,
      body: productData,
    });
  }

  updateProduct(productData: any): ServiceResponse<Product> {
    return this.dataService.put<Product>({
      url: PRODUCT_REST_SERVICE,
      body: productData,
    });
  }

  deleteProduct(id: number): ServiceResponse<Product>{
    return this.dataService.delete<Product>({
      url: `${PRODUCT_REST_SERVICE}/${id}`
    });
  }

  saveSkus(productId: number, skus: any[]): ServiceResponse<void> {
    return this.dataService.put<void>({
      url: `${PRODUCT_REST_SERVICE}/${productId}/skus`,
      body: skus
    });
  }

}
