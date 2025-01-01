import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { map, Observable } from 'rxjs';
import { KeycloakServiceWrapper } from '../keycloak/keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrlLogin = 'http://localhost:8081/api/auth/login';
  private registerApiUrl = 'http://localhost:8081/api/users/register';
  private validateSessionUrl = 'http://localhost:8081/api/auth/validate-session';
  private userRolesUrl = 'http://localhost:8081/api/auth/user-roles';

  constructor(
    private http: HttpClient,
    private router: Router,
    private keycloakServiceWrapper: KeycloakServiceWrapper
  ) {}

  register(username: string, password: string, email: string): Observable<any> {
    const body = { username, password, email };
    return this.http.post<any>(this.registerApiUrl, body);
  }  

  login(username: string, password: string): Observable<any> {
    const body = { username, password };
    return this.http.post<any>(this.apiUrlLogin, body, {
      withCredentials: true, 
    });
  }

  validateSession(): Observable<boolean> {
    return this.http.get<boolean>(this.validateSessionUrl, { withCredentials: true });
  }

  getUserRoles(): Observable<string[]> {
    return this.http.get<any>(this.userRolesUrl, { withCredentials: true }).pipe(
      map((response) => response.roles || [])
    );
  }    

  logout() {
    this.http.post('http://localhost:8081/api/auth/logout', {}, {
      withCredentials: true,
    }).subscribe(() => {
      this.keycloakServiceWrapper.logout();
      this.router.navigate(['/']);
    });
  }
  
  keycloakLogin() {
    this.keycloakServiceWrapper.login();
  }
}
