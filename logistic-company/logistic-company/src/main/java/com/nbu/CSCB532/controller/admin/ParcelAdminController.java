package com.nbu.CSCB532.controller.admin;

import com.nbu.CSCB532.model.logistics.*;
import com.nbu.CSCB532.service.logistics.ClientService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import com.nbu.CSCB532.service.logistics.OfficeService;
import com.nbu.CSCB532.service.logistics.ParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/parcels")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class ParcelAdminController {

    private final ParcelService parcelService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final OfficeService officeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("parcels", parcelService.findAll());
        Parcel parcel = new Parcel();
        parcel.setDeliveryAddress(new Address());
        model.addAttribute("parcel", parcel);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("deliveryTypes", DeliveryType.values());
        model.addAttribute("parcelStatuses", ParcelStatus.values());
        model.addAttribute("paymentTypes", PaymentType.values());
        return "admin/parcels";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("parcels", parcelService.findAll());
        Parcel parcel = parcelService.findById(id).orElseGet(() -> {
            Parcel p = new Parcel();
            p.setDeliveryAddress(new Address());
            return p;
        });
        if (parcel.getDeliveryAddress() == null) {
            parcel.setDeliveryAddress(new Address());
        }
        model.addAttribute("parcel", parcel);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("deliveryTypes", DeliveryType.values());
        model.addAttribute("parcelStatuses", ParcelStatus.values());
        model.addAttribute("paymentTypes", PaymentType.values());
        return "admin/parcels";
    }

    @PostMapping
    public String save(@ModelAttribute Parcel parcel) {
        parcelService.save(parcel);
        return "redirect:/admin/parcels";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        parcelService.deleteById(id);
        return "redirect:/admin/parcels";
    }
}

