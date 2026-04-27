import { Component, OnInit } from '@angular/core';
import { OutfitService, Outfit } from '../../services/outfit.service';
import { ClothService, Cloth } from '../../services/cloth.service';

@Component({
  selector: 'app-outfit',
  templateUrl: './outfit.component.html',
  styleUrls: ['./outfit.component.scss']
})
export class OutfitComponent implements OnInit {
  randomOutfit: Outfit | null = null;
  savedOutfits: Outfit[] = [];
  favoriteOutfits: Outfit[] = [];
  isGenerating: boolean = false;
  activeTab: string = 'random';
  showForm: boolean = false;

  constructor(
    private outfitService: OutfitService,
    private clothService: ClothService
  ) {}

  ngOnInit(): void {
    this.loadSavedOutfits();
    this.loadFavoriteOutfits();
  }

  loadSavedOutfits(): void {
    this.outfitService.getOutfits().subscribe({
      next: (outfits) => {
        this.savedOutfits = outfits;
      }
    });
  }

  loadFavoriteOutfits(): void {
    this.outfitService.getFavoriteOutfits().subscribe({
      next: (outfits) => {
        this.favoriteOutfits = outfits;
      }
    });
  }

  generateRandomOutfit(): void {
    this.isGenerating = true;
    this.randomOutfit = null;
    
    this.outfitService.generateRandomOutfit().subscribe({
      next: (outfit) => {
        this.randomOutfit = outfit;
        this.isGenerating = false;
      },
      error: () => {
        this.isGenerating = false;
        alert('生成穿搭失败，请确保您已经添加了足够的衣物');
      }
    });
  }

  saveCurrentOutfit(): void {
    if (!this.randomOutfit) return;
    
    const outfitData: Outfit = {
      name: this.randomOutfit.name || `穿搭方案 ${new Date().toLocaleDateString()}`,
      description: this.randomOutfit.description,
      isFavorite: false,
      clothIds: this.randomOutfit.clothIds
    };
    
    this.outfitService.saveOutfit(outfitData).subscribe({
      next: () => {
        alert('穿搭已保存！');
        this.loadSavedOutfits();
      }
    });
  }

  toggleFavorite(outfit: Outfit): void {
    if (!outfit.id) return;
    
    outfit.isFavorite = !outfit.isFavorite;
    this.outfitService.updateOutfit(outfit.id, outfit).subscribe({
      next: () => {
        this.loadSavedOutfits();
        this.loadFavoriteOutfits();
      }
    });
  }

  deleteOutfit(id: number | undefined): void {
    if (!id) return;
    if (confirm('确定要删除这个穿搭方案吗？')) {
      this.outfitService.deleteOutfit(id).subscribe({
        next: () => {
          this.loadSavedOutfits();
          this.loadFavoriteOutfits();
        }
      });
    }
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  getImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fashion%20clothing%20placeholder%20elegant&image_size=square';
    if (imageUrl.startsWith('http')) return imageUrl;
    return `http://localhost:8080${imageUrl}`;
  }
}
