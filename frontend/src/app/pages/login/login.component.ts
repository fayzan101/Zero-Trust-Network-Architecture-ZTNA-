import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/auth.service';

type Mode = 'login' | 'mfa' | 'stepup';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  mode = signal<Mode>('login');
  error = signal('');
  info = signal('');

  username = 'demo';
  password = 'Demo123!';
  otp = '';
  deviceId = 'demo-laptop-01';
  ipAddress = '192.168.1.50';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {
    if (auth.isLoggedIn()) router.navigate(['/dashboard']);
  }

  submit(): void {
    this.error.set('');
    this.loading = true;
    const m = this.mode();

    if (m === 'login') {
      this.auth.login({ username: this.username, password: this.password, deviceId: this.deviceId, ipAddress: this.ipAddress })
        .subscribe({
          next: (res) => this.handleLoginResponse(res),
          error: (e) => this.handleError(e),
          complete: () => this.loading = false
        });
    } else if (m === 'mfa') {
      this.auth.verifyMfa({ username: this.username, otp: this.otp, deviceId: this.deviceId, ipAddress: this.ipAddress })
        .subscribe({
          next: () => this.router.navigate(['/dashboard']),
          error: (e) => this.handleError(e),
          complete: () => this.loading = false
        });
    } else {
      this.auth.stepUp({ username: this.username, password: this.password, otp: this.otp || undefined, deviceId: this.deviceId, ipAddress: this.ipAddress })
        .subscribe({
          next: (res) => {
            this.auth.storeSession(this.username, res);
            this.router.navigate(['/dashboard']);
          },
          error: (e) => this.handleError(e),
          complete: () => this.loading = false
        });
    }
  }

  private handleLoginResponse(res: any): void {
    if (res.message === 'MFA_REQUIRED') {
      this.mode.set('mfa');
      this.info.set('MFA verification required. Enter your authenticator code.');
      return;
    }
    if (res.message === 'STEP_UP_REQUIRED') {
      this.mode.set('stepup');
      this.info.set(`Elevated risk (${res.finalRisk}). Complete step-up authentication.`);
      return;
    }
    if (res.accessToken) {
      this.auth.storeSession(this.username, res);
      this.router.navigate(['/dashboard']);
    }
  }

  private handleError(e: HttpErrorResponse): void {
    this.loading = false;
    this.error.set(e.error?.message || e.message || 'Authentication failed');
  }

  useHighRisk(): void {
    this.ipAddress = '203.0.113.99';
    this.deviceId = 'unknown-device';
    this.info.set('High-risk context loaded — expect step-up or deny.');
  }
}
