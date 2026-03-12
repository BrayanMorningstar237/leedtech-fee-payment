import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { FeePaymentService, StudentDashboard } from '../services/fee-payment';
import { AuthService } from '../services/auth';
import { StudentDataService } from '../services/student-data';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  studentNumber = '';
  dashboard: StudentDashboard | null = null;
  loading = false;
  error = '';
  viewMode: 'list' | 'grid' = 'grid';
  private subscription: Subscription = new Subscription();

  constructor(
    private paymentService: FeePaymentService,
    private authService: AuthService,
    private studentDataService: StudentDataService
  ) {}

  ngOnInit(): void {
    const currentStudent = this.authService.getCurrentStudent();
    if (currentStudent) {
      this.studentNumber = currentStudent;
      this.loadDashboard();
    }

    this.subscription = this.studentDataService.paymentMade$.subscribe(studentNumber => {
      if (this.studentNumber === studentNumber) {
        this.loadDashboard();
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  loadDashboard() {
    if (!this.studentNumber.trim()) return;

    this.loading = true;
    this.error = '';
    this.dashboard = null;

    this.paymentService.getStudentDashboard(this.studentNumber).subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
      },
      error: (err) => {
        if (err.error instanceof ProgressEvent) {
          this.error = 'Network error: Unable to reach the server.';
        } else if (err.status === 404) {
          this.error = 'Student not found.';
        } else {
          this.error = err.error || 'An error occurred';
        }
        this.loading = false;
        console.error('Dashboard error:', err);
      }
    });
  }

  toggleViewMode() {
    this.viewMode = this.viewMode === 'list' ? 'grid' : 'list';
  }

  // Format currency for PDF (XAF with commas)
  private formatCurrency(value: number): string {
    return `XAF ${value.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')}`;
  }

  // Generate PDF report
  generatePDF() {
    if (!this.dashboard) return;

    const doc = new jsPDF();
    const student = this.studentNumber;
    const balance = this.dashboard.currentBalance;
    const nextDue = this.dashboard.nextDueDate
      ? new Date(this.dashboard.nextDueDate).toLocaleDateString('en-US', {
          weekday: 'long',
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        })
      : 'Paid in full';
    const initial = this.dashboard.initialBalance;

    // Title
    doc.setFontSize(18);
    doc.text('LeedTech Fee Payment Report', 14, 22);
    doc.setFontSize(12);
    doc.text(`Student: ${student}`, 14, 32);
    doc.text(`Initial Balance: ${this.formatCurrency(initial)}`, 14, 40);
    doc.text(`Current Balance: ${this.formatCurrency(balance)}`, 14, 48);
    doc.text(`Next Due Date: ${nextDue}`, 14, 56);

    // Table headers
    const tableColumn = ['Date', 'Payment', 'Incentive', 'Rate', 'Next Due'];
    const tableRows = this.dashboard.paymentHistory.map(tx => [
      new Date(tx.paymentDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      }),
      this.formatCurrency(tx.paymentAmount),
      this.formatCurrency(tx.incentiveAmount),
      `${tx.incentiveRate * 100}%`,
      new Date(tx.nextDueDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    ]);

    // Add table
    autoTable(doc, {
      startY: 65,
      head: [tableColumn],
      body: tableRows,
      theme: 'striped',
      headStyles: { fillColor: [41, 128, 185] },
      styles: { fontSize: 10 },
      columnStyles: {
        0: { cellWidth: 35 },
        1: { cellWidth: 35 },
        2: { cellWidth: 35 },
        3: { cellWidth: 25 },
        4: { cellWidth: 40 }
      }
    });

    // Save PDF
    doc.save(`payment_report_${student}.pdf`);
  }
}