spool netvertex-session.log
set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

prompt -------NetVertex SESSION ---------------------------------

prompt -------create tablespaces for netvertex session module----

prompt Enter the datafile location for tablespace of NETVERTEX SESSION - TABLE PARTITIONING ===>

DEFINE db_datafile_coresess="&&coresessions_storage_dir"
prompt &db_datafile_coresess

CREATE TABLESPACE tbs_tblcoresession1 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession1.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE tbs_tblcoresession2 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession2.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE tbs_tblcoresession3 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession3.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE tbs_tblcoresession4 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession4.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE tbs_tblcoresession5 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession5.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE tbs_tblcoresession6 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession6.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE tbs_tblcoresession7 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession7.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE tbs_tblcoresession8 DATAFILE '&&db_datafile_coresess/tbs_tblcoresession8.dbf' size 100M AUTOEXTEND ON NEXT 25M;


prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLMSESSIONRULE ===>

DEFINE db_datafile_rsess="&&sessionrule_storage_dir"
prompt &db_datafile_rsess
                                          

CREATE TABLESPACE tbs_tblsessionrule1 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule1.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_tblsessionrule2 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule2.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_tblsessionrule3 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule3.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_tblsessionrule4 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule4.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_tblsessionrule5 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule5.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_tblsessionrule6 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule6.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_tblsessionrule7 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule7.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_tblsessionrule8 DATAFILE '&&db_datafile_rsess/tbs_tblsessionrule8.dbf' size 100M AUTOEXTEND ON NEXT 25M;

prompt Enter the username of NETVERTEX ===>
connect &&username/&&password@&&NET_STR

set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;
BEGIN

IF sys_context('userenv','current_schema') IN ('SYS','SYSTEM','OUTLN','DBSNMP','SYSMAN','ORACLE_OCM',
'DIP','APPQOSSYS','FLOWS_FILES','MDSYS','ORDSYS','SCOTT','HR','EXFSYS','WMSYS','XS$NULL','ORDDATA',
'MDDATA','CTXSYS','ANONYMOUS','XDB','APEX_PUBLIC_USER','SPATIAL_CSW_ADMIN_USR','SPATIAL_WFS_ADMIN_USR',
'ORDPLUGINS','OWBSYS','SI_INFORMTN_SCHEMA','OLAPSYS') THEN

 Raise_Application_Error (-20343, 'You are connected in Oracle Schemas ,Please switch the proper netvertex schema name.');

END IF;
END;
/

--Global Section FUNC_CHECK_OBJ
create or replace FUNCTION FUNC_CHECK_OBJ(OBJ_TYPE VARCHAR,
      OBJ_NAME VARCHAR)
    RETURN NUMBER
  AS
    CNT NUMBER;
  BEGIN
    IF Upper(OBJ_TYPE)	= 'TABLE' THEN
    SELECT COUNT(*) INTO CNT FROM USER_TABLES WHERE TABLE_NAME = ''||OBJ_NAME||'';
    RETURN CNT;
    ELSIF Upper(OBJ_TYPE)	= 'INDEX' THEN
    SELECT COUNT(*) INTO CNT FROM USER_INDEXES WHERE TABLE_NAME = ''||OBJ_NAME||'';
    RETURN CNT;
	ELSIF Upper(OBJ_TYPE) = 'SEQUENCE' THEN
	SELECT COUNT(*) INTO CNT FROM USER_SEQUENCES WHERE SEQUENCE_NAME = ''||OBJ_NAME||'';
    RETURN CNT;
	ELSE
	RETURN 0;
	END IF;
  END;
  /
  
