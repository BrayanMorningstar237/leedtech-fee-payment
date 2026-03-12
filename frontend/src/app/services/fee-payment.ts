import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PaymentRequest {
  studentNumber: string;
  paymentAmount: number;
  paymentDate?: string;
}

export interface PaymentResponse {
  studentNumber: string;
  paymentAmount: number;
  previousBalance: number;
  incentiveRate: number;
  incentiveAmount: number;
  newBalance: number;
  nextDueDate: string;
}

export interface StudentAccount {
  studentNumber: string;
  balance: number;
  nextDueDate: string | null;
}

export interface PaymentTransaction {
  id: number;
  studentNumber: string;
  paymentAmount: number;
  incentiveAmount: number;
  incentiveRate: number;
  paymentDate: string;
  nextDueDate: string;
}

export interface StudentDashboard {
  studentNumber: string;
  currentBalance: number;
  initialBalance: number;        // <-- ADD THIS LINE
  nextDueDate: string | null;
  paymentHistory: PaymentTransaction[];
}

@Injectable({
  providedIn: 'root'
})
export class FeePaymentService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  makePayment(payment: PaymentRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/one-time-fee-payment`, payment);
  }

  getStudentAccount(studentNumber: string): Observable<StudentAccount> {
    return this.http.get<StudentAccount>(`${this.apiUrl}/students/${studentNumber}/account`);
  }

  getPaymentHistory(studentNumber: string): Observable<PaymentTransaction[]> {
    return this.http.get<PaymentTransaction[]>(`${this.apiUrl}/students/${studentNumber}/payments`);
  }

  getStudentDashboard(studentNumber: string): Observable<StudentDashboard> {
    return this.http.get<StudentDashboard>(`${this.apiUrl}/students/${studentNumber}/dashboard`);
  }
}