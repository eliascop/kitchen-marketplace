import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material/sidenav';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { HeaderComponent } from './shared/components/header/header.component';
import { User } from './core/model/user.model';
import { AuthService } from './core/service/auth.service';
import { CartService } from './core/service/cart.service';
import { UserService } from './core/service/user.service';
import { SearchService } from './core/service/search.service';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
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
  title = 'kitchen-web';
  totalItems = 0;
  user!: User;
  sidenav!: MatSidenav;
  @Output() searchChange = new EventEmitter<Event>();

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router,
    private searchService: SearchService,
    private userService: UserService
  ) {}

  onSidenavReady(sidenav: MatSidenav) {
    this.sidenav = sidenav;
  }

  toggleSidenav() {
    if (this.sidenav) {
      this.sidenav.toggle();
    }
  }

  ngOnInit() {
    this.authService.user$.subscribe(user => {
      if (user) {
        this.userService.getUserById(user.id).subscribe(response => {
          this.user = new User(response.data!);
          localStorage.setItem('userData', JSON.stringify(this.user));
          this.loadCart();
        });
      } else {
        this.user = undefined!;
        this.totalItems = 0;
      }
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

  logout(event?: Event) {
    if (event) event.preventDefault();
    this.authService.logout();
    this.totalItems = 0;
    this.router.navigate(['/login']);
  }

  isHomePage(): boolean {
    return this.router.url === '/' || this.router.url === '/login';
  }

  onSearchChange(event: Event) {
    const term = (event.target as HTMLInputElement).value;
    this.searchService.setSearchTerm(term);
  }
}
