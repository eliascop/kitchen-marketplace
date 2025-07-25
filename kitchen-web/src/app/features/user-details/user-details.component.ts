import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../core/service/user.service';
import { PhoneNumberPipe } from "../../core/pipes/phone-number.pipe";
import { ToastService } from '../../core/service/toast.service';
import { CepService } from '../../core/service/cep.service';
import { User } from '../../core/model/user.model';
import { AuthService } from '../../core/service/auth.service';
import { Address } from '../../core/model/address.model';

@Component({
  selector: 'app-user-details',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, PhoneNumberPipe],
  templateUrl: './user-details.component.html',
  styleUrl: './user-details.component.css'
})
export class UserDetailsComponent implements OnInit {
  userForm: FormGroup;
  phoneNumberPipe = new PhoneNumberPipe();
  cepLoading = false;
  cepErrorMessage: string | null = null;
  errorMessage = null;
  hideBillingForm = true;
  userId:number = 0;
  userAuth: User | null = null;

  constructor(private fb: FormBuilder, 
    private authService: AuthService,
    private userService: UserService, 
    private cepService: CepService,
    private toast: ToastService) {
    this.userForm = this.fb.group({
      id: [''],
      login: ['', Validators.required],
      name: ['', Validators.required],
      phone: ['', Validators.required],
      email: ['', Validators.required],

      shippingZipCode: ['',[Validators.required, Validators.pattern(/^\d{5}-?\d{3}$/)]],
      shippingStreet: ['', Validators.required],
      shippingNumber: [''],
      shippingComplement: [''],
      shippingDistrict: ['', Validators.required],      
      shippingCity: ['', Validators.required],
      shippingState: ['', Validators.required],
      shippingCountry: ['Brazil'],

      billingZipCode: [''],
      billingStreet: [''],
      billingNumber: [''],
      billingComplement: [''],
      billingDistrict: [''],
      billingCity: [''],
      billingState: [''],
      billingCountry: ['Brazil'],
    });
    this.setBillingValidators(false);
  } 

  ngOnInit() {
    this.loadUserDetails();
  }

  private loadUserDetails() {
    this.userId = this.authService.currentUserId!;
    this.userService.getUserById(this.userId).subscribe(data => {
      this.userAuth = new User(data.data!);
  
      this.userForm.patchValue({
        id: this.userAuth.id,
        login: this.userAuth.login,
        name: this.userAuth.name,
        phone: this.userAuth.phone,
        email: this.userAuth.email
      });
  
      this.patchAddressToForm('shipping', this.userAuth.shippingAddress);
      this.patchAddressToForm('billing', this.userAuth.billingAddress);

      const shipping = this.userAuth.addresses.find((a: any) => a.type === 'SHIPPING');
      const billing = this.userAuth.addresses.find((a: any) => a.type === 'BILLING');

      if (shipping && billing) {
        const isSameAddress = this.compareAddresses(shipping, billing);
        this.hideBillingForm = isSameAddress;

        if (isSameAddress) {
          this.copyShippingToBilling();
          this.setBillingValidators(false);
        } else {
          this.setBillingValidators(true);
        }
      }
    });
  }

  private setBillingValidators(enable: boolean): void {
    const billingControls = [
      'billingStreet',
      'billingDistrict',
      'billingCity',
      'billingState',
      'billingZipCode',
      'billingCountry'
    ];
  
    billingControls.forEach(controlName => {
      const control = this.userForm.get(controlName);
      if (control) {
        if (enable) {
          control.setValidators(Validators.required);
        } else {
          control.clearValidators();
          control.setValue('');
        }
        control.updateValueAndValidity();
      }
    });
  }

  onCheckboxChange(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    this.hideBillingForm = checked;
  
    if (checked) {
      this.copyShippingToBilling();
      this.setBillingValidators(false);
    } else {
      this.setBillingValidators(true);
    }
  }

  copyShippingToBilling() {
    const shippingAddress = this.extractAddressFromForm('shipping');
    this.patchAddressToForm('billing', shippingAddress);
  }
  

  onSubmit() {
    this.errorMessage = null;
  
    if (this.userForm.valid) {
      const formData = this.userForm.value;

      if (this.hideBillingForm) {
        this.copyShippingToBilling();
      }
  
      const payload = new User({
        id: formData.id,
        login: formData.login,
        name: formData.name,
        phone: formData.phone,
        email: formData.email,
        addresses: [
          this.extractAddressFromForm('shipping'),
          this.extractAddressFromForm('billing')
        ]
      });
  
      this.userService.updateUser(payload).subscribe({
        next: () => this.toast.show('Usuário atualizado com sucesso.'),
        error: (err) => {
          this.errorMessage = err.error?.error || 'Erro ao atualizar os dados do usuário';
        }
      });
    } else {
      Object.keys(this.userForm.controls).forEach(key =>
        this.userForm.controls[key].markAsTouched()
      );
    }
  }
  

  updateFormControl(event: any) {
    const inputElement = event.target as HTMLInputElement;
    const rawValue = this.phoneNumberPipe.unmask(inputElement.value);
    this.userForm.controls['phone'].setValue(rawValue);
  }

  onCepBlur(type: 'shipping' | 'billing') {
    const cepRaw = this.userForm.get(`${type}ZipCode`)?.value;
    const cep = cepRaw?.replace(/\D/g, '');
  
    this.cepErrorMessage = null;
    this.cepLoading = true;
  
    if (cep?.length !== 8) {
      this.cepLoading = false;
      this.cepErrorMessage = 'CEP inválido';
      return;
    }
  
    this.cepService.search(cep).subscribe({
      next: (data) => {
        this.cepLoading = false;
  
        if (data.erro) {
          this.cepErrorMessage = 'CEP não encontrado.';
        } else {
          this.userForm.patchValue({
            [`${type}Street`]: data.logradouro,
            [`${type}District`]: data.bairro,
            [`${type}City`]: data.localidade,
            [`${type}State`]: data.uf,
            [`${type}Country`]: 'Brasil'
          });
        }
      },
      error: () => {
        this.cepLoading = false;
        this.cepErrorMessage = 'Erro ao buscar o CEP.';
      }
    });
  }  

  private patchAddressToForm(type: 'shipping' | 'billing', address?: Address) {
    if (!address) return;
    this.userForm.patchValue({
      [`${type}ZipCode`]: address.zipCode,
      [`${type}Street`]: address.street,
      [`${type}Number`]: address.number,
      [`${type}Complement`]: address.complement,
      [`${type}District`]: address.district,
      [`${type}City`]: address.city,
      [`${type}State`]: address.state,
      [`${type}Country`]: address.country
    });
  }  

  private extractAddressFromForm(type: 'shipping' | 'billing'): Address {
    return new Address({
      type: type.toUpperCase() as 'SHIPPING' | 'BILLING',
      zipCode: this.userForm.get(`${type}ZipCode`)?.value,
      street: this.userForm.get(`${type}Street`)?.value,
      number: this.userForm.get(`${type}Number`)?.value,
      complement: this.userForm.get(`${type}Complement`)?.value,
      district: this.userForm.get(`${type}District`)?.value,
      city: this.userForm.get(`${type}City`)?.value,
      state: this.userForm.get(`${type}State`)?.value,
      country: this.userForm.get(`${type}Country`)?.value
    });
  }
  
  private compareAddresses(a: any, b: any): boolean {
    const keysToCompare = ['zipCode', 'street', 'number', 'complement', 'district', 'city', 'state', 'country'];
    return keysToCompare.every(key => (a[key] || '') === (b[key] || ''));
  }

}
