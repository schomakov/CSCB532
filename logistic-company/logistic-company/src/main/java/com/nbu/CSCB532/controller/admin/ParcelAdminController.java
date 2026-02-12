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

import java.util.List;

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
    public String list(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long clientId,
            Model model) {
        model.addAttribute("parcels", resolveParcelList(filter, employeeId, clientId));
        model.addAttribute("filter", filter != null ? filter : "all");
        model.addAttribute("filterEmployeeId", employeeId);
        model.addAttribute("filterClientId", clientId);
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

    private List<Parcel> resolveParcelList(String filter, Long employeeId, Long clientId) {
        if (filter == null || "all".equals(filter)) return parcelService.findAll();
        if ("byEmployee".equals(filter) && employeeId != null) return parcelService.findByRegisteredBy(employeeId);
        if ("notReceived".equals(filter)) return parcelService.findNotReceived();
        if ("bySender".equals(filter) && clientId != null) return parcelService.findBySender(clientId);
        if ("byRecipient".equals(filter) && clientId != null) return parcelService.findReceivedByRecipient(clientId);
        return parcelService.findAll();
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

