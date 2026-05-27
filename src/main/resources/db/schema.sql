CREATE DATABASE IF NOT EXISTS lokki_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lokki_db;

CREATE TABLE IF NOT EXISTS master_config (
    id                              INT PRIMARY KEY AUTO_INCREMENT,
    password_hash                   VARCHAR(512) NOT NULL,
    salt_master                     VARCHAR(128) NOT NULL,
    salt_recovery                   VARCHAR(128) NOT NULL,
    encrypted_vault_key_by_master   TEXT NOT NULL,
    encrypted_vault_key_by_recovery TEXT NOT NULL,
    created_at                      DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT IGNORE INTO categories (id, name) VALUES (1, 'General'), (2, 'Social'), (3, 'Work'), (4, 'Finance'), (5, 'Developer');

CREATE TABLE IF NOT EXISTS credentials (
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    site_name          VARCHAR(255) NOT NULL,
    site_url           VARCHAR(500),
    username           VARCHAR(255),
    encrypted_password TEXT NOT NULL,
    category_id        INT DEFAULT 1,
    notes              TEXT,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET DEFAULT
);

CREATE INDEX IF NOT EXISTS idx_credentials_site_name ON credentials(site_name);
CREATE INDEX IF NOT EXISTS idx_credentials_category  ON credentials(category_id);
