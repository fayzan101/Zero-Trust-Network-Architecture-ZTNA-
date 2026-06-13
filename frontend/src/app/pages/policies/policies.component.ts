import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { Policy, PolicyEvaluateResponse } from '../../core/models';

@Component({
  selector: 'app-policies',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Policies</h1><p>Zero-trust access rules and live evaluation</p></div>

    <div class="card eval-card">
      <h3 class="card-title">Evaluate Access</h3>
      <div class="grid-2">
        <div class="field"><label>User ID</label><input type="number" [(ngModel)]="userId" /></div>
        <div class="field"><label>Resource</label><input [(ngModel)]="resource" /></div>
        <div class="field"><label>Action</label><input [(ngModel)]="action" /></div>
        <div class="field"><label>Device ID</label><input [(ngModel)]="deviceId" /></div>
      </div>
      <button class="btn btn-primary" (click)="evaluate()">Evaluate</button>
      @if (evalResult) {
        <div class="eval-result" [class.allowed]="evalResult.allowed">
          <strong>{{ evalResult.decision }}</strong> — {{ evalResult.reason }}
          @if (evalResult.finalRisk) { <span class="mono"> (risk {{ evalResult.finalRisk }})</span> }
        </div>
      }
    </div>

    <div class="card table-wrap">
      <table>
        <thead><tr><th>Name</th><th>Resource</th><th>Action</th><th>Role</th><th>Max Risk</th><th>Min Trust</th><th>Status</th></tr></thead>
        <tbody>
          @for (p of policies; track p.id) {
            <tr>
              <td><strong>{{ p.name }}</strong><br><small>{{ p.description }}</small></td>
              <td class="mono">{{ p.resource }}</td>
              <td>{{ p.action }}</td>
              <td>{{ p.requiredRole || '—' }}</td>
              <td>{{ p.maxRiskThreshold ?? '—' }}</td>
              <td>{{ p.minDeviceTrust ?? '—' }}</td>
              <td><span class="badge" [class.badge-success]="p.enabled" [class.badge-warn]="!p.enabled">{{ p.enabled ? 'ON' : 'OFF' }}</span></td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .eval-card { margin-bottom: 1.5rem; }
    .eval-result { margin-top: 1rem; padding: 0.85rem; border-radius: 8px; background: rgba(248,113,113,0.1); border: 1px solid rgba(248,113,113,0.25); }
    .eval-result.allowed { background: rgba(52,211,153,0.1); border-color: rgba(52,211,153,0.25); }
  `]
})
export class PoliciesComponent implements OnInit {
  policies: Policy[] = [];
  userId = 2;
  resource = 'login';
  action = 'access';
  deviceId = 'demo-laptop-01';
  evalResult: PolicyEvaluateResponse | null = null;
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.getPolicies().subscribe(p => this.policies = p); }
  evaluate(): void {
    this.api.evaluatePolicy({ userId: this.userId, resource: this.resource, action: this.action, deviceId: this.deviceId })
      .subscribe(r => this.evalResult = r);
  }
}
