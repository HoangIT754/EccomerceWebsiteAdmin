import { ApplicationConfig, provideZoneChangeDetection, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { routes } from './app.routes';
import { KeycloakServiceWrapper } from './services/keycloak/keycloak.service';
import { authInterceptor } from './auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: (keycloakServiceWrapper: KeycloakServiceWrapper) => () => keycloakServiceWrapper.init(),
      deps: [KeycloakServiceWrapper],
      multi: true,
    },
  ],
};