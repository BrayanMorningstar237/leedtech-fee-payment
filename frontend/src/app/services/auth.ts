import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface LoginRequest {
  studentNumber: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  studentNumber: string;
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private loggedInStudentSubject = new BehaviorSubject<string | null>(null);
  loggedInStudent$ = this.loggedInStudentSubject.asObservable();
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient) {
    const storedToken = localStorage.getItem(this.tokenKey);
    const storedStudent = localStorage.getItem('studentNumber');
    if (storedToken && storedStudent) {
      this.loggedInStudentSubject.next(storedStudent);
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.success) {
          this.loggedInStudentSubject.next(response.studentNumber);
          localStorage.setItem('studentNumber', response.studentNumber);
          localStorage.setItem(this.tokenKey, response.token);
        }
      })
    );
  }

  logout(): void {
    this.loggedInStudentSubject.next(null);
    localStorage.removeItem('studentNumber');
    localStorage.removeItem(this.tokenKey);
  }

  getCurrentStudent(): string | null {
    return this.loggedInStudentSubject.value;
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
}