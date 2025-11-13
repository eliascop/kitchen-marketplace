import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
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
  isCollapsed = true;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router,
    private searchService: SearchService,
    private userService: UserService
  ) {}

  expandMenu() {
    this.isCollapsed = false;
  }

  collapseMenu() {
    this.isCollapsed = true;
  }

  ngOnInit() {
    this.authService.user$.subscribe(user => {
      if (user) {
        console.log('User logged in:', user);
        this.userService.getUserById(user.id).subscribe(response => {
          this.user = new User(response.data!);
          console.log('Fetched user data:', this.user);
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

  onSearchChange(event: Event) {
    this.searchService.setSearchTerm(event);
  }
}
