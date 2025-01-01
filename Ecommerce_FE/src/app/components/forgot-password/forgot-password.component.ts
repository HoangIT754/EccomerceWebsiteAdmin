import { Component } from '@angular/core';
import { ForgotPasswordService } from '../../services/forgot-password.service';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  providers: [],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  email: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private forgotPasswordService: ForgotPasswordService,
    private router: Router
  ) {}

  onForgotPassword() {
    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;

    if (!this.email) {
      this.errorMessage = 'Email is required.';
      this.isLoading = false;
      return;
    }

    this.forgotPasswordService.sendForgotPasswordEmail(this.email).subscribe(
      () => {
        this.successMessage =
          'A password reset email has been sent to your email address.';
        this.isLoading = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      () => {
        this.errorMessage = 'Failed to send reset email. Please try again.';
        this.isLoading = false;
      }
    );
  }
}
