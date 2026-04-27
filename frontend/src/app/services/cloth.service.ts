import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cloth {
  id?: number;
  name: string;
  description?: string;
  imageUrl?: string;
  categoryId?: number;
  categoryName?: string;
  brand?: string;
  color?: string;
  material?: string;
  purchaseDate?: string;
  price?: number;
  wearCount?: number;
  status?: string;
  seasonIds?: number[];
}

export interface Category {
  id: number;
  name: string;
  icon?: string;
  parentId?: number;
}

export interface Season {
  id: number;
  name: string;
  description?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClothService {
  private apiUrl = 'http://localhost:8080/api/clothes';
  private categoryUrl = 'http://localhost:8080/api/categories';
  private seasonUrl = 'http://localhost:8080/api/seasons';

  constructor(private http: HttpClient) {}

  getClothes(categoryId?: number, seasonId?: number): Observable<Cloth[]> {
    let params = new HttpParams();
    if (categoryId) params = params.set('categoryId', categoryId);
    if (seasonId) params = params.set('seasonId', seasonId);
    return this.http.get<Cloth[]>(this.apiUrl, { params });
  }

  getClothById(id: number): Observable<Cloth> {
    return this.http.get<Cloth>(`${this.apiUrl}/${id}`);
  }

  createCloth(cloth: Cloth): Observable<Cloth> {
    return this.http.post<Cloth>(this.apiUrl, cloth);
  }

  updateCloth(id: number, cloth: Cloth): Observable<Cloth> {
    return this.http.put<Cloth>(`${this.apiUrl}/${id}`, cloth);
  }

  deleteCloth(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<string>(`${this.apiUrl}/upload`, formData);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryUrl);
  }

  getTopCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.categoryUrl}/top`);
  }

  getSubCategories(parentId: number): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.categoryUrl}/${parentId}/children`);
  }

  getSeasons(): Observable<Season[]> {
    return this.http.get<Season[]>(this.seasonUrl);
  }
}
