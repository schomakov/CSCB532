package com.nbu.CSCB532.controller.admin;

import com.nbu.CSCB532.model.Role;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.model.logistics.Employee;
import com.nbu.CSCB532.model.logistics.EmployeeType;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import com.nbu.CSCB532.service.logistics.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class EmployeeAdminController {

    private final EmployeeService employeeService;
    private final OfficeService officeService;
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("employee", new Employee());
        // Only users with EMPLOYEE role
        List<User> employeeUsers = userService.findAllUsers().stream()
                .filter(u -> u.getRole() == Role.EMPLOYEE)
                .toList();
        model.addAttribute("employeeUsers", employeeUsers);
        model.addAttribute("employeeTypes", EmployeeType.values());
        return "admin/employees";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("employee", employeeService.findById(id).orElse(new Employee()));
        List<User> employeeUsers = userService.findAllUsers().stream()
                .filter(u -> u.getRole() == Role.EMPLOYEE)
                .toList();
        model.addAttribute("employeeUsers", employeeUsers);
        model.addAttribute("employeeTypes", EmployeeType.values());
        return "admin/employees";
    }

    @PostMapping
    public String save(@ModelAttribute Employee employee) {
        employeeService.save(employee);
        return "redirect:/admin/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        employeeService.deleteById(id);
        return "redirect:/admin/employees";
    }
}

