import { Component, OnInit } from '@angular/core';
import { CalendarService, CalendarEntry } from '../../services/calendar.service';
import { OutfitService, Outfit } from '../../services/outfit.service';

interface CalendarDay {
  date: number;
  currentMonth: boolean;
  hasEntry: boolean;
  entry?: CalendarEntry;
  isToday: boolean;
  isPast: boolean;
}

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {
  currentYear: number;
  currentMonth: number;
  currentMonthName: string = '';
  calendarDays: CalendarDay[] = [];
  weekDays = ['日', '一', '二', '三', '四', '五', '六'];
  entries: CalendarEntry[] = [];
  outfits: Outfit[] = [];
  selectedDay: CalendarDay | null = null;
  showEntryForm: boolean = false;
  
  entryForm = {
    outfitId: null as number | null,
    note: '',
    weather: ''
  };

  constructor(
    private calendarService: CalendarService,
    private outfitService: OutfitService
  ) {
    const now = new Date();
    this.currentYear = now.getFullYear();
    this.currentMonth = now.getMonth() + 1;
  }

  ngOnInit(): void {
    this.loadOutfits();
    this.generateCalendar();
  }

  loadOutfits(): void {
    this.outfitService.getOutfits().subscribe({
      next: (outfits) => {
        this.outfits = outfits;
      }
    });
  }

  generateCalendar(): void {
    const monthNames = ['一月', '二月', '三月', '四月', '五月', '六月', 
                       '七月', '八月', '九月', '十月', '十一月', '十二月'];
    this.currentMonthName = monthNames[this.currentMonth - 1];
    
    this.calendarService.getEntriesByMonth(this.currentYear, this.currentMonth).subscribe({
      next: (entries) => {
        this.entries = entries;
        this.buildCalendar();
      },
      error: () => {
        this.entries = [];
        this.buildCalendar();
      }
    });
  }

  buildCalendar(): void {
    this.calendarDays = [];
    
    const firstDay = new Date(this.currentYear, this.currentMonth - 1, 1);
    const lastDay = new Date(this.currentYear, this.currentMonth, 0);
    const today = new Date();
    
    const startDayOfWeek = firstDay.getDay();
    const prevMonthLastDay = new Date(this.currentYear, this.currentMonth - 1, 0).getDate();
    
    for (let i = startDayOfWeek - 1; i >= 0; i--) {
      this.calendarDays.push({
        date: prevMonthLastDay - i,
        currentMonth: false,
        hasEntry: false,
        isToday: false,
        isPast: true
      });
    }
    
    for (let i = 1; i <= lastDay.getDate(); i++) {
      const date = new Date(this.currentYear, this.currentMonth - 1, i);
      const dateStr = this.formatDate(date);
      const entry = this.entries.find(e => e.entryDate === dateStr);
      
      const isToday = today.getFullYear() === this.currentYear && 
                      today.getMonth() === this.currentMonth - 1 && 
                      today.getDate() === i;
      
      const isPast = date < new Date(today.getFullYear(), today.getMonth(), today.getDate());
      
      this.calendarDays.push({
        date: i,
        currentMonth: true,
        hasEntry: !!entry,
        entry: entry,
        isToday: isToday,
        isPast: isPast
      });
    }
    
    const remainingDays = 42 - this.calendarDays.length;
    for (let i = 1; i <= remainingDays; i++) {
      this.calendarDays.push({
        date: i,
        currentMonth: false,
        hasEntry: false,
        isToday: false,
        isPast: false
      });
    }
  }

  selectDay(day: CalendarDay): void {
    if (!day.currentMonth) return;
    
    this.selectedDay = day;
    this.showEntryForm = true;
    
    if (day.entry) {
      this.entryForm = {
        outfitId: day.entry.outfitId || null,
        note: day.entry.note || '',
        weather: day.entry.weather || ''
      };
    } else {
      this.entryForm = {
        outfitId: null,
        note: '',
        weather: ''
      };
    }
  }

  saveEntry(): void {
    if (!this.selectedDay) return;
    
    const dateStr = `${this.currentYear}-${String(this.currentMonth).padStart(2, '0')}-${String(this.selectedDay.date).padStart(2, '0')}`;
    
    const entry: CalendarEntry = {
      entryDate: dateStr,
      outfitId: this.entryForm.outfitId || undefined,
      note: this.entryForm.note || undefined,
      weather: this.entryForm.weather || undefined
    };
    
    if (this.selectedDay.entry?.id) {
      entry.id = this.selectedDay.entry.id;
    }
    
    this.calendarService.saveEntry(entry).subscribe({
      next: () => {
        this.generateCalendar();
        this.closeForm();
      }
    });
  }

  deleteEntry(): void {
    if (!this.selectedDay?.entry?.id) return;
    
    if (confirm('确定要删除这条记录吗？')) {
      this.calendarService.deleteEntry(this.selectedDay.entry.id).subscribe({
        next: () => {
          this.generateCalendar();
          this.closeForm();
        }
      });
    }
  }

  closeForm(): void {
    this.showEntryForm = false;
    this.selectedDay = null;
  }

  prevMonth(): void {
    if (this.currentMonth === 1) {
      this.currentMonth = 12;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }
    this.generateCalendar();
  }

  nextMonth(): void {
    if (this.currentMonth === 12) {
      this.currentMonth = 1;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }
    this.generateCalendar();
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
