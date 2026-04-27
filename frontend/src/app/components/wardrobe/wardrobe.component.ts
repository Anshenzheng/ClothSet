import { Component, OnInit } from '@angular/core';
import { ClothService, Cloth, Category, Season } from '../../services/cloth.service';
import { OutfitService, Outfit } from '../../services/outfit.service';

@Component({
  selector: 'app-wardrobe',
  templateUrl: './wardrobe.component.html',
  styleUrls: ['./wardrobe.component.scss']
})
export class WardrobeComponent implements OnInit {
  clothes: Cloth[] = [];
  categories: Category[] = [];
  seasons: Season[] = [];
  selectedCategory: number | null = null;
  selectedSeason: number | null = null;
  showForm: boolean = false;
  editingCloth: Cloth | null = null;
  isLoading: boolean = false;

  constructor(
    private clothService: ClothService,
    private outfitService: OutfitService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadSeasons();
    this.loadClothes();
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

  loadClothes(): void {
    this.isLoading = true;
    this.clothService.getClothes(this.selectedCategory || undefined, this.selectedSeason || undefined).subscribe({
      next: (clothes) => {
        this.clothes = clothes;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  filterByCategory(categoryId: number | null): void {
    this.selectedCategory = categoryId;
    this.loadClothes();
  }

  filterBySeason(seasonId: number | null): void {
    this.selectedSeason = seasonId;
    this.loadClothes();
  }

  openAddForm(): void {
    this.editingCloth = null;
    this.showForm = true;
  }

  openEditForm(cloth: Cloth): void {
    this.editingCloth = { ...cloth };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingCloth = null;
  }

  onFormSubmitted(): void {
    this.closeForm();
    this.loadClothes();
  }

  deleteCloth(id: number | undefined): void {
    if (!id) return;
    if (confirm('确定要删除这件衣物吗？')) {
      this.clothService.deleteCloth(id).subscribe({
        next: () => {
          this.loadClothes();
        }
      });
    }
  }

  getImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fashion%20clothing%20placeholder%20elegant&image_size=square';
    if (imageUrl.startsWith('http')) return imageUrl;
    return `http://localhost:8080${imageUrl}`;
  }

  getPercentage(count: number, total: number): string {
    if (total === 0) return '0%';
    return `${Math.min((count / total) * 100, 100)}%`;
  }
}
