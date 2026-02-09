package com.nbu.CSCB532.controller.employee;

import com.nbu.CSCB532.global.AccessControlConfig;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.model.logistics.DeliveryType;
import com.nbu.CSCB532.model.logistics.Employee;
import com.nbu.CSCB532.model.logistics.Parcel;
import com.nbu.CSCB532.model.logistics.ParcelStatus;
import com.nbu.CSCB532.service.auth.UserService;
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
@RequestMapping("/employee/parcels")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('EMPLOYEE','ADMINISTRATOR')")
public class EmployeeParcelController {

    private final ParcelService parcelService;
    private final ClientService clientService;
    private final OfficeService officeService;
    private final EmployeeService employeeService;
    private final UserService userService;

    @GetMapping
    public String listAndForm(Model model) {
        model.addAttribute("parcels", parcelService.findAll());
        model.addAttribute("parcel", new Parcel());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("deliveryTypes", DeliveryType.values());
        model.addAttribute("parcelStatuses", ParcelStatus.values());
        // Prefill defaults
        Employee currentEmployee = getCurrentEmployeeOrNull();
        model.addAttribute("currentEmployee", currentEmployee);
        return "employee/parcels";
    }

    @PostMapping
    public String create(@ModelAttribute Parcel parcel) {
        // Default fromOffice to employee's office if not provided
        Employee currentEmployee = getCurrentEmployeeOrNull();
        if (parcel.getFromOffice() == null && currentEmployee != null) {
            parcel.setFromOffice(currentEmployee.getOffice());
        }
        if (currentEmployee != null) {
            parcel.setRegisteredBy(currentEmployee);
        }
        // When registering a new outgoing parcel, initial status defaults in entity (REGISTERED)
        parcelService.save(parcel);
        return "redirect:/employee/parcels";
    }

    @PostMapping("/{id}/receive")
    public String markReceived(@PathVariable Long id) {
        parcelService.findById(id).ifPresent(p -> {
            p.setStatus(ParcelStatus.DELIVERED);
            p.setDeliveredAt(java.time.Instant.now());
            parcelService.save(p);
        });
        return "redirect:/employee/parcels";
    }

    @PostMapping("/{id}/arrived-at-office")
    public String markArrivedAtOffice(@PathVariable Long id) {
        parcelService.findById(id).ifPresent(p -> {
            p.setStatus(ParcelStatus.AT_OFFICE);
            parcelService.save(p);
        });
        return "redirect:/employee/parcels";
    }

    private Employee getCurrentEmployeeOrNull() {
        String username = AccessControlConfig.getUsername();
        return userService.findByUsername(username)
                .map(User::getId)
                .flatMap(employeeService::findById)
                .orElse(null);
    }
}

