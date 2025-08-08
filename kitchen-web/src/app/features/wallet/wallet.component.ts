import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';

import { CreditRequest, DebitRequest, Wallet, WalletTransaction } from '../../core/model/wallet.model';
import { WalletService } from '../../core/service/wallet.service';
import { ToastService } from '../../core/service/toast.service';
import { FormatDateTimePipe } from '../../core/pipes/format-date-time.pipe';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    FormatDateTimePipe,
    CurrencyInputComponent,
    CurrencyFormatterPipe
],
  templateUrl: './wallet.component.html',
  styleUrls: ['./wallet.component.css'],
  animations: [
    trigger('accordion', [
      state('open', style({ height: '*', opacity: 1, overflow: 'auto' })),
      state('closed', style({ height: '0px', opacity: 0, overflow: 'hidden' })),
      transition('open <=> closed', [animate('300ms ease-in-out')])
    ])
  ]
})
export class WalletComponent implements OnInit {
  walletForm: FormGroup;
  wallet: Wallet | null = null;
  transactions: WalletTransaction[] = [];
  debitRequest!: DebitRequest;

  isAddBalanceOpen = true;
  isTransactionsOpen = false;

  constructor(
    private route: ActivatedRoute,
    private toast: ToastService,
    private location: Location,
    private walletService: WalletService,
    private fb: FormBuilder
  ) {
    this.walletForm = this.fb.group({
      amount: [null, Validators.required],
      description: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    this.loadWallet();
    this.loadCreditStatus();
  }

  toggleSection(section: 'balance' | 'transactions') {
    if (section === 'balance') {
      this.isAddBalanceOpen = !this.isAddBalanceOpen;
    } else {
      this.isTransactionsOpen = !this.isTransactionsOpen;
    }
  }

  loadWallet() {
    this.walletService.getWallet().subscribe(wallet => this.wallet = wallet.data!);
    this.walletService.getTransactions().subscribe(txs => this.transactions = txs.data!);
  }

  loadCreditStatus() {
    let message = '';
    this.route.queryParamMap.subscribe(params => {
      const messageParam = params.get('paymentStatus');
      if (messageParam === 'succeeded') {
        message = 'Crédito efetuado com sucesso.';
      } else if (messageParam === 'cancelled') {
        message = 'Crédito cancelado pelo usuário.';
      } else if (messageParam === 'errortocancel') {
        message = 'Ocorreu um erro ao cancelar pagamento.';
      } else if (messageParam) {
        message = 'Erro desconhecido: ' + messageParam;
      }

      if (message) {
        this.toast.show(message);
        this.toggleSection('balance');
        this.toggleSection('transactions');
        this.location.replaceState('/wallet');
      }
    });
  }

  credit() {
    if (this.walletForm.valid && this.walletForm.value.amount > 0) {
      const creditRequest: CreditRequest = this.walletForm.value;

      this.walletService.credit(creditRequest).subscribe({
        next: response => {
          window.location.href = response.data!.message;
          this.loadWallet();
        },
        error: err => {
          this.toast.show('Ocorreu um erro ao iniciar pagamento.');
          console.error(err);
        }
      });
    }
  }

  debit() {
    if (this.debitRequest.amount > 0) {
      this.walletService.debit(this.debitRequest)
        .subscribe(() => this.loadWallet());
    }
  }
}
