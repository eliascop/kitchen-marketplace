import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, ViewChild, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { User } from '../../../core/model/user.model';
import { AuthService } from '../../../core/service/auth.service';
import { RouterOutlet } from '@angular/router';
import { ToastComponent } from '../toast/toast.component';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    ToastComponent
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements AfterViewInit {
  @Input() user!: User;
  @Input() totalItems = 0;
  @ViewChild('sidenav') sidenav!: MatSidenav;
  @Input() isHomePage = false;
  @Output() sidenavReady = new EventEmitter<MatSidenav>();

  constructor(private router: Router, private authService: AuthService) {}

  ngAfterViewInit() {
    if (this.sidenav) {
      this.sidenavReady.emit(this.sidenav);
    }
  }

  toggleSidenav() {
    this.sidenav.toggle();
  }

  goHome(event: Event) {
    event.preventDefault();
    this.router.navigate(['/']);
  }

  manageCoupons(event: Event) {
    event.preventDefault();
    this.router.navigate(['/manage-coupons']);
  }

  userList(event: Event) {
    event.preventDefault();
    this.router.navigate(['/users']);
  }

  myProducts(event: Event) {
    event.preventDefault();
    this.router.navigate(['/products']);
  }

  myCart(event: Event) {
    event.preventDefault();
    this.router.navigate(['/cart']);
  }

  myOrders(event: Event) {
    event.preventDefault();
    this.router.navigate(['/orders']);
  }

  userDetails(event: Event) {
    event.preventDefault();
    this.router.navigate(['/user-details']);
  }

  isSeller(): boolean {
    return !!this.user?.isSeller;
  }

  logout(event: Event) {
    event.preventDefault();
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
