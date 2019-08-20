spool netvertex-session.log
set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

prompt -------NetVertex SESSION ---------------------------------

prompt -------create tablespaces for netvertex session module----

prompt Enter the datafile location for tablespace of NETVERTEX SESSION - TABLE PARTITIONING ===>

DEFINE db_datafile_session="&&tblt_sessions_storage_dir"
prompt &db_datafile_session

CREATE TABLESPACE ts_tblt_session1 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session1.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_tblt_session2 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session2.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_tblt_session3 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session3.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_tblt_session4 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session4.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_tblt_session5 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session5.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_tblt_session6 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session6.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_tblt_session7 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session7.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_tblt_session8 DATAFILE '&&tblt_sessions_storage_dir/tbs_tblt_session8.dbf' size 100M AUTOEXTEND ON NEXT 25M;


CREATE TABLESPACE ts_idx_session1 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session1.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_idx_session2 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session2.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                               
CREATE TABLESPACE ts_idx_session3 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session3.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_idx_session4 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session4.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_idx_session5 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session5.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_idx_session6 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session6.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_idx_session7 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session7.dbf' size 100M AUTOEXTEND ON NEXT 25M;                                                     
CREATE TABLESPACE ts_idx_session8 DATAFILE '&&tblt_sessions_storage_dir/tbs_idx_session8.dbf' size 100M AUTOEXTEND ON NEXT 25M;


prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLT_SUB_SESSION ===>

DEFINE db_datafile_rsess="&&tblsubsession_storage_dir"
prompt &db_datafile_rsess
                                          

CREATE TABLESPACE ts_tbltsubsession1 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession1.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_tbltsubsession2 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession2.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_tbltsubsession3 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession3.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_tbltsubsession4 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession4.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_tbltsubsession5 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession5.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_tbltsubsession6 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession6.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_tbltsubsession7 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession7.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_tbltsubsession8 DATAFILE '&&tblsubsession_storage_dir/tbs_tbltsubsession8.dbf' size 100M AUTOEXTEND ON NEXT 25M;

CREATE TABLESPACE ts_idxsubsession2 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession2.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_idxsubsession1 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession1.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_idxsubsession3 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession3.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_idxsubsession4 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession4.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_idxsubsession5 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession5.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_idxsubsession6 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession6.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_idxsubsession7 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession7.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE ts_idxsubsession8 DATAFILE '&&tblsubsession_storage_dir/tbs_idxsubsession8.dbf' size 100M AUTOEXTEND ON NEXT 25M;

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
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME IN ('TBLT_SESSION','TBLT_SUB_SESSION') ORDER BY TABLE_NAME)
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

prompt --------------------DDL Change Start for TBLT_SESSION-------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLT_SESSION') ,
         FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SESSION') 
         INTO V_OBJ,V_OLD FROM DUAL;
        IF V_OLD > 0 THEN
          Raise_Application_Error(-20010,'Object OLD_TBLT_SESSION is already exists, Cleanup OLD_TBLT_SESSION object then execute DBC again.');
	      ELSIF V_OBJ > 0 THEN
	         execute immediate 'RENAME TBLT_SESSION TO OLD_TBLT_SESSION';
	         dbms_output.put_line('TBLT_SESSION Renamed');
	     END IF;
 END;
 / 
 
