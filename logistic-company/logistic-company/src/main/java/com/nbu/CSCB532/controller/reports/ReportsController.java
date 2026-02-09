package com.nbu.CSCB532.controller.reports;

import com.nbu.CSCB532.model.logistics.ParcelStatus;
import com.nbu.CSCB532.service.logistics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','EMPLOYEE')")
public class ReportsController {

    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final ParcelService parcelService;
    private final OfficeService officeService;
    private final CompanyService companyService;

    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "reports/employees";
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "reports/clients";
    }

    @GetMapping("/parcels/all")
    public String parcelsAll(Model model) {
        model.addAttribute("parcels", parcelService.findAll());
        return "reports/parcels";
    }

    @GetMapping("/parcels/by-employee")
    public String parcelsByEmployee(@RequestParam Long employeeId, Model model) {
        model.addAttribute("parcels", parcelService.findByRegisteredBy(employeeId));
        return "reports/parcels";
    }

    @GetMapping("/parcels/not-received")
    public String parcelsNotReceived(Model model) {
        model.addAttribute("parcels", parcelService.findNotReceived());
        return "reports/parcels";
    }

    @GetMapping("/parcels/by-sender")
    public String parcelsBySender(@RequestParam Long clientId, Model model) {
        model.addAttribute("parcels", parcelService.findBySender(clientId));
        return "reports/parcels";
    }

    @GetMapping("/parcels/by-recipient")
    public String parcelsByRecipient(@RequestParam Long clientId, Model model) {
        // Only received by this client
        model.addAttribute("parcels", parcelService.findReceivedByRecipient(clientId));
        return "reports/parcels";
    }

    @GetMapping("/revenue")
    public String revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        Instant fromTs = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant toTs = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusNanos(1);
        BigDecimal sum = parcelService.sumDeliveredBetween(fromTs, toTs);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("revenue", sum);
        return "reports/revenue";
    }
}

