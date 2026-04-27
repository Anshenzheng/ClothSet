import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClothService, Cloth, Category, Season } from '../../../services/cloth.service';

@Component({
  selector: 'app-cloth-form',
  templateUrl: './cloth-form.component.html',
  styleUrls: ['./cloth-form.component.scss']
})
export class ClothFormComponent implements OnInit {
  @Input() cloth: Cloth | null = null;
  @Output() submitted = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  clothForm: FormGroup;
  categories: Category[] = [];
  seasons: Season[] = [];
  previewImage: string | null = null;
  selectedFile: File | null = null;
  isSubmitting: boolean = false;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private clothService: ClothService
  ) {
    this.clothForm = this.fb.group({
      name: ['', [Validators.required]],
      description: [''],
      categoryId: [''],
      brand: [''],
      color: [''],
      material: [''],
      purchaseDate: [''],
      price: [''],
      seasonIds: [[]]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    this.loadSeasons();
    
    if (this.cloth) {
      this.clothForm.patchValue({
        name: this.cloth.name,
        description: this.cloth.description,
        categoryId: this.cloth.categoryId,
        brand: this.cloth.brand,
        color: this.cloth.color,
        material: this.cloth.material,
        purchaseDate: this.cloth.purchaseDate,
        price: this.cloth.price,
        seasonIds: this.cloth.seasonIds || []
      });
      
      if (this.cloth.imageUrl) {
        this.previewImage = this.getImageUrl(this.cloth.imageUrl);
      }
    }
  }

  loadCategories(): void {
    this.clothService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      }
    });
  }

  loadSeasons(): void {
    this.clothService.getSeasons().subscribe({
      next: (seasons) => {
        this.seasons = seasons;
      }
    });
  }

  triggerFileInput(): void {
    const input = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (input) {
      input.click();
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewImage = e.target?.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  isSeasonSelected(seasonId: number): boolean {
    const seasonIds = this.clothForm.get('seasonIds')?.value || [];
    return seasonIds.includes(seasonId);
  }

  onSeasonChange(seasonId: number, event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    const currentSeasonIds = this.clothForm.get('seasonIds')?.value || [];
    
    if (checkbox.checked) {
      this.clothForm.patchValue({
        seasonIds: [...currentSeasonIds, seasonId]
      });
    } else {
      this.clothForm.patchValue({
        seasonIds: currentSeasonIds.filter((id: number) => id !== seasonId)
      });
    }
  }

  onSubmit(): void {
    if (this.clothForm.invalid) {
      this.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const formData = this.clothForm.value;
    
    if (this.selectedFile) {
      this.clothService.uploadImage(this.selectedFile).subscribe({
        next: (imageUrl) => {
          formData.imageUrl = imageUrl;
          this.saveCloth(formData);
        },
        error: () => {
          this.errorMessage = '图片上传失败';
          this.isSubmitting = false;
        }
      });
    } else {
      if (this.cloth?.imageUrl) {
        formData.imageUrl = this.cloth.imageUrl;
      }
      this.saveCloth(formData);
    }
  }

  saveCloth(clothData: Cloth): void {
    if (this.cloth && this.cloth.id) {
      this.clothService.updateCloth(this.cloth.id, clothData).subscribe({
        next: () => {
          this.submitted.emit();
        },
        error: () => {
          this.errorMessage = '保存失败';
          this.isSubmitting = false;
        }
      });
    } else {
      this.clothService.createCloth(clothData).subscribe({
        next: () => {
          this.submitted.emit();
        },
        error: () => {
          this.errorMessage = '保存失败';
          this.isSubmitting = false;
        }
      });
    }
  }

  onCancel(): void {
    this.cancelled.emit();
  }

  markAllAsTouched(): void {
    Object.keys(this.clothForm.controls).forEach(field => {
      const control = this.clothForm.get(field);
      control?.markAsTouched();
    });
  }

  getImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) return '';
    if (imageUrl.startsWith('http')) return imageUrl;
    return `http://localhost:8080${imageUrl}`;
  }
}
