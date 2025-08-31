import {Component, OnInit} from '@angular/core';
import {GoogleMap, MapMarker} from '@angular/google-maps';
import {NgForOf, NgIf} from '@angular/common';
import { GoogleMapsLoaderService } from '../service/google-maps-loader.service';
import { ResourceService } from '../service/resource.service';
import {HttpClient} from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { Resource } from '../model/resource.model';
import { ResourceDialogComponent } from './resource-dialog/resource-dialog.component';

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
  characteristicTypes = ['CONSUMPTION_TYPE', 'CHARGING_POINT', 'CONNECTION_POINT_STATUS'] as const;


  constructor(
    private googleMapsLoader: GoogleMapsLoaderService,
    private resourceService: ResourceService,
    private http: HttpClient,
    private dialog: MatDialog
  ) {}

  openResourceDialog(resource: Resource): void {
    const dialogRef = this.dialog.open(ResourceDialogComponent, {
      width: '400px',
      data: { resource }
    });

    dialogRef.afterClosed().subscribe((result: Resource | undefined) => {
      if (result) {
        this.save(result);
      }
    });
  }

  save(resource: Resource): void {
    this.http.put(`/api/resources/${resource.id}`, resource)
      .subscribe(() => alert('Changes saved successfully.'));
  }

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
            title: `${resource.type} in ${resource.location.city} - ${resource.characteristics.map(c => c.value).join(', ')}`,
            resource: resource
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
  addCharacteristic(resource: Resource): void {
    resource.characteristics.push({
      code: '',
      type: 'CONSUMPTION_TYPE',
      value: ''
    });
  }

  removeCharacteristic(resource: Resource, index: number): void {
    resource.characteristics.splice(index, 1);
  }
}
