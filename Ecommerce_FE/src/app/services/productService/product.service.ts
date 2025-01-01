import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private productApiUrl = 'http://localhost:8081/api/products';
  private brandApiUrl = 'http://localhost:8081/api/brands';
  private categoryApiUrl = 'http://localhost:8081/api/categories';
  private subCategoryApiUrl = 'http://localhost:8081/api/subCategories';
  private processApiUrl = 'http://localhost:8081/api/workflow';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<any[]> {
    return this.http.get<any[]>(this.productApiUrl);
  }

  getProductById(id: string): Observable<any> {
    return this.http.get<any>(`${this.productApiUrl}/${id}`);
  }

  addProduct(product: any): Observable<any> {
    return this.http.post<any>(this.productApiUrl, product);
  }

  startProcess(processDefinitionKey: string, variables: any): Observable<any> {
    const payload = { processDefinitionKey, variables };
    return this.http.post<any>(`${this.processApiUrl}/start`, payload);
  }

  updateProduct(id: string, product: any): Observable<any> {
    return this.http.put<any>(`${this.productApiUrl}/${id}`, product);
  }

  deleteProduct(id: string): Observable<any> {
    return this.http.delete(`${this.productApiUrl}/${id}`, { responseType: 'text' });
  }

  getBrands(): Observable<any[]> {
    return this.http.get<any[]>(this.brandApiUrl);
  }

  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(this.categoryApiUrl);
  }

  getSubCategoriesByCategoryId(category_id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.subCategoryApiUrl}?category_id=${category_id}`);
  }
}
