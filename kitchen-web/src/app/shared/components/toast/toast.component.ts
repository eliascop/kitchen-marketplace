import { Component } from '@angular/core';
import { ToastService } from '../../../core/service/toast.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css',
  imports: [CommonModule]
})
export class ToastComponent {
  constructor(public toast: ToastService) {}
}
