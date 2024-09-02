package com.etrieu00.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    public record Person(String name, Integer age) {
    }

    @GetMapping("/person")
    public Person retrievePerson() {
        return new Person("Eric", 27);
    }

    public void doNothing(){

    }
}
