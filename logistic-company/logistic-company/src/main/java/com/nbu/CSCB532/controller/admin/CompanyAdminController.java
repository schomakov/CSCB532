package com.nbu.CSCB532.controller.admin;

import com.nbu.CSCB532.model.logistics.Company;
import com.nbu.CSCB532.service.logistics.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/companies")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class CompanyAdminController {

    private final CompanyService companyService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("companies", companyService.findAll());
        model.addAttribute("company", new Company());
        return "admin/companies";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("companies", companyService.findAll());
        model.addAttribute("company", companyService.findById(id).orElse(new Company()));
        return "admin/companies";
    }

    @PostMapping
    public String save(@ModelAttribute Company company) {
        companyService.save(company);
        return "redirect:/admin/companies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        companyService.deleteById(id);
        return "redirect:/admin/companies";
    }
}

