import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { NewUserComponent } from './features/new-user/new-user.component';
import { HomeComponent } from './features/home/home.component';
import { CartComponent } from './features/cart/cart.component';
import { AuthGuardService } from './core/service/auth.guard.service';
import { TrackingComponent } from './features/tracking/tracking.component';
import { ProductDetailComponent } from './features/product-detail/product-detail.component';
import { NewProductComponent } from './features/new-product/new-product.component';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },
  { path: 'new-user', component: NewUserComponent },
  { path: '', component: HomeComponent, canActivate: [AuthGuardService] },
  { path: 'cart', component: CartComponent, canActivate: [AuthGuardService] },
  { 
    path: 'coupons',
    loadComponent: () => import('./features/coupon/coupon.component')
      .then(m => m.CouponComponent),
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'manage-coupons',
    loadComponent: () => import('./features/manage-coupons/manage-coupons.component')
      .then(m => m.ManageCouponsComponent),
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'users',
    loadComponent: () => import('./features/users/users.component')
      .then(m => m.UsersComponent),
    canActivate: [() => AuthGuardService(['ADMIN'])]
  },
  { 
    path: 'user-details',
    loadComponent: () => import('./features/user-details/user-details.component')
      .then(m => m.UserDetailsComponent),
    canActivate: [() => AuthGuardService()]
  },
  { 
    path: 'orders',
    loadComponent: () => import('./features/orders/orders.component')
      .then(m => m.OrdersComponent),
    canActivate: [AuthGuardService]
  },
  { 
    path: 'wallet',
    loadComponent: () => import('./features/wallet/wallet.component')
      .then(m => m.WalletComponent),
    canActivate: [AuthGuardService]
  },
  { 
    path: 'seller-products',
    loadComponent: () => import('./features/seller-products/seller-products.component')
      .then(m => m.SellerProductsComponent),
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'seller-products/:id',
    loadComponent: () => import('./features/seller-product-details/seller-product-details.component')
      .then(m => m.SellerProductDetailsComponent),
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'seller-products/:id/skus',
    loadComponent: () => import('./features/sku-manager/sku-manager.component')
      .then(m => m.SkuManagerComponent),
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'product-list',
    loadComponent: () => import('./features/product-list/product-list.component')
      .then(m => m.ProductListComponent),
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'new-product',
    loadComponent: () => import('./features/new-product/new-product.component')
      .then(m => m.NewProductComponent),
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'new-product/:id',
    component: NewProductComponent,
    canActivate: [() => AuthGuardService(['SELLER'])]
  },
  { 
    path: 'product/:id',
    component: ProductDetailComponent,
    canActivate: [AuthGuardService]
  },
  { 
    path: 'tracking/:orderId',
    component: TrackingComponent,
    canActivate: [AuthGuardService]
  },
  { path: '**', redirectTo: '/login' }
];