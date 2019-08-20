COPY (select 1) TO PROGRAM 'mkdir --mode=777 -p /opt/postgres/eliteaaa';

CREATE ROLE "eliteaaa" LOGIN PASSWORD 'eliteaaa' INHERIT CREATEDB CREATEROLE;

CREATE TABLESPACE eliteaaa OWNER "eliteaaa" LOCATION '/opt/postgres/eliteaaa';

CREATE SCHEMA "eliteaaa"  AUTHORIZATION "eliteaaa";
GRANT ALL ON SCHEMA "eliteaaa" TO "eliteaaa";
REVOKE ALL ON SCHEMA public FROM public;

ALTER TABLESPACE eliteaaa OWNER TO "eliteaaa";

ALTER USER "eliteaaa" set default_tablespace = "eliteaaa";
ALTER USER "eliteaaa" set search_path = "eliteaaa";

GRANT ALL ON SCHEMA pgagent TO "eliteaaa";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA pgagent TO "eliteaaa";
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA pgagent TO "eliteaaa";

SET role "eliteaaa";

