CREATE TABLESPACE netvertex DATAFILE '$ORACLE_BASE/oradata/$ORACLE_SID/netvertex.dbf' size 100M AUTOEXTEND ON;

CREATE USER netvertex IDENTIFIED BY netvertex DEFAULT TABLESPACE netvertex TEMPORARY TABLESPACE temp QUOTA UNLIMITED ON netvertex;

GRANT CONNECT,RESOURCE, CREATE ANY VIEW TO netvertex;

--12c Compatibility

GRANT UNLIMITED TABLESPACE TO netvertex;
GRANT CREATE JOB TO netvertex;
grant select_catalog_role to netvertex;