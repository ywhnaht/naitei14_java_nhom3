package org.example.framgiabookingtours.controller.admin;

import org.example.framgiabookingtours.entity.*;
import org.example.framgiabookingtours.enums.*;
import org.example.framgiabookingtours.service.BookingService;
import org.example.framgiabookingtours.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final BookingService bookingService;
	private final UserService userService;

	public AdminUserController(UserService userService, BookingService bookingService) {
		this.userService = userService;
		this.bookingService = bookingService;
	}

	@GetMapping
	public String listUsers(Model model, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String status,
			@RequestParam(required = false) String role, @RequestParam(required = false) String keyword) {

		model.addAttribute("activeMenu", "users");

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<User> userPage = userService.getAllUsers(status, role, keyword, pageable);

		model.addAttribute("users", userPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", userPage.getTotalPages());
		model.addAttribute("totalItems", userPage.getTotalElements());
		model.addAttribute("pageSize", size);

		// Trả lại các giá trị lọc về View để giữ trạng thái trên Form
		model.addAttribute("currentStatus", status);
		model.addAttribute("currentRole", role);
		model.addAttribute("currentKeyword", keyword);

		return "admin/users";
	}

	@GetMapping("/{id}")
    public String viewUserDetail(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "users");
        
        User user = userService.getUserById(id);

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Chi tiết người dùng: " + (user.getProfile() != null ? user.getProfile().getFullName() : user.getEmail()));

        List<Booking> bookings = bookingService.getBookingsByUserId(id);
        
        model.addAttribute("bookings", bookings);

        return "admin/user-detail";
    }

	@PostMapping("/update-status")
	public String toggleUserStatus(@RequestParam Long id, @RequestParam String currentStatus,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String filterStatus, @RequestParam(required = false) String filterRole,
			@RequestParam(required = false) String filterKeyword, RedirectAttributes redirectAttributes) {
		// 1. Logic đổi trạng thái (Nếu đang ACTIVE -> BLOCKED, Ngược lại -> ACTIVE)
		// Lưu ý: Nếu user đang UNVERIFIED mà admin gạt nút -> Sẽ thành ACTIVE (Kích
		// hoạt)
		String newStatus = "ACTIVE".equals(currentStatus) ? "BLOCKED" : "ACTIVE";

		// 2. Gọi Service
		userService.updateUserStatus(id, newStatus);

		// 3. Gắn lại các tham số filter vào URL redirect
		redirectAttributes.addAttribute("page", page);
		redirectAttributes.addAttribute("size", size);

		if (filterStatus != null && !filterStatus.isEmpty()) {
			redirectAttributes.addAttribute("status", filterStatus);
		}
		if (filterRole != null && !filterRole.isEmpty()) {
			redirectAttributes.addAttribute("role", filterRole);
		}
		if (filterKeyword != null && !filterKeyword.isEmpty()) {
			redirectAttributes.addAttribute("keyword", filterKeyword);
		}

		// 4. Reload lại trang danh sách
		return "redirect:/admin/users";
	}
}