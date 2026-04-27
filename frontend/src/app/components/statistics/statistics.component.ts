import { Component, OnInit } from '@angular/core';
import { StatisticsService } from '../../services/statistics.service';
import { ChartConfiguration, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.scss']
})
export class StatisticsComponent implements OnInit {
  currentYear: number;
  
  categoryStats: any[] = [];
  colorStats: any[] = [];
  monthlyStats: any[] = [];
  
  categoryChartData: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: [
        '#6366f1', '#8b5cf6', '#ec4899', '#f59e0b', 
        '#10b981', '#3b82f6', '#ef4444', '#84cc16'
      ]
    }]
  };
  
  categoryChartOptions: ChartOptions<'pie'> = {
    responsive: true,
    plugins: {
      legend: {
        position: 'right'
      }
    }
  };
  
  monthlyChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    datasets: [
      {
        label: '购买数量',
        data: [],
        backgroundColor: 'rgba(99, 102, 241, 0.7)',
        borderColor: '#6366f1',
        borderWidth: 1
      },
      {
        label: '穿搭次数',
        data: [],
        backgroundColor: 'rgba(139, 92, 246, 0.7)',
        borderColor: '#8b5cf6',
        borderWidth: 1
      }
    ]
  };
  
  monthlyChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    scales: {
      y: {
        beginAtZero: true
      }
    }
  };
  
  colorChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: []
    }]
  };
  
  colorChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    plugins: {
      legend: {
        position: 'right'
      }
    }
  };

  constructor(private statisticsService: StatisticsService) {
    this.currentYear = new Date().getFullYear();
  }

  ngOnInit(): void {
    this.loadStatistics();
  }

  loadStatistics(): void {
    this.statisticsService.getCategoryStats().subscribe({
      next: (data) => {
        this.categoryStats = data.categoryStats || [];
        this.updateCategoryChart();
      }
    });
    
    this.statisticsService.getColorStats().subscribe({
      next: (data) => {
        this.colorStats = data.colorStats || [];
        this.updateColorChart();
      }
    });
    
    this.statisticsService.getMonthlyStats(this.currentYear).subscribe({
      next: (data) => {
        this.monthlyStats = data.monthlyStats || [];
        this.updateMonthlyChart();
      }
    });
  }

  updateCategoryChart(): void {
    this.categoryChartData.labels = this.categoryStats.map(s => s.categoryName);
    this.categoryChartData.datasets[0].data = this.categoryStats.map(s => s.count);
  }

  updateMonthlyChart(): void {
    const purchases = new Array(12).fill(0);
    const wears = new Array(12).fill(0);
    
    for (const stat of this.monthlyStats) {
      const monthIndex = stat.month - 1;
      purchases[monthIndex] = stat.purchases;
      wears[monthIndex] = stat.wearCount;
    }
    
    this.monthlyChartData.datasets[0].data = purchases;
    this.monthlyChartData.datasets[1].data = wears;
  }

  updateColorChart(): void {
    const colorMap: { [key: string]: string } = {
      '红色': '#ef4444',
      '蓝色': '#3b82f6',
      '绿色': '#10b981',
      '黄色': '#eab308',
      '黑色': '#1f2937',
      '白色': '#f9fafb',
      '灰色': '#9ca3af',
      '粉色': '#ec4899',
      '紫色': '#8b5cf6',
      '橙色': '#f97316',
      '棕色': '#92400e'
    };
    
    this.colorChartData.labels = this.colorStats.map(s => s.color);
    this.colorChartData.datasets[0].data = this.colorStats.map(s => s.count);
    this.colorChartData.datasets[0].backgroundColor = this.colorStats.map(s => 
      colorMap[s.color] || `hsl(${Math.random() * 360}, 70%, 60%)`
    );
  }

  prevYear(): void {
    this.currentYear--;
    this.loadStatistics();
  }

  nextYear(): void {
    this.currentYear++;
    this.loadStatistics();
  }

  getPercentage(count: number, total: number): string {
    if (total === 0) return '0%';
    return `${Math.min((count / total) * 100, 100)}%`;
  }

  getTotalClothes(): number {
    return this.categoryStats.reduce((sum, s) => sum + (s.count || 0), 0);
  }
}
