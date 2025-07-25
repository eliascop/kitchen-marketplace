import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-password-input',
  templateUrl: './password-input.component.html',
  styleUrl: './password-input.component.css',
  standalone: true,
  imports: [CommonModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PasswordInputComponent),
      multi: true,
    },
  ],
})
export class PasswordInputComponent implements ControlValueAccessor {
  @Input() placeholder = 'Senha';
  @Input() id = 'password';
  @Output() valueChange = new EventEmitter<string>();

  password = '';
  passwordVisible = false;
  onChange: any = () => {};
  onTouched: any = () => {};

  writeValue(value: string): void {
    this.password = value || '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  updatePassword(event: Event) {
    const target = event.target as HTMLInputElement;
    this.password = target.value;
    this.onChange(this.password);
    this.valueChange.emit(this.password);
  }
}