CREATE TABLE tb_orders
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    customer_name VARCHAR(255) NOT NULL,
    total_price   DECIMAL(10, 2) NULL,
    order_status  VARCHAR(50)  NOT NULL CHECK (order_status IN ('PENDING', 'PROCESSING', 'PROCESSED', 'CANCELED')),
    order_hash    TEXT UNIQUE  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP
);

CREATE TABLE tb_order_items
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id     UUID           NOT NULL REFERENCES tb_orders (id) ON DELETE CASCADE,
    product_name VARCHAR(255)   NOT NULL,
    quantity     INT            NOT NULL,
    price        DECIMAL(10, 2) NOT NULL
);
