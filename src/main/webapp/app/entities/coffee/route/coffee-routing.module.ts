import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CoffeeComponent } from '../list/coffee.component';
import { CoffeeDetailComponent } from '../detail/coffee-detail.component';
import { CoffeeUpdateComponent } from '../update/coffee-update.component';
import { CoffeeRoutingResolveService } from './coffee-routing-resolve.service';

const coffeeRoute: Routes = [
  {
    path: '',
    component: CoffeeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CoffeeDetailComponent,
    resolve: {
      coffee: CoffeeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CoffeeUpdateComponent,
    resolve: {
      coffee: CoffeeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CoffeeUpdateComponent,
    resolve: {
      coffee: CoffeeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(coffeeRoute)],
  exports: [RouterModule],
})
export class CoffeeRoutingModule {}
