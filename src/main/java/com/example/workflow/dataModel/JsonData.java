package com.example.workflow.dataModel;

public class JsonData {
    private String id;
    private String bpmn20Xml;
  
    public JsonData() {}
  
    public JsonData(String id, String bpmn20Xml){
      this.id = id;
      this.bpmn20Xml = bpmn20Xml;
    }
  
    public String getBpmn20Xml() {
        return this.bpmn20Xml;
    }
  
    public void setBpmn20Xml(String value) {
        this.bpmn20Xml = value;
    }
  
    public String getId() {
      return this.id;
    }
  
    public void setId(String id) {
      this.id = id;
    }
}