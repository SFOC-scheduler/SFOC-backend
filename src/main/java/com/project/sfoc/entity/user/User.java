package com.project.sfoc.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Provider provider;

    private String email;

    private String sub;

    @Column(name = "user_grant")
    @Enumerated(value = EnumType.STRING)
    private Grant grant;

    private User(Provider provider, String email, String sub, Grant grant) {
        this.provider = provider;
        this.email = email;
        this.sub = sub;
        this.grant = grant;
    }

    public static User of(Provider provider, String email, String sub) {
        return new User(provider, email, sub, Grant.USER);
    }

    public User update(String email) {
        this.email = email;
        return this;
    }

}
