import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../core/service/user.service';
import { Router } from '@angular/router';
import { PasswordInputComponent } from '../../shared/components/password-input/password-input.component';
import { PhoneNumberPipe } from "../../core/pipes/phone-number.pipe";
import { ToastService } from '../../core/service/toast.service';

@Component({
  selector: 'app-new-user',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, PasswordInputComponent, PhoneNumberPipe],
  templateUrl: './new-user.component.html',
  styleUrl: './new-user.component.css'
})
export class NewUserComponent {
  userForm: FormGroup;
  phoneNumberPipe = new PhoneNumberPipe();
  errorMessage = null;

  constructor(private fb: FormBuilder, private userService: UserService, private router: Router, private toast: ToastService) {
    this.userForm = this.fb.group({
      login: ['', Validators.required],
      name: ['', Validators.required],
      phone: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required],
      passwordconfirm: ['', Validators.required],
    });
  } 

  onSubmit() {
    this.errorMessage = null;
    if (this.userForm.valid) {
      const formData = { ...this.userForm.value };

      this.userService.createUser(formData).subscribe({
        next: (res: any) => {
          this.router.navigate(['/login']);
          this.userForm.reset();
          this.toast.show('Usuário cadatrado com sucesso.');
        },
        error: (err) => {
          this.errorMessage = err.error?.error || 'Erro ao efetuar login';
        }
      });
    } else {
      Object.keys(this.userForm.controls).forEach(key => {
        this.userForm.controls[key].markAsTouched();
      });
    }
  }

  updateFormControl(event: any) {
    const inputElement = event.target as HTMLInputElement;
    const rawValue = this.phoneNumberPipe.unmask(inputElement.value);
    this.userForm.controls['phone'].setValue(rawValue);
  }

}