prompt --------------------Backup DBC Before Change-------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OUTPUT CLOB:= NULL;
  BEGIN
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME IN ('TBLMCORESESSIONS','TBLMSESSIONRULE') ORDER BY TABLE_NAME)
  LOOP
     FOR REC_T IN (select FUNC_CHECK_OBJ('TABLE',REC.TABLE_NAME) V_OBJ FROM DUAL)
	 LOOP
	  IF REC_T.V_OBJ > 0 THEN
	  dbms_output.put_line('-----------------DDL FOR TABLE----------------------------------');
	  select dbms_metadata.get_ddl('TABLE',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
	 END LOOP; 
	 
  FOR REC_I IN (select FUNC_CHECK_OBJ('INDEX',REC.TABLE_NAME) V_OBJ FROM DUAL)
  LOOP
      IF REC_I.V_OBJ >0 THEN
	  dbms_output.put_line('-----------------DDL FOR INDEX----------------------------------');
	  select dbms_metadata.get_dependent_ddl('INDEX',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
  END LOOP;	  
  END LOOP;	  
END;
/  

prompt --------------------DDL Change Start for TBLMCORESESSIONS-------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLMCORESESSIONS') ,
         FUNC_CHECK_OBJ('TABLE','OLD_TBLMCORESESSIONS') 
         INTO V_OBJ,V_OLD FROM DUAL;
        IF V_OLD > 0 THEN
          Raise_Application_Error(-20010,'Object OLD_TBLMCORESESSIONS is already exists, Cleanup OLD_TBLMCORESESSIONS object then execute DBC again.');
	      ELSIF V_OBJ > 0 THEN
	         execute immediate 'RENAME TBLMCORESESSIONS TO OLD_TBLMCORESESSIONS';
	         dbms_output.put_line('TBLMCORESESSIONS Renamed');
	     END IF;
 END;
 / 
 
CREATE TABLE TBLMCORESESSIONS (
    CSID                            NUMERIC(12),
    CORESESSIONID                   VARCHAR(255),
    USERIDENTITY                    VARCHAR(50),
    SESSIONID                       VARCHAR(255),
    GATEWAYADDRESS                  VARCHAR(50),
    SESSIONMANAGERID                VARCHAR(50),
    SESSIONIPV4                     VARCHAR(35),
    SESSIONIPV6                     VARCHAR(35),
    ACCESSNETWORK                   VARCHAR(20),
    GATEWAYREALM                    VARCHAR(50),
    SESSIONTYPE                     VARCHAR(20),
    MULTISESSIONID                  VARCHAR(50),
    STARTTIME                       TIMESTAMP,
    LASTUPDATETIME                  TIMESTAMP,
    TIMEZONE                        VARCHAR(35),
    QOSPROFILE   		    		VARCHAR(255),
    ADDONS  			    		VARCHAR(512),
    SUBSCRIBERIDENTITY 		    	VARCHAR(255),
    DATAPACKAGE                     VARCHAR(100),
    IMSPACKAGE                      VARCHAR(100),
    SOURCEGATEWAY 					VARCHAR(64),
    SYSESSIONID                     VARCHAR(255),
    GATEWAYNAME 					VARCHAR(40),
    SYGATEWAYNAME 					VARCHAR(40),
    CONGESTIONSTATUS  				NUMBER (2),
    IMSI                            VARCHAR(50),
    MSISDN                          VARCHAR(50),
    NAI                             VARCHAR(50),                   
    NAIREALM                        VARCHAR(50),          
    NAIUSERNAME                     VARCHAR(50),        
    SIPURL                          VARCHAR(50),
    PCCRULES                        VARCHAR(1000),
    REQUESTEDQOS                    VARCHAR(500),
    SESSIONUSAGE                    VARCHAR(150),
    REQUESTNUMBER                   VARCHAR(10),
    USAGERESERVATION                VARCHAR(1000),
	  CHARGINGRULEBASENAMES			      VARCHAR2(1000), 
	  PACKAGE_USAGE                   VARCHAR(2000),
	CALLINGSTATIONID                VARCHAR2(253),
	CALLEDSTATIONID                    VARCHAR2(253),
	QUOTARESERVATION                   VARCHAR2(2000),	
    PARAM1                          VARCHAR(255),
    PARAM2                          VARCHAR(255),
    PARAM3                          VARCHAR(255),
    PARAM4                          VARCHAR(255),
    PARAM5                          VARCHAR(255)
 )
PARTITION BY HASH(CORESESSIONID)
( PARTITION PART_tbs_tblcoresession1       TABLESPACE tbs_tblcoresession1,
  PARTITION PART_tbs_tblcoresession2       TABLESPACE tbs_tblcoresession2,
  PARTITION PART_tbs_tblcoresession3       TABLESPACE tbs_tblcoresession3,
  PARTITION PART_tbs_tblcoresession4       TABLESPACE tbs_tblcoresession4,
  PARTITION PART_tbs_tblcoresession5       TABLESPACE tbs_tblcoresession5,
  PARTITION PART_tbs_tblcoresession6       TABLESPACE tbs_tblcoresession6,
  PARTITION PART_tbs_tblcoresession7       TABLESPACE tbs_tblcoresession7,
  PARTITION PART_tbs_tblcoresession8       TABLESPACE tbs_tblcoresession8 
)NOLOGGING;

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLMCORESESSIONS') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLMCORESESSIONS SELECT * FROM OLD_TBLMCORESESSIONS';

      COMMIT;
	  dbms_output.put_line('TBLMCORESESSIONS Data Imported');

	  END IF;
 END;
 / 

CREATE INDEX NIDXUQ_CORESESS_CID ON TBLMCORESESSIONS (CORESESSIONID)
GLOBAL PARTITION BY HASH (CORESESSIONID)  
( PARTITION PART_idx_tblcoresession1       TABLESPACE tbs_tblcoresession1,
  PARTITION PART_idx_tblcoresession2       TABLESPACE tbs_tblcoresession2,
  PARTITION PART_idx_tblcoresession3       TABLESPACE tbs_tblcoresession3,
  PARTITION PART_idx_tblcoresession4       TABLESPACE tbs_tblcoresession4,
  PARTITION PART_idx_tblcoresession5       TABLESPACE tbs_tblcoresession5,  
  PARTITION PART_idx_tblcoresession6       TABLESPACE tbs_tblcoresession6,  
  PARTITION PART_idx_tblcoresession7       TABLESPACE tbs_tblcoresession7,  
  PARTITION PART_idx_tblcoresession8       TABLESPACE tbs_tblcoresession8  
) ONLINE NOLOGGING;


CREATE INDEX NIDXUQ_CORESESS_SID ON TBLMCORESESSIONS (SUBSCRIBERIDENTITY)
GLOBAL PARTITION BY HASH (SUBSCRIBERIDENTITY)  
( PARTITION PART_idx_tblcoresession1       TABLESPACE tbs_tblcoresession1,
  PARTITION PART_idx_tblcoresession2       TABLESPACE tbs_tblcoresession2,
  PARTITION PART_idx_tblcoresession3       TABLESPACE tbs_tblcoresession3,
  PARTITION PART_idx_tblcoresession4       TABLESPACE tbs_tblcoresession4,
  PARTITION PART_idx_tblcoresession5       TABLESPACE tbs_tblcoresession5,  
  PARTITION PART_idx_tblcoresession6       TABLESPACE tbs_tblcoresession6,  
  PARTITION PART_idx_tblcoresession7       TABLESPACE tbs_tblcoresession7,  
  PARTITION PART_idx_tblcoresession8       TABLESPACE tbs_tblcoresession8  
)NOLOGGING;

--DROP INDEX NIDXUQ_CORESESS_SID;
--DROP INDEX NIDXUQ_CORESESS_IPTYPE;
--CREATE INDEX NIDXUQ_CORESESS_IPTYPE ON TBLMCORESESSIONS (SESSIONIP,SESSIONTYPE)
--GLOBAL PARTITION BY HASH (SESSIONIP,SESSIONTYPE)  
--( PARTITION PART_idx_tblcoresession1       TABLESPACE tbs_tblcoresession1,
--  PARTITION PART_idx_tblcoresession2       TABLESPACE tbs_tblcoresession2,
--  PARTITION PART_idx_tblcoresession3       TABLESPACE tbs_tblcoresession3,
--  PARTITION PART_idx_tblcoresession4       TABLESPACE tbs_tblcoresession4,
--  PARTITION PART_idx_tblcoresession5       TABLESPACE tbs_tblcoresession5,  
--  PARTITION PART_idx_tblcoresession6       TABLESPACE tbs_tblcoresession6,  
--  PARTITION PART_idx_tblcoresession7       TABLESPACE tbs_tblcoresession7,  
--  PARTITION PART_idx_tblcoresession8       TABLESPACE tbs_tblcoresession8  
--)NOLOGGING;

CREATE INDEX NIDXUQ_CORESESS_IP ON TBLMCORESESSIONS (SESSIONIPV4)
GLOBAL PARTITION BY HASH (SESSIONIPV4)  
( PARTITION PART_idx_tblcoresession1       TABLESPACE tbs_tblcoresession1,
  PARTITION PART_idx_tblcoresession2       TABLESPACE tbs_tblcoresession2,
  PARTITION PART_idx_tblcoresession3       TABLESPACE tbs_tblcoresession3,
  PARTITION PART_idx_tblcoresession4       TABLESPACE tbs_tblcoresession4,
  PARTITION PART_idx_tblcoresession5       TABLESPACE tbs_tblcoresession5,  
  PARTITION PART_idx_tblcoresession6       TABLESPACE tbs_tblcoresession6,  
  PARTITION PART_idx_tblcoresession7       TABLESPACE tbs_tblcoresession7,  
  PARTITION PART_idx_tblcoresession8       TABLESPACE tbs_tblcoresession8  
)NOLOGGING;

CREATE INDEX NIDXUQ_CORESESS_IPV6 ON TBLMCORESESSIONS (SESSIONIPV6)
GLOBAL PARTITION BY HASH (SESSIONIPV6)  
( PARTITION PART_idx_tblcoresession1       TABLESPACE tbs_tblcoresession1,
  PARTITION PART_idx_tblcoresession2       TABLESPACE tbs_tblcoresession2,
  PARTITION PART_idx_tblcoresession3       TABLESPACE tbs_tblcoresession3,
  PARTITION PART_idx_tblcoresession4       TABLESPACE tbs_tblcoresession4,
  PARTITION PART_idx_tblcoresession5       TABLESPACE tbs_tblcoresession5,  
  PARTITION PART_idx_tblcoresession6       TABLESPACE tbs_tblcoresession6,  
  PARTITION PART_idx_tblcoresession7       TABLESPACE tbs_tblcoresession7,  
  PARTITION PART_idx_tblcoresession8       TABLESPACE tbs_tblcoresession8  
)NOLOGGING;

CREATE INDEX NIDX_SY_SESSIONID ON TBLMCORESESSIONS (SYSESSIONID)
GLOBAL PARTITION BY HASH (SYSESSIONID)  
( PARTITION PART_idx_tblcoresession1       TABLESPACE tbs_tblcoresession1,
  PARTITION PART_idx_tblcoresession2       TABLESPACE tbs_tblcoresession2,
  PARTITION PART_idx_tblcoresession3       TABLESPACE tbs_tblcoresession3,
  PARTITION PART_idx_tblcoresession4       TABLESPACE tbs_tblcoresession4,
  PARTITION PART_idx_tblcoresession5       TABLESPACE tbs_tblcoresession5,  
  PARTITION PART_idx_tblcoresession6       TABLESPACE tbs_tblcoresession6,  
  PARTITION PART_idx_tblcoresession7       TABLESPACE tbs_tblcoresession7,  
  PARTITION PART_idx_tblcoresession8       TABLESPACE tbs_tblcoresession8  
)NOLOGGING;

ALTER TABLE TBLMCORESESSIONS INITRANS 169;

ALTER INDEX NIDXUQ_CORESESS_SID INITRANS 169;
ALTER INDEX NIDXUQ_CORESESS_CID INITRANS 169;
ALTER INDEX NIDXUQ_CORESESS_IP INITRANS 169;
ALTER INDEX NIDX_SY_SESSIONID INITRANS 169;
ALTER INDEX NIDXUQ_CORESESS_IPV6 INITRANS 169;   

DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('SEQUENCE','SEQ_MCORESESSIONS') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  execute immediate 'ALTER SEQUENCE SEQ_MCORESESSIONS CACHE 4000';
	  dbms_output.put_line('SEQUENCE Altered');
	  ELSE
	  execute immediate 'CREATE SEQUENCE SEQ_MCORESESSIONS INCREMENT BY 1 START WITH 101 NOMAXVALUE NOMINVALUE NOCYCLE CACHE 4000 NOORDER';
	  dbms_output.put_line('SEQUENCE Created');

	  END IF;
 END;
 /

prompt --------------------DDL Change End for TBLMCORESESSIONS-------------------------

prompt --------------------DDL Change Start for TBLMSESSIONRULE------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLMSESSIONRULE'), 
         FUNC_CHECK_OBJ('TABLE','OLD_TBLMSESSIONRULE') INTO V_OBJ,V_OLD
        FROM DUAL;
          IF V_OLD > 0 THEN
            Raise_Application_Error(-20010,'Object OLD_TBLMSESSIONRULE IS already exists,Drop OLD_TBLMSESSIONRULE object then execute DBC again.');
          ELSIF V_OBJ > 0 THEN
	           execute immediate 'RENAME TBLMSESSIONRULE TO OLD_TBLMSESSIONRULE';
	           dbms_output.put_line('TBLMSESSIONRULE Renamed');
    	   END IF;
END;
 /

CREATE TABLE TBLMSESSIONRULE 
(	SRID                        NUMERIC(12,0),
	SESSIONID                   VARCHAR(255),
	GATEWAYADDRESS 				VARCHAR(64),
	AFSESSIONID             	VARCHAR(255),
  	MEDIATYPE                   VARCHAR(36),
	PCCRULE                     VARCHAR(255),
	MEDIACOMPONENTNUMBER        VARCHAR(9),
	FLOWNUMBER        			VARCHAR(9),
	UPLINKFLOW					VARCHAR(255),
	DOWNLINKFLOW				VARCHAR(255),
	STARTTIME                   TIMESTAMP,
  	LASTUPDATETIME              TIMESTAMP,
	ADDITIONAL_PARAMETER        VARCHAR(4000)
)
PARTITION BY HASH(SESSIONID)                                        
( PARTITION PART_tbs_tblsessionrule1       TABLESPACE tbs_tblsessionrule1,  
  PARTITION PART_tbs_tblsessionrule2       TABLESPACE tbs_tblsessionrule2,  
  PARTITION PART_tbs_tblsessionrule3       TABLESPACE tbs_tblsessionrule3,  
  PARTITION PART_tbs_tblsessionrule4       TABLESPACE tbs_tblsessionrule4,   
  PARTITION PART_tbs_tblsessionrule5       TABLESPACE tbs_tblsessionrule5,    
  PARTITION PART_tbs_tblsessionrule6       TABLESPACE tbs_tblsessionrule6,    
  PARTITION PART_tbs_tblsessionrule7       TABLESPACE tbs_tblsessionrule7,    
  PARTITION PART_tbs_tblsessionrule8       TABLESPACE tbs_tblsessionrule8     
)NOLOGGING;                                                                       

DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLMSESSIONRULE') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
		execute immediate 'INSERT INTO TBLMSESSIONRULE SELECT * FROM OLD_TBLMSESSIONRULE';

		COMMIT;
	  dbms_output.put_line('TBLMSESSIONRULE Data Imported');

	  END IF;
 END;
 /
 

CREATE INDEX NIDX_SESSRULE_CID ON TBLMSESSIONRULE (SESSIONID)
GLOBAL PARTITION BY HASH (SESSIONID)  
( PARTITION PART_IDX_tblsessionrule1       TABLESPACE tbs_tblsessionrule1,
  PARTITION PART_IDX_tblsessionrule2       TABLESPACE tbs_tblsessionrule2,
  PARTITION PART_IDX_tblsessionrule3       TABLESPACE tbs_tblsessionrule3,
  PARTITION PART_IDX_tblsessionrule4       TABLESPACE tbs_tblsessionrule4,
  PARTITION PART_IDX_tblsessionrule5       TABLESPACE tbs_tblsessionrule5, 
  PARTITION PART_IDX_tblsessionrule6       TABLESPACE tbs_tblsessionrule6,  
  PARTITION PART_IDX_tblsessionrule7       TABLESPACE tbs_tblsessionrule7,  
  PARTITION PART_IDX_tblsessionrule8       TABLESPACE tbs_tblsessionrule8  
)NOLOGGING;

CREATE INDEX NIDX_SESSRULE_RULE ON TBLMSESSIONRULE (SESSIONID,PCCRULE)
GLOBAL PARTITION BY HASH (SESSIONID,PCCRULE)
( PARTITION PART_IDX_tblsessionrule1       TABLESPACE tbs_tblsessionrule1,
  PARTITION PART_IDX_tblsessionrule2       TABLESPACE tbs_tblsessionrule2,
  PARTITION PART_IDX_tblsessionrule3       TABLESPACE tbs_tblsessionrule3,
  PARTITION PART_IDX_tblsessionrule4       TABLESPACE tbs_tblsessionrule4,
  PARTITION PART_IDX_tblsessionrule5       TABLESPACE tbs_tblsessionrule5, 
  PARTITION PART_IDX_tblsessionrule6       TABLESPACE tbs_tblsessionrule6,  
  PARTITION PART_IDX_tblsessionrule7       TABLESPACE tbs_tblsessionrule7,  
  PARTITION PART_IDX_tblsessionrule8       TABLESPACE tbs_tblsessionrule8  
)NOLOGGING;

ALTER INDEX NIDX_SESSRULE_RULE INITRANS 169;

ALTER INDEX NIDX_SESSRULE_CID INITRANS 169;

ALTER TABLE TBLMSESSIONRULE INITRANS 169;

DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('SEQUENCE','SEQ_MSESSIONRULE') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  execute immediate 'ALTER SEQUENCE SEQ_MSESSIONRULE CACHE 4000';
	  dbms_output.put_line('SEQUENCE Altered');
	  ELSE
	  execute immediate 'CREATE SEQUENCE SEQ_MSESSIONRULE INCREMENT BY 1 START WITH 101 NOMAXVALUE NOMINVALUE NOCYCLE CACHE 4000 NOORDER';
	  dbms_output.put_line('SEQUENCE Created');

	  END IF;
 END;
 /

prompt --------------------DDL Change End for TBLMSESSIONRULE--------------------------

prompt --------------------Backup DBC After Change-------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OUTPUT CLOB:= NULL;
  BEGIN
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME IN ('TBLMCORESESSIONS','TBLMSESSIONRULE') ORDER BY TABLE_NAME)
  LOOP
     FOR REC_T IN (select FUNC_CHECK_OBJ('TABLE',REC.TABLE_NAME) V_OBJ FROM DUAL)
	 LOOP
	  IF REC_T.V_OBJ > 0 THEN
	  dbms_output.put_line('-----------------DDL FOR TABLE----------------------------------');
	  select dbms_metadata.get_ddl('TABLE',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
	 END LOOP; 
	 
  FOR REC_I IN (select FUNC_CHECK_OBJ('INDEX',REC.TABLE_NAME) V_OBJ FROM DUAL)
  LOOP
      IF REC_I.V_OBJ >0 THEN
	  dbms_output.put_line('-----------------DDL FOR INDEX----------------------------------');
	  select dbms_metadata.get_dependent_ddl('INDEX',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
  END LOOP;	  
  END LOOP;	  
END;
/
  
spool off;
exit;
