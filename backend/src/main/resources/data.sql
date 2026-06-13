-- Seed roles
INSERT INTO roles (name) VALUES ('USER') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;

-- Seed default zero-trust policies (only if policies table is empty)
INSERT INTO policies (name, description, resource, action, required_role, min_device_trust, max_risk_threshold, enabled, created_at)
SELECT 'Allow Login', 'Permit login when risk is within acceptable bounds', 'login', 'access', NULL, NULL, 75, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE name = 'Allow Login');

INSERT INTO policies (name, description, resource, action, required_role, min_device_trust, max_risk_threshold, enabled, created_at)
SELECT 'User General Access', 'Standard user access with moderate risk tolerance', '*', 'access', 'USER,ADMIN', NULL, 80, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE name = 'User General Access');

INSERT INTO policies (name, description, resource, action, required_role, min_device_trust, max_risk_threshold, enabled, created_at)
SELECT 'Admin Access', 'Admin access with stricter risk threshold', 'admin', 'access', 'ADMIN', 50, 60, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE name = 'Admin Access');

INSERT INTO policies (name, description, resource, action, required_role, min_device_trust, max_risk_threshold, enabled, created_at)
SELECT 'High Trust Device', 'Sensitive resources require trusted devices', 'sensitive', 'access', NULL, 70, 50, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE name = 'High Trust Device');
