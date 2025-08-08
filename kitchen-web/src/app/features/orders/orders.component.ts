import { Component, OnInit } from '@angular/core';
import { Order } from '../../core/model/order.model';
import { CommonModule } from '@angular/common';
import { FormatDateTimePipe } from "../../core/pipes/format-date-time.pipe";
import { OrderService } from '../../core/service/order.service';
import { OrderStatus } from '../../core/enums/order-status.enum';
import { OrderSearchComponent } from '../components/order-search/order-search.component';
import { CurrencyFormatterPipe } from "../../core/pipes/currency-input.pipe";

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormatDateTimePipe, OrderSearchComponent, CurrencyFormatterPipe],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit{

  allOrders: Order[] = [];
  orders: Order[] = [];
  orderStatusEnum = OrderStatus;
  orderStatusList: string[] = Object.values(OrderStatus);
  
  selectedOrder: Order | null = null;
  selectedStatus: string = '';

  constructor(private orderService: OrderService) {}
  
  ngOnInit() {
    this.getOrders();
  }

  getOrders() {
    this.orderService.getOrders().subscribe((data) => {
      this.allOrders = data.data ?? [];
      this.orders = [...this.allOrders];
    });
  }

  onSearch({ term, status }: { term: string; status: string }) {
    const lowerTerm = term.toLowerCase();
  
    this.orders = this.allOrders.filter(order => {
      const matchesText =
        order.status?.toLowerCase().includes(lowerTerm) ||
        order.id?.toString().includes(lowerTerm);
  
      const matchesStatus = status ? order.status === status : true;
  
      return matchesText && matchesStatus;
    });
  }
  
  

  openModal(order: any) {
    this.selectedOrder = order;
  }

  closeModal() {
    this.selectedOrder = null;
  }
  
}
