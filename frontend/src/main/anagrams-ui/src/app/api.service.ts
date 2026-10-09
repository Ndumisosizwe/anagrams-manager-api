import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WordResponse {
  id: number;
  word: string;
  wordLength: number;
  createdAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AnagramCountEntry {
  wordLength: number;
  anagramCount: number;
  summary: string;
}

export interface AnagramCountResponse {
  computationTimeMs: number;
  results: AnagramCountEntry[];
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = 'http://localhost:8080/api';

  getWords(page = 0, size = 50): Observable<PagedResponse<WordResponse>> {
    const params = new HttpParams().set('page', page).set('size', size).set('sortBy', 'word');
    return this.http.get<PagedResponse<WordResponse>>(`${this.base}/words`, { params });
  }

  addWord(word: string): Observable<WordResponse> {
    return this.http.post<WordResponse>(`${this.base}/words`, { word });
  }

  deleteWord(word: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/words/${encodeURIComponent(word)}`);
  }

  getAnagrams(word: string): Observable<WordResponse[]> {
    return this.http.get<WordResponse[]>(`${this.base}/words/${encodeURIComponent(word)}/anagrams`);
  }

  getAnagramCounts(): Observable<AnagramCountResponse> {
    return this.http.get<AnagramCountResponse>(`${this.base}/anagrams/counts`);
  }
}
