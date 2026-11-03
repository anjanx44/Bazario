package com.bazario.auth.adapters.out.persistence;

import com.bazario.auth.domain.model.RefreshToken;
import com.bazario.auth.ports.out.RefreshTokenRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostgresRefreshTokenAdapter implements RefreshTokenRepositoryPort {

    private final SpringDataRefreshTokenRepository repository;
    private final EntityManager entityManager;

    @Override
    public Optional<RefreshToken> findByTokenIdHash(String tokenIdHash) {
        return repository.findByTokenIdHash(tokenIdHash).map(this::toDomain);
    }

    @Override
    public List<RefreshToken> lockFamily(UUID familyId) {
        List<RefreshTokenJpaEntity> lockedTokens = repository.lockFamily(familyId);
        lockedTokens.forEach(token -> entityManager.refresh(token, LockModeType.PESSIMISTIC_WRITE));
        return lockedTokens.stream().map(this::toDomain).toList();
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = toEntity(refreshToken);
        return toDomain(repository.save(entity));
    }

    @Override
    public void revokeFamily(UUID familyId) {
        repository.updateFamilyState(familyId, RefreshToken.State.REVOKED);
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(entity.getTokenIdHash(), entity.getCustomerId(), entity.getFamilyId(),
                entity.getExpiresAt(), entity.getState());
    }

    private RefreshTokenJpaEntity toEntity(RefreshToken refreshToken) {
        var entity = new RefreshTokenJpaEntity();
        entity.setTokenIdHash(refreshToken.tokenIdHash());
        entity.setCustomerId(refreshToken.customerId());
        entity.setFamilyId(refreshToken.familyId());
        entity.setExpiresAt(refreshToken.expiresAt());
        entity.setState(refreshToken.state());
        return entity;
    }
}