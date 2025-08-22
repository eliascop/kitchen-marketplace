import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { User } from './core/model/user.model';
import { AuthService } from './core/service/auth.service';
import { CartService } from './core/service/cart.service';
import { UserService } from './core/service/user.service';
import { ToastComponent } from './shared/components/toast/toast.component';
import { SearchService } from './core/service/search.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,CommonModule, ToastComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'kitchen-web';
  totalItems = 0;
  userId!: number | null;
  user!: User;

  constructor(private authService: AuthService, 
    private cartService: CartService,
    private router: Router, 
    private searchService: SearchService,
    private userService: UserService) {}

  ngOnInit() {
    this.authService.user$.subscribe(user => {
      if (user) {
        this.userId = user.id;
        this.userService.getUserById(this.userId!).subscribe(response => {
          this.user = response.data!;
          localStorage.setItem('userData', JSON.stringify(this.user)); 
        });
        this.cartService.getCartTotalItems().subscribe();
        this.cartService.cartItemsCount$.subscribe(count => {
        this.totalItems = count;
    });
      } else {
        this.userId = null;
        this.user = undefined!;
      }
    });
  }
  goHome(event?: Event) {
    if (event) {
      event.preventDefault();
    }
    this.router.navigate(['/']);
  }

  userList(event: Event){
    if (event){
      event.preventDefault();
    }
    this.router.navigate(['/users']);
  }

  myProducts(event: Event){
    if (event){
      event.preventDefault();
    }
    this.router.navigate(['/products']);
  }

  myCart(event: Event){
    if (event){
      event.preventDefault();
    }
    this.router.navigate(['/cart']);
  }

  myOrders(event: Event) {
    if (event){
      event.preventDefault();
    }
    this.router.navigate(['/orders']);
  }

  userDetails(event: Event){
    if (event) {
      event.preventDefault();
    }
    this.router.navigate(['/user-details']);
  }

  isSeller(): boolean{
    if(!this.user)
      return false;

    return this.user.isSeller;
  }

  logout(event?: Event) {
    if (event) {
      event.preventDefault();
    }
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isHomePage(): boolean {
    return this.router.url === '/' || this.router.url === '/login' ;
  }

  onSearchChange(event: Event) {
    const term = (event.target as HTMLInputElement).value;
    this.searchService.setSearchTerm(term);
  }
}
