package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
public class User {
    //

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    public User() {

    }
}
