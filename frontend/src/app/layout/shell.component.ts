import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { WebsocketService } from '../core/websocket.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent implements OnInit {
  readonly nav = [
    { path: '/dashboard', icon: '◉', label: 'Overview' },
    { path: '/sessions', icon: '◎', label: 'Sessions' },
    { path: '/risk', icon: '△', label: 'Risk Engine' },
    { path: '/policies', icon: '▣', label: 'Policies' },
    { path: '/compare', icon: '⇌', label: 'ZT Compare' },
    { path: '/attacks', icon: '⚡', label: 'Attacks' },
    { path: '/incidents', icon: '◈', label: 'Incidents' },
    { path: '/audit', icon: '☰', label: 'Audit Logs' },
    { path: '/devices', icon: '▤', label: 'Devices' },
  ];

  constructor(public auth: AuthService, public ws: WebsocketService) {}

  ngOnInit(): void {
    this.ws.connect();
  }

  logout(): void {
    this.ws.disconnect();
    this.auth.logout();
  }
}
