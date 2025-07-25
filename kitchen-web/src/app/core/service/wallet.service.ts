import { Injectable } from '@angular/core';
import { CreditRequest, DebitRequest, Wallet, WalletTransaction } from '../model/wallet.model';
import { environment } from '../../../environments/environment.dev';
import { DataService } from './data.service';
import { ServiceResponse } from './model/http-options-request.model';
import { BehaviorSubject, map, tap } from 'rxjs';

export const WALLET_REST_SERVICE = environment.WALLET_REST_SERVICE;
export const PAYMENT_SERVICE_URL = environment.PAYMENT_REST_SERVICE;

@Injectable({ providedIn: 'root' })
export class WalletService {
  private balanceSubject = new BehaviorSubject<number>(0);
  balance$ = this.balanceSubject.asObservable();

  constructor(private dataService: DataService) { }

  getWallet(): ServiceResponse<Wallet> {
    return this.dataService.get<Wallet>({
        url: `${WALLET_REST_SERVICE}`
    });
  }

  getTransactions(): ServiceResponse<WalletTransaction[]> {
    return this.dataService.get<WalletTransaction[]>({
        url: `${WALLET_REST_SERVICE}/transactions`
    });
  }

  credit(data: CreditRequest) {
    return this.dataService.post<{code: string, message: string}>({
        url: `${PAYMENT_SERVICE_URL}`,
        body: data,
    });
  }

  debit(data: DebitRequest): ServiceResponse<void> {
    return this.dataService.post<void>({
        url: `${WALLET_REST_SERVICE}/debit`,
        body: data,
    });
  }

  getBalance(){
    return this.dataService.get<number>({
      url: `${WALLET_REST_SERVICE}/balance`,
    }).pipe(
      map(res => res.data ?? 0),
      tap(balance => this.balanceSubject.next(balance))
    )
  }

  refreshBalance(): void {
    this.getBalance().subscribe();
  }
}
