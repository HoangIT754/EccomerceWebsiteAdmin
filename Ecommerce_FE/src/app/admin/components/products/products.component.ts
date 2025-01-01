import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/productService/product.service';
import { CategoryService } from '../../../services/categoryService/category.service';
import { SubCategoryService } from '../../../services/subCategoryService/sub-category.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: any[] = [];
  categories: any[] = [];
  subCategories: any[] = [];
  brands: any[] = [];
  selectedProduct: any = null;
  isModalOpen = false;
  isEditMode = false;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private subCategoryService: SubCategoryService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
    this.loadBrands();
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe(
      (data) => (this.products = data),
      (error) => console.error('Failed to fetch products', error)
    );
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe(
      (data) => (this.categories = data),
      (error) => console.error('Failed to fetch categories', error)
    );
  }

  loadSubCategories(categoryId: string): void {
    this.subCategoryService.getSubCategoriesByCategory(categoryId).subscribe(
      (data) => (this.subCategories = data),
      (error) => console.error('Failed to fetch subcategories', error)
    );
  }

  loadBrands(): void {
    this.productService.getBrands().subscribe(
      (data) => (this.brands = data),
      (error) => console.error('Failed to fetch brands', error)
    );
    
  }

  onCategoryChange(event: Event): void {
    const selectedCategoryId = (event.target as HTMLSelectElement).value;
    if (selectedCategoryId) {
      this.loadSubCategories(selectedCategoryId);
    }
  }

  openProductDetails(id: string): void {
    this.isEditMode = false;
    this.productService.getProductById(id).subscribe(
      (data) => {
        this.selectedProduct = data;
        this.selectedProduct.category_id = data.subCategories[0]?.category?.category_id || '';
        this.loadSubCategories(this.selectedProduct.category_id);
        this.isModalOpen = true;
      },
      (error) => console.error('Failed to fetch product details', error)
    );
  }

  openAddProduct(): void {
    this.isEditMode = true;
    this.selectedProduct = {
      name: '',
      brand: '',
      category_id: '',
      subCategories: [],
      price: 0,
      stock: 0,
      description: '',
      imgURL: ''
    };
    this.isModalOpen = true;
  }

  saveProduct(): void {
    const payload = {
      name: this.selectedProduct.name,
      brand: this.selectedProduct.brand,
      subCategories: this.selectedProduct.subCategories,
      price: this.selectedProduct.price,
      stock: this.selectedProduct.stock,
      description: this.selectedProduct.description,
      imgURL: this.selectedProduct.imgURL,
      isShow: true
    };
  
    const processVariables = {
      productName: payload.name,
      price: payload.price,
      description: payload.description,
      stock: payload.stock,
      brandId: payload.brand,
      subCategoryIds: payload.subCategories,
      imgUrl: payload.imgURL
    };
  
    if (this.isEditMode) {
      Swal.fire({
        title: 'Waiting for approval...',
        text: 'Your request is being processed. Please wait for the admin to approve.',
        icon: 'info',
        confirmButtonText: 'OK'
      });
  
      this.productService.startProcess('add_product', processVariables).subscribe(
        () => {
          this.loadProducts();
          this.closeModal();
        },
        (error) => {
          console.error('Failed to start process', error);
        }
      );
    } else {
      this.productService.updateProduct(this.selectedProduct.productId, payload).subscribe(
        () => {
          this.loadProducts();
          this.closeModal();
        },
        (error) => console.error('Failed to update product', error)
      );
    }
  }
  
  

  deleteProduct(id: string): void {
    this.productService.deleteProduct(id).subscribe(
      () => this.loadProducts(),
      (error) => console.error('Failed to delete product', error)
    );
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedProduct = null;
  }
}
