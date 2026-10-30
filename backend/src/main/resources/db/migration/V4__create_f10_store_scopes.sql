CREATE TABLE IF NOT EXISTS khuyen_mai_cua_hang (
    id CHAR(36) NOT NULL, promotion_id CHAR(36) NOT NULL, store_id CHAR(36) NOT NULL,
    PRIMARY KEY (id), UNIQUE KEY uq_khuyen_mai_cua_hang (promotion_id, store_id),
    CONSTRAINT fk_store_scope_promotion FOREIGN KEY (promotion_id)
        REFERENCES chuong_trinh_khuyen_mai(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS phieu_uu_dai_cua_hang (
    id CHAR(36) NOT NULL, voucher_id CHAR(36) NOT NULL, store_id CHAR(36) NOT NULL,
    PRIMARY KEY (id), UNIQUE KEY uq_phieu_uu_dai_cua_hang (voucher_id, store_id),
    CONSTRAINT fk_store_scope_voucher FOREIGN KEY (voucher_id)
        REFERENCES phieu_uu_dai(id) ON DELETE CASCADE
);
