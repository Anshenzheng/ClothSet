import { Component, OnInit } from '@angular/core';
import { ClothService, Cloth } from '../../services/cloth.service';
import { OutfitService, Outfit } from '../../services/outfit.service';
import { CalendarService } from '../../services/calendar.service';
import { StatisticsService } from '../../services/statistics.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  totalClothes: number = 0;
  totalOutfits: number = 0;
  recentClothes: Cloth[] = [];
  categoryStats: any[] = [];
  currentMonth: number;
  currentYear: number;

  constructor(
    private clothService: ClothService,
    private outfitService: OutfitService,
    private calendarService: CalendarService,
    private statisticsService: StatisticsService
  ) {
    const now = new Date();
    this.currentMonth = now.getMonth() + 1;
    this.currentYear = now.getFullYear();
  }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.clothService.getClothes().subscribe({
      next: (clothes) => {
        this.totalClothes = clothes.length;
        this.recentClothes = clothes.slice(0, 6);
      }
    });

    this.outfitService.getOutfits().subscribe({
      next: (outfits) => {
        this.totalOutfits = outfits.length;
      }
    });

    this.statisticsService.getCategoryStats().subscribe({
      next: (data) => {
        this.categoryStats = data.categoryStats || [];
      }
    });
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
