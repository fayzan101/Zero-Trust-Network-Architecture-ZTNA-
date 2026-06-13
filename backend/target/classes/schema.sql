-- Optional schema setup

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

CREATE TABLE IF NOT EXISTS policies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    required_role VARCHAR(255),
    min_device_trust INTEGER,
    max_risk_threshold INTEGER,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessions (
    id SERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    device_id VARCHAR(255),
    ip_address VARCHAR(100),
    user_risk INTEGER NOT NULL,
    device_risk INTEGER NOT NULL,
    context_risk INTEGER NOT NULL,
    final_risk INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    anomaly_detected BOOLEAN DEFAULT FALSE,
    anomaly_reason VARCHAR(500),
    started_at TIMESTAMP,
    last_activity_at TIMESTAMP,
    terminated_at TIMESTAMP,
    termination_reason VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS risk_scores (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(255),
    user_risk INTEGER NOT NULL,
    device_risk INTEGER NOT NULL,
    context_risk INTEGER NOT NULL,
    final_risk INTEGER NOT NULL,
    calculated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    user_id BIGINT,
    username VARCHAR(255),
    ip_address VARCHAR(100),
    details VARCHAR(1000),
    severity VARCHAR(20) NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS attacks (
    id SERIAL PRIMARY KEY,
    attack_type VARCHAR(100) NOT NULL,
    target_user_id BIGINT,
    target_username VARCHAR(255),
    source_ip VARCHAR(100),
    details VARCHAR(1000),
    detected BOOLEAN NOT NULL,
    detection_method VARCHAR(100),
    detection_details VARCHAR(1000),
    severity VARCHAR(20) NOT NULL,
    simulated_at TIMESTAMP
);
