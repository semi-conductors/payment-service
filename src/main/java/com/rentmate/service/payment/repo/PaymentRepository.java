package com.rentmate.service.payment.repo;

import com.rentmate.service.payment.entity.PaymentDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentDataEntity, Long> {
}
