import { Component, CUSTOM_ELEMENTS_SCHEMA, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import { Resource, Characteristic } from '../../model/resource.model';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResourceService } from '../../service/resource.service';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-resource-dialog',
  templateUrl: './resource-dialog.component.html',
  styleUrls: ['./resource-dialog.component.css'],
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    NgForOf,
    FormsModule,
    MatDialogTitle,
    MatDialogContent,
    MatButton,
    MatDialogActions
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ResourceDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ResourceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { resource: Resource },
    private resourceService: ResourceService
  ) {}

  addCharacteristic(): void {
    this.data.resource.characteristics.push({
      code: '',
      type: 'CONSUMPTION_TYPE',
      value: ''
    });
  }

  removeCharacteristic(index: number): void {
    this.data.resource.characteristics.splice(index, 1);
  }

  save(): void {
    this.resourceService.updateResource(this.data.resource).subscribe({
      next: (updatedResource) => {
        this.dialogRef.close(updatedResource);
      },
      error: (err) => {
        console.error('Failed to update resource:', err);
      }
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
