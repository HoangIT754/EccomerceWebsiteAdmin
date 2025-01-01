import { Component,OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../../services/orderService/order.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {
  orders: any[] = [];
  selectedOrder: any = null;
  isModalOpen = false;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getOrders().subscribe(
      (data) => {
        this.orders = data;
      },
      (error) => {
        console.error('Failed to fetch orders', error);
      }
    );
  }

  openOrderDetails(id: string): void {
    this.orderService.getOrderById(id).subscribe(
      (data) => {
        this.selectedOrder = data;
        this.isModalOpen = true;
      },
      (error) => {
        console.error('Failed to fetch order details', error);
      }
    );
  }

  updateOrder(status: number): void {
    if (this.selectedOrder) {
      // Chuẩn bị danh sách sản phẩm với các `productId` và `quantity`
      const products = this.selectedOrder.orderProducts.map((product: any) => ({
        productId: product.product.productId,
        quantity: product.quantity
      }));
      
      // Gọi API để cập nhật trạng thái đơn hàng
      this.orderService.updateOrderStatus(this.selectedOrder.orderId, status, products).subscribe(
        (updatedOrder) => {
          this.loadOrders();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to update order status', error);
        }
      );
    }
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedOrder = null;
  }
}