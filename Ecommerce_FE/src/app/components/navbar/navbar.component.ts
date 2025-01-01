import { Component, OnInit  } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isLoggedIn: boolean = false;
  
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.checkLoginStatus();
  }

  checkLoginStatus(): void {
    this.authService.validateSession().subscribe({
      next: (response: boolean) => {
        this.isLoggedIn = response;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error validating session:', err);
        this.isLoggedIn = false;
      },
    });
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
  }
}
