package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framgiabookingtours.dto.request.AdminDashboardStatsDTO; // Đảm bảo đúng package DTO của bạn
import org.example.framgiabookingtours.entity.Booking;
import org.example.framgiabookingtours.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingService bookingService;

    /**
     * Hiển thị trang danh sách Booking KÈM Thống kê
     * URL: GET /admin/bookings
     */
    @GetMapping
    public String showBookingsList(Model model) {
        List<Booking> bookingsList = bookingService.getAllBookings();

        AdminDashboardStatsDTO stats = bookingService.getBookingStats();

        model.addAttribute("bookingsList", bookingsList);
        model.addAttribute("stats", stats);
        return "admin/bookings-list";
    }

    @PostMapping("/{id}/confirm")
    public String confirmBooking(@PathVariable("id") Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.adminApproveBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt thành công Booking ID: " + bookingId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.adminRejectBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn và hoàn slot thành công cho Booking ID: " + bookingId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}