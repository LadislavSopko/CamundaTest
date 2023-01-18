package com.example.workflow;

@Controller
public class AngularController {

    @RequestMapping("/") 
    public String index() {
        return "forward:/index.html"; 
    } 
}