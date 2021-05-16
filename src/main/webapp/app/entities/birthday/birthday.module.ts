import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { BirthdayComponent } from './list/birthday.component';
import { BirthdayDetailComponent } from './detail/birthday-detail.component';
import { BirthdayUpdateComponent } from './update/birthday-update.component';
import { BirthdayDeleteDialogComponent } from './delete/birthday-delete-dialog.component';
import { BirthdayRoutingModule } from './route/birthday-routing.module';
import { AgGridModule } from 'ag-grid-angular';

@NgModule({
  imports: [SharedModule, BirthdayRoutingModule, AgGridModule.withComponents()],
  declarations: [BirthdayComponent, BirthdayDetailComponent, BirthdayUpdateComponent, BirthdayDeleteDialogComponent],
  entryComponents: [BirthdayDeleteDialogComponent],
  providers: [],
  bootstrap: [BirthdayComponent],
})
export class BirthdayModule {}
