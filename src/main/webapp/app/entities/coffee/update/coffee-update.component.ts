import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICoffee, Coffee } from '../coffee.model';
import { CoffeeService } from '../service/coffee.service';

@Component({
  selector: 'jhi-coffee-update',
  templateUrl: './coffee-update.component.html',
})
export class CoffeeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.minLength(10), Validators.maxLength(50)]],
    price: [null, [Validators.min(5), Validators.max(10)]],
  });

  constructor(protected coffeeService: CoffeeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coffee }) => {
      this.updateForm(coffee);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const coffee = this.createFromForm();
    if (coffee.id !== undefined) {
      this.subscribeToSaveResponse(this.coffeeService.update(coffee));
    } else {
      this.subscribeToSaveResponse(this.coffeeService.create(coffee));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICoffee>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(coffee: ICoffee): void {
    this.editForm.patchValue({
      id: coffee.id,
      name: coffee.name,
      price: coffee.price,
    });
  }

  protected createFromForm(): ICoffee {
    return {
      ...new Coffee(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      price: this.editForm.get(['price'])!.value,
    };
  }
}
