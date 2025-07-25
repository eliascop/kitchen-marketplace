import { Injectable } from '@angular/core';
import { map } from 'rxjs';
import { DataService } from './data.service';
import { ServiceResponse } from './model/http-options-request.model';
import { environment } from '../../../environments/environment.dev';
import { User } from '../model/user.model';

export const AUTH_REST_SERVICE = environment.AUTH_REST_SERVICE;
export const USER_REST_SERVICE = environment.USER_REST_SERVICE;

@Injectable({
  providedIn: 'root'
})

export class UserService {

  constructor(private dataService: DataService) { }

  login(userData: any): ServiceResponse<User> {
    return this.dataService.post<any>({
      url: `${AUTH_REST_SERVICE}/login`,
      body: userData
    }).pipe(
      map(response => {
        if (response.error) {
          throw response.error;
        }
        return response.data!;
      })
    );
  }
  
  getUserById(userId: number): ServiceResponse<User> {
    return this.dataService.get<User>({
      url: `${USER_REST_SERVICE}/${userId}`
    });
  }
  getUsers(): ServiceResponse<User[]> {
    return this.dataService.get<User[]>({
      url: `${USER_REST_SERVICE}`
    });
  }

  createUser(userData: any): ServiceResponse<User> {
    return this.dataService.post<User>({
      url: `${AUTH_REST_SERVICE}/signup`,
      body: userData,
    });
  }

  updateUser(userData: any): ServiceResponse<User> {
    return this.dataService.put<User>({
      url: `${USER_REST_SERVICE}`,
      body: userData,
    });
  }

  deleteUser(userId: number): ServiceResponse<User>{
    return this.dataService.delete<User>({
      url: `${USER_REST_SERVICE}/${userId}`
    });
  }
}