import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyFormatter',
  standalone: true
})
export class CurrencyFormatterPipe implements PipeTransform {

  transform(value: string | number | null | undefined): string {
    if (value == null) {
      return '';
    }
  
    const fixedValue = Number(value).toFixed(2);
  
    const numericValue = fixedValue.replace(/[^\d]/g, '');
  
    let formattedValue = '';
    const integerPart = numericValue.slice(0, -2);
    const decimalPart = numericValue.slice(-2);
  
    for (let i = integerPart.length - 1, count = 0; i >= 0; i--, count++) {
      formattedValue = integerPart[i] + formattedValue;
      if (count % 3 === 2 && i !== 0) {
        formattedValue = '.' + formattedValue;
      }
    }
  
    return 'R$ '+formattedValue + ',' + decimalPart;
  }

}