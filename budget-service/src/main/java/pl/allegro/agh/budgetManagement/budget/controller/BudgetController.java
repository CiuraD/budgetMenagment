package pl.allegro.agh.budgetManagement.budget.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.agh.budgetManagement.budget.security.SecurityUtils;

@RestController
@Profile("budget")
@RequestMapping("/budgets")
public class BudgetController {

    @GetMapping("/hello")
    public String hello() {
        return "hello from budget-room";
    }

    @GetMapping
    public String test() {
        Long userId = SecurityUtils.currentUserId();
        return "Hello user " + userId;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public String createBudget() {
        return "Budget created";
    }
}