CREATE TABLE TBLT_SESSION (
     "CS_ID" VARCHAR2(36),
        "CORE_SESSION_ID" VARCHAR2(255),
        "SESSION_ID" VARCHAR2(255),
        "GATEWAY_ADDRESS" VARCHAR2(50),
        "SESSION_MANAGER_ID" VARCHAR2(50),
        "SESSION_IP_V4" VARCHAR2(35),
        "SESSION_IP_V6" VARCHAR2(35),
        "ACCESS_NETWORK" VARCHAR2(20),
        "GATEWAY_REALM" VARCHAR2(50),
        "SESSION_TYPE" VARCHAR2(20),
        "START_TIME" TIMESTAMP (6),
        "LAST_UPDATE_TIME" TIMESTAMP (6),
        "TIME_ZONE" VARCHAR2(35),
        "QOS_PROFILE" VARCHAR2(255),
        "SUBSCRIBER_IDENTITY" VARCHAR2(255),
        "SOURCE_GATEWAY" VARCHAR2(64),
        "SY_SESSION_ID" VARCHAR2(255),
        "GATEWAY_NAME" VARCHAR2(40),
        "SY_GATEWAY_NAME" VARCHAR2(40),
        "CONGESTION_STATUS" NUMBER(2,0),
        "IMSI" VARCHAR2(50),
        "MSISDN" VARCHAR2(50),
        "NAI" VARCHAR2(50),
        "NAI_REALM" VARCHAR2(50),
        "NAI_USER_NAME" VARCHAR2(50),
        "SIP_URL" VARCHAR2(50),
        "PCC_RULES" VARCHAR2(1000),
        "REQUESTED_QOS" VARCHAR2(500),
        "SESSION_USAGE" VARCHAR2(150),
        "REQUEST_NUMBER" VARCHAR2(10),
        "USAGE_RESERVATION" VARCHAR2(1000),
        "CHARGING_RULE_BASE_NAMES" VARCHAR2(1000),
        "PACKAGE_USAGE" VARCHAR2(2000),
        "CALLING_STATION_ID" VARCHAR2(253),
        "CALLED_STATION_ID" VARCHAR2(253),
        "QUOTA_RESERVATION" VARCHAR2(2000),
        "PCC_PROFILE_SELECTION_STATE" VARCHAR2(1000),
        "UNACCOUNTED_QUOTA" VARCHAR2(2000),
        "PARAM1" VARCHAR2(255),
        "PARAM2" VARCHAR2(255),
        "PARAM3" VARCHAR2(255),
        "PARAM4" VARCHAR2(255),
        "PARAM5" VARCHAR2(255),
        "SGSN_MCC_MNC" VARCHAR2(6),
        "LOCATION" VARCHAR2(2000),
        "SERVICE" VARCHAR2(100)
)
PARTITION BY HASH(CORE_SESSION_ID)
( PARTITION PART_tbs_tblt_session1       TABLESPACE ts_tblt_session1,
  PARTITION PART_tbs_tblt_session2       TABLESPACE ts_tblt_session2,
  PARTITION PART_tbs_tblt_session3       TABLESPACE ts_tblt_session3,
  PARTITION PART_tbs_tblt_session4       TABLESPACE ts_tblt_session4,
  PARTITION PART_tbs_tblt_session5       TABLESPACE ts_tblt_session5,
  PARTITION PART_tbs_tblt_session6       TABLESPACE ts_tblt_session6,
  PARTITION PART_tbs_tblt_session7       TABLESPACE ts_tblt_session7,
  PARTITION PART_tbs_tblt_session8       TABLESPACE ts_tblt_session8 
)NOLOGGING;

COMMENT ON COLUMN TBLT_SESSION.CALLING_STATION_ID IS 'This column stores caller party address that can be MAC Address or  Access Point Name value or MSISDN with country code.';
COMMENT ON COLUMN TBLT_SESSION.CALLED_STATION_ID IS  'This column stores called party address that can be MAC Address or  Access Point Name value or MSISDN with country code.';
COMMENT ON COLUMN TBLT_SESSION.QUOTA_RESERVATION IS 'This column stores key value mapping of quota reserved for a current session. This value contains Subscription, Service, Rating Group wise service units reserved';
COMMENT ON COLUMN TBLT_SESSION.PCC_PROFILE_SELECTION_STATE IS 'STORES PER PACKAGE SELECTED PCC PROFILE DURING POLICY HUNTING';
COMMENT ON COLUMN TBLT_SESSION.UNACCOUNTED_QUOTA IS 'This column stores key value mapping of unaccounted quota for a current session';

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SESSION') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLT_SESSION SELECT * FROM OLD_TBLT_SESSION';

      COMMIT;
	  dbms_output.put_line('TBLT_SESSION Data Imported');

	  END IF;
 END;
 / 

