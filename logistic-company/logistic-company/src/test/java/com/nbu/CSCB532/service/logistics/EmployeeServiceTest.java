package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Employee;
import com.nbu.CSCB532.model.logistics.EmployeeType;
import com.nbu.CSCB532.repository.logistics.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().id(1L).type(EmployeeType.COURIER).active(true).build();
    }

    @Test
    void findAll_returnsAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        List<Employee> result = employeeService.findAll();
        assertThat(result).hasSize(1).containsExactly(employee);
        verify(employeeRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        assertThat(employeeService.findById(1L)).contains(employee);
    }

    @Test
    void findByCompany_returnsEmployeesOfCompany() {
        when(employeeRepository.findByCompanyId(1L)).thenReturn(List.of(employee));
        List<Employee> result = employeeService.findByCompany(1L);
        assertThat(result).hasSize(1).containsExactly(employee);
    }

    @Test
    void findByType_returnsEmployeesOfType() {
        when(employeeRepository.findByType(EmployeeType.COURIER)).thenReturn(List.of(employee));
        List<Employee> result = employeeService.findByType(EmployeeType.COURIER);
        assertThat(result).hasSize(1).containsExactly(employee);
    }

    @Test
    void save_persistsEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Employee saved = employeeService.save(employee);
        assertThat(saved).isEqualTo(employee);
        verify(employeeRepository).save(employee);
    }

    @Test
    void deleteById_callsRepository() {
        employeeService.deleteById(1L);
        verify(employeeRepository).deleteById(1L);
    }
}
