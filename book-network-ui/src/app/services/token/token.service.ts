import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

 setToken (token : string) {
   localStorage.setItem('token', token);
 }

 getToken (token: string) {
   return localStorage.getItem('token') as string;
 }
}
