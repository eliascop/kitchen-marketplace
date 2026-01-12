import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, ViewChild, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { MatSidenav, MatSidenavContainer, MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ToastComponent } from './shared/components/toast/toast.component';
import { HeaderComponent } from './shared/components/header/header.component';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { AuthService } from './core/service/auth.service';
import { CartService } from './core/service/cart.service';
import { UserService } from './core/service/user.service';
import { SearchService } from './core/service/search.service';
import { User } from './core/model/user.model';
import { NotificationManager } from './core/manager/notification.manager';
import { Subscription } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthUser } from './core/model/auth.model';
import { NotificationEventService } from './core/event/notification.event.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    ToastComponent,
    HeaderComponent,
    SidebarComponent,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  @ViewChild('sidenav', { static: false }) sidenav!: MatSidenav;
  @ViewChild(MatSidenavContainer) sidenavContainer!: MatSidenavContainer;
  user!: User;
  totalItems = 0;
  totalNotifications = 0;
  lastNotificationTotal = 0;
  isCollapsed = true;

  private notificationSubscription?: Subscription;
  private readonly destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private notificationManager: NotificationManager,
    private router: Router,
    private searchService: SearchService,
    private userService: UserService,
    private notificationEvents: NotificationEventService
  ) {}

  expandMenu() {
    this.isCollapsed = false;
  }

  collapseMenu() {
    this.isCollapsed = true;
  }

  ngOnInit() {
    this.authService.user$
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap((authUser) => {
          if (!authUser) this.handleLoggedOutState();
        }),
        filter((authUser): authUser is AuthUser => authUser !== null),
        switchMap((authUser) => this.userService.getUserById(authUser.id)),
        tap((response) => this.handleLoggedInState(response.data))
      )
      .subscribe();
  }

  private handleLoggedInState(userData: any) {
    this.user = new User(userData);
    localStorage.setItem('userData', JSON.stringify(this.user));
    this.loadCart();
    this.initNotifications();
  }

  private handleLoggedOutState() {
    this.user = undefined!;
    this.notificationManager.stopPolling();
    this.notificationSubscription?.unsubscribe();
    this.notificationSubscription = undefined;

    this.totalNotifications = 0;
    this.lastNotificationTotal = 0;
    this.totalItems = 0;
  }

  private initNotifications() {
    this.notificationManager.startPolling();
    this.notificationSubscription?.unsubscribe();
    this.notificationSubscription = new Subscription();
  
    this.notificationManager.total().subscribe(total => {
      if (total > this.lastNotificationTotal) {
        this.notificationEvents.emitNew();
      }

      this.lastNotificationTotal = total;
      this.totalNotifications = total;
    });
  }

  private loadCart() {
    this.cartService.getCartTotalItems().subscribe();
    this.cartService.cartItemsCount$.subscribe(count => {
      this.totalItems = count;
    });
  }

  goHome(event?: Event) {
    if (event) event.preventDefault();
    this.router.navigate(['/']);
  }

  onSearchChange(event: Event) {
    this.searchService.setSearchTerm(event);
  }
}
