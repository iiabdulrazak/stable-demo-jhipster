import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICoffee, Coffee } from '../coffee.model';
import { CoffeeService } from '../service/coffee.service';

@Injectable({ providedIn: 'root' })
export class CoffeeRoutingResolveService implements Resolve<ICoffee> {
  constructor(protected service: CoffeeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICoffee> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((coffee: HttpResponse<Coffee>) => {
          if (coffee.body) {
            return of(coffee.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Coffee());
  }
}
