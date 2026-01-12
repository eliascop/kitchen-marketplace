import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, OnChanges } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { RouterModule } from '@angular/router';
import { User } from '../../../core/model/user.model';
import { Observable } from 'rxjs';
import { NotificationManager } from '../../../core/manager/notification.manager';
import { Notification } from '../../../core/model/notification.model';
import { FormatDateTimePipe } from "../../../core/pipes/format-date-time.pipe";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    RouterModule,
    FormatDateTimePipe
],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnChanges {

  @Input() user!: User | undefined;
  @Input() totalItems = 0;
  @Input() totalNotifications = 0;
  @Input() isHomePage = false;
  @Input() newNotificationTrigger = 0;

  @Output() goHome = new EventEmitter<Event>();
  @Output() searchChange = new EventEmitter<Event>();

  constructor(private notificationManager: NotificationManager){}

  shakeBell = false;
  notifications$!: Observable<Notification[]>;

  ngOnChanges(): void {
    if (this.newNotificationTrigger > 0) {
      this.triggerBellAnimation();
    }
  }

  onGoHome(event: Event): void {
    this.goHome.emit(event);
  }

  onSearchEnter(event?: Event): void {
    this.searchChange.emit(event);
  }

  onBellClick(): void {
    this.notificationManager.loadNotifications();
    this.notifications$ = this.notificationManager.notifications();
  }

  private triggerBellAnimation(): void {
    this.shakeBell = false;

    setTimeout(() => {
      this.shakeBell = true;
    });

    setTimeout(() => {
      this.shakeBell = false;
    }, 700);
  }
}
