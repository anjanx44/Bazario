package com.bazario.auth.adapters.out.persistence;

import com.bazario.auth.domain.model.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, String> {

    Optional<RefreshTokenJpaEntity> findByTokenIdHash(String tokenIdHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshTokenJpaEntity token where token.familyId = :familyId order by token.tokenIdHash")
    List<RefreshTokenJpaEntity> lockFamily(@Param("familyId") UUID familyId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update RefreshTokenJpaEntity token set token.state = :state where token.familyId = :familyId")
    void updateFamilyState(@Param("familyId") UUID familyId, @Param("state") RefreshToken.State state);
}