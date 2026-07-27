package com.codishuttle.razorpay.vault.repository;

import com.codishuttle.razorpay.vault.entity.CardToken;
import com.codishuttle.razorpay.vault.entity.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
    Optional<CardToken> findByTokenAndRevokedAtIsNull(String token);
}
