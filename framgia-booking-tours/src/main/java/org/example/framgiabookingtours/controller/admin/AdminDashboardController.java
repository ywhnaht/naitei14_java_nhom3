package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.dto.response.AdminDashboardStatsDTO;
import org.example.framgiabookingtours.service.DashboardService;
import org.example.framgiabookingtours.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final DashboardService dashboardService;

     @GetMapping
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

    // --- TEST JSON ---
    @GetMapping("/test-api")
    @ResponseBody
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStatsAPI() {
        AdminDashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}