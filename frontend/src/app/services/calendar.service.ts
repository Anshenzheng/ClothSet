import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CalendarEntry {
  id?: number;
  entryDate: string;
  outfitId?: number;
  note?: string;
  weather?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CalendarService {
  private apiUrl = 'http://localhost:8080/api/calendar';

  constructor(private http: HttpClient) {}

  getEntryByDate(date: string): Observable<CalendarEntry | null> {
    let params = new HttpParams().set('date', date);
    return this.http.get<CalendarEntry>(`${this.apiUrl}/date`, { params });
  }

  getEntriesByMonth(year: number, month: number): Observable<CalendarEntry[]> {
    let params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());
    return this.http.get<CalendarEntry[]>(`${this.apiUrl}/month`, { params });
  }

  saveEntry(entry: CalendarEntry): Observable<CalendarEntry> {
    return this.http.post<CalendarEntry>(this.apiUrl, entry);
  }

  deleteEntry(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
