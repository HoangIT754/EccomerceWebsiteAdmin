import { Component, OnInit } from '@angular/core';
import { BrandService } from '../../../services/brandService/brand.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-brands',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './brands.component.html',
  styleUrl: './brands.component.css'
})
export class BrandsComponent implements OnInit {

  brands: any[] = [];
  selectedBrand: any = null;
  isModalOpen = false;
  isEditMode = false;
  selectedFile: File | null = null;
  statusMessage: string = '';
  isError: boolean = false;


  constructor(private brandService: BrandService) {}

  ngOnInit(): void {
    this.loadBrands();
  }

  loadBrands(): void {
    this.brandService.getBrands().subscribe(
      (data) => {
        this.brands = data;
      },
      (error) => {
        console.error('Failed to fetch brands', error);
      }
    );
  }

  openBrandDetails(id: string): void {
    this.isEditMode = false;
    this.brandService.getBrandById(id).subscribe(
      (data) => {
        this.selectedBrand = data;
        this.isModalOpen = true;
      },
      (error) => {
        console.error('Failed to fetch brand details', error);
      }
    );
  }

  openAddBrand(): void {
    this.isEditMode = true;
    this.selectedBrand = { name: '', description: '' };
    this.isModalOpen = true;
  }

  saveBrand(): void {
    if (this.isEditMode) {
      this.brandService.addBrand(this.selectedBrand).subscribe(
        () => {
          this.loadBrands();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to add brand', error);
        }
      );
    } else {
      this.brandService.updateBrand(this.selectedBrand.brand_id, this.selectedBrand).subscribe(
        () => {
          this.loadBrands();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to update brand', error);
        }
      );
    }
  }

  deleteBrand(id: string): void {
    this.brandService.deleteBrand(id).subscribe(
      () => {
        this.loadBrands();
      },
      (error) => {
        console.error('Failed to delete brand', error);
      }
    );
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedBrand = null;
  }

  exportReport(format: string): void {
    this.brandService.exportReport(format).subscribe(
      (response) => {
        const blob = new Blob([response], { type: format === 'pdf' ? 'application/pdf' : format === 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `brand-report.${format}`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error(`Failed to export report as ${format}`, error);
      }
    );
  }

  onFileSelect(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  importFile(): void {
    if (!this.selectedFile) {
      this.statusMessage = 'Please select a file to import.';
      Swal.fire({
        title: 'Error!',
        text: 'Please select a file to import.',
        icon: 'error',
        confirmButtonText: 'OK'
      });
      this.isError = true;
      return;
    }
  
    this.brandService.importFile(this.selectedFile).subscribe(
      (response: any) => {
        this.statusMessage = response || 'File imported successfully!';
        this.isError = false;
        Swal.fire({
          title: 'Success!',
          text: 'File imported successfully!',
          icon: 'success',
          confirmButtonText: 'OK'
        });
        
        this.loadBrands();
        this.selectedFile = null;
      },
      (error) => {
        this.statusMessage = error?.error?.message || 'Failed to import file.';
        this.isError = true;
  
        Swal.fire({
          title: 'Error!',
          text: this.statusMessage,
          icon: 'error',
          confirmButtonText: 'OK'
        });
  
        if (error?.status) {
          console.log(`Error status: ${error?.status} - ${error?.message}`);
        } else {
          console.log(`Error response: ${error?.error?.message || 'Unknown error'}`);
        }
      }
    );
  }
  
}
