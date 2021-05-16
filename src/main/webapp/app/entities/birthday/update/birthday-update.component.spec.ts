jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BirthdayService } from '../service/birthday.service';
import { IBirthday, Birthday } from '../birthday.model';

import { BirthdayUpdateComponent } from './birthday-update.component';

describe('Component Tests', () => {
  describe('Birthday Management Update Component', () => {
    let comp: BirthdayUpdateComponent;
    let fixture: ComponentFixture<BirthdayUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let birthdayService: BirthdayService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BirthdayUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BirthdayUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BirthdayUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      birthdayService = TestBed.inject(BirthdayService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const birthday: IBirthday = { id: 456 };

        activatedRoute.data = of({ birthday });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(birthday));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const birthday = { id: 123 };
        spyOn(birthdayService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ birthday });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: birthday }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(birthdayService.update).toHaveBeenCalledWith(birthday);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const birthday = new Birthday();
        spyOn(birthdayService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ birthday });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: birthday }));
        saveSubject.complete();

        // THEN
        expect(birthdayService.create).toHaveBeenCalledWith(birthday);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const birthday = { id: 123 };
        spyOn(birthdayService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ birthday });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(birthdayService.update).toHaveBeenCalledWith(birthday);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
