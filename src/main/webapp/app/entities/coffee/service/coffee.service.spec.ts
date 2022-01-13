import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICoffee, Coffee } from '../coffee.model';

import { CoffeeService } from './coffee.service';

describe('Coffee Service', () => {
  let service: CoffeeService;
  let httpMock: HttpTestingController;
  let elemDefault: ICoffee;
  let expectedResult: ICoffee | ICoffee[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CoffeeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      price: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Coffee', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Coffee()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Coffee', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          price: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Coffee', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
        },
        new Coffee()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Coffee', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          price: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Coffee', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCoffeeToCollectionIfMissing', () => {
      it('should add a Coffee to an empty array', () => {
        const coffee: ICoffee = { id: 123 };
        expectedResult = service.addCoffeeToCollectionIfMissing([], coffee);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coffee);
      });

      it('should not add a Coffee to an array that contains it', () => {
        const coffee: ICoffee = { id: 123 };
        const coffeeCollection: ICoffee[] = [
          {
            ...coffee,
          },
          { id: 456 },
        ];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, coffee);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Coffee to an array that doesn't contain it", () => {
        const coffee: ICoffee = { id: 123 };
        const coffeeCollection: ICoffee[] = [{ id: 456 }];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, coffee);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coffee);
      });

      it('should add only unique Coffee to an array', () => {
        const coffeeArray: ICoffee[] = [{ id: 123 }, { id: 456 }, { id: 2184 }];
        const coffeeCollection: ICoffee[] = [{ id: 123 }];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, ...coffeeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const coffee: ICoffee = { id: 123 };
        const coffee2: ICoffee = { id: 456 };
        expectedResult = service.addCoffeeToCollectionIfMissing([], coffee, coffee2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coffee);
        expect(expectedResult).toContain(coffee2);
      });

      it('should accept null and undefined values', () => {
        const coffee: ICoffee = { id: 123 };
        expectedResult = service.addCoffeeToCollectionIfMissing([], null, coffee, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coffee);
      });

      it('should return initial array if no Coffee is added', () => {
        const coffeeCollection: ICoffee[] = [{ id: 123 }];
        expectedResult = service.addCoffeeToCollectionIfMissing(coffeeCollection, undefined, null);
        expect(expectedResult).toEqual(coffeeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
