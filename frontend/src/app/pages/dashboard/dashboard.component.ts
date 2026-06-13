import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { WebsocketService } from '../../core/websocket.service';
import { ComparisonMetrics } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  metrics: ComparisonMetrics | null = null;

  constructor(public api: ApiService, public ws: WebsocketService) {}

  ngOnInit(): void {
    this.api.getMetrics().subscribe(m => this.metrics = m);
  }

  severityClass(s?: string): string {
    return (s || 'INFO').toLowerCase();
  }
}
