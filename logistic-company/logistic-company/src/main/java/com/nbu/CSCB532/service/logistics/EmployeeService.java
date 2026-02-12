package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Employee;
import com.nbu.CSCB532.model.logistics.EmployeeType;
import com.nbu.CSCB532.repository.logistics.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    /** Филтриране по име (в първо/фамилия) и/или офис. Празни стойности = без филтър. */
    public List<Employee> findFiltered(String name, Long officeId) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasOffice = officeId != null;
        if (!hasName && !hasOffice) return employeeRepository.findAll();
        if (hasName && !hasOffice) return employeeRepository.findByUserNameContainingIgnoreCase(name.trim());
        if (!hasName && hasOffice) return employeeRepository.findByOfficeId(officeId);
        return employeeRepository.findByOfficeId(officeId).stream()
                .filter(e -> e.getUser() != null && matchesName(e.getUser().getFirstName(), e.getUser().getLastName(), name.trim()))
                .toList();
    }

    private static boolean matchesName(String first, String last, String search) {
        if (search == null || search.isBlank()) return true;
        String full = (first != null ? first : "") + " " + (last != null ? last : "");
        return full.toLowerCase().contains(search.toLowerCase());
    }

    public List<Employee> findByType(EmployeeType type) {
        return employeeRepository.findByType(type);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }
}

