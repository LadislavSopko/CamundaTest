package com.example.workflow.dataModel;

public class Field {
    
    private String id;
    private String label;
    private FieldType type;
    private String customeType;
    private String defaultValue;
    private Property[] properties;
    private Value[] values;

    public Field() {}

    public Field(String id, String label, FieldType type, String customType, String defaultValue, Property[] properties, Value[] values) {
        this.id = id;
        this.label = label;
        this.type = type;

        if(this.type == FieldType.CUSTOMETYPE){
            this.customeType = customType;
        }else{
            this.customeType = null;
        }

        this.defaultValue = defaultValue;
        this.properties = properties;

        if(this.type == FieldType.ENUM){
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

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getCustomeType() {
        return customeType;
    }

    public void setCustomeType(String customeType) {
        this.customeType = customeType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Property[] getProperties() {
        return properties;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }

    public Value[] getValues() {
        return values;
    }

    public void setValues(Value[] values) {
        this.values = values;
    }

}
