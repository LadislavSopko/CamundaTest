import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ConfigButton, ConfigCalendar, ConfigCheckbox, ConfigCheckboxList, ConfigNumber, ConfigRadio, ConfigSelect, ConfigString, ConfigTextArea } from '@common/common-ui';
import { DataSourceConfiguration, RestSelectDataSource } from 'src/shared/rest-select-data-source';

export class GenerateField{
  fieldConfig: any;
  type: string = 'input';
  order: number = 999;
  action?: string;
  buttons?: any;

  constructor(fieldConfig: any, type: string, order:number, action: string = 'none', buttons: any = []){
    this.fieldConfig = fieldConfig;
    this.type = type;
    this.action = action;
    this.order = order;
    this.buttons = buttons;
  }
}

@Component({
  selector: 'alf-user-task-generator',
  templateUrl: './alf-user-task-generator.component.html',
  styleUrls: ['./alf-user-task-generator.component.scss']
})
export class AlfUserTaskGeneratorComponent implements OnInit {

  public fieldsForGenerate: GenerateField[] = [];

  private _json: any;
  private _form?: FormGroup;

  @Output() submit: EventEmitter<any> = new EventEmitter<any>();
  @Input() set json(value: any){
    if(this.fieldsForGenerate.length > 0){
      this.fieldsForGenerate = [];
      this._form = new FormGroup({});
    }

    if(value){
      this._json = value;
      //this._json = JSON.parse(value);
      this.generateFormular();
      // this.prepareForm();
    }
  }

  constructor(
    private httpClient: HttpClient
  ) { }

  ngOnInit(): void {
  }

  public clickSubmit(){
    this.submit.emit(this._form?.getRawValue());
  }

  public clickSubmitChose(key:any, value: any){
    this._form?.controls[key]?.setValue(value);
    this.submit.emit(this._form?.getRawValue());
  }

  public clickClear(){
    this._form?.reset();
  }

