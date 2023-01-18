package com.example.workflow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AngularController {

    @RequestMapping("/tester") 
    public String index() {
        return "forward:/index.html"; 
    } 
}