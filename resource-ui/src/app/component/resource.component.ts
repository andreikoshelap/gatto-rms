import {Component, OnInit} from '@angular/core';
import {GoogleMap, MapMarker} from '@angular/google-maps';
import {NgForOf, NgIf} from '@angular/common';
import { GoogleMapsLoaderService } from '../service/google-maps-loader.service';
import { ResourceService } from '../service/resource.service';
import {HttpClient} from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { Resource } from '../model/resource.model';
import { ResourceDialogComponent } from './resource-dialog/resource-dialog.component';

@Component({
  selector: 'app-resource',
  templateUrl: './resource.component.html',
  imports: [
    MatDialogModule,
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

  openResourceDialog(resource: Resource, isNew: boolean = false): void {
    const dialogRef = this.dialog.open(ResourceDialogComponent, {
      width: '400px',
      data: { resource, isNew }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (result.deleted) {
          this.deleteResource(result.id);
        } else if (isNew) {
          this.create(result);
        } else {
          this.save(result);
        }
      }
    });
  }

  onMapDblClick(event: google.maps.MapMouseEvent): void {
    if (event.latLng) {
      const newResource: Resource = {
        id: undefined,
        type: '',
        countryCode: '',
        location: {
          latitude: event.latLng.lat(),
          longitude: event.latLng.lng(),
          city: '',
          streetAddress: '',
          postalCode: '',
          countryCode: ''
        },
        characteristics: []
      };
      this.openResourceDialog(newResource, true);
    }
  }

  create(resource: Resource): void {
    this.http.post<Resource>(`/api/resources`, resource)
      .subscribe((created: Resource) => {
        this.markers.push({
          position: {
            lat: created.location.latitude,
            lng: created.location.longitude
          },
          label: created.type.charAt(0),
          title: `${created.type} in ${created.location.city} - ${created.characteristics.map(c => c.value).join(', ')}`,
          resource: created
        });
        alert('Resource successfully created.');
      });
  }

  save(resource: Resource): void {
    this.http.put(`/api/resources/${resource.id}`, resource)
      .subscribe(() => alert('Changes saved successfully.'));
  }

  deleteResource(resourceId: number): void {
    if (typeof resourceId === 'number') {
      this.resourceService.delete(resourceId).subscribe(() => {
        this.markers = this.markers.filter(m => m.resource.id !== resourceId);
      });
    }
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
