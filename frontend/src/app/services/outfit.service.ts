import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Outfit {
  id?: number;
  name?: string;
  description?: string;
  imageUrl?: string;
  occasion?: string;
  isFavorite?: boolean;
  clothIds?: number[];
  clothes?: any[];
}

@Injectable({
  providedIn: 'root'
})
export class OutfitService {
  private apiUrl = 'http://localhost:8080/api/outfits';

  constructor(private http: HttpClient) {}

  getOutfits(): Observable<Outfit[]> {
    return this.http.get<Outfit[]>(this.apiUrl);
  }

  getFavoriteOutfits(): Observable<Outfit[]> {
    return this.http.get<Outfit[]>(`${this.apiUrl}/favorites`);
  }

  generateRandomOutfit(): Observable<Outfit> {
    return this.http.post<Outfit>(`${this.apiUrl}/random`, {});
  }

  saveOutfit(outfit: Outfit): Observable<Outfit> {
    return this.http.post<Outfit>(this.apiUrl, outfit);
  }

  updateOutfit(id: number, outfit: Outfit): Observable<Outfit> {
    return this.http.put<Outfit>(`${this.apiUrl}/${id}`, outfit);
  }

  deleteOutfit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
