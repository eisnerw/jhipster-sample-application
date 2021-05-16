import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IBirthday, Birthday } from '../birthday.model';
import { BirthdayService } from '../service/birthday.service';

@Component({
  selector: 'jhi-birthday-update',
  templateUrl: './birthday-update.component.html',
})
export class BirthdayUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    lname: [],
    fname: [],
    dob: [],
    isAlive: [],
    additional: [],
  });

  constructor(protected birthdayService: BirthdayService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ birthday }) => {
      this.updateForm(birthday);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const birthday = this.createFromForm();
    if (birthday.id !== undefined) {
      this.subscribeToSaveResponse(this.birthdayService.update(birthday));
    } else {
      this.subscribeToSaveResponse(this.birthdayService.create(birthday));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBirthday>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
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

  protected updateForm(birthday: IBirthday): void {
    this.editForm.patchValue({
      id: birthday.id,
      lname: birthday.lname,
      fname: birthday.fname,
      dob: birthday.dob,
      isAlive: birthday.isAlive,
      additional: birthday.additional,
    });
  }

  protected createFromForm(): IBirthday {
    return {
      ...new Birthday(),
      id: this.editForm.get(['id'])!.value,
      lname: this.editForm.get(['lname'])!.value,
      fname: this.editForm.get(['fname'])!.value,
      dob: this.editForm.get(['dob'])!.value,
      isAlive: this.editForm.get(['isAlive'])!.value,
      additional: this.editForm.get(['additional'])!.value,
    };
  }
}
