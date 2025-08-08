import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CurrencyFormatterPipe } from "../../../core/pipes/currency-input.pipe";

@Component({
  selector: 'app-product-details-modal',
  standalone: true,
  imports: [CommonModule, CurrencyFormatterPipe],
  templateUrl: './product-details-modal.component.html',
  styleUrls: ['./product-details-modal.component.scss']
})
export class ProductDetailsModalComponent {
  @Input() product: any;
  @Output() close = new EventEmitter<void>();
}
