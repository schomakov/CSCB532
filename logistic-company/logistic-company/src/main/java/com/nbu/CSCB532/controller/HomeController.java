package com.nbu.CSCB532.controller;

import com.nbu.CSCB532.global.AccessControlConfig;
import com.nbu.CSCB532.model.Role;
import com.nbu.CSCB532.model.logistics.EmployeeType;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final EmployeeService employeeService;

    @GetMapping("/")
    public String homePage(Model model) {
        if (AccessControlConfig.isEmployee()) {
            String username = AccessControlConfig.getUsername();
            if (username != null) {
                var emp = userService.findByUsername(username)
                        .filter(u -> u.getRole() == Role.EMPLOYEE)
                        .flatMap(u -> employeeService.findById(u.getId()));
                if (emp.isPresent() && emp.get().getType() == EmployeeType.OFFICE_EMPLOYEE) {
                    return "redirect:/employee/track";
                }
            }
        }
        return "home";
    }

    @GetMapping("/error")
    public String errorPage(Model model) {
        return "error";
    }
}