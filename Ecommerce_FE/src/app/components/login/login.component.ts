import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { KeycloakServiceWrapper } from '../../services/keycloak/keycloak.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  providers: [KeycloakServiceWrapper],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';
  countdown: number = 0;
  showPassword: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onLogin() {
    console.log("In onLogin function");
    this.authService.login(this.username, this.password).subscribe(
      () => {
        this.router.navigate(['/home']);
      },
      (error) => {
        console.log("Error is: ", error.error.message);
        
        if (error.status === 403 && error.error.message === 'Account is not fully set up') {
          this.countdown = 4;
          const interval = setInterval(() => {
            this.countdown--;
            this.errorMessage = `Your account is not verify! Verify account in ${this.countdown} seconds`;

            if (this.countdown <= 0) {
              clearInterval(interval);
              this.authService.keycloakLogin();
            }
          }, 1000);
        } else {
          this.errorMessage = 'An error occurred during login';
        }
      }
    );
  }
}
