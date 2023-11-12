package com.wakeupneo.security.repository;

import com.wakeupneo.security.model.Token;
import com.wakeupneo.security.model.TokenType;
import com.wakeupneo.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenAndType(String token, TokenType type);

    Token findByUser(User user);

    Stream<Token> findAllByExpiryDateLessThan(Date now);

    void deleteByUserIdAndType(UUID uuid, TokenType type);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from Token t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);


}
