import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  studentNumber = '';
  password = '';
  error = '';
  loading = false;

  // UI state
  showPassword = false;
  studentNumberFocus = false;
  passwordFocus = false;

  constructor(private authService: AuthService) {}

  onSubmit() {
    if (!this.studentNumber.trim() || !this.password.trim()) {
      this.error = 'Please enter both student number and password';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login({ studentNumber: this.studentNumber, password: this.password }).subscribe({
      next: (response) => {
        this.loading = false;
        if (!response.success) {
          this.error = response.message;
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Login failed. Please check your credentials.';
      }
    });
  }
}