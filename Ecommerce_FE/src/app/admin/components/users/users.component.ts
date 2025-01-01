import { Component, OnInit  } from '@angular/core';
import { UsersService } from '../../../services/usersService/users.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit{
  users: any[] = [];
  selectedUser: any = null;
  isModalOpen = false;

  constructor(private userService: UsersService) {}

  ngOnInit(): void {
    this.userService.getUsers().subscribe(
      (data) => {
        this.users = data;
      },
      (error) => {
        console.error('Failed to fetch users', error);
      }
    );
  }

  openUserDetails(id: string): void {
    this.userService.getUserById(id).subscribe(
      (data) => {
        this.selectedUser = data;
        this.isModalOpen = true;
      },
      (error) => {
        console.error('Failed to fetch user details', error);
      }
    );
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedUser = null;
  }
}
