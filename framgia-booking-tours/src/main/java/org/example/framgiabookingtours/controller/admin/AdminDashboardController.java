package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

import org.example.framgiabookingtours.service.DashboardService;
import org.example.framgiabookingtours.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

		model.addAttribute("activeMenu", "dashboard");

		// Lấy KPI tổng quan đổ vào Model để Thymeleaf hiển thị
		model.addAttribute("stats", dashboardService.getDashboardStats());

		// Gửi năm hiện tại để hiển thị mặc định ở Dropdown
		model.addAttribute("currentYear", LocalDate.now().getYear());

		return "admin/dashboard";
	}

	// API Dữ liệu Biểu đồ có lọc theo năm
	@GetMapping("/api/chart-data")
	@ResponseBody
	public ResponseEntity<?> getChartDataAPI(@RequestParam(name = "year", required = false) Integer year) {
		// Nếu không truyền year, lấy năm hiện tại
		int selectedYear = (year != null) ? year : LocalDate.now().getYear();

		return ResponseEntity.ok(Map.of("revenueChart", dashboardService.getRevenueChartData(selectedYear),
				"statusChart", dashboardService.getStatusChartData()));
	}
}