CREATE INDEX NIDXUQ_SESSION_CID ON TBLT_SESSION (CORE_SESSION_ID)
GLOBAL PARTITION BY HASH (CORE_SESSION_ID)  
( PARTITION PART_idx_tblsession1       TABLESPACE ts_idx_session1,
  PARTITION PART_idx_tblsession2       TABLESPACE ts_idx_session2,
  PARTITION PART_idx_tblsession3       TABLESPACE ts_idx_session3,
  PARTITION PART_idx_tblsession4       TABLESPACE ts_idx_session4,
  PARTITION PART_idx_tblsession5       TABLESPACE ts_idx_session5,  
  PARTITION PART_idx_tblsession6       TABLESPACE ts_idx_session6,  
  PARTITION PART_idx_tblsession7       TABLESPACE ts_idx_session7,  
  PARTITION PART_idx_tblsession8       TABLESPACE ts_idx_session8  
) ONLINE NOLOGGING;


CREATE INDEX NIDXUQ_SESSION_SID ON TBLT_SESSION (SUBSCRIBER_IDENTITY)
GLOBAL PARTITION BY HASH (SUBSCRIBER_IDENTITY)  
( PARTITION PART_idx_tblsession1       TABLESPACE ts_idx_session1,
  PARTITION PART_idx_tblsession2       TABLESPACE ts_idx_session2,
  PARTITION PART_idx_tblsession3       TABLESPACE ts_idx_session3,
  PARTITION PART_idx_tblsession4       TABLESPACE ts_idx_session4,
  PARTITION PART_idx_tblsession5       TABLESPACE ts_idx_session5,  
  PARTITION PART_idx_tblsession6       TABLESPACE ts_idx_session6,  
  PARTITION PART_idx_tblsession7       TABLESPACE ts_idx_session7,  
  PARTITION PART_idx_tblsession8       TABLESPACE ts_idx_session8  
)NOLOGGING;

--DROP INDEX NIDXUQ_SESSION_SID;
--DROP INDEX NIDXUQ_SESSION_IPTYPE;
--CREATE INDEX NIDXUQ_SESSION_IPTYPE ON TBLT_SESSION (SESSIONIP,SESSIONTYPE)
--GLOBAL PARTITION BY HASH (SESSIONIP,SESSIONTYPE)  
--( PARTITION PART_idx_tblsession1       TABLESPACE tbs_tblt_session1,
--  PARTITION PART_idx_tblsession2       TABLESPACE tbs_tblt_session2,
--  PARTITION PART_idx_tblsession3       TABLESPACE tbs_tblt_session3,
--  PARTITION PART_idx_tblsession4       TABLESPACE tbs_tblt_session4,
--  PARTITION PART_idx_tblsession5       TABLESPACE tbs_tblt_session5,  
--  PARTITION PART_idx_tblsession6       TABLESPACE tbs_tblt_session6,  
--  PARTITION PART_idx_tblsession7       TABLESPACE tbs_tblt_session7,  
--  PARTITION PART_idx_tblsession8       TABLESPACE tbs_tblt_session8  
--)NOLOGGING;

CREATE INDEX NIDXUQ_SESSION_IP ON TBLT_SESSION (SESSION_IP_V4)
GLOBAL PARTITION BY HASH (SESSION_IP_V4)  
( PARTITION PART_idx_tblsession1       TABLESPACE ts_idx_session1,
  PARTITION PART_idx_tblsession2       TABLESPACE ts_idx_session2,
  PARTITION PART_idx_tblsession3       TABLESPACE ts_idx_session3,
  PARTITION PART_idx_tblsession4       TABLESPACE ts_idx_session4,
  PARTITION PART_idx_tblsession5       TABLESPACE ts_idx_session5,  
  PARTITION PART_idx_tblsession6       TABLESPACE ts_idx_session6,  
  PARTITION PART_idx_tblsession7       TABLESPACE ts_idx_session7,  
  PARTITION PART_idx_tblsession8       TABLESPACE ts_idx_session8  
)NOLOGGING;

CREATE INDEX NIDXUQ_SESSION_IPV6 ON TBLT_SESSION (SESSION_IP_V6)
GLOBAL PARTITION BY HASH (SESSION_IP_V6)  
( PARTITION PART_idx_tblsession1       TABLESPACE ts_idx_session1,
  PARTITION PART_idx_tblsession2       TABLESPACE ts_idx_session2,
  PARTITION PART_idx_tblsession3       TABLESPACE ts_idx_session3,
  PARTITION PART_idx_tblsession4       TABLESPACE ts_idx_session4,
  PARTITION PART_idx_tblsession5       TABLESPACE ts_idx_session5,  
  PARTITION PART_idx_tblsession6       TABLESPACE ts_idx_session6,  
  PARTITION PART_idx_tblsession7       TABLESPACE ts_idx_session7,  
  PARTITION PART_idx_tblsession8       TABLESPACE ts_idx_session8  
)NOLOGGING;

