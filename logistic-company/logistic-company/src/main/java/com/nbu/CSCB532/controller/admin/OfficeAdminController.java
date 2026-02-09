package com.nbu.CSCB532.controller.admin;

import com.nbu.CSCB532.model.logistics.Office;
import com.nbu.CSCB532.service.logistics.CompanyService;
import com.nbu.CSCB532.service.logistics.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/offices")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class OfficeAdminController {

    private final OfficeService officeService;
    private final CompanyService companyService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("companies", companyService.findAll());
        model.addAttribute("office", new Office());
        return "admin/offices";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("companies", companyService.findAll());
        model.addAttribute("office", officeService.findById(id).orElse(new Office()));
        return "admin/offices";
    }

    @PostMapping
    public String save(@ModelAttribute Office office) {
        officeService.save(office);
        return "redirect:/admin/offices";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        officeService.deleteById(id);
        return "redirect:/admin/offices";
    }
}

