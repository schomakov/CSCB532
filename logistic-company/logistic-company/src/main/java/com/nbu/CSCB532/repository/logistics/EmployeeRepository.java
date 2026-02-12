package com.nbu.CSCB532.repository.logistics;

import com.nbu.CSCB532.model.logistics.Employee;
import com.nbu.CSCB532.model.logistics.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByType(EmployeeType type);

    List<Employee> findByOfficeId(Long officeId);

    @Query("SELECT e FROM Employee e JOIN e.user u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> findByUserNameContainingIgnoreCase(@Param("name") String name);
}

