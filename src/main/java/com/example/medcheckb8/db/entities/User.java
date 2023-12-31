package com.example.medcheckb8.db.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_seq", allocationSize = 1, initialValue = 6)
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @OneToOne(cascade = ALL)
    private Account account;
    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Appointment> appointments;
    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Result> results;

    public void addResult(Result result) {
        if (result == null){
            results = new ArrayList<>();
        }
        results.add(result);
    }
}