CREATE TABLE inventory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL UNIQUE, -- Logical relation to Catalog
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold INT DEFAULT 10 CHECK (low_stock_threshold >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_product_id ON inventory(product_id);
