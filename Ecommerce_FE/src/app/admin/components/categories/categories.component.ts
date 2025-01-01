import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../../services/categoryService/category.service';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css'
})
export class CategoriesComponent implements OnInit{
  categories: any[] = [];
  selectedCategory: any = null;
  isModalOpen = false;
  isEditMode = false;

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe(
      (data) => {
        this.categories = data;
      },
      (error) => {
        console.error('Failed to fetch categories', error);
      }
    );
  }

  openCategoryDetails(category_id: string): void {
    this.isEditMode = false;
    this.categoryService.getCategoryById(category_id).subscribe(
      (data) => {
        this.selectedCategory = data;
        this.isModalOpen = true;
      },
      (error) => {
        console.error('Failed to fetch category details', error);
      }
    );
  }

  openAddCategory(): void {
    this.isEditMode = true;
    this.selectedCategory = { name: '', description: '' };
    this.isModalOpen = true;
  }

  saveCategory(): void {
    if (this.isEditMode) {
      this.categoryService.addCategory(this.selectedCategory).subscribe(
        () => {
          this.loadCategories();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to add category', error);
        }
      );
    } else {
      this.categoryService.updateCategory(this.selectedCategory.category_id, this.selectedCategory).subscribe(
        () => {
          this.loadCategories();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to update category', error);
        }
      );
    }
  }

  deleteCategory(category_id: string): void {
    this.categoryService.deleteCategory(category_id).subscribe(
      () => {
        this.loadCategories();
      },
      (error) => {
        console.error('Failed to delete category', error);
      }
    );
  }
  

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedCategory = null;
  }

  exportReport(format: string): void {
    this.categoryService.exportReport(format).subscribe(
      (response) => {
        const blob = new Blob([response], { type: format === 'pdf' ? 'application/pdf' : format === 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `category-report.${format}`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error(`Failed to export report as ${format}`, error);
      }
    );
  }

}
