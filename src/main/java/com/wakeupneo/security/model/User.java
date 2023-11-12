package com.wakeupneo.security.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(unique = true)
    private String email;
    @Column(unique = true, length = 20)
    private String username;
    private String password;
    @Column(length = 25)
    private String name;
    @Column(length = 25)
    private String surname;
    private String profileImageUrl;
    private Date lastLoginDateDisplay;
    private Date lastLoginDate;
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();
    @Builder.Default
    private boolean verified = false;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Builder.Default
    private boolean accountExpired = false;
    @Builder.Default
    private boolean accountLocked = false;
    @Builder.Default
    private boolean credentialsExpired = false;
    @Builder.Default
    private boolean enabled = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
