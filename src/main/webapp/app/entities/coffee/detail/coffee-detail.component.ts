import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICoffee } from '../coffee.model';

@Component({
  selector: 'jhi-coffee-detail',
  templateUrl: './coffee-detail.component.html',
})
export class CoffeeDetailComponent implements OnInit {
  coffee: ICoffee | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coffee }) => {
      this.coffee = coffee;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
