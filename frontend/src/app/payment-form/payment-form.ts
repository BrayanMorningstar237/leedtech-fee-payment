import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FeePaymentService, PaymentRequest, PaymentResponse, StudentAccount } from '../services/fee-payment';
import { AuthService } from '../services/auth';
import { StudentDataService } from '../services/student-data';

@Component({
  selector: 'app-payment-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-form.html',
  styleUrls: ['./payment-form.css']
})
export class PaymentFormComponent implements OnInit {
  request: PaymentRequest = {
    studentNumber: '',
    paymentAmount: 0
  };
  response: PaymentResponse | null = null;
  loading = false;
  error = '';

  // Balance and validation properties
  currentBalance: number | null = null;
  balanceLoading = false;
  balanceError = '';
  validationError = '';

  // Calculated values
  incentiveRate: number | null = null;
  incentiveAmount: number | null = null;
  totalDeduction: number | null = null;
  payoffSuggestion: number | null = null;

  constructor(
    private paymentService: FeePaymentService,
    private authService: AuthService,
    private studentDataService: StudentDataService
  ) {}

  ngOnInit(): void {
    const studentNumber = this.authService.getCurrentStudent();
    if (studentNumber) {
      this.request.studentNumber = studentNumber;
      this.loadStudentBalance();
    }
    // Set default payment date to today
    const today = new Date();
    const year = today.getFullYear();
    const month = ('0' + (today.getMonth() + 1)).slice(-2);
    const day = ('0' + today.getDate()).slice(-2);
    this.request.paymentDate = `${year}-${month}-${day}`;
  }

  loadStudentBalance() {
    if (!this.request.studentNumber.trim()) {
      this.resetBalanceInfo();
      return;
    }

    this.balanceLoading = true;
    this.balanceError = '';
    this.currentBalance = null;
    this.validationError = '';
    this.clearIncentiveCalculations();

    this.paymentService.getStudentAccount(this.request.studentNumber).subscribe({
      next: (account: StudentAccount) => {
        this.currentBalance = account.balance;
        this.balanceLoading = false;
        this.calculatePayoffSuggestion();
        this.onAmountChange();
      },
      error: (err) => {
        this.balanceError = err.error || 'Student not found';
        this.balanceLoading = false;
      }
    });
  }

  private resetBalanceInfo() {
    this.currentBalance = null;
    this.balanceError = '';
    this.validationError = '';
    this.clearIncentiveCalculations();
    this.payoffSuggestion = null;
  }

  private clearIncentiveCalculations() {
    this.incentiveRate = null;
    this.incentiveAmount = null;
    this.totalDeduction = null;
  }

  private getIncentiveRate(amount: number): number {
    if (amount >= 500000) return 0.05;
    if (amount >= 100000) return 0.03;
    return 0.01;
  }

  onAmountChange() {
    const amount = this.request.paymentAmount;
    if (!this.currentBalance || amount <= 0) {
      this.clearIncentiveCalculations();
      this.validationError = '';
      return;
    }

    this.incentiveRate = this.getIncentiveRate(amount);
    this.incentiveAmount = amount * this.incentiveRate;
    this.totalDeduction = amount + this.incentiveAmount;

    if (this.totalDeduction > this.currentBalance) {
      this.validationError = `Total deduction (${this.formatCurrency(this.totalDeduction)}) exceeds current balance (${this.formatCurrency(this.currentBalance)}).`;
    } else {
      this.validationError = '';
    }
  }

  calculatePayoffSuggestion() {
    if (!this.currentBalance) return;

    const balance = this.currentBalance;
    const tiers = [
      { min: 500000, rate: 0.05 },
      { min: 100000, rate: 0.03 },
      { min: 0, rate: 0.01 }
    ];

    for (const tier of tiers) {
      const p = balance / (1 + tier.rate);
      if (p >= tier.min) {
        this.payoffSuggestion = Math.round(p * 100) / 100;
        return;
      }
    }
    this.payoffSuggestion = Math.round(balance / 1.01 * 100) / 100;
  }

  applyPayoffSuggestion() {
    if (this.payoffSuggestion) {
      this.request.paymentAmount = this.payoffSuggestion;
      this.onAmountChange();
    }
  }

  private formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'XAF' }).format(value);
  }

  onSubmit() {
    if (!this.currentBalance || this.validationError) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.response = null;

    this.paymentService.makePayment(this.request).subscribe({
      next: (res) => {
        this.response = res;
        this.loading = false;

        // Refresh balance after successful payment
        this.loadStudentBalance();

        // Reset payment amount for next payment
        this.request.paymentAmount = 0;
        this.clearIncentiveCalculations();
        this.validationError = '';

        // Notify dashboard to refresh
        this.studentDataService.notifyPaymentMade(this.request.studentNumber);
      },
      error: (err) => {
        this.error = err.error || 'An error occurred';
        this.loading = false;
      }
    });
  }
}