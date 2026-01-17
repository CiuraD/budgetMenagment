package com.aghallegro.budgetmenagment.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("user")
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello from user-service";
    }
}

