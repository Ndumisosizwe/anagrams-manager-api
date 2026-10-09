import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, WordResponse } from '../../api.service';
import { AlertComponent } from '../../shared/alert.component';

@Component({
  selector: 'app-anagram-lookup',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertComponent],
  template: `
    <h2>Anagram Lookup</h2>
    <app-alert [message]="alertMsg" [type]="alertType"></app-alert>

    <form class="form" (ngSubmit)="search()" #f="ngForm">
      <div class="form-group">
        <label for="lookup">Enter a word</label>
        <div class="input-row">
          <input
            id="lookup"
            name="lookup"
            type="text"
            [(ngModel)]="query"
            required
            pattern="[a-zA-Z]+"
            placeholder="e.g. LISTEN"
            autocomplete="off"
          />
          <button class="btn btn-primary" type="submit" [disabled]="f.invalid || loading">
            {{ loading ? 'Searching…' : 'Find Anagrams' }}
          </button>
        </div>
      </div>
    </form>

    <ng-container *ngIf="searched">
      <p *ngIf="results.length === 0 && !loading" class="empty">No anagrams found for "{{ lastQuery }}".</p>
      <div *ngIf="results.length > 0">
        <p class="result-count">{{ results.length }} anagram(s) of "{{ lastQuery }}":</p>
        <div class="chips">
          <span class="chip" *ngFor="let w of results">{{ w.word }}</span>
        </div>
      </div>
    </ng-container>
  `,
  styles: [`
    .form { max-width: 520px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; margin-bottom: 16px; }
    label { font-size: 0.9rem; font-weight: 600; color: #444; }
    .input-row { display: flex; gap: 8px; }
    .input-row input { flex: 1; padding: 8px 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1rem; }
    .input-row input:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 2px #bfdbfe; }
    .result-count { color: #555; font-size: 0.9rem; margin-bottom: 8px; }
    .empty { color: #888; }
    .chips { display: flex; flex-wrap: wrap; gap: 8px; }
    .chip { background: #e0f2fe; color: #0369a1; border-radius: 20px; padding: 4px 14px; font-weight: 600; font-size: 0.9rem; }
  `]
})
export class AnagramLookupComponent {
  private api = inject(ApiService);

  query = '';
  results: WordResponse[] = [];
  loading = false;
  searched = false;
  lastQuery = '';
  alertMsg = '';
  alertType: 'success' | 'error' | 'info' = 'info';

  search() {
    if (!this.query.trim()) return;
    this.loading = true;
    this.lastQuery = this.query.trim().toUpperCase();
    this.api.getAnagrams(this.query.trim()).subscribe({
      next: r => { this.results = r; this.searched = true; this.loading = false; },
      error: err => {
        const detail = err.status === 404
          ? `"${this.lastQuery}" not found in dictionary.`
          : 'Failed to fetch anagrams.';
        this.showAlert(detail, 'error');
        this.searched = false;
        this.loading = false;
      }
    });
  }

  private showAlert(msg: string, type: 'success' | 'error' | 'info') {
    this.alertMsg = msg; this.alertType = type;
    setTimeout(() => this.alertMsg = '', 5000);
  }
}
