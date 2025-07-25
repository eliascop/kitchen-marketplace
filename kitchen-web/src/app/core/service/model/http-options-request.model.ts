import {
    HttpContext,
    HttpErrorResponse,
    HttpHeaders,
    HttpParams,
  } from '@angular/common/http';
  import { Observable } from 'rxjs';
  
  export type RequestVerbs = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  
  export interface HttpOptionsRequest {
    url: string;
    headers?:
      | HttpHeaders
      | {
          [header: string]: string | string[];
        };
    context?: HttpContext;
    params?:
      | HttpParams
      | {
          [param: string]:
            | string
            | number
            | boolean
            | (string | number | boolean)[];
        };
    reportProgress?: boolean;
    responseType?: 'json' | 'blob';
    withCredentials?: boolean;
    body?: any | null;
  }
  
  export type HttpRequestError = <T>() => T | void;
  
  export interface ResponseData<T> {
    data: T | null;
    error?: HttpErrorResponse;
  }
  
  export type ServiceResponse<T> = Observable<ResponseData<T>>;