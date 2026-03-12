import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth';
import { LoginComponent } from './login/login';
import { PaymentFormComponent } from './payment-form/payment-form';
import { DashboardComponent } from './dashboard/dashboard';
import { InfoSlideshowComponent } from './info-slideshow/info-slideshow.component'; // <-- import the standalone component

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, LoginComponent, PaymentFormComponent, DashboardComponent, InfoSlideshowComponent],
  template: `
    <div class="app-shell">
      <ng-container *ngIf="authService.getCurrentStudent(); else loginTemplate">

        <!-- Top Nav Bar -->
        <header class="app-header">
          <div class="header-inner">
            <div class="brand">
              <div class="brand-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>
                </svg>
              </div>
              <span class="brand-name">LeedTech <strong>Payments</strong></span>
            </div>
            <button (click)="logout()" class="logout-btn">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
              </svg>
              <span>Logout</span>
            </button>
          </div>
        </header>

        <!-- Content -->
        <main class="lg:px-20 lg:py-5">
          <!-- Slideshow sits full-width above the two panels -->
          <app-info-slideshow></app-info-slideshow>

          <!-- Mobile Tab Nav -->
          <nav class="mobile-tabs">
            <button class="tab-btn" [class.active]="activeTab === 'payment'" (click)="activeTab = 'payment'">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>
              </svg>
              Make Payment
            </button>
            <button class="tab-btn" [class.active]="activeTab === 'history'" (click)="activeTab = 'history'">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>
              </svg>
              History
            </button>
          </nav>

          <div class="content-grid">
            <div class="col-payment" [class.tab-hidden]="!isLargeScreen && activeTab !== 'payment'">
              <app-payment-form></app-payment-form>
            </div>
            <div class="col-history" [class.tab-hidden]="!isLargeScreen && activeTab !== 'history'">
              <app-dashboard></app-dashboard>
            </div>
          </div>
        </main>

      </ng-container>
      <ng-template #loginTemplate>
        <app-login></app-login>
      </ng-template>
    </div>
  `,
  styles: [`
    @import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&family=Sora:wght@400;600;700&display=swap');

    :host {
      --primary: #4F6EF7;
      --primary-dark: #3A56E0;
      --primary-light: #EEF1FE;
      --accent: #F5A623;
      --accent-light: #FFF8EC;
      --success: #22C993;
      --success-light: #E8FAF4;
      --danger: #F25C5C;
      --danger-light: #FEF0F0;
      --text-primary: #1A1D2E;
      --text-secondary: #6B7280;
      --text-muted: #9CA3AF;
      --border: #E8EAF0;
      --surface: #FFFFFF;
      --bg: #F4F6FC;
      --shadow-sm: 0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);
      --shadow-md: 0 4px 16px rgba(79,110,247,0.08), 0 1px 4px rgba(0,0,0,0.04);
      --radius: 16px;
      font-family: 'Nunito', sans-serif;
    }

    .app-shell {
      min-height: 100vh;
      background: var(--bg);
      background-image: radial-gradient(circle at 10% 20%, rgba(79,110,247,0.06) 0%, transparent 50%),
                        radial-gradient(circle at 90% 80%, rgba(245,166,35,0.06) 0%, transparent 50%);
    }

    .app-header {
      background: var(--surface);
      border-bottom: 1px solid var(--border);
      position: sticky; top: 0; z-index: 100;
      box-shadow: var(--shadow-sm);
    }
    .header-inner {
      max-width: 1200px; margin: 0 auto; padding: 0 16px;
      height: 60px; display: flex; align-items: center; justify-content: space-between;
    }
    .brand { display: flex; align-items: center; gap: 10px; }
    .brand-icon {
      width: 36px; height: 36px; background: var(--primary);
      border-radius: 10px; display: flex; align-items: center; justify-content: center; color: white;
    }
    .brand-name { font-family: 'Sora', sans-serif; font-size: 16px; color: var(--text-primary); letter-spacing: -0.3px; }
    .brand-name strong { color: var(--primary); }
    .logout-btn {
      display: flex; align-items: center; gap: 6px;
      padding: 8px 14px; background: var(--danger-light); color: var(--danger);
      border: none; border-radius: 10px; font-family: 'Nunito', sans-serif;
      font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s;
    }
    .logout-btn:hover { background: var(--danger); color: white; }

    .mobile-tabs {
      display: flex; background: var(--surface);
      border-bottom: 1px solid var(--border); padding: 0 16px;
    }
    @media (min-width: 1024px) { .mobile-tabs { display: none; } }

    .tab-btn {
      flex: 1; display: flex; align-items: center; justify-content: center; gap: 6px;
      padding: 12px 8px; background: transparent; border: none;
      border-bottom: 2px solid transparent; color: var(--text-secondary);
      font-family: 'Nunito', sans-serif; font-size: 14px; font-weight: 600;
      cursor: pointer; transition: all 0.2s;
    }
    .tab-btn.active { color: var(--primary); border-bottom-color: var(--primary); }

    .app-main { max-width: 1200px; margin: 0 auto; padding: 20px 16px 40px; }
    @media (min-width: 1024px) { .app-main { padding: 32px 24px 48px; } }

    .content-grid { display: grid; grid-template-columns: 1fr; gap: 20px; }
    @media (min-width: 1024px) { .content-grid { grid-template-columns: 1fr 1fr; gap: 24px; } }

    .tab-hidden { display: none; }
    @media (min-width: 1024px) { .tab-hidden { display: block !important; } }
  `]
})
export class AppComponent implements OnDestroy {
  activeTab: 'payment' | 'history' = 'payment';
  isLargeScreen = window.innerWidth >= 1024;

  constructor(public authService: AuthService) {
    window.addEventListener('resize', this.onResize);
  }

  onResize = () => { this.isLargeScreen = window.innerWidth >= 1024; };

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.onResize);
  }

  logout(): void {
    this.authService.logout();
  }
}