package com.nbu.CSCB532.controller.employee;

import com.nbu.CSCB532.global.AccessControlConfig;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.model.logistics.Address;
import com.nbu.CSCB532.model.logistics.DeliveryType;
import com.nbu.CSCB532.model.logistics.Employee;
import com.nbu.CSCB532.model.logistics.EmployeeType;
import com.nbu.CSCB532.model.logistics.Parcel;
import com.nbu.CSCB532.model.logistics.ParcelStatus;
import com.nbu.CSCB532.model.logistics.PaymentType;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.ClientService;
import com.nbu.CSCB532.service.logistics.OfficeService;
import com.nbu.CSCB532.service.logistics.ParcelService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('EMPLOYEE','ADMINISTRATOR')")
public class EmployeeParcelController {

    private final ParcelService parcelService;
    private final ClientService clientService;
    private final OfficeService officeService;
    private final EmployeeService employeeService;
    private final UserService userService;

    /** По задание: всеки служител вижда всички пратки в системата. */
    @GetMapping("/parcels")
    public String listAndForm(Model model) {
        Employee currentEmployee = getCurrentEmployeeOrNull();

        List<Parcel> parcels = parcelService.findAll();
        List<com.nbu.CSCB532.model.logistics.Office> offices = officeService.findAll();
        List<com.nbu.CSCB532.model.logistics.Client> clients = clientService.findAll();

        model.addAttribute("parcels", parcels);
        Parcel parcel = new Parcel();
        parcel.setDeliveryAddress(new Address());
        parcel.setPaymentType(PaymentType.SENDER_PAYS);
        model.addAttribute("parcel", parcel);
        model.addAttribute("clients", clients);
        model.addAttribute("offices", offices);
        model.addAttribute("deliveryTypes", DeliveryType.values());
        model.addAttribute("paymentTypes", PaymentType.values());
        model.addAttribute("currentEmployee", currentEmployee);
        return "employee/parcels";
    }

    @PostMapping("/parcels")
    public String create(@ModelAttribute Parcel parcel, RedirectAttributes redirectAttributes) {
        Employee currentEmployee = getCurrentEmployeeOrNull();
        if (currentEmployee != null) {
            if (parcel.getFromOffice() == null) {
                parcel.setFromOffice(currentEmployee.getOffice());
            }
            parcel.setRegisteredBy(currentEmployee);
        }
        if (parcel.getCourier() == null) {
            var couriers = employeeService.findByType(EmployeeType.COURIER);
            if (!couriers.isEmpty()) {
                parcel.setCourier(couriers.get(0));
            }
        }
        if (parcel.getPaymentType() == null) {
            parcel.setPaymentType(PaymentType.SENDER_PAYS);
        }
        parcelService.save(parcel);
        return "redirect:/employee/parcels";
    }

    @PostMapping("/parcels/{id}/receive")
    public String markReceived(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!canModifyParcel(id, redirectAttributes)) return "redirect:/employee/parcels";
        parcelService.findById(id).ifPresent(p -> {
            p.setStatus(ParcelStatus.DELIVERED);
            p.setDeliveredAt(java.time.Instant.now());
            parcelService.save(p);
        });
        return "redirect:/employee/parcels";
    }

    @PostMapping("/parcels/{id}/arrived-at-office")
    public String markArrivedAtOffice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!canModifyParcel(id, redirectAttributes)) return "redirect:/employee/parcels";
        parcelService.findById(id).ifPresent(p -> {
            p.setStatus(ParcelStatus.AT_OFFICE);
            parcelService.save(p);
        });
        return "redirect:/employee/parcels";
    }

    /** По задание: служителите виждат всички пратки – проследяването показва всяка пратка. */
    @GetMapping("/track")
    public String track(@RequestParam(required = false) String code, Model model) {
        model.addAttribute("code", code != null ? code : "");
        if (code != null && !code.isBlank()) {
            parcelService.findByTrackingCode(code.strip()).ifPresentOrElse(
                    parcel -> {
                        model.addAttribute("parcel", parcel);
                        model.addAttribute("allowed", true);
                    },
                    () -> model.addAttribute("notFound", true)
            );
        }
        return "employee/track";
    }

    /** По задание: служителите виждат всички клиенти на компанията. */
    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "employee/clients";
    }

    /** По задание: служителите могат да регистрират/променят всички пратки. */
    private boolean canModifyParcel(Long parcelId, RedirectAttributes redirectAttributes) {
        Optional<Parcel> opt = parcelService.findById(parcelId);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Пратката не е намерена.");
            return false;
        }
        return true;
    }

    private Employee getCurrentEmployeeOrNull() {
        String username = AccessControlConfig.getUsername();
        if (username == null) return null;
        return userService.findByUsername(username)
                .map(User::getId)
                .flatMap(employeeService::findById)
                .orElse(null);
    }
}
