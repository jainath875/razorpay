package com.codishuttle.razorpay.merchant.entity;

import com.codishuttle.razorpay.common.entity.BaseEntity;
import com.codishuttle.razorpay.common.enums.BusinessType;
import com.codishuttle.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant", indexes = {
        @Index(name = "idx_merchant_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "name", length = 200)
    private String name;

    @Column(unique = true, name = "email", nullable = false)
    private String email;

    @Column(length = 10, name = "contact_number")
    private String contactNumber;

    @Column(length = 50, name = "business_type")
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 100, name = "business_name")
    private String businessName;

    @Column(length = 200, name = "website_url")
    private String websiteUrl;

    @Column(length = 200, nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(length = 20, name = "gst_id")
    private String gstId;

    @Column(length = 20, name = "pan_id")
    private String panId;

    @Column(length = 200, name = "settlement_bank_account")
    private String settlementBankAccount;

    @Column(length = 20, name = "settlement_bank_ifsc")
    private String settlementBankIfsc;

    @Column(length = 200, name = "settlement_bank_account_holder_name")
    private String settlementBankAccountHolderName;

}
