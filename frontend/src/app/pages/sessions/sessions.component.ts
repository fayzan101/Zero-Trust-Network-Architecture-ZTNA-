import { Component, OnInit } from '@angular/core';
import { SlicePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { Session } from '../../core/models';

@Component({
  selector: 'app-sessions',
  standalone: true,
  imports: [SlicePipe],
  template: `
    <div class="page-header"><h1>Active Sessions</h1><p>Monitor and terminate live user sessions <span class="admin-note">(admin)</span></p></div>
    <div class="card table-wrap">
      @if (sessions.length === 0) { <div class="empty">No active sessions</div> }
      @if (sessions.length > 0) {
        <table>
          <thead><tr><th>Session</th><th>User</th><th>Device</th><th>IP</th><th>Risk</th><th>Status</th><th></th></tr></thead>
          <tbody>
            @for (s of sessions; track s.sessionId) {
              <tr>
                <td class="mono">{{ s.sessionId | slice:0:8 }}…</td>
                <td>{{ s.userId }}</td>
                <td>{{ s.deviceId || '—' }}</td>
                <td class="mono">{{ s.ipAddress || '—' }}</td>
                <td><span class="badge" [class.badge-warn]="(s.finalRisk||0)>=40" [class.badge-critical]="(s.finalRisk||0)>=70">{{ s.finalRisk }}</span></td>
                <td>{{ s.status }}</td>
                <td><button class="btn btn-danger" (click)="terminate(s)">Terminate</button></td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `,
  styles: [`
    button { font-size: 0.75rem; padding: 0.4rem 0.75rem; }
    .admin-note { color: var(--warn); font-size: 0.8rem; }
  `]
})
export class SessionsComponent implements OnInit {
  sessions: Session[] = [];
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.load(); }
  load(): void { this.api.getSessions().subscribe(s => this.sessions = s); }
  terminate(s: Session): void {
    this.api.terminateSession(s.sessionId, 'Terminated from dashboard').subscribe(() => this.load());
  }
}
