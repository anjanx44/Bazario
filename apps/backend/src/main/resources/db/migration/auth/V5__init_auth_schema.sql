CREATE TABLE auth_refresh_tokens (
    token_id_hash CHAR(64) PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    family_id UUID NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    state VARCHAR(16) NOT NULL CHECK (state IN ('ACTIVE', 'CONSUMED', 'REVOKED'))
);

CREATE INDEX idx_auth_refresh_tokens_family_id ON auth_refresh_tokens(family_id);
CREATE INDEX idx_auth_refresh_tokens_customer_id ON auth_refresh_tokens(customer_id);