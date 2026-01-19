package com.erp.erp_accounting.hr.employee.entity;

import com.erp.erp_accounting.common.base.BaseEntity;
import com.erp.erp_accounting.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String empNo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate hireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String department;

    @Builder
    public Employee(String empNo, String name, LocalDate hireDate, User user, String position, String department) {
        this.empNo = empNo;
        this.name = name;
        this.hireDate = hireDate;
        this.user = user;
        this.position = position;
        this.department = department;
    }
}
