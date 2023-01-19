import { HttpClient } from '@angular/common/http';
import { IConfigSelectOption, IConfigSelectValuesProvider } from '@common/common-ui';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface DataSourceConfiguration {
  method: string;
  url: string;
  params: {};
  dataForm: {key: string, value: string};
}

export class RestSelectDataSource implements IConfigSelectValuesProvider {

  private _data: BehaviorSubject<Array<IConfigSelectOption>> = new BehaviorSubject<Array<IConfigSelectOption>>([]);
  public data$: Observable<Array<IConfigSelectOption>> = this._data.asObservable();

  private translateMap: Map<string, string> = new Map<string, string>();
  private configuration: DataSourceConfiguration;

  constructor(httClient: HttpClient, configuration: DataSourceConfiguration) {
    this.configuration = configuration;

    if(configuration.method === "GET"){
      httClient.get(`${environment.url}${configuration.url}`).subscribe({
        next: (data:any) => {
          this.dataProcessing(data);
        },  
        error: (error) => {
          console.error(error);
          this._data.next([]);
        }
      });
    }else if(configuration.method === "POST"){
      console.log(configuration.params);

      httClient.post<any>(`${environment.url}${configuration.url}`, configuration.params).subscribe({
        next: data => {
          this.dataProcessing(data);
        },
        error: (error) => {
          console.error(error);
          this._data.next([]);
        }
      });
    }
  }
  
  transform(value: any, ...args: any[]): string {
    return this.translateMap.get(value)?? "";
  }

  getKeyName(): string {
    return 'key';
  }
  getValueName(): string {
    return 'value';
  }

  getSelectOptions(): Array<IConfigSelectOption> {
    return this._data.value;
  }

  private dataProcessing(data: any){
    if(data.result){
      this._data.next(data.result?.map((e:any, index:number) => {
        if(e.hasOwnProperty(this.configuration.dataForm.key) && e.hasOwnProperty(this.configuration.dataForm.value)){
          let tmp:IConfigSelectOption = {
            key: e[this.configuration.dataForm.key] ?? index,
            value: e[this.configuration.dataForm.value] ?? index
          }
          return tmp;
        }else{
          return [];
        }
      }));

      this._data.value.forEach(e => {
        this.translateMap = this.translateMap.set(e.key, e.value);
      });
    
    }else{
      if(data.hasOwnProperty(this.configuration.dataForm.key) && data.hasOwnProperty(this.configuration.dataForm.value)){
        this._data.next(data.result?.map((e:any, index:number) => {
          let tmp:IConfigSelectOption = {
            key: e[this.configuration.dataForm.key] ?? index,
            value: e[this.configuration.dataForm.value] ?? index
          }
          return tmp;
        }));
      }else{
        this._data.next([]);
      }
    }
  }
}
