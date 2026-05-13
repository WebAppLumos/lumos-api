package com.group4.lumos_api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController {
    @GetMapping("hello")
    public String hello() {
        return "Hello Spring!";
    }
    
}
