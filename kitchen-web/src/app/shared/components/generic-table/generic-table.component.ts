import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

type SortDir = 'asc' | 'desc';

export interface TableColumn<T> {
  key: keyof T | string;
  label: string;
  sortable?: boolean;
  formatter?: (value: any, row: T) => string | number | null | undefined;
}

@Component({
  selector: 'app-generic-table',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './generic-table.component.html',
  styleUrls: ['./generic-table.component.scss']
})
export class GenericTableComponent<T> {
  @Input() columns: TableColumn<T>[] = [];
  @Input() data: T[] = [];
  @Input() loading = false;

  @Input() page = 1;
  @Input() pageSize = 10;
  @Input() totalItems = 0;

  @Input() sortField: string = '';
  @Input() sortDir: SortDir = 'asc';

  @Output() paginate = new EventEmitter<{ page: number; pageSize: number }>();
  @Output() sortChange = new EventEmitter<{ field: keyof T | null; direction: SortDir | null }>();

  @Output() view = new EventEmitter<T>();
  @Output() remove = new EventEmitter<T>();
  @Output() edit = new EventEmitter<T>();
  @Output() activate = new EventEmitter<T>();

  get displayedColumns(): string[] {
    return [...this.columns.map(c => c.key as string), 'actions'];
  }

  onPage(e: PageEvent) {
    this.paginate.emit({
      page: e.pageIndex + 1,
      pageSize: e.pageSize
    });
  }

  onSort(e: Sort) {
    const field = e.direction ? (e.active as keyof T) : null;
    const direction = e.direction ? (e.direction as SortDir) : null;
    this.sortChange.emit({ field, direction });
  }

  getValue(row: T, column: TableColumn<T>) {
    const value = (row as any)[column.key];
    return column.formatter ? column.formatter(value, row) : value;
  }
}