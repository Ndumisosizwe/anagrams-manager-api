import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { ApiService, AnagramCountResponse } from '../../api.service';
import { AlertComponent } from '../../shared/alert.component';

@Component({
  selector: 'app-anagram-counts',
  standalone: true,
  imports: [NgFor, NgIf, AlertComponent],
  template: `
    <h2>Anagram Counts</h2>
    <app-alert [message]="alertMsg" [type]="alertType"></app-alert>

    <div class="toolbar">
      <p class="description">Number of anagram groups per word length across the dictionary.</p>
      <button class="btn btn-primary" (click)="load()" [disabled]="loading">
        {{ loading ? 'Computing…' : data ? 'Recompute' : 'Compute' }}
      </button>
    </div>

    <div *ngIf="data" class="results">
      <p class="timing">Computed in <strong>{{ data.computationTimeMs }} ms</strong></p>
      <table>
        <thead>
          <tr><th>Word Length</th><th>Anagram Groups</th><th>Summary</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let r of data.results">
            <td>{{ r.wordLength }}</td>
            <td>{{ r.anagramCount }}</td>
            <td class="summary">{{ r.summary }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; gap: 16px; }
    .description { color: #555; font-size: 0.9rem; margin: 0; max-width: 500px; }
    .timing { color: #2563eb; font-size: 0.9rem; margin-bottom: 12px; }
    .results { }
    table { width: 100%; border-collapse: collapse; }
    th, td { text-align: left; padding: 8px 12px; border-bottom: 1px solid #e0e0e0; }
    th { background: #f5f5f5; font-weight: 600; }
    tr:hover td { background: #fafafa; }
    .summary { color: #555; font-size: 0.9rem; }
  `]
})
export class AnagramCountsComponent {
  private api = inject(ApiService);

  data: AnagramCountResponse | null = null;
  loading = false;
  alertMsg = '';
  alertType: 'success' | 'error' | 'info' = 'info';

  load() {
    this.loading = true;
    this.api.getAnagramCounts().subscribe({
      next: r => { this.data = r; this.loading = false; },
      error: () => { this.showAlert('Failed to compute anagram counts.', 'error'); this.loading = false; }
    });
  }

  private showAlert(msg: string, type: 'success' | 'error' | 'info') {
    this.alertMsg = msg; this.alertType = type;
    setTimeout(() => this.alertMsg = '', 5000);
  }
}
