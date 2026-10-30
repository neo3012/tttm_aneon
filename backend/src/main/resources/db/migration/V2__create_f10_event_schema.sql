CREATE TABLE IF NOT EXISTS su_kien (
    id CHAR(36) NOT NULL, code VARCHAR(50) NOT NULL, name VARCHAR(200) NOT NULL,
    type VARCHAR(100), location_id CHAR(36), start_at DATETIME(6), end_at DATETIME(6),
    estimated_budget DECIMAL(18,2), approved_budget DECIMAL(18,2), target_attendees INT,
    status VARCHAR(40), proposed_by CHAR(36), approved_by CHAR(36), approved_at DATETIME(6),
    rejection_reason VARCHAR(500), public_visible BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), created_by CHAR(36),
    updated_at DATETIME(6), version BIGINT NOT NULL DEFAULT 0, archived_at DATETIME(6),
    PRIMARY KEY (id), UNIQUE KEY uq_su_kien_code (code)
);

CREATE TABLE IF NOT EXISTS hoat_dong_su_kien (
    id CHAR(36) NOT NULL, name VARCHAR(200) NOT NULL, start_at DATETIME(6), end_at DATETIME(6),
    location_id CHAR(36), person_in_charge CHAR(36), description VARCHAR(1000), event_id CHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), created_by CHAR(36),
    updated_at DATETIME(6), version BIGINT NOT NULL DEFAULT 0, archived_at DATETIME(6),
    PRIMARY KEY (id), INDEX ix_activity_schedule (location_id, start_at, end_at),
    CONSTRAINT fk_activity_event FOREIGN KEY (event_id) REFERENCES su_kien(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ma_qr_tham_gia (
    id CHAR(36) NOT NULL, event_id CHAR(36) NOT NULL, token_hash VARCHAR(128) NOT NULL,
    check_in_url VARCHAR(512), image_storage_key VARCHAR(300), expires_at DATETIME(6),
    active BOOLEAN NOT NULL DEFAULT TRUE, created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36), updated_at DATETIME(6), version BIGINT NOT NULL DEFAULT 0, archived_at DATETIME(6),
    PRIMARY KEY (id), UNIQUE KEY uq_qr_token_hash (token_hash),
    CONSTRAINT fk_qr_event FOREIGN KEY (event_id) REFERENCES su_kien(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS luot_tham_gia_su_kien (
    id CHAR(36) NOT NULL, event_id CHAR(36) NOT NULL, customer_id CHAR(36) NOT NULL,
    qr_code_id CHAR(36), check_in_at DATETIME(6), scanning_device VARCHAR(200), reward_reference VARCHAR(200),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), created_by CHAR(36),
    PRIMARY KEY (id), UNIQUE KEY uq_event_customer_check_in (event_id, customer_id),
    CONSTRAINT fk_attendance_event FOREIGN KEY (event_id) REFERENCES su_kien(id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_qr FOREIGN KEY (qr_code_id) REFERENCES ma_qr_tham_gia(id) ON DELETE SET NULL
);
