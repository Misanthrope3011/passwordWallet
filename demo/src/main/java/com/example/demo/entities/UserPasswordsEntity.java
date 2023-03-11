package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

@Entity(name = "usr_password")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "URL")
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "password")
    private String password;

}
