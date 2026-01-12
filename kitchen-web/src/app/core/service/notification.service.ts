import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.dev";
import { DataService } from "./data.service";
import { ServiceResponse } from "./model/http-options-request.model";
import { Notification } from "../model/notification.model";

export const NOTIFICATION_SERVICE_REST = environment.NOTIFICATION_REST_SERVICE;

@Injectable({
  providedIn: 'root',
})
export class NotificationService {

  constructor(private dataService: DataService) {}

  checkForNewNotifications(): ServiceResponse<number> {
    return this.dataService.get<number>({
      url: `${NOTIFICATION_SERVICE_REST}/unread-count`,
    });
  }
        
  fetchAll(): ServiceResponse<Notification[]> {
    return this.dataService.get<Notification[]>({
      url: `${NOTIFICATION_SERVICE_REST}`,
    });
  }

  markAsRead(id: string): ServiceResponse<Notification> {
    return this.dataService.patch<Notification>({
        url: `${NOTIFICATION_SERVICE_REST}/${id}/read`,
    });
  }
}