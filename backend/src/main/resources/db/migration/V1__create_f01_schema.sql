CREATE TABLE toa_nha (
    id CHAR(36) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_toa_nha_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tang (
    id CHAR(36) NOT NULL,
    building_id CHAR(36) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    level INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_tang_code UNIQUE (building_id, code),
    CONSTRAINT fk_tang_building FOREIGN KEY (building_id) REFERENCES toa_nha(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE khu_vuc (
    id CHAR(36) NOT NULL,
    floor_id CHAR(36) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_khu_vuc_code UNIQUE (floor_id, code),
    CONSTRAINT fk_khu_vuc_floor FOREIGN KEY (floor_id) REFERENCES tang(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE vi_tri (
    id CHAR(36) NOT NULL,
    zone_id CHAR(36) NOT NULL,
    x DECIMAL(14,4),
    y DECIMAL(14,4),
    text_description VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_vi_tri_zone FOREIGN KEY (zone_id) REFERENCES khu_vuc(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mat_bang (
    id CHAR(36) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    zone_id CHAR(36) NOT NULL,
    total_area DECIMAL(14,2) NOT NULL,
    type VARCHAR(40),
    status VARCHAR(40) NOT NULL,
    location_id CHAR(36),
    description VARCHAR(1000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_mat_bang_code UNIQUE (code),
    CONSTRAINT ck_mat_bang_area CHECK (total_area > 0),
    CONSTRAINT fk_mat_bang_zone FOREIGN KEY (zone_id) REFERENCES khu_vuc(id),
    CONSTRAINT fk_mat_bang_location FOREIGN KEY (location_id) REFERENCES vi_tri(id),
    INDEX ix_mat_bang_filter (zone_id, status, archived_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE nganh_hang (
    id CHAR(36) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_nganh_hang_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cua_hang (
    id CHAR(36) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    premise_id CHAR(36) NOT NULL,
    area DECIMAL(14,2) NOT NULL,
    category_id CHAR(36),
    location_id CHAR(36),
    status VARCHAR(40) NOT NULL,
    operating_hours VARCHAR(200),
    description VARCHAR(1000),
    public_visible BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_cua_hang_code UNIQUE (code),
    CONSTRAINT ck_cua_hang_area CHECK (area > 0),
    CONSTRAINT fk_cua_hang_premise FOREIGN KEY (premise_id) REFERENCES mat_bang(id),
    CONSTRAINT fk_cua_hang_category FOREIGN KEY (category_id) REFERENCES nganh_hang(id),
    CONSTRAINT fk_cua_hang_location FOREIGN KEY (location_id) REFERENCES vi_tri(id),
    INDEX ix_cua_hang_filter (premise_id, status, archived_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lich_su_mat_bang_cua_hang (
    id CHAR(36) NOT NULL,
    target_type VARCHAR(40) NOT NULL,
    target_id CHAR(36) NOT NULL,
    action VARCHAR(100) NOT NULL,
    old_value_json JSON,
    new_value_json JSON,
    changed_by CHAR(36),
    reason VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by CHAR(36),
    PRIMARY KEY (id),
    INDEX ix_ls_mb_target (target_type, target_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

