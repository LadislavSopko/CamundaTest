package com.example.workflow;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class SendMail implements JavaDelegate {

    @Override
    public void execute(DelegateExecution arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println(arg0.getVariable("UserName"));
    }

    
}