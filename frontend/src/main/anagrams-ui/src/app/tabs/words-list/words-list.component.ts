import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { ApiService, WordResponse } from '../../api.service';
import { AlertComponent } from '../../shared/alert.component';

@Component({
  selector: 'app-words-list',
  standalone: true,
  imports: [NgFor, NgIf, AlertComponent],
  template: `
    <h2>All Words</h2>
    <app-alert [message]="alertMsg" [type]="alertType"></app-alert>

    <div class="toolbar">
      <span class="count">{{ totalElements }} words total</span>
      <button class="btn btn-secondary" (click)="load()">Refresh</button>
    </div>

    <div *ngIf="loading" class="loading">Loading…</div>

    <table *ngIf="!loading && words.length > 0">
      <thead>
        <tr><th>Word</th><th>Length</th><th></th></tr>
      </thead>
      <tbody>
        <tr *ngFor="let w of words">
          <td>{{ w.word }}</td>
          <td>{{ w.wordLength }}</td>
          <td><button class="btn btn-danger btn-sm" (click)="delete(w.word)">Delete</button></td>
        </tr>
      </tbody>
    </table>

    <p *ngIf="!loading && words.length === 0">No words found.</p>

    <div class="pagination" *ngIf="totalPages > 1">
      <button class="btn btn-secondary btn-sm" [disabled]="page === 0" (click)="prev()">‹ Prev</button>
      <span>Page {{ page + 1 }} of {{ totalPages }}</span>
      <button class="btn btn-secondary btn-sm" [disabled]="page >= totalPages - 1" (click)="next()">Next ›</button>
    </div>
  `,
  styles: [`
    .toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    .count { color: #666; font-size: 0.9rem; }
    table { width: 100%; border-collapse: collapse; }
    th, td { text-align: left; padding: 8px 12px; border-bottom: 1px solid #e0e0e0; }
    th { background: #f5f5f5; font-weight: 600; }
    tr:hover td { background: #fafafa; }
    .pagination { display: flex; align-items: center; gap: 12px; margin-top: 16px; }
    .loading { color: #888; padding: 20px 0; }
  `]
})
export class WordsListComponent implements OnInit {
  private api = inject(ApiService);

  words: WordResponse[] = [];
  page = 0;
  totalPages = 0;
  totalElements = 0;
  loading = false;
  alertMsg = '';
  alertType: 'success' | 'error' | 'info' = 'info';

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.api.getWords(this.page).subscribe({
      next: r => { this.words = r.content; this.totalPages = r.totalPages; this.totalElements = r.totalElements; this.loading = false; },
      error: () => { this.showAlert('Failed to load words.', 'error'); this.loading = false; }
    });
  }

  delete(word: string) {
    if (!confirm(`Delete "${word}"?`)) return;
    this.api.deleteWord(word).subscribe({
      next: () => { this.showAlert(`"${word}" deleted.`, 'success'); this.load(); },
      error: () => this.showAlert(`Failed to delete "${word}".`, 'error')
    });
  }

  prev() { this.page--; this.load(); }
  next() { this.page++; this.load(); }

  private showAlert(msg: string, type: 'success' | 'error' | 'info') {
    this.alertMsg = msg; this.alertType = type;
    setTimeout(() => this.alertMsg = '', 4000);
  }
}
