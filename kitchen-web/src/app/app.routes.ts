import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { NewUserComponent } from './features/new-user/new-user.component';
import { HomeComponent } from './features/home/home.component';
import { CartComponent } from './features/cart/cart.component';
import { UsersComponent } from './features/users/users.component';
import { UserDetailsComponent } from './features/user-details/user-details.component';
import { OrdersComponent } from './features/orders/orders.component';
import { WalletComponent } from './features/wallet/wallet.component';
import { OrdersPainelComponent } from './shared/components/orders-painel/orders-painel.component';
import { ProductListComponent } from './features/product-list/product-list.component';
import { NewProductComponent } from './features/new-product/new-product.component';
import { ProductDetailComponent } from './features/product-detail/product-detail.component';
import { TrackingComponent } from './features/tracking/tracking.component';
import { AuthGuardService } from './core/service/auth.guard.service';
import { CouponComponent } from './features/coupon/coupon.component';
import { ManageCouponsComponent } from './features/manage-coupons/manage-coupons.component';
import { SellerProductsComponent } from './features/seller-products/seller-products.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'new-user', component: NewUserComponent},
  
    { path: '', component: HomeComponent, canActivate: [AuthGuardService] },
    { path: 'cart', component: CartComponent, canActivate: [AuthGuardService] },
    { path: 'coupons', component: CouponComponent, canActivate: [()=> AuthGuardService(['SELLER'])]},
    { path: 'manage-coupons', component: ManageCouponsComponent, canActivate: [()=> AuthGuardService(['SELLER'])]},
    { path: 'users', component: UsersComponent, canActivate: [()=> AuthGuardService(['ADMIN','SELLER'])] },
    { path: 'user-details', component: UserDetailsComponent, canActivate: [()=> AuthGuardService] },
    { path: 'orders', component: OrdersComponent, canActivate: [AuthGuardService] },
    { path: 'wallet', component: WalletComponent, canActivate: [AuthGuardService] },
    { path: 'orders-painel', component: OrdersPainelComponent, canActivate: [()=> AuthGuardService(['ADMIN','SELLER'])] },
    { path: 'seller-products', component: SellerProductsComponent, canActivate: [()=> AuthGuardService(['SELLER'])] },
    { path: 'product-list', component: ProductListComponent, canActivate: [()=> AuthGuardService(['SELLER'])] },
    { path: 'new-product', component: NewProductComponent, canActivate: [()=> AuthGuardService(['SELLER'])] },
    { path: 'product/:id', component: ProductDetailComponent,canActivate: [AuthGuardService]  },
    { path: 'tracking/:orderId', component: TrackingComponent, canActivate: [AuthGuardService] },
    { path: '**', redirectTo: '/login' }
  ];