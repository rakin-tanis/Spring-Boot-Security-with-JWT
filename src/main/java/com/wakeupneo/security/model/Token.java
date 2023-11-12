package com.wakeupneo.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = "user")
@ToString
public class Token {

    private static final int EXPIRATION = 24 * 60 * 60 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(updatable = false)
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    public static Date calculateExpiryDate() {
        return new Date(Instant.now().plusMillis(EXPIRATION).toEpochMilli());
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate();
    }

}
