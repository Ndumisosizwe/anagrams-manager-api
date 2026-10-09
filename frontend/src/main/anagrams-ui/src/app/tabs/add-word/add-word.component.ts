import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../api.service';
import { AlertComponent } from '../../shared/alert.component';

@Component({
  selector: 'app-add-word',
  standalone: true,
  imports: [FormsModule, AlertComponent],
  template: `
    <h2>Add Word</h2>
    <app-alert [message]="alertMsg" [type]="alertType"></app-alert>

    <form class="form" (ngSubmit)="submit()" #f="ngForm">
      <div class="form-group">
        <label for="word">Word</label>
        <input
          id="word"
          name="word"
          type="text"
          [(ngModel)]="word"
          required
          pattern="[a-zA-Z]+"
          placeholder="Enter a word…"
          autocomplete="off"
        />
      </div>
      <button class="btn btn-primary" type="submit" [disabled]="f.invalid || submitting">
        {{ submitting ? 'Saving…' : 'Add Word' }}
      </button>
    </form>
  `,
  styles: [`
    .form { max-width: 360px; }
    .form-group { display: flex; flex-direction: column; gap: 4px; margin-bottom: 16px; }
    label { font-size: 0.9rem; font-weight: 600; color: #444; }
    input { padding: 8px 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1rem; }
    input:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 2px #bfdbfe; }
  `]
})
export class AddWordComponent {
  private api = inject(ApiService);

  word = '';
  submitting = false;
  alertMsg = '';
  alertType: 'success' | 'error' | 'info' = 'info';

  submit() {
    if (!this.word.trim()) return;
    this.submitting = true;
    this.api.addWord(this.word.trim()).subscribe({
      next: r => { this.showAlert(`"${r.word}" added successfully.`, 'success'); this.word = ''; this.submitting = false; },
      error: err => {
        const detail = err.error?.detail ?? 'Failed to add word.';
        this.showAlert(detail, 'error');
        this.submitting = false;
      }
    });
  }

  private showAlert(msg: string, type: 'success' | 'error' | 'info') {
    this.alertMsg = msg; this.alertType = type;
    setTimeout(() => this.alertMsg = '', 5000);
  }
}
