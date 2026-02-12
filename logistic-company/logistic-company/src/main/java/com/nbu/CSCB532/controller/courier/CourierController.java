package com.nbu.CSCB532.controller.courier;

import com.nbu.CSCB532.global.AccessControlConfig;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.model.logistics.Employee;
import com.nbu.CSCB532.model.logistics.Parcel;
import com.nbu.CSCB532.model.logistics.ParcelStatus;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import com.nbu.CSCB532.service.logistics.ParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Начална страница за куриери: пратки за доставка, адрес/офис, получател, плащане при предаване/доставка.
 * Достъп за служители; само куриерите виждат своите назначени пратки.
 */
@Controller
@RequestMapping("/courier")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('EMPLOYEE','ADMINISTRATOR')")
public class CourierController {

    private final ParcelService parcelService;
    private final EmployeeService employeeService;
    private final UserService userService;

    @GetMapping
    public String myDeliveries(Model model) {
        Employee current = getCurrentEmployeeOrNull();
        List<Parcel> toDeliver = current != null ? parcelService.findToDeliverByCourier(current.getId()) : List.of();
        model.addAttribute("parcels", toDeliver);
        model.addAttribute("currentEmployee", current);
        return "courier/deliveries";
    }

    @PostMapping("/parcels/{id}/arrived-at-office")
    public String markArrivedAtOffice(@PathVariable Long id, RedirectAttributes ra) {
        if (!courierCanModify(id, ra)) return "redirect:/courier";
        parcelService.findById(id).ifPresent(p -> {
            p.setStatus(ParcelStatus.AT_OFFICE);
            parcelService.save(p);
        });
        return "redirect:/courier";
    }

    @PostMapping("/parcels/{id}/delivered")
    public String markDelivered(@PathVariable Long id, RedirectAttributes ra) {
        if (!courierCanModify(id, ra)) return "redirect:/courier";
        parcelService.findById(id).ifPresent(p -> {
            p.setStatus(ParcelStatus.DELIVERED);
            p.setDeliveredAt(java.time.Instant.now());
            parcelService.save(p);
        });
        return "redirect:/courier";
    }

    private boolean courierCanModify(Long parcelId, RedirectAttributes ra) {
        Employee current = getCurrentEmployeeOrNull();
        Optional<Parcel> opt = parcelService.findById(parcelId);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Пратката не е намерена.");
            return false;
        }
        Parcel p = opt.get();
        if (current == null || p.getCourier() == null || !p.getCourier().getId().equals(current.getId())) {
            ra.addFlashAttribute("error", "Нямате права върху тази пратка.");
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
