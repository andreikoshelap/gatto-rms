import { Component, signal } from '@angular/core';
import { GoogleMapsModule } from '@angular/google-maps';
import { ResourceComponent } from './component/resource.component';

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [
    ResourceComponent,
    GoogleMapsModule
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('resource-ui');
}
