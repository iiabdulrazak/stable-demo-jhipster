import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CoffeeComponent } from './list/coffee.component';
import { CoffeeDetailComponent } from './detail/coffee-detail.component';
import { CoffeeUpdateComponent } from './update/coffee-update.component';
import { CoffeeDeleteDialogComponent } from './delete/coffee-delete-dialog.component';
import { CoffeeRoutingModule } from './route/coffee-routing.module';

@NgModule({
  imports: [SharedModule, CoffeeRoutingModule],
  declarations: [CoffeeComponent, CoffeeDetailComponent, CoffeeUpdateComponent, CoffeeDeleteDialogComponent],
  entryComponents: [CoffeeDeleteDialogComponent],
})
export class CoffeeModule {}
