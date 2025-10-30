import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { User } from '../../../core/model/user.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  @Input() user!: User | undefined;
  @Input() totalItems = 0;
  @Input() isHomePage = false;
  @Output() goHome = new EventEmitter<Event>();
  @Output() searchChange = new EventEmitter<Event>();

  onGoHome(event: Event) {
    this.goHome.emit(event);
  }

  onSearchEnter(event?: Event) {
    this.searchChange.emit(event);
  }
}
