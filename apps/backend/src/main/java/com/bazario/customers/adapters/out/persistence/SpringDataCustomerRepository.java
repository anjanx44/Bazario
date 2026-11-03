package com.bazario.customers.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    Optional<CustomerJpaEntity> findByEmail(String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE customers SET password_hash = :newPasswordHash, updated_at = CURRENT_TIMESTAMP
            WHERE id = :customerId AND password_hash = :expectedPasswordHash AND is_enabled = TRUE
            """, nativeQuery = true)
    int updatePasswordIfUnchanged(@Param("customerId") UUID customerId,
                                  @Param("expectedPasswordHash") String expectedPasswordHash,
                                  @Param("newPasswordHash") String newPasswordHash);
}
