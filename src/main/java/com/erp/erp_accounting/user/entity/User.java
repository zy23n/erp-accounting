package com.erp.erp_accounting.user.entity;

import com.erp.erp_accounting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 로그인 ID

    @Column(nullable = false)
    private String password; // 추후 BCrypt 암호화 예정

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // USER, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status; // ACTIVE, INACTIVE

    @Column(nullable = false)
    private boolean locked;

    @Builder
    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.locked = false;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }
}
