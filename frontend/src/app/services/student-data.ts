import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StudentDataService {
  private paymentMadeSource = new Subject<string>();
  paymentMade$ = this.paymentMadeSource.asObservable();

  notifyPaymentMade(studentNumber: string): void {
    this.paymentMadeSource.next(studentNumber);
  }
}