CREATE INDEX NIDX_SY_SESSIONID ON TBLT_SESSION (SY_SESSION_ID)
GLOBAL PARTITION BY HASH (SY_SESSION_ID)  
( PARTITION PART_idx_tblsession1       TABLESPACE ts_idx_session1,
  PARTITION PART_idx_tblsession2       TABLESPACE ts_idx_session2,
  PARTITION PART_idx_tblsession3       TABLESPACE ts_idx_session3,
  PARTITION PART_idx_tblsession4       TABLESPACE ts_idx_session4,
  PARTITION PART_idx_tblsession5       TABLESPACE ts_idx_session5,  
  PARTITION PART_idx_tblsession6       TABLESPACE ts_idx_session6,  
  PARTITION PART_idx_tblsession7       TABLESPACE ts_idx_session7,  
  PARTITION PART_idx_tblsession8       TABLESPACE ts_idx_session8  
)NOLOGGING;

CREATE INDEX NIDX_SRID ON TBLT_SUB_SESSION(SR_ID) 
GLOBAL PARTITION BY HASH (SR_ID) 
( PARTITION PART_IDX_tblsessionrule1 TABLESPACE ts_idxsubsession1, 
  PARTITION PART_IDX_tblsessionrule2 TABLESPACE ts_idxsubsession2, 
  PARTITION PART_IDX_tblsessionrule3 TABLESPACE ts_idxsubsession3, 
  PARTITION PART_IDX_tblsessionrule4 TABLESPACE ts_idxsubsession4, 
  PARTITION PART_IDX_tblsessionrule5 TABLESPACE ts_idxsubsession5, 
  PARTITION PART_IDX_tblsessionrule6 TABLESPACE ts_idxsubsession6, 
  PARTITION PART_IDX_tblsessionrule7 TABLESPACE ts_idxsubsession7, 
  PARTITION PART_IDX_tblsessionrule8 TABLESPACE ts_idxsubsession8 
)INITRANS 169;

ALTER TABLE TBLT_SESSION INITRANS 169;

ALTER INDEX NIDXUQ_SESSION_SID INITRANS 169;
ALTER INDEX NIDXUQ_SESSION_CID INITRANS 169;
ALTER INDEX NIDXUQ_SESSION_IP INITRANS 169;
ALTER INDEX NIDX_SY_SESSIONID INITRANS 169;
ALTER INDEX NIDXUQ_SESSION_IPV6 INITRANS 169;   
---- Sequence name is not available in default dbc
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

prompt --------------------DDL Change End for TBLT_SESSION-------------------------

prompt --------------------DDL Change Start for TBLT_SUB_SESSION------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLT_SUB_SESSION'), 
         FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SUB_SESSION') INTO V_OBJ,V_OLD
        FROM DUAL;
          IF V_OLD > 0 THEN
            Raise_Application_Error(-20010,'Object OLD_TBLT_SUB_SESSION IS already exists,Drop OLD_TBLT_SUB_SESSION object then execute DBC again.');
          ELSIF V_OBJ > 0 THEN
	           execute immediate 'RENAME TBLT_SUB_SESSION TO OLD_TBLT_SUB_SESSION';
	           dbms_output.put_line('TBLT_SUB_SESSION Renamed');
    	   END IF;
END;
 /

