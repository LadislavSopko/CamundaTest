package com.example.workflow;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Documentation;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.workflow.dataModel.Field;
import com.example.workflow.dataModel.Form;
import com.example.workflow.dataModel.JsonData;
import com.example.workflow.dataModel.Property;
import com.example.workflow.dataModel.UserTaskCustome;
import com.example.workflow.dataModel.Value;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CamundaController {

    private final MyProperty myProperties;

    public CamundaController(MyProperty myProperties) {
        this.myProperties = myProperties;
    }

    @GetMapping("/form/{procesId}/{taskId}")
    String getFormInJson(@PathVariable String procesId, @PathVariable String taskId){
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/process-definition/%s/xml", myProperties.getUrl(), procesId);
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
                    var preProps = f.getCamundaProperties();
                    if(preProps != null){
                        var props = preProps.getCamundaProperties();

                        props.forEach((p) -> {
                            properties.add(new Property(p.getCamundaId(), p.getCamundaValue()));
                        });
                    }

                    // create values
                    ArrayList<Value> values = new ArrayList<>();
                    var preValues = f.getCamundaValues();
                    if(preValues.size() != 0){
                        preValues.forEach((v) -> {
                            values.add(new Value(v.getCamundaId(), v.getCamundaName()));
                        });
                    }
            
                    // create new field
                    fieldsList.add(new Field(f.getCamundaId(), f.getCamundaLabel(), f.getCamundaType(), f.getCamundaDefaultValue(), properties, values));
                });

                return new Gson().toJson(new Form(fieldsList, docValue));
            }

            lst.forEach(e -> System.out.println(e.getName()+ e.getId()+e.getElementType().getTypeName()));
        }
        return procesId + " | " + taskId;
    }

    @GetMapping("/task-list/{procesId}")
    String getTaskList(@PathVariable String procesId){
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/process-definition/%s/xml", myProperties.getUrl(), procesId);
        JsonData data = restTemplate.getForObject(url, JsonData.class);

        if(data != null){
            BpmnModelInstance modelInst = Bpmn.readModelFromStream(new ByteArrayInputStream(data.getBpmn20Xml().getBytes()));

            var lst = modelInst.getModelElementsByType(UserTask.class);
            if(lst.size() > 0){
                var tasks = new ArrayList<UserTaskCustome>();
                lst.forEach(task -> {
                    tasks.add(new UserTaskCustome(task.getId(), task.getName()));
                });

                return "{\"tasks\":" + new Gson().toJson(tasks) + "}";
            }
        }
        return procesId;
    }

    @GetMapping("/process-list")
    String getProcessList(){
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/process-definition", myProperties.getUrl());
        var response = restTemplate.getForEntity(url , Object[].class);
        var data = response.getBody();

        if(data != null){
            return "{\"process\":" + new Gson().toJson(data) + "}";
        }
        return "[]";
    }

    @GetMapping("/executed-task-list")
    String getExecutedTaskList(){
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/task", myProperties.getUrl());
        var response = restTemplate.getForEntity(url , Object[].class);
        var data = response.getBody();

        if(data != null){
            return "{\"tasks\":" + new Gson().toJson(data) + "}";
        }
        return "[]";
    }

    @PostMapping(path="/submit-form/{taskId}")
    public void submitForm(@PathVariable String taskId, @RequestBody String inputJson) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(inputJson, headers);
        String url = String.format("%s/task/%s/submit-form", myProperties.getUrl(), taskId);
        var response = restTemplate.postForObject(url , request, String.class);
    }
    
}
