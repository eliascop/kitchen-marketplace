import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../../environments/environment.dev';
import { FormatDateTimePipe } from '../../../core/pipes/format-date-time.pipe';
import { Order } from '../../../core/model/order.model';
import { OrderService } from '../../../core/service/order.service';
import { OrderSocketService } from '../../../core/service/order.socket.service';

export const ORDER_SOCKET_SERVICE = environment.ORDER_SOCKET_SERVICE;

@Component({
  selector: 'app-orders-painel',
  standalone: true,
  imports: [CommonModule, FormatDateTimePipe],
  templateUrl: './orders-painel.component.html',
  styleUrl: './orders-painel.component.css'
})
export class OrdersPainelComponent implements OnInit, OnDestroy {

  orders: Record<string, Order[]> = {
    PENDING: [],
    PREPARING: [],
    PREPARED: []
  };

  private pendingRecoveryOrderId: number | null = null;

  constructor(
    private orderSocketService: OrderSocketService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.loadInitialOrders();

    this.orderSocketService.connectToAllOrders(({ id, status }) => {
      const updated = this.updateOrderStatus(id, status);

      if (!updated) {
        this.pendingRecoveryOrderId = id;
        this.recoverAndInsertOrder(id);
      }
    });
  }

  ngOnDestroy(): void {
    this.orderSocketService.disconnect();
  }

  private loadInitialOrders(): void {
    this.orderService.getOrders().subscribe(response => {
      this.clearOrders();
      (response.data || []).forEach(order => this.insertOrder(order));
    });
  }

  private clearOrders(): void {
    for (const key of Object.keys(this.orders)) {
      this.orders[key] = [];
    }
  }

  private insertOrder(order: Order): void {
    order.blink = true;
    const status = (order.status || '').toUpperCase();
    if (!this.orders[status]) {
      this.orders[status] = [];
    }
    this.orders[status] = [...(this.orders[status] || []), order];
    setTimeout(() => order.blink = false, 5000);
  }

  private removeOrder(order: Order): void {
    setTimeout(() => {
      order.blink = true;
  
      setTimeout(() => {
        order.blink = false;
        this.orders['PREPARED'] = this.orders['PREPARED'].filter(item => item.id !== order.id);
      }, 10000);
    }, 100);
  }
  

  private updateOrderStatus(orderId: number, newStatus: string): boolean {
    for (const statusKey in this.orders) {
      const orderList = this.orders[statusKey];
      const order = orderList.find(o => o.id === Number(orderId));
  
      if (order) {
        this.orders[statusKey] = orderList.filter(o => o.id !== Number(orderId));
        if(newStatus === 'DELIVERED') {
          this.removeOrder(order);
        }else{
          order.status = newStatus;
          this.insertOrder(order);
        }
        return true;
      }
    }
    return false;
  }

  private recoverAndInsertOrder(orderId: number): void {
    this.orderService.getOrderById(orderId).subscribe(response => {
      const recoveredOrder = response.data;
      if (recoveredOrder) {
        this.insertOrder(recoveredOrder);
      } else {
        console.warn(`Pedido ID ${orderId} não encontrado na recuperação.`);
      }
      this.pendingRecoveryOrderId = null;
    });
  }
}
