package com.example.demo.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerOneService {
    @RequestMapping(value = "/getserver1",method = RequestMethod.GET)
    public String hello(@RequestParam("name")String name){
        return "hello ,"+name;
    }

    @RequestMapping(value = "/postserver1",method = RequestMethod.POST)
    public String helloPost(@RequestParam("name")String name){
        return "hello ,"+name;
    }
}
