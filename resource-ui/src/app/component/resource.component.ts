import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {GoogleMap, MapMarker} from '@angular/google-maps';
import {NgForOf} from '@angular/common';
import { GoogleMapsLoaderService } from '../service/google-maps-loader.service';

@Component({
  selector: 'app-resource',
  templateUrl: './resource.component.html',
  imports: [
    GoogleMap,
    MapMarker,
    NgForOf
  ],
  styleUrls: ['./resource.component.css']
})
export class ResourceComponent implements OnInit {
  apiLoaded: Observable<boolean>;
  center = { lat: 56.95, lng: 24.11 };
  markers = [
    { position: { lat: 56.95, lng: 24.11 }, label: 'R', title: 'Resource in Riga' },
    { position: { lat: 59.44, lng: 24.75 }, label: 'T', title: 'Resource in Tallinn' }
  ];
  zoom = 7;

  constructor(private httpClient: HttpClient) {
    this.apiLoaded = this.httpClient.jsonp(
      `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}`,
      'callback'
    ).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  ngOnInit(): void {}
}