  private generateFormular(){
    this._form = new FormGroup({});

    this._json.fields.forEach((field:any) => {

      let type: string = 'input';
      let action: string = 'none';
      let order: number = 999;
      let configDataSource: DataSourceConfiguration | null = null;
      if(field.properties != null && field.properties.length > 0){
        field.properties.forEach((property:any) => {
          if(property.id == 'UiType'){
            type = property.value;
          }else if(property.id == 'ActionType'){
            action = property.value;
          }else if(property.id == 'Order'){
            order = property.value;
          }else if(property.id == 'DataSource'){
            let response = JSON.parse(property.value);
            if(response.type === "rest"){
              configDataSource = response;
            }
          }
        });
      }

      if(field.type == "enum"){

        let values: any[] = [];
        if(field.values != null && field.values.length > 0){
          values = field.values
        }
        
        if(type == 'select'){
          // Select ------------------------------
          let element = new ConfigSelect({
            name: field.id,
            label: field.label,
            options: values,
            keyName: 'id',
            valueName: 'name'
          });

          if(configDataSource != null){
            element.optionsProvider = new RestSelectDataSource(this.httpClient, configDataSource)
          } 

          this.fieldsForGenerate.push(new GenerateField(element , "select", order));
          this._form?.addControl(field.id, element.formControl);

          if(field.defaultValue){
            this._form?.controls[field.id].setValue(field.defaultValue);
          }
        }else if(type == 'checkList'){
          // Check box list ------------------------------
          let checkboxesList: any[] = [];
          for(let value of values){
            checkboxesList.push(new ConfigCheckbox({
              name: value.id,
              label: value.value,
            }));
          }

          let element = new ConfigCheckboxList({
            name: field.id,
            label: field.label,
            checkboxes: checkboxesList
          });

          this.fieldsForGenerate.push(new GenerateField(element ,"checkBox-list", order));
          this._form?.addControl(field.id, element.formGroup);

          if(field.defaultValue){
            let checked = field.defaultValue.split(',');
            checked.forEach((ch: string) => {
              this._form?.controls[field.id].value.controls[ch].setValue(true);
            })
          }
        }else if(type == 'radioList'){
          // Radio box list ------------------------------
          let radioList: any[] = [];
          for(let value of values){
            radioList.push({
              value: value.id,
              label: value.value,
            });
          }

          let element = new ConfigRadio({
            name: field.id,
            radioButtons: radioList,
            inline: true,
          });

          this.fieldsForGenerate.push(new GenerateField(element ,"radiobox-list", order));
          this._form?.addControl(field.id, element.formControl);

          if(field.defaultValue){
            this._form?.controls[field.id].setValue(field.defaultValue);
          }
        }else if(type == 'button'){
          // Button list ------------------------------ (choose)
          let buttonsList: any[] = [];
          for(let value of values){
            buttonsList.push(new ConfigCheckbox({
              name: value.id,
              label: value.name,
            }));
          }

          let element = {
            name: field.id,
            label: field.label,
            formControl: new FormControl()
          };

          this.fieldsForGenerate.push(new GenerateField(element ,"button-list" , order, action ,buttonsList));
          this._form?.addControl(field.id, element.formControl);
        }

      }else if(field.type == "string"){

        if(type == 'input'){
          // String input ------------------------------
          let element = new ConfigString ({
            name: field.id,
            label: field.label,
          });

          this.fieldsForGenerate.push(new GenerateField(element ,"string-input", order));
          this._form?.addControl(field.id, element.formControl);

          if(field.defaultValue){
            this._form?.controls[field.id].setValue(field.defaultValue);
          }
        }else if(type == 'textArea'){
          // String text ------------------------------
          let element = new ConfigTextArea({
            name: field.id,
            label: field.label,
            rows: 6, // defoult maiby latter
          });

          this.fieldsForGenerate.push(new GenerateField(element ,"String-text", order));
          this._form?.addControl(field.id, element.formControl);

          if(field.defaultValue){
            this._form?.controls[field.id].setValue(field.defaultValue);
          }
        }
      }else if(field.type == "long"){
        // Number ------------------------------
        let element = new ConfigNumber ({
          name: field.id,
          label: field.label
        });

        this.fieldsForGenerate.push(new GenerateField(element ,"number", order));
        this._form?.addControl(field.id, element.formControl);

        if(field.defaultValue){
          this._form?.controls[field.id].setValue(field.defaultValue);
        }
      }else if(field.type == "date"){
        // Date ------------------------------
        let element = new ConfigCalendar ({
          name: field.id,
          label: field.label,
        });

        this.fieldsForGenerate.push(new GenerateField(element,"date", order));
        this._form?.addControl(field.id, element.formControl);

        if(field.defaultValue){
          this._form?.controls[field.id].setValue(field.defaultValue);
        }
      }else if(field.type == "boolean"){
        if(type == 'input'){
          // CheckBox ------------------------------
          let element = new ConfigCheckbox({
            name: field.id,
            label: field.label,
          });

          this.fieldsForGenerate.push(new GenerateField(element,"checkBox", order));
          this._form?.addControl(field.id, element.formControl);

          if(field.defaultValue){
            if(field.defaultValue == 'true'){
              this._form?.controls[field.id].setValue(true)
            }else if(field.defaultValue == 'false'){
              this._form?.controls[field.id].setValue(false);
            }
          }
        }else if(type == 'button'){
          // Button ------------------------------
          this.fieldsForGenerate.push(new GenerateField(
            new ConfigButton({
              name: field.id,
              label: field.label,
              class: action == 'submit' ? 'primary' : 'secondary',
            }),
            "button",
            order,
            action
          ));
        }
      }
    });

    // Sort list;
    this.fieldsForGenerate.sort((a, b) => {
      return b.order - a.order;
    })

    this.fieldsForGenerate.sort((a, b) => {
      if(a.action == "submit" && b.action == "submit"){
        return 0;
      }else if(a.action == "submit"){
        return 1;
      } else {
        return -1;
      }
    });

    // if not define commit add it
    let submitField = this.fieldsForGenerate.find( checkField => {
      return checkField.action === "submit";
    })

    if(submitField == undefined){
      this.fieldsForGenerate.push(new GenerateField(
        new ConfigButton({
          name: 'submit',
          label: 'potvrdiť',
          class: 'primary',
        }),
        'button',
        999,
        'submit'
      ));
    }

  }

  // private prepareForm(){
  //   this._form = new FormGroup({});

  //   this.fieldsForGenerate.forEach((field) => {

  //     if(field.type != "button"){
  //       if(field.type == "checkBox-list"){
  //         this._form?.addControl(field.fieldConfig.name, field.fieldConfig.formGroup);
  //       }else{
  //         this._form?.addControl(field.fieldConfig.name, field.fieldConfig.formControl);
  //       }
  //     }
  //   });
  // }

}
