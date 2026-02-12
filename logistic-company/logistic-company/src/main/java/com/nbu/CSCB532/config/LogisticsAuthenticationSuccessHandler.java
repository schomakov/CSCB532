package com.nbu.CSCB532.config;

import com.nbu.CSCB532.model.Role;
import com.nbu.CSCB532.model.logistics.EmployeeType;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * След успешен вход: офис служители → проследяване, куриери → мои доставки, останалите → начална.
 */
@Component
@RequiredArgsConstructor
public class LogisticsAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final EmployeeService employeeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String[] redirectUrl = { "/" };
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "EMPLOYEE".equals(a.getAuthority()))) {
            String username = authentication.getName();
            userService.findByUsername(username)
                    .filter(u -> u.getRole() == Role.EMPLOYEE)
                    .flatMap(u -> employeeService.findById(u.getId()))
                    .ifPresent(emp -> {
                        if (emp.getType() == EmployeeType.OFFICE_EMPLOYEE) {
                            redirectUrl[0] = "/employee/track";
                        } else if (emp.getType() == EmployeeType.COURIER) {
                            redirectUrl[0] = "/courier";
                        }
                    });
        }
        response.sendRedirect(request.getContextPath() + redirectUrl[0]);
    }
}
