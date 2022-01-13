import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CoffeeService } from '../service/coffee.service';

import { CoffeeComponent } from './coffee.component';

describe('Coffee Management Component', () => {
  let comp: CoffeeComponent;
  let fixture: ComponentFixture<CoffeeComponent>;
  let service: CoffeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [CoffeeComponent],
    })
      .overrideTemplate(CoffeeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoffeeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CoffeeService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.coffees?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
