import * as dayjs from 'dayjs';

export interface IBirthday {
  id?: number;
  lname?: string | null;
  fname?: string | null;
  dob?: dayjs.Dayjs | null;
  isAlive?: boolean | null;
  additional?: string | null;
}

export class Birthday implements IBirthday {
  constructor(
    public id?: number,
    public lname?: string | null,
    public fname?: string | null,
    public dob?: dayjs.Dayjs | null,
    public isAlive?: boolean | null,
    public additional?: string | null
  ) {
    this.isAlive = this.isAlive ?? false;
  }
}

export function getBirthdayIdentifier(birthday: IBirthday): number | undefined {
  return birthday.id;
}
