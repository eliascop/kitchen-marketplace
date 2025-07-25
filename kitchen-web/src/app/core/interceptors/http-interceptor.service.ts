import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { ToastService } from '../service/toast.service';

@Injectable()
export class HttpInterceptorService implements HttpInterceptor {

  constructor(private authService: AuthService, private router: Router, private toast: ToastService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

    const clonedReq = token ? req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    }) : req;

    return next.handle(clonedReq).pipe(
      catchError((error: HttpErrorResponse) => {
        const statusCode = error.status;
        if (statusCode === 0) {
          this.toast.show('Você foi deslogado!');
          this.router.navigate(['/login']);
        }

        return throwError(() => error);
      })
    );
  }
}
