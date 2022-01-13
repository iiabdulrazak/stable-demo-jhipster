import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'coffee',
        data: { pageTitle: 'stabledemojhipsterApp.coffee.home.title' },
        loadChildren: () => import('./coffee/coffee.module').then(m => m.CoffeeModule),
      },
      {
        path: 'customer',
        data: { pageTitle: 'stabledemojhipsterApp.customer.home.title' },
        loadChildren: () => import('./customer/customer.module').then(m => m.CustomerModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
