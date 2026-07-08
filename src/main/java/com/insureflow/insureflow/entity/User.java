package com.insureflow.insureflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Email(message = "Enter a valid email")
    @Column(unique = true)
    private String email;

    // Nullable now - OAuth users don't have a password
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Tracks whether this user signed up via email/password or Google
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider = AuthProvider.LOCAL;
}