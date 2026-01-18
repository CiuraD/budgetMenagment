package pl.allegro.agh.budgetManagement.budget.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("budget")
public class BudgetController {

    @GetMapping("/hello")
    public String hello() {
        return "hello from budget-room";
    }
}

