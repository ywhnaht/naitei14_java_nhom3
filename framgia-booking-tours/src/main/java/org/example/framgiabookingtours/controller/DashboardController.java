package org.example.framgiabookingtours.controller;

import org.example.framgiabookingtours.util.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("admin/dashboard")
    public String dashboard(Model model) {
        var user = SecurityUtils.getCurrentUser().orElse(null);
        if (user != null) {
            model.addAttribute("currentUser", user);

            System.out.println("Current user: " + user.getEmail());
            if (user.getProfile() != null) {
                System.out.println("Full name: " + user.getProfile().getFullName());
            }
        }

        return "admin/dashboard"; 
    }
}