package com.nbu.CSCB532.controller.admin;

import com.nbu.CSCB532.model.logistics.Address;
import com.nbu.CSCB532.model.logistics.Office;
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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("offices", officeService.findAll());
        Office office = new Office();
        office.setAddress(new Address());
        model.addAttribute("office", office);
        return "admin/offices";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("offices", officeService.findAll());
        Office office = officeService.findById(id).orElseGet(() -> {
            Office o = new Office();
            o.setAddress(new Address());
            return o;
        });
        model.addAttribute("office", office);
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

