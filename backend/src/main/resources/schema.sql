-- Optional schema setup

-- Device table for device trust
CREATE TABLE IF NOT EXISTS devices (
    id SERIAL PRIMARY KEY,
    device_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    device_type VARCHAR(100) NOT NULL,
    os VARCHAR(100) NOT NULL,
    ip_address VARCHAR(100) NOT NULL,
    trust_score INTEGER NOT NULL,
    registered_at TIMESTAMP NOT NULL
);