CREATE TABLE TBLT_SUB_SESSION 
(	 "SR_ID" VARCHAR2(36),
        "SESSION_ID" VARCHAR2(255),
        "GATEWAY_ADDRESS" VARCHAR2(64),
        "AF_SESSION_ID" VARCHAR2(255),
        "MEDIA_TYPE" VARCHAR2(36),
        "PCC_RULE" VARCHAR2(255),
        "MEDIA_COMPONENT_NUMBER" VARCHAR2(9),
        "FLOW_NUMBER" VARCHAR2(9),
        "UPLINK_FLOW" VARCHAR2(255),
        "DOWNLINK_FLOW" VARCHAR2(255),
        "START_TIME" TIMESTAMP (6),
        "LAST_UPDATE_TIME" TIMESTAMP (6),
        "ADDITIONAL_PARAMETER" VARCHAR2(4000)
)PARTITION BY HASH(SESSION_ID)                                        
( PARTITION PART_tbs_tbltsubsession1       TABLESPACE ts_tbltsubsession1,  
  PARTITION PART_tbs_tbltsubsession2       TABLESPACE ts_tbltsubsession2,  
  PARTITION PART_tbs_tbltsubsession3       TABLESPACE ts_tbltsubsession3,  
  PARTITION PART_tbs_tbltsubsession4       TABLESPACE ts_tbltsubsession4,   
  PARTITION PART_tbs_tbltsubsession5       TABLESPACE ts_tbltsubsession5,    
  PARTITION PART_tbs_tbltsubsession6       TABLESPACE ts_tbltsubsession6,    
  PARTITION PART_tbs_tbltsubsession7       TABLESPACE ts_tbltsubsession7,    
  PARTITION PART_tbs_tbltsubsession8       TABLESPACE ts_tbltsubsession8     
)NOLOGGING;                                                                       


DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SUB_SESSION') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
		execute immediate 'INSERT INTO TBLT_SUB_SESSION SELECT * FROM OLD_TBLT_SUB_SESSION';

		COMMIT;
	  dbms_output.put_line('TBLT_SUB_SESSION Data Imported');

	  END IF;
 END;
 /
 

CREATE INDEX NIDX_SUBSESS_CID ON TBLT_SUB_SESSION (SESSION_ID)
GLOBAL PARTITION BY HASH (SESSION_ID)  
( PARTITION PART_IDX_tbltsubsession1       TABLESPACE ts_idxsubsession1,
  PARTITION PART_IDX_tbltsubsession2       TABLESPACE ts_idxsubsession2,
  PARTITION PART_IDX_tbltsubsession3       TABLESPACE ts_idxsubsession3,
  PARTITION PART_IDX_tbltsubsession4       TABLESPACE ts_idxsubsession4,
  PARTITION PART_IDX_tbltsubsession5       TABLESPACE ts_idxsubsession5, 
  PARTITION PART_IDX_tbltsubsession6       TABLESPACE ts_idxsubsession6,  
  PARTITION PART_IDX_tbltsubsession7       TABLESPACE ts_idxsubsession7,  
  PARTITION PART_IDX_tbltsubsession8       TABLESPACE ts_idxsubsession8  
)NOLOGGING;

CREATE INDEX NIDX_SUBSESS_RULE ON TBLT_SUB_SESSION (SESSION_ID,PCC_RULE)
GLOBAL PARTITION BY HASH (SESSION_ID,PCC_RULE)
( PARTITION PART_IDX_tbltsubsession1       TABLESPACE ts_idxsubsession1,
  PARTITION PART_IDX_tbltsubsession2       TABLESPACE ts_idxsubsession2,
  PARTITION PART_IDX_tbltsubsession3       TABLESPACE ts_idxsubsession3,
  PARTITION PART_IDX_tbltsubsession4       TABLESPACE ts_idxsubsession4,
  PARTITION PART_IDX_tbltsubsession5       TABLESPACE ts_idxsubsession5, 
  PARTITION PART_IDX_tbltsubsession6       TABLESPACE ts_idxsubsession6,  
  PARTITION PART_IDX_tbltsubsession7       TABLESPACE ts_idxsubsession7,  
  PARTITION PART_IDX_tbltsubsession8       TABLESPACE ts_idxsubsession8  
)NOLOGGING;

ALTER INDEX NIDX_SUBSESS_RULE INITRANS 169;

ALTER INDEX NIDX_SUBSESS_CID INITRANS 169;

ALTER TABLE TBLT_SUB_SESSION INITRANS 169;

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

prompt --------------------DDL Change End for TBLT_SUB_SESSION--------------------------

prompt --------------------Backup DBC After Change-------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OUTPUT CLOB:= NULL;
  BEGIN
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME IN ('TBLT_SESSION','TBLT_SUB_SESSION') ORDER BY TABLE_NAME)
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
