package com.example.workflow.dataModel;

public class Constrain {
    private String config;
    private String name;

    public Constrain() {}

    public Constrain(String name, String config) {
        this.config = config;
        this.name = name;
    }

    public String getCpnfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
