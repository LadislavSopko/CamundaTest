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
  public result: any = null;
  public executedTaskId: any = "";

  public processSelected: boolean = false;
  public sucessSend: boolean = false;

  public processList: any;
  public taskList: any;
  public executedTaskList: any;

  constructor(
    private httpClient: HttpClient
    ) {
    this.loadProcess();
  }

  ngOnInit(): void {
    
  }

  submit(data: any){
    this.result = JSON.stringify(data);
    this.loadExecutedTasks(this.processId);
  }

  sendToServer(){
    this.sendDataToCamunde();
  }

  selectProcess(){
    this.processSelected = true;
    this.loadTasks(this.processId);
  }

  private loadJsonForFormular(procesId: string, taskId: string){
    this.httpClient.get(`http://localhost:8080/form/${procesId}/${taskId}`).subscribe({
      next: data => {
        this.userTaskJson = data;
      },
      error: error => console.log(error)
    });
  }

  private loadProcess(){
    this.httpClient.get(`http://localhost:8080/process-list`).subscribe({
      next: (data: any) => {
        this.processList = data.process;
      },
      error: error => console.log(error)
    });
  }

  private loadTasks(processId: string){
    this.httpClient.get(`http://localhost:8080/task-list/${processId}`).subscribe({
      next: (data:any) => {
        this.taskList = data.tasks;
      },
      error: error => console.log(error)
    });
  }

  private loadExecutedTasks(processId: string){
    this.httpClient.get(`http://localhost:8080/executed-task-list`).subscribe({
      next: (data:any) => {
        this.executedTaskList = data.tasks;
        this.executedTaskList = this.executedTaskList.filter((task:any) => task.processDefinitionId === processId);
      },
      error: error => console.log(error)
    });
  }

  private sendDataToCamunde(){
    this.httpClient.post(`http://localhost:8080/submit-form/${this.executedTaskId}`, this.result).subscribe({
      next: (data:any) => {
        console.log("sucess");
        this.sucessSend = true;
      },
      error: error => console.log(error)
    });
  }

  generate(){
    this.loadJsonForFormular(this.processId, this.taskId);
  }

}
