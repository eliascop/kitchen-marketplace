import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ToastService {
  private messageSubject = new BehaviorSubject<string | null>(null);
  message$ = this.messageSubject.asObservable();

  show(message: string, duration: number = 10000) {
    this.messageSubject.next(message);
    setTimeout(() => this.clear(), duration);
  }

  clear() {
    this.messageSubject.next(null);
  }
}
