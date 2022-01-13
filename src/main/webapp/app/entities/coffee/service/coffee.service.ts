import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICoffee, getCoffeeIdentifier } from '../coffee.model';

export type EntityResponseType = HttpResponse<ICoffee>;
export type EntityArrayResponseType = HttpResponse<ICoffee[]>;

@Injectable({ providedIn: 'root' })
export class CoffeeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/coffees');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(coffee: ICoffee): Observable<EntityResponseType> {
    return this.http.post<ICoffee>(this.resourceUrl, coffee, { observe: 'response' });
  }

  update(coffee: ICoffee): Observable<EntityResponseType> {
    return this.http.put<ICoffee>(`${this.resourceUrl}/${getCoffeeIdentifier(coffee) as number}`, coffee, { observe: 'response' });
  }

  partialUpdate(coffee: ICoffee): Observable<EntityResponseType> {
    return this.http.patch<ICoffee>(`${this.resourceUrl}/${getCoffeeIdentifier(coffee) as number}`, coffee, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICoffee>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICoffee[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCoffeeToCollectionIfMissing(coffeeCollection: ICoffee[], ...coffeesToCheck: (ICoffee | null | undefined)[]): ICoffee[] {
    const coffees: ICoffee[] = coffeesToCheck.filter(isPresent);
    if (coffees.length > 0) {
      const coffeeCollectionIdentifiers = coffeeCollection.map(coffeeItem => getCoffeeIdentifier(coffeeItem)!);
      const coffeesToAdd = coffees.filter(coffeeItem => {
        const coffeeIdentifier = getCoffeeIdentifier(coffeeItem);
        if (coffeeIdentifier == null || coffeeCollectionIdentifiers.includes(coffeeIdentifier)) {
          return false;
        }
        coffeeCollectionIdentifiers.push(coffeeIdentifier);
        return true;
      });
      return [...coffeesToAdd, ...coffeeCollection];
    }
    return coffeeCollection;
  }
}
