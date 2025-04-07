CREATE EXTENSION IF NOT EXISTS pg_cron;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE OR REPLACE FUNCTION generate_unique_code()
    RETURNS INTEGER AS
$$
DECLARE
    unique_code INTEGER;
BEGIN
    LOOP
        unique_code := FLOOR(100000 + RANDOM() * 900000)::INTEGER;
        IF NOT EXISTS (SELECT 1 FROM box_signup WHERE code = unique_code) THEN
            RETURN unique_code;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE boxes
(
    mac_address VARCHAR(17) PRIMARY KEY,
    owner_sub   VARCHAR(255) NULL,
    alias       VARCHAR(100),
    address     VARCHAR(255) NOT NULL,

    CHECK (mac_address ~ '^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$')
);

CREATE INDEX idx_boxes_owner_sub ON boxes (owner_sub);

CREATE TABLE box_signup
(
    id         BIGSERIAL PRIMARY KEY,
    code       INTEGER   DEFAULT generate_unique_code() UNIQUE CHECK (code >= 100000 AND code <= 999999),
    issuer_sub VARCHAR(255)                        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE OR REPLACE FUNCTION prevent_code_modification()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.code IS DISTINCT FROM OLD.code THEN
        RAISE EXCEPTION 'Modification of the code field is not allowed';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_code_modification
    BEFORE UPDATE
    ON box_signup
    FOR EACH ROW
EXECUTE FUNCTION prevent_code_modification();

CREATE INDEX idx_box_signup_issuer_sub ON box_signup (issuer_sub);
CREATE INDEX idx_box_signup_created_at ON box_signup (created_at);

CREATE TABLE box_statuses
(
    id              BIGSERIAL PRIMARY KEY,
    mac_address     VARCHAR(17)                         NOT NULL REFERENCES boxes (mac_address) ON DELETE CASCADE,
    battery_level   DOUBLE PRECISION                    NOT NULL CHECK (battery_level >= 0 AND battery_level <= 100),
    signal_strength DOUBLE PRECISION                    NOT NULL CHECK (signal_strength >= -100 AND signal_strength <= 0),
    opened          BOOLEAN                             NOT NULL,
    timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,


    CHECK (mac_address ~ '^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$')
);

CREATE INDEX idx_box_statuses_mac_address ON box_statuses (mac_address);
CREATE INDEX idx_box_statuses_timestamp ON box_statuses (timestamp);

CREATE TABLE box_accesses
(
    uuid        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mac_address VARCHAR(17)                                                       NOT NULL REFERENCES boxes (mac_address) ON DELETE CASCADE,
    token       TEXT                                                              NOT NULL UNIQUE CHECK (length(token) >= 64),
    start_time  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP                        NOT NULL,
    end_time    TIMESTAMP                                                         NOT NULL,
    expires_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP + INTERVAL '5 minutes' NOT NULL,


    CHECK (mac_address ~ '^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$'),

    CHECK (end_time > start_time)
);

CREATE INDEX idx_box_accesses_mac_address ON box_accesses (mac_address);
CREATE INDEX idx_box_accesses_expires_at ON box_accesses (expires_at);

CREATE TABLE box_updates
(
    id           BIGSERIAL PRIMARY KEY,
    mac_address  VARCHAR(17)                         NOT NULL REFERENCES boxes (mac_address) ON DELETE CASCADE,
    call         VARCHAR(50)                         NOT NULL,
    timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    issuer_sub   VARCHAR(255),
    issuer_token UUID,


    CHECK (mac_address ~ '^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$')
);

CREATE INDEX idx_box_updates_mac_address ON box_updates (mac_address);
CREATE INDEX idx_box_updates_timestamp ON box_updates (timestamp);
CREATE INDEX idx_box_updates_issuer_token ON box_updates (issuer_token);

CREATE OR REPLACE VIEW view_boxes_statuses AS
SELECT b.mac_address,
       b.owner_sub,
       b.alias,
       b.address,
       bs.battery_level,
       bs.signal_strength,
       bs.opened,
       bs.timestamp AS last_status_timestamp
FROM boxes b
         LEFT JOIN (SELECT DISTINCT ON (mac_address) *
                    FROM box_statuses
                    ORDER BY mac_address, timestamp DESC) bs ON b.mac_address = bs.mac_address;

SELECT cron.schedule('*/5 * * * *', $$DELETE FROM box_accesses WHERE expires_at < NOW()$$);
SELECT cron.schedule('*/1 * * * *', $$DELETE FROM box_signup WHERE created_at < NOW() - INTERVAL '15 minutes'$$);