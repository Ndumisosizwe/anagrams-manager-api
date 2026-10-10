import { Component, OnInit, inject } from '@angular/core';
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
        <label for="lookup">Enter a word from the dictionary</label>
        <div class="input-row">
          <input
            id="lookup"
            name="lookup"
            type="text"
            [(ngModel)]="query"
            required
            pattern="[a-zA-Z]+"
            placeholder="e.g. SPARE"
            autocomplete="off"
          />
          <button class="btn btn-primary" type="submit" [disabled]="f.invalid || loading">
            {{ loading ? 'Searching…' : 'Find Anagrams' }}
          </button>
        </div>
      </div>
    </form>

    <div *ngIf="examples.length > 0" class="examples">
      <span class="examples-label">Try one of these:</span>
      <span
        class="example-chip"
        *ngFor="let e of examples"
        (click)="tryExample(e)"
      >{{ e }}</span>
      <button class="btn-link" (click)="loadExamples()">refresh</button>
    </div>

    <ng-container *ngIf="searched">
      <p *ngIf="results.length === 0 && !loading" class="empty">
        No anagrams found for <strong>"{{ lastQuery }}"</strong> in the dictionary.
      </p>
      <div *ngIf="results.length > 0">
        <p class="result-count">{{ results.length }} anagram(s) of <strong>"{{ lastQuery }}"</strong>:</p>
        <div class="chips">
          <span class="chip" *ngFor="let w of results">{{ w.word }}</span>
        </div>
      </div>
    </ng-container>
  `,
  styles: [`
    .form { max-width: 520px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; }
    label { font-size: 0.9rem; font-weight: 600; color: #444; }
    .input-row { display: flex; gap: 8px; }
    .input-row input { flex: 1; padding: 8px 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1rem; }
    .input-row input:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 2px #bfdbfe; }

    .examples { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; margin-bottom: 20px; }
    .examples-label { font-size: 0.8rem; color: #888; }
    .example-chip {
      background: #f1f5f9; color: #334155; border: 1px solid #cbd5e1;
      border-radius: 20px; padding: 3px 12px; font-size: 0.85rem;
      cursor: pointer; transition: background 0.15s;
    }
    .example-chip:hover { background: #e2e8f0; }
    .btn-link { background: none; border: none; color: #2563eb; font-size: 0.8rem; cursor: pointer; padding: 0; }
    .btn-link:hover { text-decoration: underline; }

    .result-count { color: #555; font-size: 0.9rem; margin-bottom: 8px; }
    .empty { color: #888; }
    .chips { display: flex; flex-wrap: wrap; gap: 8px; }
    .chip { background: #e0f2fe; color: #0369a1; border-radius: 20px; padding: 4px 14px; font-weight: 600; font-size: 0.9rem; }
  `]
})
export class AnagramLookupComponent implements OnInit {
  private api = inject(ApiService);

  query = '';
  results: WordResponse[] = [];
  examples: string[] = [];
  loading = false;
  searched = false;
  lastQuery = '';
  alertMsg = '';
  alertType: 'success' | 'error' | 'info' = 'info';

  ngOnInit() {
    this.loadExamples();
  }

  loadExamples() {
    this.api.getExampleWords(6).subscribe({
      next: words => this.examples = words,
      error: () => {}  // silently skip if endpoint not yet available
    });
  }

  tryExample(word: string) {
    this.query = word;
    this.search();
  }

  search() {
    if (!this.query.trim()) return;
    this.loading = true;
    this.lastQuery = this.query.trim().toUpperCase();
    this.api.getAnagrams(this.query.trim()).subscribe({
      next: r => { this.results = r; this.searched = true; this.loading = false; },
      error: err => {
        const detail = err.status === 404
          ? `"${this.lastQuery}" was not found in the dictionary. Try one of the suggestions below.`
          : 'Failed to fetch anagrams.';
        this.showAlert(detail, 'error');
        this.searched = false;
        this.loading = false;
      }
    });
  }

  private showAlert(msg: string, type: 'success' | 'error' | 'info') {
    this.alertMsg = msg; this.alertType = type;
    setTimeout(() => this.alertMsg = '', 6000);
  }
}
