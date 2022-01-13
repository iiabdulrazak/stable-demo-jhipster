import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICoffee } from '../coffee.model';
import { CoffeeService } from '../service/coffee.service';
import { CoffeeDeleteDialogComponent } from '../delete/coffee-delete-dialog.component';

@Component({
  selector: 'jhi-coffee',
  templateUrl: './coffee.component.html',
})
export class CoffeeComponent implements OnInit {
  coffees?: ICoffee[];
  isLoading = false;

  constructor(protected coffeeService: CoffeeService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.coffeeService.query().subscribe({
      next: (res: HttpResponse<ICoffee[]>) => {
        this.isLoading = false;
        this.coffees = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICoffee): number {
    return item.id!;
  }

  delete(coffee: ICoffee): void {
    const modalRef = this.modalService.open(CoffeeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.coffee = coffee;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
