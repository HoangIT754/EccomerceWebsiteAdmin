import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SubCategoryService } from '../../../services/subCategoryService/sub-category.service';

@Component({
  selector: 'app-sub-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sub-categories.component.html',
  styleUrl: './sub-categories.component.css'
})
export class SubCategoriesComponent implements OnInit{
  subCategories: any[] = [];
  categories: any[] = [];
  selectedSubCategory: any = null;
  isModalOpen = false;
  isEditMode = false;

  constructor(private subCategoryService: SubCategoryService) {}

  ngOnInit(): void {
    this.loadSubCategories();
    this.loadCategories();
  }

  loadSubCategories(): void {
    this.subCategoryService.getSubCategories().subscribe(
      (data) => {
        this.subCategories = data;
      },
      (error) => {
        console.error('Failed to fetch sub categories', error);
      }
    );
  }

  loadCategories(): void {
    this.subCategoryService.getCategories().subscribe(
      (data) => {
        this.categories = data;
      },
      (error) => {
        console.error('Failed to fetch categories', error);
      }
    );
  }

  openSubCategoryDetails(id: string): void {
    this.isEditMode = false;
    this.subCategoryService.getSubCategoryById(id).subscribe(
      (data) => {
        this.selectedSubCategory = data;
        this.selectedSubCategory.category_id = data.category.category_id;
        this.isModalOpen = true;
      },
      (error) => {
        console.error('Failed to fetch sub category details', error);
      }
    );
  }
  

  openAddSubCategory(): void {
    this.isEditMode = true;
    this.selectedSubCategory = { name: '', description: '', category_id: '' };
    this.isModalOpen = true;
  }

  saveSubCategory(): void {
    const payload = {
      name: this.selectedSubCategory.name,
      description: this.selectedSubCategory.description,
      category_id: this.selectedSubCategory.category_id,
    };
  
    if (this.isEditMode) {
      this.subCategoryService.addSubCategory(payload).subscribe(
        () => {
          this.loadSubCategories();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to add sub category', error);
        }
      );
    } else {
      this.subCategoryService.updateSubCategory(this.selectedSubCategory.sub_category_id, payload).subscribe(
        () => {
          this.loadSubCategories();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to update sub category', error);
        }
      );
    }
  }

  deleteSubCategory(id: string): void {
    this.subCategoryService.deleteSubCategory(id).subscribe(
      () => {
        this.loadSubCategories();
      },
      (error) => {
        console.error('Failed to delete sub category', error);
      }
    );
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedSubCategory = null;
  }

  exportReport(format: string): void {
    this.subCategoryService.exportReport(format).subscribe(
      (response) => {
        const blob = new Blob([response], { type: format === 'pdf' ? 'application/pdf' : format === 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `sub-category-report.${format}`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error(`Failed to export report as ${format}`, error);
      }
    );
  }
}
