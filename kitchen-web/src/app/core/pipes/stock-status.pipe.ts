import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'stockStatus',
  standalone: true
})
export class StockStatusPipe implements PipeTransform {

  transform(value: string): string {
    if (!value) return '';

    switch (value) {
      case 'STOCK_RESERVED':
        return 'Reservado';
      case 'STOCK_CONFIRMED':
        return 'Confirmado';
      case 'STOCK_RELEASED':
        return 'Liberado';
      default:
        return value;
    }
  }

}
