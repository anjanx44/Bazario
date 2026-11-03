package com.bazario.auth.ports.out;

import com.bazario.auth.domain.model.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    Optional<RefreshToken> findByTokenIdHash(String tokenIdHash);

    List<RefreshToken> lockFamily(UUID familyId);

    RefreshToken save(RefreshToken refreshToken);

    void revokeFamily(UUID familyId);
}