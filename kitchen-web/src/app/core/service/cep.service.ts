import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment.dev';

export const VIACEP_SERVICE_WS = environment.VIACEP_SERVICE_WS;

@Injectable({ providedIn: 'root' })
export class CepService {
  constructor(private http: HttpClient) {}

  search(cep: string): Observable<any> {
    return this.http.get(`${environment.VIACEP_SERVICE_WS}${cep}/json/`).pipe(
      catchError(() => throwError(() => new Error('Erro ao buscar CEP')))
    );
  }
}
