-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Create enum type for flat status
CREATE TYPE flat_status_enum AS ENUM (
    'available',
    'hidden',
    'unavailable'
    );

-- Flats table with validations
CREATE TABLE IF NOT EXISTS flats
(
    flat_id     UUID PRIMARY KEY          DEFAULT uuid_generate_v4(),
    owner_sub   VARCHAR(36)      NOT NULL CHECK (length(owner_sub) = 36),
    title       VARCHAR(255)     NOT NULL CHECK (char_length(title) > 3),        -- Title must be at least 4 characters
    description TEXT             NOT NULL CHECK (char_length(description) > 10), -- Description must be at least 11 len
    longitude   DOUBLE PRECISION CHECK (longitude BETWEEN -180 AND 180),         -- Valid longitude range
    latitude    DOUBLE PRECISION CHECK (latitude BETWEEN -90 AND 90),            -- Valid latitude range
    floor       INT CHECK (floor >= 0),                                          -- Floor must be non-negative
    area        DECIMAL(4, 2)    NOT NULL CHECK (area > 0),                      -- Area must be positive
    rooms       INT              NOT NULL CHECK (rooms > 0),                     -- At least one room
    status      flat_status_enum NOT NULL DEFAULT 'hidden',                      -- Default status is available
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

-- Amenities table
CREATE TABLE IF NOT EXISTS amenities
(
    amenity_id UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    name       VARCHAR(100) NOT NULL UNIQUE CHECK (char_length(name) > 2), -- Name must be at least 3 characters
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Join table for flats and amenities
CREATE TABLE IF NOT EXISTS flat_amenities
(
    flat_id    UUID NOT NULL,
    amenity_id UUID NOT NULL,
    PRIMARY KEY (flat_id, amenity_id),
    CONSTRAINT fk_flat FOREIGN KEY (flat_id) REFERENCES flats (flat_id) ON DELETE CASCADE,
    CONSTRAINT fk_amenity FOREIGN KEY (amenity_id) REFERENCES amenities (amenity_id) ON DELETE CASCADE
);


-- Create hypertable for flats
-- SELECT create_hypertable('flats', 'created_at', if_not_exists => TRUE, migrate_data => TRUE); todo

CREATE INDEX flats_location_idx ON flats USING gist (ll_to_earth(latitude, longitude));
CREATE INDEX IF NOT EXISTS idx_flats_status ON flats (status);
CREATE INDEX IF NOT EXISTS idx_flats_coordinates ON flats (longitude, latitude);
CREATE INDEX IF NOT EXISTS idx_flat_amenities_flat_id ON flat_amenities (flat_id);
CREATE INDEX IF NOT EXISTS idx_flat_amenities_amenity_id ON flat_amenities (amenity_id);