import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { AuditLog } from '../../core/models';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [DatePipe],
  template: `
    <div class="page-header">
      <h1>Audit Logs</h1>
      <p>Security audit trail with severity filtering</p>
    </div>
    <div class="filters">
      <button class="btn btn-ghost" [class.active]="filter===''" (click)="setFilter('')">All</button>
      <button class="btn btn-ghost" [class.active]="filter==='CRITICAL'" (click)="setFilter('CRITICAL')">Critical</button>
      <button class="btn btn-ghost" [class.active]="filter==='WARN'" (click)="setFilter('WARN')">Warn</button>
      <button class="btn btn-ghost" [class.active]="filter==='INFO'" (click)="setFilter('INFO')">Info</button>
    </div>
    <div class="card table-wrap">
      <table>
        <thead><tr><th>Event</th><th>Severity</th><th>User</th><th>IP</th><th>Details</th><th>Time</th></tr></thead>
        <tbody>
          @for (l of logs; track l.id) {
            <tr>
              <td class="mono">{{ l.eventType }}</td>
              <td><span class="badge" [class]="'badge-' + (l.severity||'info').toLowerCase()">{{ l.severity }}</span></td>
              <td>{{ l.username || '—' }}</td>
              <td class="mono">{{ l.ipAddress || '—' }}</td>
              <td>{{ l.details }}</td>
              <td class="mono">{{ l.createdAt | date:'short' }}</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .filters { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
    .filters .active { border-color: var(--accent); color: var(--accent); }
  `]
})
export class AuditComponent implements OnInit {
  logs: AuditLog[] = [];
  filter = '';
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.load(); }
  setFilter(f: string): void { this.filter = f; this.load(); }
  load(): void {
    this.api.getAuditLogs(this.filter || undefined).subscribe(l => this.logs = l);
  }
}
