import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { User } from '../../core/model/user.model';
import { UserService } from '../../core/service/user.service';
import { PhoneNumberPipe } from "../../core/pipes/phone-number.pipe";
import { ToastService } from '../../core/service/toast.service';
import { Subscription, take } from 'rxjs';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, PhoneNumberPipe],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit, OnDestroy {

  userId: number = 1;
  users: User[] = [];
  selectedOrder: any = null;

  private sub?: Subscription;

  constructor(
    private userService: UserService, 
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  loadUsers() {
    this.sub = this.userService.getUsers()
      .pipe(take(1))
      .subscribe(data => {
        this.users = data.data ?? [];
      });
  }

  removeUser(userId: number | null): void {
    if (!userId) return;
    
    const confirmed = window.confirm('Tem certeza de que quer excluir esse usuário ?');
    if (!confirmed) return;

    this.userService.deleteUser(userId).pipe(take(1)).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== userId);
        this.toast.show("Usuário removido com sucesso!");
      },
      error: () => {
        this.toast.show("Ocorreu um erro ao excluir o usuário.");
      }
    });
  }

  goToNewUser(){
    this.router.navigate(['/new-user']);
  }

  openModal(order: any) {
    this.selectedOrder = order;
  }

  closeModal() {
    this.selectedOrder = null;
  }
}