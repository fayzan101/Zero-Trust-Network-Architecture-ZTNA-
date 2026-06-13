import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  LoginRequest, LoginResponse, MfaRequest, StepUpRequest
} from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = environment.apiUrl + '/api/auth';
  readonly username = signal<string | null>(this.getStoredUsername());
  readonly isAdmin = signal(false);

  constructor(private http: HttpClient, private router: Router) {}

  login(req: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/login`, req);
  }

  stepUp(req: StepUpRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/step-up`, req);
  }

  verifyMfa(req: MfaRequest): Observable<LoginResponse & { message?: string }> {
    return this.http.post<LoginResponse>(`${this.base}/mfa`, req).pipe(
      tap((res: any) => {
        if (res.accessToken) this.storeSession(req.username, res);
      })
    );
  }

  storeSession(username: string, res: LoginResponse): void {
    if (res.accessToken) localStorage.setItem('accessToken', res.accessToken);
    if (res.refreshToken) localStorage.setItem('refreshToken', res.refreshToken);
    localStorage.setItem('username', username);
    this.username.set(username);
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getStoredUsername(): string | null {
    return localStorage.getItem('username');
  }

  logout(): void {
    const refresh = localStorage.getItem('refreshToken');
    if (refresh) {
      this.http.post(`${this.base}/logout`, { refreshToken: refresh }).subscribe();
    }
    localStorage.clear();
    this.username.set(null);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
