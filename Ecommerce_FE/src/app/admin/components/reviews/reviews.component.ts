import { Component, OnInit } from '@angular/core';
import { ReviewService } from '../../../services/reviewService/review.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.css']
})
export class ReviewsComponent implements OnInit {
  reviews: any[] = [];
  selectedReview: any = null;
  isModalOpen = false;
  isEditMode = false;

  constructor(private reviewService: ReviewService) {}

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(): void {
    this.reviewService.getReviews().subscribe(
      (data) => {
        this.reviews = data;
      },
      (error) => {
        console.error('Failed to fetch reviews', error);
      }
    );
  }

  openAddReview(): void {
    this.isEditMode = true;
    this.selectedReview = { title: '', content: '' };
    this.isModalOpen = true;
  }

  saveReview(): void {
    if (this.isEditMode) {
      this.reviewService.addReview(this.selectedReview).subscribe(
        () => {
          this.loadReviews();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to add review', error);
        }
      );
    } else {
      this.reviewService.updateReview(this.selectedReview.review_id, this.selectedReview).subscribe(
        () => {
          this.loadReviews();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to update review', error);
        }
      );
    }
  }

  deleteReview(id: string): void {
    this.reviewService.deleteReview(id).subscribe(
      () => {
        this.loadReviews();
      },
      (error) => {
        console.error('Failed to delete review', error);
      }
    );
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedReview = null;
  }
}
