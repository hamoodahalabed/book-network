import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideHttpClient } from '@angular/common/http';
import { LoginComponent } from './pages/login/login.component';
import {FormsModule} from '@angular/forms';
import { ActivateAccountComponent } from './pages/activate-account/activate-account.component';
import {CodeInputModule} from 'angular-code-input';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ActivateAccountComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CodeInputModule
  ],
  providers: [
    provideHttpClient(),
    provideClientHydration()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
