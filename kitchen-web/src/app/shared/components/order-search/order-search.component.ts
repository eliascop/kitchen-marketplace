import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { OrderStatus } from '../../../core/enums/order-status.enum';

@Component({
  selector: 'app-order-search',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './order-search.component.html',
  styleUrls: ['./order-search.component.css'],
})
export class OrderSearchComponent implements OnInit {
  @Output() search = new EventEmitter<{ term: string; status: string }>();

  searchTerm = '';
  selectedStatus = '';
  orderStatusList = Object.values(OrderStatus);

  private searchSubject = new Subject<string>();

  ngOnInit() {
    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((term) => {
        this.searchTerm = term;
        this.emitSearch();
      });
  }

  onInputChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchSubject.next(input.value);
  }

  onStatusChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.selectedStatus = select.value;
    this.emitSearch();
  }

  emitSearch() {
    this.search.emit({
      term: this.searchTerm.trim(),
      status: this.selectedStatus,
    });
  }
}
