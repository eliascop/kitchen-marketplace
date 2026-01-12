import { Injectable } from "@angular/core";
import { Subject } from "rxjs";

@Injectable({ providedIn: 'root' })
export class NotificationEventService {
  private newNotification$ = new Subject<void>();

  emitNew() {
    this.newNotification$.next();
  }
}