import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SubCategoryService {
  private subCategoryApiUrl = 'http://localhost:8081/api/subCategories';
  private categoryApiUrl = 'http://localhost:8081/api/categories';

  constructor(private http: HttpClient) {}

  getSubCategories(): Observable<any[]> {
    return this.http.get<any[]>(this.subCategoryApiUrl);
  }

  getSubCategoryById(id: string): Observable<any> {
    return this.http.get<any>(`${this.subCategoryApiUrl}/${id}`);
  }

  addSubCategory(subCategory: any): Observable<any> {
    const payload = {
      name: subCategory.name,
      description: subCategory.description,
      category_id: subCategory.category_id,
    };
    return this.http.post<any>(this.subCategoryApiUrl, payload);
  }

  updateSubCategory(id: string, subCategory: any): Observable<any> {
    const payload = {
      name: subCategory.name,
      description: subCategory.description,
      category_id: subCategory.category_id,
    };
    return this.http.put<any>(`${this.subCategoryApiUrl}/${id}`, payload);
  }

  deleteSubCategory(id: string): Observable<any> {
    return this.http.delete<any>(`${this.subCategoryApiUrl}/${id}`);
  }

  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(this.categoryApiUrl);
  }

  getSubCategoriesByCategory(category_id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.subCategoryApiUrl}?category_id=${category_id}`);
  }
  
  exportReport(format: string): Observable<Blob> {
    const url = `http://localhost:8081/api/report/export/subCategory/${format}`;
    return this.http.get(url, { responseType: 'blob' });
  }
}