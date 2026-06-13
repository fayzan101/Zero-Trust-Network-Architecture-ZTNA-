import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { IncidentSummary, IncidentTimeline } from '../../core/models';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [DatePipe],
  template: `
    <div class="page-header"><h1>Incidents</h1><p>Forensic timeline replay for security events</p></div>
    <div class="grid-2">
      <div class="card table-wrap">
        <h3 class="card-title">Incident List</h3>
        <table>
          <thead><tr><th>Event</th><th>Severity</th><th>User</th><th>Time</th><th></th></tr></thead>
          <tbody>
            @for (i of incidents; track i.id) {
              <tr>
                <td>{{ i.eventType }}</td>
                <td><span class="badge" [class]="'badge-' + (i.severity||'info').toLowerCase()">{{ i.severity }}</span></td>
                <td>{{ i.username || '—' }}</td>
                <td class="mono">{{ i.createdAt | date:'short' }}</td>
                <td><button class="btn btn-ghost" (click)="loadTimeline(i.id)">Timeline</button></td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      @if (timeline) {
        <div class="card">
          <h3 class="card-title">Timeline — {{ timeline.incident.eventType }}</h3>
          <div class="timeline">
            @for (e of timeline.timeline; track $index) {
              <div class="tl-item">
                <div class="tl-dot"></div>
                <div class="tl-content">
                  <div class="tl-phase">{{ e.phase }} · {{ e.eventType }}</div>
                  <div class="tl-time mono">{{ e.timestamp | date:'medium' }}</div>
                  <div class="tl-details">{{ e.details }}</div>
                </div>
              </div>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .timeline { display: flex; flex-direction: column; gap: 0; }
    .tl-item { display: flex; gap: 1rem; padding-bottom: 1.25rem; position: relative; }
    .tl-item:not(:last-child)::before { content: ''; position: absolute; left: 5px; top: 14px; bottom: 0; width: 2px; background: var(--border); }
    .tl-dot { width: 12px; height: 12px; border-radius: 50%; background: var(--accent); flex-shrink: 0; margin-top: 4px; }
    .tl-phase { font-weight: 600; font-size: 0.85rem; }
    .tl-time { font-size: 0.75rem; color: var(--text-muted); }
    .tl-details { font-size: 0.82rem; color: var(--text-muted); margin-top: 0.25rem; }
    button { font-size: 0.75rem; padding: 0.35rem 0.65rem; }
  `]
})
export class IncidentsComponent implements OnInit {
  incidents: IncidentSummary[] = [];
  timeline: IncidentTimeline | null = null;
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.getIncidents().subscribe(i => this.incidents = i); }
  loadTimeline(id: number): void {
    this.api.getIncidentTimeline(id).subscribe(t => this.timeline = t);
  }
}
