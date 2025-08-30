import { Component, OnInit } from '@angular/core';
import {GoogleMap, MapMarker} from '@angular/google-maps';
import {NgForOf, NgIf} from '@angular/common';
import { GoogleMapsLoaderService } from '../service/google-maps-loader.service';

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
  markers = [
    {position: {lat: 56.95, lng: 24.11}, label: 'R', title: 'Resource in Riga'},
    {position: {lat: 59.44, lng: 24.75}, label: 'T', title: 'Resource in Tallinn'}
  ];
  zoom = 7;
  mapReady = false;

  constructor(private googleMapsLoader: GoogleMapsLoaderService) {
  }

  async ngOnInit(): Promise<void> {
    try {
      await this.googleMapsLoader.load();
      this.mapReady = true;
    } catch (error) {
      console.error('Google Maps failed to load', error);
    }
  }
}
