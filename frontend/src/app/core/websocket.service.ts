import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { SecurityEvent } from './models';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private ws: WebSocket | null = null;
  readonly connected = signal(false);
  readonly events = signal<SecurityEvent[]>([]);

  connect(): void {
    if (this.ws?.readyState === WebSocket.OPEN) return;
    this.ws = new WebSocket(environment.wsUrl);
    this.ws.onopen = () => this.connected.set(true);
    this.ws.onclose = () => {
      this.connected.set(false);
      setTimeout(() => this.connect(), 3000);
    };
    this.ws.onmessage = (msg) => {
      try {
        const event: SecurityEvent = JSON.parse(msg.data);
        if (event.type === 'CONNECTED') return;
        this.events.update(list => [event, ...list].slice(0, 100));
      } catch { /* ignore */ }
    };
  }

  disconnect(): void {
    this.ws?.close();
    this.ws = null;
    this.connected.set(false);
  }
}
