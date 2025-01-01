import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private apiUrl = 'http://localhost:8081/api/brands';
  private importUrl = 'http://localhost:8081/api/report/import/brand'; // Địa chỉ API import

  constructor(private http: HttpClient) {}

  getBrands(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }

  getBrandById(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  addBrand(brand: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, brand, { withCredentials: true });
  }

  updateBrand(id: string, brand: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, brand, { withCredentials: true });
  }

  deleteBrand(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text', withCredentials: true });
  }

  exportReport(format: string): Observable<Blob> {
    const url = `http://localhost:8081/api/report/export/brand/${format}`;
    return this.http.get(url, { responseType: 'blob', withCredentials: true });
  }

  importFile(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
  
    return this.http.post(`${this.importUrl}`, formData, {
      headers: new HttpHeaders(),
      responseType: 'text'
    });
  }
}
