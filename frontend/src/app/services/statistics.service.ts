import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private apiUrl = 'http://localhost:8080/api/statistics';

  constructor(private http: HttpClient) {}

  getOverview(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/overview`);
  }

  getCategoryStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/categories`);
  }

  getColorStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/colors`);
  }

  getMonthlyStats(year: number): Observable<any> {
    let params = new HttpParams().set('year', year.toString());
    return this.http.get<any>(`${this.apiUrl}/monthly`, { params });
  }
}
