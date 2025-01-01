import { Component } from '@angular/core';
import { UsersService } from '../../../services/usersService/users.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admins',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admins.component.html',
  styleUrl: './admins.component.css'
})
export class AdminsComponent {
  userId: string | null = null;
  userProfile: any = null;
  loading: boolean = true;
  error: string | null = null;

  constructor(private userService: UsersService) {}

  ngOnInit(): void {
    this.fetchUserId();
  }

  fetchUserId(): void {
    this.userService.getUserId().subscribe({
      next: (id) => {
        this.userId = id;
        this.fetchUserProfile(id);
      },
      error: (err) => {
        this.error = 'Failed to fetch user ID.';
        this.loading = false;
        console.error(err);
      },
    });
  }

  fetchUserProfile(userId: string): void {
    this.userService.getUserProfile(userId).subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to fetch user profile.';
        this.loading = false;
        console.error(err);
      },
    });
  }
}
