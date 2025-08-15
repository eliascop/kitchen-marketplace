
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { BehaviorSubject } from 'rxjs';

export interface AuthUser {
  id: number
  user: string
  seller: boolean
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSubject = new BehaviorSubject<AuthUser | null>(this.getUserFromToken());
  private loggedIn = new BehaviorSubject<boolean>(false);
  loggedIn$ = this.loggedIn.asObservable();

  constructor(){
    const isLogged = this.isLoggedIn();
    this.loggedIn.next(isLogged);
    if (isLogged) {
      this.userSubject.next(this.getUserFromToken());
    }
  }
  
  get currentUserId(): number | null {
    return this.userSubject.value?.id ?? null;
  }

  get currentUserSeller(): boolean | null {
    return this.userSubject.value?.seller ?? null;
  }
  
  private getUserFromToken(): AuthUser | null {
    const token = this.getToken();
    if (!token || token === 'undefined') return null;

    try {
      const payload = jwtDecode<any>(token);
      return {
        id: payload['id'],
        user: payload['sub'],
        seller: payload['seller']
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
  }

}
