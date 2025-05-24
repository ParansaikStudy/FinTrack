package com.track.fin.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Grade grade;

    private String name;

    private String phone;

    private String loginId;

    private String password;

    private LocalDateTime birthDate;

    @Builder
    private User(Long id, Grade grade, String name, String phone, String loginId, String password, LocalDateTime birthDate) {
        this.id = id;
        this.grade = grade;
        this.name = name;
        this.phone = phone;
        this.loginId = loginId;
        this.password = password;
        this.birthDate = birthDate;
    }

}
