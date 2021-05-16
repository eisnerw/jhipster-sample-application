import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBirthday } from '../birthday.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { BirthdayService } from '../service/birthday.service';
import { BirthdayDeleteDialogComponent } from '../delete/birthday-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { FormatMediumDatePipe } from '../../../shared/date/format-medium-date.pipe';
import * as dayjs from 'dayjs';
import { Observable } from 'rxjs';
import { of } from 'rxjs';

@Component({
  selector: 'jhi-birthday',
  templateUrl: './birthday.component.html',
})
export class BirthdayComponent implements OnInit {
  birthdays: IBirthday[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;
  formatMediumPipe = new FormatMediumDatePipe();

  columnDefs = [
    { field: 'lname', sortable: true, filter: true },
    { field: 'fname', sortable: true, filter: true },
    { field: 'dob', sortable: true, filter: true, valueFormatter: (data: any) => this.formatMediumPipe.transform(dayjs(data.value)) },
    { field: 'additional', headerName: 'sign', sortable: true, filter: true },
    { field: 'isAlive', sortable: true, filter: true },
  ];

  rowData = new Observable<IBirthday[]>();
  /*
  rowData = [
    { lname: 'Toyota', fname: 'Celica', dob: '2021-05-14T04:00:00.000Z' },
    { lname: 'Ford', fname: 'Mondeo', dob: '2021-05-14T04:00:00.000Z' },
    { lname: 'Porsche', fname: 'Boxter', dob: '2021-05-14T04:00:00.000Z' }
  ];
  */

  constructor(protected birthdayService: BirthdayService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.birthdays = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.birthdayService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IBirthday[]>) => {
          this.isLoading = false;
          this.paginateBirthdays(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.birthdays = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBirthday): number {
    return item.id!;
  }

  delete(birthday: IBirthday): void {
    const modalRef = this.modalService.open(BirthdayDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.birthday = birthday;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateBirthdays(data: IBirthday[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.birthdays.push(d);
      }
      this.rowData = of(this.birthdays);
    }
  }
}
