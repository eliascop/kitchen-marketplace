import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CurrencyFormatterPipe } from "../../../core/pipes/currency-input.pipe";
import { FormatDateTimePipe } from "../../../core/pipes/format-date-time.pipe";
import { StockStatusPipe } from "../../../core/pipes/stock-status.pipe";

@Component({
  selector: 'app-product-details-modal',
  standalone: true,
  imports: [CommonModule, CurrencyFormatterPipe, FormatDateTimePipe, StockStatusPipe],
  templateUrl: './product-details-modal.component.html',
  styleUrls: ['./product-details-modal.component.scss']
})
export class ProductDetailsModalComponent {
  @Input() product: any;
  @Input() histories: any;
  
  @Output() close = new EventEmitter<void>();
}
