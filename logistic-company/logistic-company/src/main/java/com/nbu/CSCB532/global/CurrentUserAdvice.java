package com.nbu.CSCB532.global;

import com.nbu.CSCB532.model.logistics.EmployeeType;
import com.nbu.CSCB532.service.auth.UserService;
import com.nbu.CSCB532.service.logistics.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Добавя атрибути към модела за всички заявки, за да може header да показва/скрива табове по тип служител.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserAdvice {

    private final UserService userService;
    private final EmployeeService employeeService;

    @ModelAttribute("currentUserIsCourier")
    public boolean currentUserIsCourier() {
        if (!AccessControlConfig.isEmployee()) return false;
        String username = AccessControlConfig.getUsername();
        if (username == null) return false;
        return userService.findByUsername(username)
                .map(u -> employeeService.findById(u.getId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get().getType() == EmployeeType.COURIER)
                .orElse(false);
    }
}
