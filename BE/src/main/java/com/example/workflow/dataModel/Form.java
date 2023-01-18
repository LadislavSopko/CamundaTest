package com.example.workflow.dataModel;

import java.util.ArrayList;

public class Form {
    
    private ArrayList<Field> fields;
    private String documentation;

    public Form() {}
    
    public Form(ArrayList<Field> fields, String documentation) {
        this.fields = fields;
        this.documentation = documentation;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

}
