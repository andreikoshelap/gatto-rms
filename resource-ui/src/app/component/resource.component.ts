import {Component, OnInit, signal} from '@angular/core';
import {GoogleMap, MapMarker} from '@angular/google-maps';
import {NgForOf, NgIf} from '@angular/common';
import { GoogleMapsLoaderService } from '../service/google-maps-loader.service';
import { ResourceService } from '../service/resource.service';
import { Resource } from '../model/resource.model';

@Component({
  selector: 'app-resource',
  templateUrl: './resource.component.html',
  imports: [
    GoogleMap,
    MapMarker,
    NgForOf,
    NgIf
  ],
  styleUrls: ['./resource.component.css']
})
export class ResourceComponent implements OnInit {
  center = {lat: 56.95, lng: 24.11};
  markers: any[] = [];
  zoom = 7;
  mapReady = false;
  resources = signal<Resource[]>([]);

  constructor(
    private googleMapsLoader: GoogleMapsLoaderService,
    private resourceService: ResourceService
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      await this.googleMapsLoader.load();
      this.resourceService.getAll().subscribe({
        next: (resources: Resource[]) => {
          this.markers = resources.map(resource => ({
            position: {
              lat: resource.location.latitude,
              lng: resource.location.longitude
            },
            label: resource.type.charAt(0),
            title: `${resource.type} in ${resource.location.city} - ${resource.characteristics.map(c => c.value).join(', ')}`
          }));
          this.mapReady = true;
        },
        error: (error) => {
          console.error('Failed to load resources', error);
          this.mapReady = true;
        }
      });
    } catch (error) {
      console.error('Google Maps failed to load', error);
    }
  }
}
