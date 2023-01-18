package com.example.workflow.dataModel;

import java.util.ArrayList;

public class Field {
    
    private String id;
    private String label;
    private String type;
    private String defaultValue;
    private ArrayList<Property> properties;
    private ArrayList<Value> values;
    // private ArrayList<Constrain> constrints;

    public Field() {}

    public Field(String id, String label, String type, String defaultValue, ArrayList<Property> properties, ArrayList<Value> values) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.defaultValue = defaultValue;

        if(properties.size() == 0){
            this.properties = null;
        }else{
            this.properties = properties;
        }

        if(values.size() == 0){
            this.values = null;
        }else{
            this.values = values;
        }

        // if(constrints.size() == 0){
        //     this.constrints = null;
        // }else{
        //     this.constrints = constrints;
        // }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }

    // public ArrayList<Constrain> getConstrints() {
    //     return constrints;
    // }

    // public void setConstrints(ArrayList<Constrain> constrints) {
    //     this.constrints = constrints;
    // }

}
