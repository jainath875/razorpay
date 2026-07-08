package com.codishuttle.razorpay.merchant.repository;

import com.codishuttle.razorpay.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
