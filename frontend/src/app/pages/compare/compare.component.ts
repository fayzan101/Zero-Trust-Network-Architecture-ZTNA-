import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AccessComparisonResponse } from '../../core/models';

@Component({
  selector: 'app-compare',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>ZT Comparison</h1><p>Same request — traditional RBAC vs zero-trust decision</p></div>
    <div class="card" style="margin-bottom:1.5rem">
      <div class="grid-2">
        <div class="field"><label>User ID</label><input type="number" [(ngModel)]="userId" /></div>
        <div class="field"><label>Resource</label><input [(ngModel)]="resource" /></div>
        <div class="field"><label>Device ID</label><input [(ngModel)]="deviceId" /></div>
        <div class="field"><label>IP Address</label><input [(ngModel)]="ipAddress" /></div>
      </div>
      <button class="btn btn-primary" (click)="compare()">Compare Access</button>
      <button class="btn btn-ghost" style="margin-left:0.5rem" (click)="useRisky()">Use risky context</button>
    </div>
    @if (result) {
      <div class="differ-banner" [class.differ]="result.outcomesDiffer">
        {{ result.outcomesDiffer ? '⚠ Outcomes differ — zero-trust blocked what RBAC allowed' : '✓ Both models agree' }}
      </div>
      <div class="grid-2">
        <div class="card outcome" [class.allowed]="result.traditional.allowed">
          <h3 class="card-title">Traditional RBAC</h3>
          <div class="outcome-badge">{{ result.traditional.decision }}</div>
          <p>{{ result.traditional.reason }}</p>
        </div>
        <div class="card outcome" [class.allowed]="result.zeroTrust.allowed">
          <h3 class="card-title">Zero Trust</h3>
          <div class="outcome-badge">{{ result.zeroTrust.decision }}</div>
          <p>{{ result.zeroTrust.reason }}</p>
          @if (result.zeroTrust.finalRisk) { <p class="mono">Risk: {{ result.zeroTrust.finalRisk }}</p> }
        </div>
      </div>
    }
  `,
  styles: [`
    .differ-banner { padding: 1rem; border-radius: 8px; margin-bottom: 1rem; background: var(--bg-elevated); border: 1px solid var(--border); }
    .differ-banner.differ { background: rgba(251,191,36,0.1); border-color: rgba(251,191,36,0.3); color: var(--warn); }
    .outcome { border-left: 3px solid var(--danger); }
    .outcome.allowed { border-left-color: var(--success); }
    .outcome-badge { font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; }
    .outcome p { color: var(--text-muted); font-size: 0.9rem; margin: 0.25rem 0; }
  `]
})
export class CompareComponent {
  userId = 2;
  resource = 'login';
  deviceId = 'demo-laptop-01';
  ipAddress = '192.168.1.50';
  result: AccessComparisonResponse | null = null;
  constructor(private api: ApiService) {}
  compare(): void {
    this.api.compareAccess({ userId: this.userId, resource: this.resource, action: 'access', deviceId: this.deviceId, ipAddress: this.ipAddress })
      .subscribe(r => this.result = r);
  }
  useRisky(): void { this.ipAddress = '203.0.113.99'; this.deviceId = 'unknown'; this.compare(); }
}
