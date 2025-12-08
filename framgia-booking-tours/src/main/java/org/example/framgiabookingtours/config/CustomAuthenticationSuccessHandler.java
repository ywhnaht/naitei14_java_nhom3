package org.example.framgiabookingtours.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean hasAdminRole = authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        if (hasAdminRole) {
            response.sendRedirect("admin/dashboard");
        } else {
            String email = authentication.getName();
            request.getSession().invalidate();

            ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

            response.sendRedirect("/login?error=access_denied&message=" + errorCode.getMessage() +
                    "&email=" + email);
        }
    }
}
