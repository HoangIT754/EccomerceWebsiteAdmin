import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
 
@Injectable({
  providedIn: 'root',
})
export class KeycloakServiceWrapper {
  private tokenKey = 'token';

  constructor(private keycloakService: KeycloakService) {}

  async init(): Promise<void> {
    try {
      await this.keycloakService.init({
        config: {
          url: 'http://localhost:9091/',
          realm: 'Ecommerce_Website',
          clientId: 'ecommerce',
        },
        initOptions: {
          onLoad: 'check-sso',
          checkLoginIframe: false,
        },
      });
      const token = await this.keycloakService.getToken();
      if (token) {
        localStorage.setItem(this.tokenKey, token);
      }
      console.log('Keycloak initialized');
    } catch (error) {
      console.error('Keycloak initialization failed', error);
    }
  }

  async login(): Promise<void> {
    try {
      await this.keycloakService.login({
        redirectUri: window.location.origin + '/home',
      });
      // const token = await this.keycloakService.getToken();
      // if (token) {
      //   localStorage.setItem(this.tokenKey, token);
      // }
      console.log("Logged in with Keycloak");
    } catch (error) {
      console.error('Keycloak login failed', error);
    }
  }

  async logout(): Promise<void> {
    try {
      const logoutUrl = `http://localhost:9091/realms/Ecommerce_Website/protocol/openid-connect/logout?redirect_uri=${window.location.origin}`;
      localStorage.removeItem('token');
      sessionStorage.clear();
      localStorage.clear();

      window.location.href = logoutUrl;
      this.keycloakService.logout("http://localhost:4200");
    } catch (error) {
      console.error('Logout failed:', error);
    }
  }
}
