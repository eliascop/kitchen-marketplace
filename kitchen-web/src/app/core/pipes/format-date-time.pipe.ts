import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatDateTime',
  standalone: true
})
export class FormatDateTimePipe implements PipeTransform {

  transform(value: string | Date | undefined): string {
    if(value === undefined) return '';

    const date = new Date(value);
    if (isNaN(date.getTime())) return '';

    const pad = (n: number) => n.toString().padStart(2, '0');

    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1);
    const year = date.getFullYear();

    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const seconds = pad(date.getSeconds());

    return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
  }

}
