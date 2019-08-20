COPY (select 1) TO PROGRAM 'mkdir --mode=777 -p /opt/postgres/netvertex';

CREATE ROLE "netvertex" LOGIN PASSWORD 'netvertex' INHERIT CREATEDB CREATEROLE;

CREATE TABLESPACE netvertex OWNER "netvertex" LOCATION '/opt/postgres/netvertex';

CREATE SCHEMA "netvertex"  AUTHORIZATION "netvertex";
GRANT ALL ON SCHEMA "netvertex" TO "netvertex";
REVOKE ALL ON SCHEMA public FROM public;

ALTER TABLESPACE netvertex OWNER TO "netvertex";

ALTER USER "netvertex" set default_tablespace = "netvertex";
ALTER USER "netvertex" set search_path = "netvertex";

GRANT ALL ON SCHEMA pgagent TO "netvertex";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA pgagent TO "netvertex";
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA pgagent TO "netvertex";
grant select on pg_authid to "netvertex";

SET role "netvertex";