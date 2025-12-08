package org.example.framgiabookingtours.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminLoginController {

    @GetMapping("login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            if ("access_denied".equals(error)) {
                model.addAttribute("errorMessage",
                        message != null ? message : "Bạn không có quyền truy cập vào trang quản trị!");
            } else if (message != null) {
                model.addAttribute("errorMessage", message);
            } else {
                model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng!");
            }
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }

        if (email != null && !email.isEmpty()) {
            model.addAttribute("email", email);
        }

        return "admin/login";
    }
}
