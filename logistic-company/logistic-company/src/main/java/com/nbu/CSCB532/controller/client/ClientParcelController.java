package com.nbu.CSCB532.controller.client;

import com.nbu.CSCB532.global.AccessControlConfig;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.model.logistics.Client;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.ClientService;
import com.nbu.CSCB532.service.logistics.ParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client/parcels")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('CLIENT','ADMINISTRATOR')")
public class ClientParcelController {

    private final ParcelService parcelService;
    private final ClientService clientService;
    private final UserService userService;

    @GetMapping
    public String myParcels(Model model) {
        Client currentClient = getCurrentClientOrNull();
        if (currentClient == null) {
            model.addAttribute("sentParcels", java.util.List.of());
            model.addAttribute("receivedParcels", java.util.List.of());
            model.addAttribute("warning", "Вашият потребител не е регистриран като клиент. Свържете се с администратор.");
            return "client/parcels";
        }
        model.addAttribute("sentParcels", parcelService.findBySender(currentClient.getId()));
        model.addAttribute("receivedParcels", parcelService.findReceivedByRecipient(currentClient.getId()));
        return "client/parcels";
    }

    private Client getCurrentClientOrNull() {
        String username = AccessControlConfig.getUsername();
        return userService.findByUsername(username)
                .map(User::getId)
                .flatMap(clientService::findById)
                .orElse(null);
    }
}

