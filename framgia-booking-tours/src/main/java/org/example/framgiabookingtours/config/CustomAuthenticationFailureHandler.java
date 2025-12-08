package org.example.framgiabookingtours.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        ErrorCode errorCode;

        if (exception instanceof BadCredentialsException) {
            errorCode = ErrorCode.INVALID_CREDENTIALS;
        } else if (exception instanceof LockedException) {
            errorCode = ErrorCode.ACCOUNT_LOCKED;
        } else if (exception instanceof DisabledException) {
            errorCode = ErrorCode.UNVERIFIED_EMAIL;
        } else if (exception instanceof AccountExpiredException) {
            errorCode = ErrorCode.ACCOUNT_LOCKED;
        } else if (exception instanceof CredentialsExpiredException) {
            errorCode = ErrorCode.INVALID_CREDENTIALS;
        } else {
            errorCode = ErrorCode.UNAUTHENTICATED;
        }

        String email = request.getParameter("username");

        response.sendRedirect("/login?error=true&message=" + errorCode.getMessage() +
                "&email=" + email);
    }
}
