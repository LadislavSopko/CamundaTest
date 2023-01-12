package com.example.workflow;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Documentation;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.workflow.dataModel.Field;
import com.example.workflow.dataModel.Form;
import com.example.workflow.dataModel.JsonData;
import com.example.workflow.dataModel.Property;

import com.google.gson.Gson;

@RestController
public class camundaController {

    // camundaTest-process:2:c793f2d8-91c5-11ed-aa10-106fd9dce29f - procesId
    // camundaTest-process:4:8deb489f-9271-11ed-9fbe-106fd9dce29f
    // 79fbf611-91bf-11ed-aa10-106fd9dce29f - taskId
    // Activity_0w3j2wz - mozno toto je taskId

    @GetMapping("/form/{procesId}/{taskId}")
    String getFormInJson(@PathVariable String procesId, @PathVariable String taskId){
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("http://localhost:8080/engine-rest/process-definition/%s/xml", procesId);
        JsonData data = restTemplate.getForObject(url, JsonData.class);

        if(data != null){
            BpmnModelInstance modelInst = Bpmn.readModelFromStream(new ByteArrayInputStream(data.getBpmn20Xml().getBytes()));

            var lst = modelInst.getModelElementsByType(UserTask.class);
            var task = lst.stream().filter(userTask -> userTask.getId().equals(taskId)).findFirst();
            if(task.isPresent()){
                var doc = task.get().getChildElementsByType(Documentation.class).stream().findFirst();
                String docValue = null;
                if(doc.isPresent()){
                    docValue = doc.get().getTextContent();
                }
                var extEls = task.get().getChildElementsByType(ExtensionElements.class);
                var fds = extEls.stream().findFirst().get().getChildElementsByType(CamundaFormData.class);
                var fields = fds.stream().findFirst().get().getCamundaFormFields();

                // vytvorenie datoveho modelu
                ArrayList<Field> fieldsList = new ArrayList<>();

                fields.forEach((f) -> {
                    // create property
                    ArrayList<Property> properties = new ArrayList<>();
                    var props = f.getCamundaProperties().getCamundaProperties();
                    props.forEach((p) -> {
                        properties.add(new Property(p.getCamundaId(), p.getCamundaValue()));
                    });
                    
                    // create new field
                    fieldsList.add(new Field(f.getCamundaId(), f.getCamundaLabel(), f.getCamundaType(), "", f.getCamundaDefaultValue(), properties, null));
                });

                return new Gson().toJson(new Form(fieldsList, docValue));
            }

            lst.forEach(e -> System.out.println(e.getName()+ e.getId()+e.getElementType().getTypeName()));
        }
        return procesId + " | " + taskId;
    }
    
}
