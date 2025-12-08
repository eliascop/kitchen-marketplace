
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { BehaviorSubject, Subject } from 'rxjs';
import { AuthUser } from '../model/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSubject = new BehaviorSubject<AuthUser | null>(this.getUserFromToken());
  private loggedIn = new BehaviorSubject<boolean>(false);
  private logoutSubject = new Subject<void>();
  
  logout$ = this.logoutSubject.asObservable();
  loggedIn$ = this.loggedIn.asObservable();

  constructor(){
    const isLogged = this.isLoggedIn();
    this.loggedIn.next(isLogged);
    if (isLogged) {
      this.userSubject.next(this.getUserFromToken());
    } else {
      localStorage.removeItem('token');
    }
  }
  
  get currentUserId(): number | null {
    return this.userSubject.value?.id ?? null;
  }

  get isSeller(): boolean {
    return this.userSubject.value?.roles.includes('SELLER') ?? false;
  }
  
  get isAdmin(): boolean {
    return this.userSubject.value?.roles.includes('ADMIN') ?? false;
  }
  
  get isUser(): boolean {
    return this.userSubject.value?.roles.includes('USER') ?? false;
  }

  get currentUserRoles(): string[] {
    return this.userSubject.value?.roles ?? [];
  }

  get normalizedRoles(): string[] {
    return (this.userSubject.value?.roles ?? []).map(r => r.replace('ROLE_', ''));
  }

  hasRole(role: string): boolean {
    return this.currentUserRoles.includes(role);
  }
  
  private getUserFromToken(): AuthUser | null {
    const token = this.getToken();
    if (!token || token === 'undefined') return null;

    if (this.isTokenExpired(token)) {
      return null;
    }
    
    try {
      const payload = jwtDecode<any>(token);
      return {
        id: payload['id'],
        user: payload['sub'],
        roles: Array.isArray(payload['roles']) ? payload['roles'] : []
      };
    } catch (e) {
      return null;
    }
  }

  notifyLogin() {
    const user = this.getUserFromToken();
    this.userSubject.next(user);
    this.loggedIn.next(!!user);
  }

  get user$() {
    return this.userSubject.asObservable();
  }
  
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isTokenExpired(token?: string): boolean {
    if (!token) token = this.getToken() || '';
    if (!token) return true;

    try {
      const decoded = jwtDecode<any>(token);
      const now = Math.floor(Date.now() / 1000);
      return decoded.exp < now;
    } catch (e) {
      return true;
    }
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(token);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.userSubject.next(null);
    this.loggedIn.next(false);
    this.logoutSubject.next()
  }

}
