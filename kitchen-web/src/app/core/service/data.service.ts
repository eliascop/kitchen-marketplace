import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import {
  HttpOptionsRequest,
  ResponseData,
  RequestVerbs,
  ServiceResponse,
} from './model/http-options-request.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class DataService {
  constructor(private http: HttpClient, private authService: AuthService) {}

  getHeaders() {
    return { Authorization: 'Bearer ' + this.authService.getToken() };
  }

  public callBackend<T>(
    method: RequestVerbs,
    options: HttpOptionsRequest
  ): Observable<ResponseData<T>> {
    const { url, ...restOptions } = options;

    return this.http
      .request(method, url, {
        ...restOptions,
        headers: {
          ...this.getHeaders(),
          ...restOptions.headers,
        },
        observe: 'response',
      })
      .pipe(
        map((resp) => ({
          data: resp.body ? (resp.body as T) : null,
          error: undefined,
        })),
        catchError((error) => {
          return throwError(() => ({ data: null, error: error }));
        })
      );
  }

  public get<T>(options: Omit<HttpOptionsRequest, 'body'>): ServiceResponse<T> {
    return this.callBackend<T>('GET', options);
  }

  public post<T>(options: HttpOptionsRequest): ServiceResponse<T> {
    return this.callBackend<T>('POST', options);
  }

  public put<T>(options: HttpOptionsRequest): ServiceResponse<T> {
    return this.callBackend<T>('PUT', options);
  }

  public patch<T>(options: HttpOptionsRequest): ServiceResponse<T> {
    return this.callBackend<T>('PATCH', options);
  }

  public delete<T>(options: HttpOptionsRequest): ServiceResponse<T> {
    return this.callBackend<T>('DELETE', options);
  }
}