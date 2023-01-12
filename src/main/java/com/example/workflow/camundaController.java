package com.example.workflow;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class camundaController {

    @GetMapping("/form/{procesId}/{taskId}")
    String getFormInJson(@PathVariable String procesId, @PathVariable String taskId){
        

        return procesId + " | " + taskId;
    }
    
}
