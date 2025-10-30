import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface SearchQuery {
  term: string;
  field?: string | null;
}

@Injectable({
    providedIn: 'root'
})
export class SearchService {
  private querySubject = new BehaviorSubject<SearchQuery>({ term: '' });
  query$ = this.querySubject.asObservable();

  setQuery(query: SearchQuery) {
    this.querySubject.next({
      term: query.term?.trim() ?? '',
      field: query.field ?? null,
    });
  }

  setSearchTerm(event: Event) {
    const input = event.target as HTMLInputElement;
    const term = input.value.trim();
    this.setQuery({ term });
  }
}
