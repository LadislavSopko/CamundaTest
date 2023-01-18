import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{

  public userTaskJson: any;
  public processId: any = "";
  public taskId: any = "";
  public result: any = "";

  constructor(
    private httpClient: HttpClient
    ) {

  }

  ngOnInit(): void {
    
  }

  submit(data: any){
    console.log(data);
    this.result = JSON.stringify(data);
  }

  private loadJsonForFormular(procesId: string, taskId: string){
    this.httpClient.get(`http://localhost:8080/form/${procesId}/${taskId}`).subscribe({
      next: data => {
        console.log(data);
        this.userTaskJson = data;
      },
      error: error => console.log(error)
    });
  }

  generate(){
    this.loadJsonForFormular(this.processId, this.taskId);
  }

}
