import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.dev';
import { AuthService } from './auth.service';

export const order_socket_service = environment.ORDER_SOCKET_SERVICE;

@Injectable({ providedIn: 'root' })
export class OrderSocketService {
  private socket!: WebSocket;

  constructor(private authService: AuthService) {}

  private connectWebSocket(url: string, onMessage: (msg: any) => void) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      console.log('Já está conectado.');
      return;
    }

    const token = this.authService.getToken();
    this.socket = new WebSocket(`${url}?token=${token}`);

    this.socket.onopen = () => {
      console.log('WebSocket conectado');
    };

    this.socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        onMessage(data);
      } catch (e) {
        console.error('Erro ao processar mensagem WebSocket:', e);
      }
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket erro:', error);
    };

    this.socket.onclose = () => {
      console.log('WebSocket desconectado');
    };
  }

  connectToOrder(orderId: number, onMessage: (msg: any) => void) {
    const url = `${order_socket_service}/${orderId}`;
    this.connectWebSocket(url, onMessage);
  }

  connectToAllOrders(onMessage: (msg: any) => void) {
    const url = `${order_socket_service}`;
    this.connectWebSocket(url, onMessage);
  }

  disconnect() {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.close();
      console.log('Conexão WebSocket fechada');
    }
  }
}
