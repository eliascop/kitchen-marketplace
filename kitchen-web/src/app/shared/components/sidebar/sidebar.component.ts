import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { User } from '../../../core/model/user.model';
import { AuthService } from '../../../core/service/auth.service';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, MatListModule, MatIconModule, MatButtonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  @Input() collapsed = false;
  @Input() user!: User;
  @Input() totalItems = 0;
  @Output() sidenavReady = new EventEmitter<MatSidenav>();

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

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
