CREATE USER user_user WITH PASSWORD 'user_password';
CREATE USER box_user WITH PASSWORD 'box_password';
CREATE USER flat_user WITH PASSWORD 'flat_password';
CREATE USER keycloak WITH ENCRYPTED PASSWORD 'keycloak_password';
CREATE USER file_user WITH PASSWORD 'file_password';

GRANT ALL PRIVILEGES ON DATABASE users TO user_user;
GRANT ALL PRIVILEGES ON DATABASE boxes TO box_user;
GRANT ALL PRIVILEGES ON DATABASE flats TO flat_user;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
GRANT ALL PRIVILEGES ON DATABASE files TO file_user;

\connect keycloak

GRANT USAGE, CREATE ON SCHEMA public TO keycloak;

CREATE OR REPLACE VIEW public.user_entity_ids AS
SELECT NULL::character varying(36) AS id
WHERE false;

GRANT CONNECT ON DATABASE keycloak TO flat_user;
GRANT USAGE ON SCHEMA public TO flat_user;
GRANT SELECT ON public.user_entity_ids TO flat_user;

\connect users

GRANT CONNECT ON DATABASE boxes TO user_user;
GRANT CONNECT ON DATABASE flats TO user_user;

GRANT USAGE ON SCHEMA public TO user_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO user_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO user_user;
ALTER ROLE user_user SET search_path TO users;

\connect boxes

GRANT CONNECT ON DATABASE users TO box_user;
GRANT CONNECT ON DATABASE flats TO box_user;

GRANT USAGE ON SCHEMA public TO box_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO box_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO box_user;
ALTER ROLE box_user SET search_path TO boxes;

\connect files

GRANT USAGE ON SCHEMA public TO file_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO file_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO file_user;
ALTER ROLE file_user SET search_path TO files;

\connect flats

GRANT CONNECT ON DATABASE users TO flat_user;
GRANT CONNECT ON DATABASE boxes TO flat_user;

GRANT USAGE ON SCHEMA public TO flat_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO flat_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO flat_user;
ALTER ROLE flat_user SET search_path TO public, flats;
