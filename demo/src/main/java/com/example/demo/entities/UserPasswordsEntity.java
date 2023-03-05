package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

@Entity(name = "usr_password")
@Getter
@Setter
public class UserPasswordsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "credential_name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "password")
    private String password;

}
