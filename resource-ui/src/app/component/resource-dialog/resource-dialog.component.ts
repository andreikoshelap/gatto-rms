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
import {NgForOf, NgIf} from '@angular/common';
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
    MatDialogActions,
    NgIf
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ResourceDialogComponent {
  isNew: boolean;
  constructor(
    public dialogRef: MatDialogRef<ResourceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { resource: Resource, isNew?: boolean },
    private resourceService: ResourceService
  ) {
    this.isNew = !data.resource.id;
  }

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
    if (this.isNew) {
      console.log('Creating resource with id:', this.data.resource.id);
      this.resourceService.createResource(this.data.resource).subscribe({
        next: (createdResource) => {
          console.log('Resource created:', createdResource);
          this.dialogRef.close(createdResource || this.data.resource);
        },
        error: (err) => {
          console.error('Failed to create resource:', err);
          this.dialogRef.close();
        }
      });
    } else {
      console.log('update resource with id:', this.data.resource.id);
      this.resourceService.updateResource(this.data.resource).subscribe({
        next: (updatedResource) => {
          this.dialogRef.close(updatedResource);
        },
        error: (err) => {
          console.error('Failed to update resource:', err);
          this.dialogRef.close();
        }
      });
    }
  }

  deleteResource(): void {
    const id = this.data.resource.id;
    if (typeof id === 'number') {
      this.resourceService.delete(id).subscribe({
        next: () => {
          this.dialogRef.close({ deleted: true, id });
        },
        error: (err) => {
          console.error('Failed to delete resource:', err);
          this.dialogRef.close({ deleted: true, id });
        }
      });
    } else {
      console.error('Resource id is undefined, cannot delete');
      this.dialogRef.close();
    }
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
