CREATE USER monitoring WITH PASSWORD 'monitoring_password';

GRANT pg_read_all_stats to monitoring;
GRANT pg_monitor TO monitoring;

GRANT CONNECT ON DATABASE users TO monitoring;
GRANT CONNECT ON DATABASE boxes TO monitoring;
GRANT CONNECT ON DATABASE flats TO monitoring;
GRANT CONNECT ON DATABASE keycloak TO monitoring;

\c users
GRANT USAGE ON SCHEMA public TO monitoring;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO monitoring;

\c boxes
GRANT USAGE ON SCHEMA public TO monitoring;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO monitoring;

\c flats
GRANT USAGE ON SCHEMA public TO monitoring;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO monitoring;

\c keycloak
GRANT USAGE ON SCHEMA public TO monitoring;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO monitoring;
