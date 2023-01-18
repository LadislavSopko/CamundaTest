package com.example.workflow;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Documentation;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.workflow.dataModel.Field;
import com.example.workflow.dataModel.Form;
import com.example.workflow.dataModel.JsonData;
import com.example.workflow.dataModel.Property;
import com.example.workflow.dataModel.Value;
import com.google.gson.Gson;

@RestController
public class CamundaController {

    // camundaTest-process:2:c793f2d8-91c5-11ed-aa10-106fd9dce29f - procesId
    // Activity_0w3j2wz - tento je spravny

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

                    // create constraints (nedokazem to potom deploinut)
                    // ArrayList<Constrain> constraints = new ArrayList<>();
                    // var preConstraints = f.getCamundaValidation();
                    // if(preConstraints != null){
                    //     var constra = preConstraints.getCamundaConstraints();

                    //     constra.forEach((c) -> {
                    //         constraints.add(new Constrain(c.getCamundaName(), c.getCamundaConfig()));
                    //     });
                    // }
            
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
        String url = String.format("http://localhost:8080/engine-rest/process-definition/%s/xml", procesId);
        JsonData data = restTemplate.getForObject(url, JsonData.class);

        if(data != null){
            BpmnModelInstance modelInst = Bpmn.readModelFromStream(new ByteArrayInputStream(data.getBpmn20Xml().getBytes()));

            var lst = modelInst.getModelElementsByType(UserTask.class);
            if(lst.size() > 0){
                var tasks = new ArrayList<String>();
                lst.forEach(task -> {
                    tasks.add(task.getId());
                });

                return "{\"tasks\":" + new Gson().toJson(tasks) + "}";
            }
        }
        return procesId;
    }

    @GetMapping("/process-list")
    String getProcessList(){
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("http://localhost:8080/engine-rest/process-definition");
        var response = restTemplate.getForEntity(url , Object[].class);
        var data = response.getBody();

        if(data != null){
            return "{\"process\":" + new Gson().toJson(data) + "}";
        }
        return "[]";
    }
    
}
