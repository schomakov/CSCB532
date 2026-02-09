package com.nbu.CSCB532.controller.admin;

import com.nbu.CSCB532.model.Role;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.model.logistics.Client;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class ClientAdminController {

    private final ClientService clientService;
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("client", new Client());
        List<User> clientUsers = userService.findAllUsers().stream()
                .filter(u -> u.getRole() == Role.CLIENT)
                .toList();
        model.addAttribute("clientUsers", clientUsers);
        return "admin/clients";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("client", clientService.findById(id).orElse(new Client()));
        List<User> clientUsers = userService.findAllUsers().stream()
                .filter(u -> u.getRole() == Role.CLIENT)
                .toList();
        model.addAttribute("clientUsers", clientUsers);
        return "admin/clients";
    }

    @PostMapping
    public String save(@ModelAttribute Client client) {
        clientService.save(client);
        return "redirect:/admin/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        clientService.deleteById(id);
        return "redirect:/admin/clients";
    }
}

