import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyFormatter',
  standalone: true
})
export class CurrencyFormatterPipe implements PipeTransform {

  transform(value: string | null | undefined): string {
    if (!value) {
      return '';
    }

    const numericValue = value.replace(/[^\d]/g, '');

    if (numericValue.length === 0) {
      return '';
    }

    let formattedValue = '';
    const integerPart = numericValue.slice(0, -2);
    const decimalPart = numericValue.slice(-2).padStart(2, '0');

    for (let i = integerPart.length - 1, count = 0; i >= 0; i--, count++) {
      formattedValue = integerPart[i] + formattedValue;
      if (count % 3 === 2 && i !== 0) {
        formattedValue = '.' + formattedValue;
      }
    }

    return formattedValue + ',' + decimalPart;
  }

}