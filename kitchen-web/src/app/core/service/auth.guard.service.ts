import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

export const AuthGuardService = (allowedRoles?: string[]) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  if (!Array.isArray(allowedRoles) || allowedRoles.length === 0) {
    return true;
  }

  const userRoles = auth.normalizedRoles;
  const hasPermission = allowedRoles.some(role => userRoles.includes(role));

  if (!hasPermission) {
    router.navigate(['/unauthorized']);
    return false;
  }

  return true;
};
