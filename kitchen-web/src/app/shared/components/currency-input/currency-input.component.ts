import { CommonModule } from '@angular/common';
import { Component, forwardRef, Input, Optional, Self} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, NgControl} from '@angular/forms';

@Component({
  selector: 'app-currency-input',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './currency-input.component.html',
  styleUrls: ['./currency-input.component.css'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => CurrencyInputComponent),
    multi: true
  }]
})
export class CurrencyInputComponent implements ControlValueAccessor {
  @Input() id = '';
  @Input() label = '';
  @Input() placeholder = '';
  @Input() required = false;

  rawValue: string = '';
  onChange = (_: any) => {};
  onTouched = () => {};

  onInput(event: Event) {
    const input = (event.target as HTMLInputElement);
    const clean = input.value.replace(/[^0-9,]/g, '');
    this.rawValue = clean;
    
    const numberValue = parseFloat(clean.replace(',', '.'));
  
    if (!isNaN(numberValue)) {
      this.onChange(numberValue);
    } else {
      this.onChange(null);
    }
  }
  
  onBlur() {
    const numberValue = parseFloat(this.rawValue.replace(',', '.'));
  
    if (!isNaN(numberValue)) {
      this.rawValue = numberValue.toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
      this.onChange(numberValue);
    } else {
      this.rawValue = '';
      this.onChange(null);
    }
    this.onTouched();
  }
  
  onFocus() {
    if (this.rawValue) {
      this.rawValue = this.rawValue.replace(/\./g, '');
    }
  }

  writeValue(value: any): void {
    this.rawValue = this.formatCurrency(value);
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  formatCurrency(value: string | number): string {
    if (!value) return '';
    const number = typeof value === 'string' ? parseFloat(value) : value;
    return number.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2
    });
  }

  handleInput(event: Event) {
    const input = (event.target as HTMLInputElement).value;
    const clean = input.replace(/[^\d,]/g, '').replace(',', '.');
  
    const number = parseFloat(clean);
    this.rawValue = this.formatCurrency(number);
  
    if (!isNaN(number)) {
      this.onChange(number);
    } else {
      this.onChange(null);
    }
  }
}
