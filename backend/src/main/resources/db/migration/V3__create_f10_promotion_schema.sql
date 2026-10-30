CREATE TABLE IF NOT EXISTS chi_phi_thuc_te (
    id CHAR(36) NOT NULL, event_id CHAR(36) NOT NULL, supplier_name VARCHAR(200),
    invoice_number VARCHAR(100), amount DECIMAL(18,2) NOT NULL, incurred_at DATETIME(6),
    attachment_id CHAR(36), description VARCHAR(1000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), created_by CHAR(36),
    updated_at DATETIME(6), version BIGINT NOT NULL DEFAULT 0, archived_at DATETIME(6),
    PRIMARY KEY (id), CONSTRAINT fk_cost_event FOREIGN KEY (event_id) REFERENCES su_kien(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chuong_trinh_khuyen_mai (
    id CHAR(36) NOT NULL, code VARCHAR(50) NOT NULL, name VARCHAR(200) NOT NULL, type VARCHAR(40) NOT NULL,
    start_at DATETIME(6), end_at DATETIME(6), minimum_invoice_amount DECIMAL(18,2),
    target_tiers JSON, status VARCHAR(40), terms TEXT, public_visible BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), created_by CHAR(36),
    updated_at DATETIME(6), version BIGINT NOT NULL DEFAULT 0, archived_at DATETIME(6),
    PRIMARY KEY (id), UNIQUE KEY uq_khuyen_mai_code (code)
);

CREATE TABLE IF NOT EXISTS chuong_trinh_giam_gia (
    id CHAR(36) NOT NULL, discount_rate DECIMAL(9,4), maximum_discount DECIMAL(18,2),
    PRIMARY KEY (id), CONSTRAINT fk_discount_promotion FOREIGN KEY (id)
        REFERENCES chuong_trinh_khuyen_mai(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chuong_trinh_tang_phieu (
    id CHAR(36) NOT NULL, voucher_id CHAR(36), total_to_issue INT, issued_count INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id), CONSTRAINT fk_gift_promotion FOREIGN KEY (id)
        REFERENCES chuong_trinh_khuyen_mai(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chuong_trinh_tich_diem (
    id CHAR(36) NOT NULL, reward_points BIGINT, conversion_amount DECIMAL(18,2), points_per_conversion INT,
    PRIMARY KEY (id), CONSTRAINT fk_points_promotion FOREIGN KEY (id)
        REFERENCES chuong_trinh_khuyen_mai(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS phieu_uu_dai (
    id CHAR(36) NOT NULL, code VARCHAR(50) NOT NULL, name VARCHAR(200) NOT NULL,
    discount_type VARCHAR(40), value DECIMAL(18,2), maximum_discount DECIMAL(18,2),
    minimum_invoice_amount DECIMAL(18,2), issued_limit INT, issued_count INT NOT NULL DEFAULT 0,
    used_count INT NOT NULL DEFAULT 0, per_customer_limit INT, valid_from DATETIME(6), expires_at DATETIME(6),
    active BOOLEAN NOT NULL DEFAULT TRUE, created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36), updated_at DATETIME(6), version BIGINT NOT NULL DEFAULT 0, archived_at DATETIME(6),
    PRIMARY KEY (id), UNIQUE KEY uq_phieu_uu_dai_code (code)
);

CREATE TABLE IF NOT EXISTS phieu_uu_dai_khach_hang (
    id CHAR(36) NOT NULL, voucher_id CHAR(36) NOT NULL, customer_id CHAR(36) NOT NULL,
    promotion_id CHAR(36), issued_at DATETIME(6), status VARCHAR(40), redeemed_at DATETIME(6),
    purchase_invoice_id CHAR(36), issue_reference VARCHAR(200),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), created_by CHAR(36),
    updated_at DATETIME(6), version BIGINT NOT NULL DEFAULT 0, archived_at DATETIME(6), PRIMARY KEY (id),
    INDEX ix_customer_voucher (customer_id, voucher_id),
    CONSTRAINT fk_customer_voucher FOREIGN KEY (voucher_id) REFERENCES phieu_uu_dai(id)
);

CREATE TABLE IF NOT EXISTS lan_ap_dung_khuyen_mai (
    id CHAR(36) NOT NULL, promotion_id CHAR(36) NOT NULL, customer_id CHAR(36) NOT NULL,
    invoice_id CHAR(36) NOT NULL, applied_at DATETIME(6), discount_amount DECIMAL(18,2),
    points_granted BIGINT, customer_voucher_id CHAR(36), idempotency_key VARCHAR(120),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), created_by CHAR(36), PRIMARY KEY (id),
    UNIQUE KEY uq_promotion_invoice (promotion_id, invoice_id),
    CONSTRAINT fk_application_promotion FOREIGN KEY (promotion_id) REFERENCES chuong_trinh_khuyen_mai(id)
);

CREATE TABLE IF NOT EXISTS f10_idempotency (
    id CHAR(36) NOT NULL, operation_name VARCHAR(40) NOT NULL, idempotency_key VARCHAR(120) NOT NULL,
    result_id CHAR(36) NOT NULL, created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id), UNIQUE KEY uq_f10_idempotency (operation_name, idempotency_key)
);
