package com.nbu.CSCB532.controller.reports;

import com.nbu.CSCB532.service.logistics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nbu.CSCB532.model.logistics.Parcel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Контролер за справки: служители, клиенти, пратки (всички, по служител/подател/получател, неполучени), приходи за период.
 * Достъп само за администратор и служител.
 */
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','EMPLOYEE')")
public class ReportsController {

    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final ParcelService parcelService;
    private final OfficeService officeService;

    @GetMapping("/employees")
    public String employees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long officeId,
            Model model) {
        model.addAttribute("employees", employeeService.findFiltered(name, officeId));
        model.addAttribute("offices", officeService.findAll());
        model.addAttribute("filterName", name);
        model.addAttribute("filterOfficeId", officeId);
        return "reports/employees";
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "reports/clients";
    }

    @GetMapping("/parcels/all")
    public String parcelsAll(
            @RequestParam(required = false) String recipientName,
            @RequestParam(required = false) String senderName,
            Model model) {
        model.addAttribute("parcels", parcelService.findFiltered(recipientName, senderName));
        model.addAttribute("filterRecipientName", recipientName);
        model.addAttribute("filterSenderName", senderName);
        return "reports/parcels";
    }

    /**
     * Страница с форми за избор на справки по служител или клиент (подател/получател).
     */
    @GetMapping("/parcels/select")
    public String parcelsSelect(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("clients", clientService.findAll());
        return "reports/parcels-select";
    }

    @GetMapping("/parcels/by-employee")
    public String parcelsByEmployee(@RequestParam Long employeeId, Model model) {
        model.addAttribute("parcels", parcelService.findByRegisteredBy(employeeId));
        model.addAttribute("filterRecipientName", null);
        model.addAttribute("filterSenderName", null);
        return "reports/parcels";
    }

    @GetMapping("/parcels/not-received")
    public String parcelsNotReceived(Model model) {
        model.addAttribute("parcels", parcelService.findNotReceived());
        model.addAttribute("filterRecipientName", null);
        model.addAttribute("filterSenderName", null);
        return "reports/parcels";
    }

    @GetMapping("/parcels/by-sender")
    public String parcelsBySender(@RequestParam Long clientId, Model model) {
        model.addAttribute("parcels", parcelService.findBySender(clientId));
        model.addAttribute("filterRecipientName", null);
        model.addAttribute("filterSenderName", null);
        return "reports/parcels";
    }

    @GetMapping("/parcels/by-recipient")
    public String parcelsByRecipient(@RequestParam Long clientId, Model model) {
        model.addAttribute("parcels", parcelService.findReceivedByRecipient(clientId));
        model.addAttribute("filterRecipientName", null);
        model.addAttribute("filterSenderName", null);
        return "reports/parcels";
    }

    /** Приходи – само за администратор. Изчисление: сума от цените на доставените пратки в периода. */
    @GetMapping("/revenue")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public String revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        Instant fromTs = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant toTs = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusNanos(1);
        BigDecimal sum = parcelService.sumPaidBetween(fromTs, toTs);
        List<Parcel> unpaidParcels = parcelService.findUnpaidParcels();
        BigDecimal unpaidTotal = parcelService.sumUnpaidAmount();
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("revenue", sum);
        model.addAttribute("unpaidParcels", unpaidParcels);
        model.addAttribute("unpaidTotal", unpaidTotal);
        return "reports/revenue";
    }
}

