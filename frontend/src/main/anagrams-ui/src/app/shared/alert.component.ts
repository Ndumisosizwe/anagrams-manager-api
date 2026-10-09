import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [NgIf],
  template: `
    <div *ngIf="message" [class]="'alert alert-' + type">{{ message }}</div>
  `,
  styles: [`
    .alert { padding: 10px 14px; border-radius: 4px; margin: 10px 0; font-size: 0.9rem; }
    .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .alert-error   { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    .alert-info    { background: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb; }
  `]
})
export class AlertComponent {
  @Input() message = '';
  @Input() type: 'success' | 'error' | 'info' = 'info';
}
