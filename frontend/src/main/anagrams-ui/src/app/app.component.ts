import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { WordsListComponent } from './tabs/words-list/words-list.component';
import { AddWordComponent } from './tabs/add-word/add-word.component';
import { AnagramLookupComponent } from './tabs/anagram-lookup/anagram-lookup.component';
import { AnagramCountsComponent } from './tabs/anagram-counts/anagram-counts.component';

type Tab = 'words' | 'add' | 'lookup' | 'counts';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, WordsListComponent, AddWordComponent, AnagramLookupComponent, AnagramCountsComponent],
  template: `
    <header>
      <h1>BSG Anagrams</h1>
    </header>

    <nav class="tabs">
      <button [class.active]="active === 'words'"  (click)="active = 'words'">All Words</button>
      <button [class.active]="active === 'add'"    (click)="active = 'add'">Add Word</button>
      <button [class.active]="active === 'lookup'" (click)="active = 'lookup'">Anagram Lookup</button>
      <button [class.active]="active === 'counts'" (click)="active = 'counts'">Anagram Counts</button>
    </nav>

    <main class="tab-content">
      <app-words-list   *ngIf="active === 'words'"></app-words-list>
      <app-add-word     *ngIf="active === 'add'"></app-add-word>
      <app-anagram-lookup *ngIf="active === 'lookup'"></app-anagram-lookup>
      <app-anagram-counts *ngIf="active === 'counts'"></app-anagram-counts>
    </main>
  `,
  styleUrl: './app.component.css'
})
export class AppComponent {
  active: Tab = 'words';
}
