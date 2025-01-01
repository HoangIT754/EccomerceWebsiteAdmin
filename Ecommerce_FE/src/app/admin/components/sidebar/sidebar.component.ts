import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule,RouterLink],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  sidebarItems = [
    { label: 'Dashboard', link: '/admin/dashboard', icon: 'fa-solid fa-chart-column' },
    { label: 'Users', link: '/admin/users', icon: 'fa-solid fa-user' },
    { label: 'Reviews', link: '/admin/reviews', icon: 'fa-regular fa-comment' },
    { label: 'Products', link: '/admin/products', icon: 'fa-solid fa-laptop' },
    { label: 'Categories', link: '/admin/categories', icon: 'fa-solid fa-list' },
    { label: 'Sub Categories', link: '/admin/sub_categories', icon: 'fa-solid fa-list' },
    { label: 'Brands', link: '/admin/brands', icon: 'fa-solid fa-building' },
    { label: 'Orders', link: '/admin/orders', icon: 'fa-solid fa-cart-shopping' },
    { label: 'Process', link: '/admin/process', icon: 'fa-solid fa-gears' },
    { label: 'Your Profile', link: '/admin/admins', icon: 'fa-solid fa-lock' },
  ];

  constructor(private router: Router) {}

  isActive(link: string): boolean {
    return this.router.url === link;
  }

}
