import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { User } from './core/model/user.model';
import { AuthService } from './core/service/auth.service';
import { UserService } from './core/service/user.service';
import { WalletService } from './core/service/wallet.service';
import { ToastComponent } from './shared/components/toast/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,CommonModule, ToastComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'kitchen-web';

  balance: number | null = null;
  userId!: number | null;
  user!: User;

  constructor(private authService: AuthService, 
    private router: Router, 
    private userService: UserService,
    private walletService: WalletService) {}

  ngOnInit() {
    this.authService.user$.subscribe(user => {
      if (user) {
        this.userId = user.id;
        this.userService.getUserById(this.userId!).subscribe(response => {
          this.user = response.data!;
          this.loadBalance();
        });
      } else {
        this.balance = 0;
        this.userId = null;
        this.user = undefined!;
      }
    });

    this.walletService.balance$.subscribe(balance => {
      this.balance = balance;
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

  loadBalance(): void {
    this.walletService.getBalance().subscribe({
      next: (balance) => {
        this.balance = balance;
      },
      error: (err) => {
        console.error('Erro ao carregar saldo', err);
      },
    });
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
}
