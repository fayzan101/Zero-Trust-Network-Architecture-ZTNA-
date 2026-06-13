import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { Device } from '../../core/models';

@Component({
  selector: 'app-devices',
  standalone: true,
  template: `
    <div class="page-header"><h1>Devices</h1><p>Device trust scores and registration status</p></div>
    <div class="card" style="margin-bottom:1rem">
      <button class="btn btn-primary" (click)="loadDemo()">Load demo-laptop-01</button>
      <button class="btn btn-ghost" style="margin-left:0.5rem" (click)="loadUserDevices()">Load user #2 devices</button>
    </div>
    <div class="grid-2">
      @if (device) {
        <div class="card">
          <h3 class="card-title">Device Detail</h3>
          <p><strong>{{ device.deviceId }}</strong></p>
          <p>Type: {{ device.deviceType }} · OS: {{ device.os }}</p>
          <p class="mono">IP: {{ device.ipAddress }}</p>
          <div class="trust-meter">
            <div class="trust-bar" [style.width.%]="device.trustScore"></div>
          </div>
          <p>Trust Score: <strong>{{ device.trustScore }}/100</strong></p>
        </div>
      }
      @if (userDevices.length) {
        <div class="card table-wrap">
          <h3 class="card-title">User Devices</h3>
          <table>
            <thead><tr><th>ID</th><th>Type</th><th>Trust</th></tr></thead>
            <tbody>
              @for (d of userDevices; track d.deviceId) {
                <tr><td class="mono">{{ d.deviceId }}</td><td>{{ d.deviceType }}</td><td>{{ d.trustScore }}</td></tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
  styles: [`
    .trust-meter { height: 8px; background: var(--bg-elevated); border-radius: 4px; margin: 1rem 0; overflow: hidden; }
    .trust-bar { height: 100%; background: linear-gradient(90deg, #06b6d4, #34d399); border-radius: 4px; }
  `]
})
export class DevicesComponent implements OnInit {
  device: Device | null = null;
  userDevices: Device[] = [];
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadDemo(); }
  loadDemo(): void { this.api.getDevice('demo-laptop-01').subscribe(d => this.device = d); }
  loadUserDevices(): void { this.api.getDevicesByUser('2').subscribe(d => this.userDevices = d); }
}
