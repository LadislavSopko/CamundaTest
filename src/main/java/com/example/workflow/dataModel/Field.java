package com.example.workflow.dataModel;

import java.util.ArrayList;

public class Field {
    
    private String id;
    private String label;
    private String type;
    private String defaultValue;
    private ArrayList<Property> properties;
    private Value[] values;

    public Field() {}

    public Field(String id, String label, String type, String customType, String defaultValue, ArrayList<Property> properties, Value[] values) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.defaultValue = defaultValue;
        this.properties = properties;

        if(this.type == "enum"){
            this.values = values;
        }else{
            this.values = null;
        }
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

    public Value[] getValues() {
        return values;
    }

    public void setValues(Value[] values) {
        this.values = values;
    }

}
