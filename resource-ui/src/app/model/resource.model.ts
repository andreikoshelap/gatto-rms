export interface Location {
  streetAddress: string;
  city: string;
  postalCode: string;
  countryCode: string;
  latitude: number;
  longitude: number;
}

export interface Resource {
  id?: number;
  type: string;
  countryCode: string;
  location: Location;
  characteristics: Characteristic[];
}

export interface Characteristic {
  code: string;
  type: 'CONSUMPTION_TYPE' | 'CHARGING_POINT' | 'CONNECTION_POINT_STATUS';
  value: string;
}
