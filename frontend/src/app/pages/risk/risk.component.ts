import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { RiskScore } from '../../core/models';

@Component({
  selector: 'app-risk',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Risk Engine</h1><p>Calculate weighted risk with explanatory reasons</p></div>
    <div class="grid-2">
      <div class="card">
        <h3 class="card-title">Calculate Risk</h3>
        <div class="field"><label>User ID</label><input type="number" [(ngModel)]="userId" /></div>
        <div class="field"><label>Device ID</label><input [(ngModel)]="deviceId" /></div>
        <div class="field"><label>IP Address</label><input [(ngModel)]="ipAddress" /></div>
        <button class="btn btn-primary" (click)="calc()">Calculate</button>
      </div>
      @if (result) {
        <div class="card">
          <h3 class="card-title">Risk Breakdown</h3>
          <div class="grid-3" style="margin-bottom:1rem">
            <div class="stat-card"><div class="value">{{ result.userRisk }}</div><div class="label">User</div></div>
            <div class="stat-card"><div class="value">{{ result.deviceRisk }}</div><div class="label">Device</div></div>
            <div class="stat-card"><div class="value">{{ result.contextRisk }}</div><div class="label">Context</div></div>
          </div>
          <div class="final-risk">Final Risk: <strong>{{ result.finalRisk }}</strong></div>
          <ul class="reasons">
            @for (r of result.reasons; track r) { <li>{{ r }}</li> }
          </ul>
        </div>
      }
    </div>
  `,
  styles: [`
    .final-risk { font-size: 1.1rem; margin-bottom: 1rem; }
    .reasons { margin: 0; padding-left: 1.2rem; color: var(--text-muted); font-size: 0.88rem; line-height: 1.8; }
  `]
})
export class RiskComponent {
  userId = 2;
  deviceId = 'demo-laptop-01';
  ipAddress = '192.168.1.50';
  result: RiskScore | null = null;
  constructor(private api: ApiService) {}
  calc(): void {
    this.api.calculateRisk({ userId: this.userId, deviceId: this.deviceId, ipAddress: this.ipAddress })
      .subscribe(r => this.result = r);
  }
}
