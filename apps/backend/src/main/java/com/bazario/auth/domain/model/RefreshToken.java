package com.bazario.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
        String tokenIdHash,
        UUID customerId,
        UUID familyId,
        Instant expiresAt,
        State state) {

    public RefreshToken withState(State state) {
        return new RefreshToken(tokenIdHash, customerId, familyId, expiresAt, state);
    }

    public enum State {
        ACTIVE,
        CONSUMED,
        REVOKED
    }
}