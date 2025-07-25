import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../core/model/user.model';
import { AuthService } from '../../core/service/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  userProfile: number | undefined;
  constructor(authService: AuthService, private router: Router) {
    this.userProfile = authService.currentUserId!;
  }

  newOrder() {
    this.router.navigate(['/cart']);
  }

  ordersPainel(){
    this.router.navigate(['/orders-painel']);
  }

  orderList() {
    this.router.navigate(['/orders']);
  }

  productList() {
    this.router.navigate(['/products']);
  }

  userList() {
    this.router.navigate(['/users']);
  }

  userDetails(){
    this.router.navigate(['/user-details']);
  }
}
