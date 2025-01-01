import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, retry } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8081/api/reviews';

  constructor(private http: HttpClient) { }

  getReviews(): Observable<any[]>{
    return this.http.get<any[]>(this.apiUrl);
  }

  getReviewById(id: string): Observable<any>{
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  addReview(review: any): Observable<any>{
    return this.http.post<any>(this.apiUrl, review);
  }

  updateReview(id: string, review: any): Observable<any>{
    return this.http.put<any>(`${this.apiUrl}/${id}`, review);
  }

  deleteReview(id: string): Observable<any>{
    return this.http.delete(`${this.apiUrl}/${id}`,{responseType: 'text'});
  }
}
