import { CommonModule, registerLocaleData } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AlfUserTaskGeneratorComponent } from './alf-user-task-generator/alf-user-task-generator.component';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { LibraryModule } from '@common/common-ui';
import localeSk from '@angular/common/locales/sk';

// registers SK locale data
registerLocaleData(localeSk);

@NgModule({
  declarations: [
    AppComponent,
    AlfUserTaskGeneratorComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    LibraryModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
