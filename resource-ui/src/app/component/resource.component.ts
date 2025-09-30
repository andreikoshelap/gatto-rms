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
import { MatSnackBar } from '@angular/material/snack-bar';


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
  private readonly DEFAULT_COUNTRY = 'EE';


  constructor(
    private googleMapsLoader: GoogleMapsLoaderService,
    private resourceService: ResourceService,
    private http: HttpClient,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  openResourceDialog(resource: Resource, isNew: boolean = false): void {
    const dialogRef = this.dialog.open(ResourceDialogComponent, {
      width: '400px',
      data: {
        resource: resource ?? this.getEmptyResource(),
        isNew: !resource
      }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (result.deleted) {
          this.deleteResource(result.id);
          this.refreshResources();
        } else if (isNew) {
          this.create(result);
          this.refreshResources();
        } else {
          this.save(result);
        }
      }
    });
  }

  onMapDblClick(event: google.maps.MapMouseEvent): void {
    const latLng = event.latLng;
    if (!latLng) return;

    const geocoder = new google.maps.Geocoder();

    geocoder.geocode({ location: latLng })
      .then(({ results }) => {
        const countryComp = this.findAddressComponent(results, 'country');
        const cityComp = this.findAddressComponent(results, 'locality')
          ?? this.findAddressComponent(results, 'postal_town'); // fallback for some regions
        const postalComp = this.findAddressComponent(results, 'postal_code');
        const routeComp = this.findAddressComponent(results, 'route');
        const numberComp = this.findAddressComponent(results, 'street_number');

        const countryCode = countryComp?.short_name ?? this.DEFAULT_COUNTRY;
        const city = cityComp?.long_name ?? '';
        const postalCode = postalComp?.long_name ?? '';
        const streetAddress = [routeComp?.long_name, numberComp?.long_name].filter(Boolean).join(' ');

        const newResource: Resource = {
          id: undefined,
          type: '',
          countryCode, // top-level if you keep it
          location: {
            latitude: latLng.lat(),
            longitude: latLng.lng(),
            city,
            streetAddress,
            postalCode,
            countryCode
          },
          characteristics: []
        };

        this.openResourceDialog(newResource, true);
      })
      .catch(() => {
        // Fallback to default country if geocoder fails
        const newResource: Resource = {
          id: undefined,
          type: '',
          countryCode: this.DEFAULT_COUNTRY,
          location: {
            latitude: latLng.lat(),
            longitude: latLng.lng(),
            city: '',
            streetAddress: '',
            postalCode: '',
            countryCode: this.DEFAULT_COUNTRY
          },
          characteristics: []
        };
        this.openResourceDialog(newResource, true);
      });
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
        this.snackBar.open('Resource successfully created.', 'Close', { duration: 3000 });
      });
  }

  save(resource: Resource): void {
    this.http.put(`/api/resources/${resource.id}`, resource)
      .subscribe(() => alert('Changes saved successfully.'));
  }

  deleteResource(resourceId: number): void {
    this.resourceService.delete(resourceId).subscribe(() => {
      this.markers = this.markers.filter(m => m.resource.id !== resourceId);
    });
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

  getEmptyResource(): Resource {
    return {
      id: undefined,
      type: '',
      countryCode: '',
      location: {
        latitude: 0,
        longitude: 0,
        city: '',
        streetAddress: '',
        postalCode: '',
        countryCode: ''
      },
      characteristics: []
    };
  }

  refreshResources(): void {
    this.resourceService.getAll().subscribe(resources => {
      this.markers = resources.map(resource => ({
        position: {
          lat: resource.location.latitude,
          lng: resource.location.longitude
        },
        label: resource.type.charAt(0),
        title: `${resource.type} in ${resource.location.city} - ${resource.characteristics.map(c => c.value).join(', ')}`,
        resource
      }));
    });
  }

  /** Extracts the first address component with given type from geocoder results. */
  private findAddressComponent(
    results: google.maps.GeocoderResult[],
    type: string
  ): google.maps.GeocoderAddressComponent | undefined {
    for (const r of results) {
      const found = r.address_components.find(c => c.types.includes(type));
      if (found) return found;
    }
    return undefined;
  }
}
