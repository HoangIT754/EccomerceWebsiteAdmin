import { Routes } from '@angular/router';
import { authGuard } from './guard/auth.guard';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { AdminComponent } from './admin/admin/admin.component';
import { UsersComponent } from './admin/components/users/users.component';
import { BrandsComponent } from './admin/components/brands/brands.component';
import { CategoriesComponent } from './admin/components/categories/categories.component';
import { SubCategoriesComponent } from './admin/components/sub-categories/sub-categories.component';
import { ProductsComponent } from './admin/components/products/products.component';
import { OrdersComponent } from './admin/components/orders/orders.component';
import { ReviewsComponent } from './admin/components/reviews/reviews.component';
import { CamundaProcessComponent } from './admin/components/camunda-process/camunda-process.component';
import { DashboardComponent } from './admin/components/dashboard/dashboard.component';
import { AdminsComponent } from './admin/components/admins/admins.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { AccessDeniedComponent } from './components/access-denied/access-denied.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'access-denied', component: AccessDeniedComponent },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [authGuard],
    children: [
      { path: 'users', component: UsersComponent },
      { path: 'brands', component: BrandsComponent },
      { path: 'categories', component: CategoriesComponent },
      { path: 'sub_categories', component: SubCategoriesComponent },
      { path: 'products', component: ProductsComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'reviews', component: ReviewsComponent },
      { path: 'process', component: CamundaProcessComponent },
      { path: 'admins', component: AdminsComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  { path: '**', component: PageNotFoundComponent },
];