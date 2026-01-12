import { Injectable } from "@angular/core";
import { BehaviorSubject, interval, startWith, Subscription, switchMap } from "rxjs";
import { NotificationService } from "../service/notification.service";
import { Notification } from "../model/notification.model";

@Injectable({ providedIn: "root" })
export class NotificationManager {

  private notifications$ = new BehaviorSubject<Notification[]>([]);
  private total$ = new BehaviorSubject<number>(0);
  private pollSubscription?: Subscription;

  constructor(private notificationService: NotificationService) {}

  startPolling() {
    if (this.pollSubscription) return;

    this.pollSubscription = interval(15000)
      .pipe(
        startWith(0),
        switchMap(() => this.notificationService.checkForNewNotifications())
      )
      .subscribe(res => {
        const total = res.data ?? 0;
        this.total$.next(total);
      });
  }

  stopPolling() {
    this.pollSubscription?.unsubscribe();
    this.pollSubscription = undefined;
    this.total$.next(0);
  }

  notifications(){
    return this.notifications$.asObservable();
  }

  total() {
    return this.total$.asObservable();
  }

  markAsRead(id: string) {
    return this.notificationService.markAsRead(id);
  }

  loadNotifications() {
    this.notificationService.fetchAll().subscribe(notifications => {
      this.notifications$.next(notifications.data || []);
    });
  }
}