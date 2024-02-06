package com.project.sfoc.entity.user;

import com.project.sfoc.entity.Provider;
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

    private User(Provider provider, String email, String sub) {
        this.provider = provider;
        this.email = email;
        this.sub = sub;
    }

    public static User of(Provider provider, String email, String sub) {
        return new User(provider, email, sub);
    }

    public User update(String email) {
        return User.of(this.provider, email, this.sub);
    }

}
