-- Enable UUID extension for PostgreSQL (if not already enabled)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Roles Table
-- Stores application roles used for authorization
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE, -- e.g., 'ROLE_CUSTOMER', 'ROLE_ADMIN'
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Pre-populate default roles
-- The ON CONFLICT clause ensures idempotent execution
INSERT INTO roles (name, description) VALUES
('ROLE_CUSTOMER', 'Regular customer with standard shopping privileges'),
('ROLE_ADMIN', 'Administrator with full system access'),
('ROLE_SELLER', 'Vendor who can manage their own products')
ON CONFLICT (name) DO NOTHING;

-- 2. Users Table
-- Stores user authentication and basic profile information
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- BCrypt hashed, requires at least 60 chars
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20) UNIQUE,
    is_enabled BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    is_account_non_expired BOOLEAN DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. User_Roles Junction Table (Many-to-Many)
-- Associates users with their roles
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    granted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- 4. Indexes for optimized queries
-- Index for email lookup during authentication
CREATE INDEX idx_users_email ON users(email);

-- Index for enabled users
CREATE INDEX idx_users_enabled ON users(is_enabled) WHERE is_enabled = TRUE;

-- Index to optimize role lookups
CREATE INDEX idx_roles_name ON roles(name);