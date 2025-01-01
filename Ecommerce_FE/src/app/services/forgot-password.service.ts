import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordService {
  private apiUrl = 'http://localhost:8081/api/auth-forgot-password/forgot-password';

  constructor(private http: HttpClient) {}

  sendForgotPasswordEmail(email: string): Observable<string> {
    const body = { email };
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(this.apiUrl, body, { headers, responseType: 'text' });
  }
}