package com.nexusfit.backend.models;

import jakarta.persistence.*;
import lombok.Data;

@Table(name="users")
@Entity
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;

    private boolean isVerified;
}