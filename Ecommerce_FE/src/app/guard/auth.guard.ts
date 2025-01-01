import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { toArray } from 'rxjs/operators';

export const authGuard: CanActivateFn = async (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  try {
    // Kiểm tra phiên đăng nhập
    const isLoggedIn = await lastValueFrom(authService.validateSession());
    console.log('isLoggedIn:', isLoggedIn);

    const roles = await lastValueFrom(authService.getUserRoles());
    console.log('User Roles:', roles);

    if (!isLoggedIn) {
      router.navigate(['/login']);
      return false;
    }

    // Kiểm tra role có quyền truy cập
    if (roles.includes('admin')) {
      return true;
    } else {
      router.navigate(['/access-denied']);
      return false;
    }
  } catch (error) {
    console.error('Error validating session or roles:', error);
    router.navigate(['/login']);
    return false;
  }
};

