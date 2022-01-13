import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CoffeeService } from '../service/coffee.service';
import { ICoffee, Coffee } from '../coffee.model';

import { CoffeeUpdateComponent } from './coffee-update.component';

describe('Coffee Management Update Component', () => {
  let comp: CoffeeUpdateComponent;
  let fixture: ComponentFixture<CoffeeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let coffeeService: CoffeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CoffeeUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CoffeeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoffeeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    coffeeService = TestBed.inject(CoffeeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const coffee: ICoffee = { id: 456 };

      activatedRoute.data = of({ coffee });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(coffee));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Coffee>>();
      const coffee = { id: 123 };
      jest.spyOn(coffeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coffee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coffee }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(coffeeService.update).toHaveBeenCalledWith(coffee);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Coffee>>();
      const coffee = new Coffee();
      jest.spyOn(coffeeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coffee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coffee }));
      saveSubject.complete();

      // THEN
      expect(coffeeService.create).toHaveBeenCalledWith(coffee);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Coffee>>();
      const coffee = { id: 123 };
      jest.spyOn(coffeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coffee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(coffeeService.update).toHaveBeenCalledWith(coffee);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
