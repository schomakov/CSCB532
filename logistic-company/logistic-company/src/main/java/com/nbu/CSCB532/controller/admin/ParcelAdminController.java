package com.nbu.CSCB532.controller.admin;

import com.nbu.CSCB532.model.logistics.*;
import com.nbu.CSCB532.service.logistics.ClientAddressService;
import com.nbu.CSCB532.service.logistics.ClientService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import com.nbu.CSCB532.service.logistics.OfficeService;
import com.nbu.CSCB532.service.logistics.ParcelService;
import com.nbu.CSCB532.service.logistics.RecipientContactService;
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
    private final RecipientContactService recipientContactService;
    private final ClientAddressService clientAddressService;

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
        if (parcel.getDeliveryType() == null) parcel.setDeliveryType(DeliveryType.TO_OFFICE);
        if (parcel.getSender() == null) parcel.setSender(new Client());
        if (parcel.getFromOffice() == null) parcel.setFromOffice(new Office());
        if (parcel.getToOffice() == null) parcel.setToOffice(new Office());
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
        Parcel parcel = parcelService.findById(id).orElseGet(Parcel::new);
        if (parcel.getSender() == null) parcel.setSender(new Client());
        if (parcel.getFromOffice() == null) parcel.setFromOffice(new Office());
        if (parcel.getToOffice() == null) parcel.setToOffice(new Office());
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
    public String save(
            @ModelAttribute Parcel parcel,
            @RequestParam(required = false) Long recipientContactId,
            @RequestParam(required = false) Long recipientClientId,
            @RequestParam(required = false) String recipientName,
            @RequestParam(required = false) String recipientPhone,
            @RequestParam(required = false) Long deliveryClientAddressId,
            @RequestParam(required = false) String addrCountry,
            @RequestParam(required = false) String addrCity,
            @RequestParam(required = false) String addrZip,
            @RequestParam(required = false) String addrStreet,
            @RequestParam(required = false) String addrDetails) {
        var rc = resolveRecipientContact(recipientContactId, recipientClientId, recipientName, recipientPhone);
        if (rc != null) parcel.setRecipientContact(rc);
        if (parcel.getDeliveryType() == DeliveryType.TO_ADDRESS) {
            var ca = resolveDeliveryAddress(deliveryClientAddressId, addrCountry, addrCity, addrZip, addrStreet, addrDetails);
            if (ca != null) parcel.setDeliveryClientAddress(ca);
        } else {
            parcel.setDeliveryClientAddress(null);
        }
        parcelService.save(parcel);
        return "redirect:/admin/parcels";
    }

    private RecipientContact resolveRecipientContact(Long recipientContactId, Long recipientClientId, String recipientName, String recipientPhone) {
        if (recipientContactId != null) return recipientContactService.findById(recipientContactId).orElse(null);
        if (recipientClientId != null) return recipientContactService.findOrCreateForClient(recipientClientId);
        if (recipientName != null && !recipientName.isBlank()) return recipientContactService.save(recipientName, recipientPhone, null);
        return null;
    }

    private ClientAddress resolveDeliveryAddress(Long deliveryClientAddressId, String country, String city, String zip, String street, String details) {
        if (deliveryClientAddressId != null) return clientAddressService.findById(deliveryClientAddressId).orElse(null);
        if (country != null || city != null || (street != null && !street.isBlank()))
            return clientAddressService.createOneOffAddress(nullToEmpty(country), nullToEmpty(city), nullToEmpty(zip), nullToEmpty(street), nullToEmpty(details));
        return null;
    }

    private static String nullToEmpty(String s) {
        return s != null ? s.trim() : "";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        parcelService.deleteById(id);
        return "redirect:/admin/parcels";
    }
}

