import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AttackReport, AttackResult } from '../../core/models';

@Component({
  selector: 'app-attacks',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Attack Simulation</h1><p>Test zero-trust detection against simulated threats <span class="admin-note">(admin)</span></p></div>
    @if (report) {
      <div class="grid-4 stats" style="margin-bottom:1.5rem">
        <div class="stat-card"><div class="value">{{ report.totalAttacks }}</div><div class="label">Total</div></div>
        <div class="stat-card"><div class="value">{{ report.detectedCount }}</div><div class="label">Detected</div></div>
        <div class="stat-card"><div class="value">{{ report.undetectedCount }}</div><div class="label">Missed</div></div>
        <div class="stat-card"><div class="value">{{ report.detectionRate?.toFixed(0) }}%</div><div class="label">Detection Rate</div></div>
      </div>
    }
    <div class="grid-3" style="margin-bottom:1.5rem">
      <div class="card">
        <h3 class="card-title">Credential Theft</h3>
        <button class="btn btn-primary" (click)="credTheft()">Simulate</button>
      </div>
      <div class="card">
        <h3 class="card-title">Privilege Escalation</h3>
        <button class="btn btn-primary" (click)="privEsc()">Simulate</button>
      </div>
      <div class="card">
        <h3 class="card-title">Lateral Movement</h3>
        <button class="btn btn-primary" (click)="lateral()">Simulate</button>
      </div>
    </div>
    @if (lastResult) {
      <div class="card">
        <h3 class="card-title">Last Result</h3>
        <span class="badge" [class.badge-success]="lastResult.detected" [class.badge-critical]="!lastResult.detected">
          {{ lastResult.detected ? 'DETECTED' : 'UNDETECTED' }}
        </span>
        <p style="margin-top:0.75rem">{{ lastResult.detectionDetails || lastResult.message }}</p>
      </div>
    }
  `
  ,
  styles: [`.admin-note { color: var(--warn); font-size: 0.8rem; }`]
})
export class AttacksComponent implements OnInit {
  report: AttackReport | null = null;
  lastResult: AttackResult | null = null;
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.load(); }
  load(): void { this.api.getAttackReport().subscribe(r => this.report = r); }
  credTheft(): void {
    this.api.simulateCredentialTheft({ username: 'demo', stolenFromIp: '198.51.100.10', attackerDeviceId: 'unknown' })
      .subscribe(r => { this.lastResult = r; this.load(); });
  }
  privEsc(): void {
    this.api.simulatePrivilegeEscalation({ userId: 2, targetRole: 'ADMIN', resource: 'admin', action: 'access' })
      .subscribe(r => { this.lastResult = r; this.load(); });
  }
  lateral(): void {
    this.api.simulateLateralMovement({ userId: 2, targetResource: 'admin', targetIp: '198.51.100.12' })
      .subscribe(r => { this.lastResult = r; this.load(); });
  }
}
