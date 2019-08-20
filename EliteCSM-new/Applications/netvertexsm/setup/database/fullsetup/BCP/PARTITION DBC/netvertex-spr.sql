spool netvertex-spr.log
set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

prompt -------NetVertex SPR ---------------------------------

prompt -------create tablespaces for netvertex spr module----

prompt Enter the datafile location for tablespace of NETVERTEX SPR - TABLE PARTITIONING ===>

DEFINE db_datafile_spr="&&subscriber_storage_dir"
prompt &db_datafile_spr                                          
                                          
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER01 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER02 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER03 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER04 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER05 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER06 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER07 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER08 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER08.dbf' size 100M AUTOEXTEND ON NEXT 25M;

create tablespace TBS_IDX_SUBSCRIBER01 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER02 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER03 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER04 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER05 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER06 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER07 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER08 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLM_SUBSCRIPTION ===>

DEFINE db_datafile_addon="&&subscription_storage_dir"                                                           
prompt &db_datafile_addon 

CREATE TABLESPACE tbs_subscription01 DATAFILE '&&db_datafile_addon/tbs_subscription01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription02 DATAFILE '&&db_datafile_addon/tbs_subscription02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription03 DATAFILE '&&db_datafile_addon/tbs_subscription03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription04 DATAFILE '&&db_datafile_addon/tbs_subscription04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription05 DATAFILE '&&db_datafile_addon/tbs_subscription05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription06 DATAFILE '&&db_datafile_addon/tbs_subscription06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription07 DATAFILE '&&db_datafile_addon/tbs_subscription07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription08 DATAFILE '&&db_datafile_addon/tbs_subscription08.dbf' size 100M AUTOEXTEND ON NEXT 25M;

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLM_SUBSCRIPTION_HISTORY ===>
                                                                                                       
DEFINE db_datafile_addon_hist="&&subscription_hist_storage_dir"                                                           
prompt &db_datafile_addon_hist                                                                                    

CREATE TABLESPACE TBS_PROMOTIONAL_HIST01 DATAFILE '&&db_datafile_addon_hist/TBS_PROMOTIONAL_HIST01.dbf' size 150M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_PROMOTIONAL_HIST02 DATAFILE '&&db_datafile_addon_hist/TBS_PROMOTIONAL_HIST02.dbf' size 150M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_PROMOTIONAL_HIST03 DATAFILE '&&db_datafile_addon_hist/TBS_PROMOTIONAL_HIST03.dbf' size 150M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_PROMOTIONAL_HIST04 DATAFILE '&&db_datafile_addon_hist/TBS_PROMOTIONAL_HIST04.dbf' size 150M AUTOEXTEND ON NEXT 25M;
                                                                                                                                
CREATE TABLESPACE IDX_PROMOTIONAL_HIST01 DATAFILE '&&db_datafile_addon_hist/IDX_PROMOTIONAL_HIST01.dbf' size 150M AUTOEXTEND ON NEXT 25M;

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLM_USAGE ===>

DEFINE db_datafile_usage="&&usage_storage_dir"
prompt &db_datafile_usage                                          
                                          
CREATE TABLESPACE TBS_TBLM_USAGE01 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_USAGE02 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_USAGE03 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_USAGE04 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_USAGE05 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_USAGE06 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_USAGE07 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_USAGE08 DATAFILE '&&db_datafile_usage/TBS_TBLM_USAGE08.dbf' size 100M AUTOEXTEND ON NEXT 25M;

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLM_USAGE_HISTORY ===>
DEFINE db_datafile_usage_hist="&&usage_hist_storage_dir"
prompt &db_datafile_usage_hist

CREATE TABLESPACE tbs_usage_hist_01   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_02   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_03   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_04   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_05   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_06   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_07   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_08   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_09   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_09.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_10   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_10.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_11   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_11.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_12   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_12.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_13   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_13.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_14   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_14.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_15   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_15.dbf' size 100M AUTOEXTEND ON NEXT 25M; 

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
    ELSIF Upper(OBJ_TYPE)	= 'TRIGGER' THEN
    SELECT COUNT(*) INTO CNT FROM USER_TRIGGERS WHERE TRIGGER_NAME = ''||OBJ_NAME||'';
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
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES 
               WHERE TABLE_NAME IN ('TBLM_SUBSCRIBER','TBLM_SUBSCRIPTION','TBLM_USAGE','TBLM_SUBSCRIPTION_HISTORY','TBLM_USAGE_HISTORY') ORDER BY TABLE_NAME)
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

prompt --------------------DDL Change Start for TBLM_SUBSCRIBER------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_SUBSCRIBER'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIBER') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_SUBSCRIBER already exists, Drop OLD_TBLM_SUBSCRIBER object then execute DBC again');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_SUBSCRIBER TO OLD_TBLM_SUBSCRIBER';
	  dbms_output.put_line('TBLM_SUBSCRIBER Renamed');

	  END IF;
 END;
 / 
 
--RENAME TBLM_SUBSCRIBER TO OLD_TBLM_SUBSCRIBER;

CREATE TABLE TBLM_SUBSCRIBER(
    USERNAME              VARCHAR(255),
    PASSWORD              VARCHAR(255),
    SUBSCRIBERIDENTITY 	  VARCHAR(255),
    PARENTID              VARCHAR(255),
    GROUPNAME             VARCHAR(255),
    ENCRYPTIONTYPE        VARCHAR(255),
    CUSTOMERTYPE          VARCHAR(10),
    BILLINGDATE           NUMERIC(2),
    EXPIRYDATE            TIMESTAMP,
    DATAPACKAGE           VARCHAR(100),
    IMSPACKAGE            VARCHAR(100),   
    EMAIL                 VARCHAR(100),
    PHONE                 VARCHAR(20),
    SIPURL                VARCHAR(200),
    AREA                  VARCHAR(20),
    CITY                  VARCHAR(20),
    ZONE                  VARCHAR(20),
    COUNTRY               VARCHAR(55),
    BIRTHDATE             TIMESTAMP,
    ROLE                  VARCHAR(20),
    COMPANY               VARCHAR(512),
    DEPARTMENT            VARCHAR(20),
    CADRE                 VARCHAR(5),
    ARPU                  NUMERIC(20),
    CUI			          VARCHAR(255),	
    IMSI                  VARCHAR(100),
    MSISDN                VARCHAR(100),
    IMEI                  VARCHAR(100),
    ESN                   VARCHAR(100),
    MEID                  VARCHAR(100),
    MAC  		          VARCHAR(100),
    EUI64  		          VARCHAR(100),
    MODIFIED_EUI64  	  VARCHAR(100),
    SUBSCRIBERLEVELMETERING   VARCHAR(10) DEFAULT 'DISABLE',
    STATUS                VARCHAR(24),
    PASSWORD_CHECK        VARCHAR(5) DEFAULT 'false',
    SY_INTERFACE          VARCHAR(5) DEFAULT 'true',	
    CALLING_STATION_ID    VARCHAR(253),
    FRAMED_IP             VARCHAR(20),
    NAS_PORT_ID           VARCHAR(50),
    PARAM1   	          VARCHAR(10),
    PARAM2  	          VARCHAR(10),
    PARAM3   	          VARCHAR(10),
    PARAM4  	          VARCHAR(10),
    PARAM5  	          VARCHAR(10),
	CREATED_DATE         TIMESTAMP,
    MODIFIED_DATE        TIMESTAMP,
    CONSTRAINT PK_SUBSCRIBER_PRT PRIMARY KEY (SUBSCRIBERIDENTITY)
)                                                        
PARTITION BY HASH(SUBSCRIBERIDENTITY)                                         
( PARTITION PART_TBS_SUBSCRIBER01   TABLESPACE TBS_TBLM_SUBSCRIBER01,   
  PARTITION PART_TBS_SUBSCRIBER02   TABLESPACE TBS_TBLM_SUBSCRIBER02,
  PARTITION PART_TBS_SUBSCRIBER03   TABLESPACE TBS_TBLM_SUBSCRIBER03,
  PARTITION PART_TBS_SUBSCRIBER04   TABLESPACE TBS_TBLM_SUBSCRIBER04, 
  PARTITION PART_TBS_SUBSCRIBER05   TABLESPACE TBS_TBLM_SUBSCRIBER05,
  PARTITION PART_TBS_SUBSCRIBER06   TABLESPACE TBS_TBLM_SUBSCRIBER06,   
  PARTITION PART_TBS_SUBSCRIBER07   TABLESPACE TBS_TBLM_SUBSCRIBER07,
  PARTITION PART_TBS_SUBSCRIBER08   TABLESPACE TBS_TBLM_SUBSCRIBER08   
)NOLOGGING;   


DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIBER') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_SUBSCRIBER SELECT * FROM OLD_TBLM_SUBSCRIBER';

      COMMIT;
	  dbms_output.put_line('TBLM_SUBSCRIBER Data Imported');

	  END IF;
 END;
 /

--Disable Primary Key

ALTER TABLE TBLM_SUBSCRIBER DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

  CREATE UNIQUE INDEX PK_SUBSCRIBER_PRT ON TBLM_SUBSCRIBER(SUBSCRIBERIDENTITY)
  GLOBAL PARTITION BY HASH (SUBSCRIBERIDENTITY)                                               
(PARTITION PRT_IDX_SUBSCRIBER01    TABLESPACE TBS_IDX_SUBSCRIBER01 ,
 PARTITION PRT_IDX_SUBSCRIBER02    TABLESPACE TBS_IDX_SUBSCRIBER02 ,
 PARTITION PRT_IDX_SUBSCRIBER03    TABLESPACE TBS_IDX_SUBSCRIBER03 ,
 PARTITION PRT_IDX_SUBSCRIBER04    TABLESPACE TBS_IDX_SUBSCRIBER04 ,
 PARTITION PRT_IDX_SUBSCRIBER05    TABLESPACE TBS_IDX_SUBSCRIBER05 ,
 PARTITION PRT_IDX_SUBSCRIBER06    TABLESPACE TBS_IDX_SUBSCRIBER06 ,
 PARTITION PRT_IDX_SUBSCRIBER07    TABLESPACE TBS_IDX_SUBSCRIBER07 ,
 PARTITION PRT_IDX_SUBSCRIBER08    TABLESPACE TBS_IDX_SUBSCRIBER08 )
 INITRANS 169 NOLOGGING;
 
--Enable Primary key

ALTER TABLE TBLM_SUBSCRIBER ENABLE PRIMARY KEY;

CREATE INDEX PIDX_SUBSCRIBER_STATUS ON TBLM_SUBSCRIBER(STATUS) INITRANS 169 NOLOGGING;

ALTER TABLE TBLM_SUBSCRIBER INITRANS 169;

DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('SEQUENCE','SEQ_MSUBSCRIBER') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  execute immediate 'ALTER SEQUENCE SEQ_MSUBSCRIBER CACHE 1000';
	  dbms_output.put_line('SEQUENCE Altered');
	  ELSE
	  execute immediate 'CREATE SEQUENCE SEQ_MSUBSCRIBER INCREMENT BY 1 START WITH 101 NOMAXVALUE NOMINVALUE NOCYCLE CACHE 1000 NOORDER';
	  dbms_output.put_line('SEQUENCE Created');

	  END IF;
 END;
 /

prompt --------------------DDL Change End for TBLM_SUBSCRIBER-----------------------------

prompt --------------------DDL Change Start for TBLM_SUBSCRIPTION-------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_SUBSCRIPTION'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIPTION') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_SUBSCRIPTION already exists, Drop OLD_TBLM_SUBSCRIPTION object then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_SUBSCRIPTION TO OLD_TBLM_SUBSCRIPTION';
	  dbms_output.put_line('TBLM_SUBSCRIPTION Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLM_SUBSCRIPTION TO OLD_TBLM_SUBSCRIPTION;

CREATE TABLE TBLM_SUBSCRIPTION 
   (
    SUBSCRIPTION_ID      VARCHAR(36),
	SUBSCRIBER_ID        VARCHAR(256) NOT NULL,
	ADDON_ID             VARCHAR(36) NOT NULL,
	START_TIME           TIMESTAMP,
	END_TIME             TIMESTAMP,
    STATUS               CHAR(1) DEFAULT '0',
    SERVER_INSTANCE_ID   NUMERIC,
    PARENT_IDENTITY      VARCHAR(256),
    REJECT_REASON        VARCHAR(1024),
    SUBSCRIPTION_TIME    TIMESTAMP,
    LAST_UPDATE_TIME     TIMESTAMP,
    PRIORITY             NUMERIC(5,0),
    USAGE_RESET_DATE     TIMESTAMP,
    PARAM1               VARCHAR(256),
    PARAM2               VARCHAR(256),
    CONSTRAINT PK_SUBSCRIPTION_PRT PRIMARY KEY(SUBSCRIPTION_ID)
   )
PARTITION BY HASH  (SUBSCRIBER_ID) 
(PARTITION prtmaddonsubscriberrel1        tablespace tbs_subscription01,
 PARTITION prtmaddonsubscriberrel2        tablespace tbs_subscription02,
 PARTITION prtmaddonsubscriberrel3        tablespace tbs_subscription03,
 PARTITION prtmaddonsubscriberrel4        tablespace tbs_subscription04,
 PARTITION prtmaddonsubscriberrel5        tablespace tbs_subscription05,
 PARTITION prtmaddonsubscriberrel6        tablespace tbs_subscription06,
 PARTITION prtmaddonsubscriberrel7        tablespace tbs_subscription07,
 PARTITION prtmaddonsubscriberrel8        tablespace tbs_subscription08 
)NOLOGGING;

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIPTION') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_SUBSCRIPTION SELECT * FROM OLD_TBLM_SUBSCRIPTION';

      COMMIT;
	  dbms_output.put_line('TBLM_SUBSCRIPTION Data Imported');

	  END IF;
 END;
 /

CREATE INDEX IDX_SUBSCRIBER_ID ON TBLM_SUBSCRIPTION (SUBSCRIBER_ID)     
GLOBAL PARTITION BY HASH (SUBSCRIBER_ID)                                                       
(PARTITION prtmaddonsubscriberrel1        tablespace tbs_subscription01,
 PARTITION prtmaddonsubscriberrel2        tablespace tbs_subscription02,
 PARTITION prtmaddonsubscriberrel3        tablespace tbs_subscription03,
 PARTITION prtmaddonsubscriberrel4        tablespace tbs_subscription04,
 PARTITION prtmaddonsubscriberrel5        tablespace tbs_subscription05,
 PARTITION prtmaddonsubscriberrel6        tablespace tbs_subscription06,
 PARTITION prtmaddonsubscriberrel7        tablespace tbs_subscription07,
 PARTITION prtmaddonsubscriberrel8        tablespace tbs_subscription08
)INITRANS 169 NOLOGGING;          

--Disable Primary Key

ALTER TABLE TBLM_SUBSCRIPTION DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

CREATE UNIQUE INDEX PK_SUBSCRIPTION_PRT ON TBLM_SUBSCRIPTION (SUBSCRIPTION_ID)           
GLOBAL PARTITION BY HASH (SUBSCRIPTION_ID)                                                       
(PARTITION prtmaddonsubscriberrel1        tablespace tbs_subscription01,
 PARTITION prtmaddonsubscriberrel2        tablespace tbs_subscription02,
 PARTITION prtmaddonsubscriberrel3        tablespace tbs_subscription03,
 PARTITION prtmaddonsubscriberrel4        tablespace tbs_subscription04,
 PARTITION prtmaddonsubscriberrel5        tablespace tbs_subscription05,
 PARTITION prtmaddonsubscriberrel6        tablespace tbs_subscription06,
 PARTITION prtmaddonsubscriberrel7        tablespace tbs_subscription07,
 PARTITION prtmaddonsubscriberrel8        tablespace tbs_subscription08
)INITRANS 169 NOLOGGING;

--Enable Primary key
ALTER TABLE TBLM_SUBSCRIPTION ENABLE PRIMARY KEY;

ALTER TABLE TBLM_SUBSCRIPTION INITRANS 169;

prompt --------------------DDL Change End for TBLM_SUBSCRIPTION-------------------------

prompt --------------------DDL Change Start for TBLM_SUBSCRIPTION_HISTORY-------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_SUBSCRIPTION_HISTORY'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIPTION_HISTORY') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_SUBSCRIPTION_HISTORY already exists, Drop OLD_TBLM_SUBSCRIPTION_HISTORY object then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	   execute immediate 'RENAME TBLM_SUBSCRIPTION_HISTORY TO OLD_TBLM_SUBSCRIPTION_HISTORY';
	  dbms_output.put_line('TBLM_SUBSCRIPTION_HISTORY Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLM_SUBSCRIPTION_HISTORY TO OLD_TBLM_SUBSCRIPTION_HISTORY;

CREATE TABLE TBLM_SUBSCRIPTION_HISTORY
(
  SUBSCRIBER_ID     VARCHAR(256) NOT NULL,
  ADDON_ID          VARCHAR(36) NOT NULL,
  START_TIME        TIMESTAMP,
  END_TIME          TIMESTAMP,
  LAST_UPDATE_TIME  TIMESTAMP,
  STATUS            VARCHAR(32) NOT NULL,
  SUBSCRIPTION_ID   VARCHAR(36),
  PARENT_IDENTITY   VARCHAR(256),
  SUBSCRIPTION_TIME TIMESTAMP,
  REJECT_REASON VARCHAR(1024)
)
PARTITION BY RANGE (START_TIME) INTERVAL (NUMTODSINTERVAL(1,'DAY')) STORE IN
 (TBS_PROMOTIONAL_HIST01,TBS_PROMOTIONAL_HIST02,TBS_PROMOTIONAL_HIST03,TBS_PROMOTIONAL_HIST04)
 (PARTITION P_PSHFIRST  VALUES LESS THAN (TIMESTAMP' 2014-07-03 00:00:00')) NOLOGGING;


DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIPTION_HISTORY') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_SUBSCRIPTION_HISTORY SELECT * FROM OLD_TBLM_SUBSCRIPTION_HISTORY';

      COMMIT;
	  dbms_output.put_line('TBLM_SUBSCRIPTION_HISTORY Data Imported');

	  END IF;
 END;
 / 
 
CREATE INDEX IDX_SUBSCRIPTION_HIST_SID ON TBLM_SUBSCRIPTION_HISTORY(SUBSCRIPTION_ID)
TABLESPACE IDX_PROMOTIONAL_HIST01 INITRANS 169 NOLOGGING;

ALTER TABLE TBLM_SUBSCRIPTION_HISTORY INITRANS 169;

--Added Procedure 22-09-2015

DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TRIGGER','TRG_SUBSCRIPTION_HISTORY_DEL') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  execute immediate 'DROP TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL';
	  dbms_output.put_line('DROP TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL');

	  END IF;
 END;
 /
 
--DROP TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL;

CREATE OR REPLACE TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL
AFTER DELETE ON TBLM_SUBSCRIPTION FOR EACH ROW
BEGIN
    IF DELETING THEN
          INSERT INTO TBLM_SUBSCRIPTION_HISTORY
         (	SUBSCRIBER_ID      ,
          ADDON_ID             ,
          START_TIME           ,
		  END_TIME             ,
		  LAST_UPDATE_TIME     ,
		  STATUS               ,
		  SUBSCRIPTION_ID      ,
		  PARENT_IDENTITY      ,
		  SUBSCRIPTION_TIME    ,
		  REJECT_REASON 		  
	
)
		  VALUES( :OLD.SUBSCRIBER_ID      ,
				  :OLD.ADDON_ID           ,
				  :OLD.START_TIME         ,
				  :OLD.END_TIME           ,
				  :OLD.LAST_UPDATE_TIME   ,
				  :OLD.STATUS             ,
				  :OLD.SUBSCRIPTION_ID    ,
				  :OLD.PARENT_IDENTITY    ,
				  :OLD.SUBSCRIPTION_TIME  ,
				  :OLD.PARAM1
                );		  
    END IF;
	
END;
/
 
prompt --------------------DDL Change End for TBLM_SUBSCRIPTION_HISTORY-------------------------

prompt --------------------DDL Change Start for TBLM_USAGE--------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_USAGE'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_USAGE') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_USAGE already exists, Drop OLD_TBLM_USAGE then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_USAGE TO OLD_TBLM_USAGE';
	  dbms_output.put_line('TBLM_USAGE Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLM_USAGE TO OLD_TBLM_USAGE;

CREATE TABLE TBLM_USAGE
   (
    ID                     VARCHAR(36),
    SUBSCRIBER_ID          VARCHAR(255),
    PACKAGE_ID             VARCHAR(36),
    SUBSCRIPTION_ID        VARCHAR(36), 
    QUOTA_PROFILE_ID       VARCHAR(36),
    SERVICE_ID             VARCHAR(36), 
    DAILY_TOTAL            NUMERIC,
    DAILY_UPLOAD           NUMERIC,
    DAILY_DOWNLOAD         NUMERIC,
    DAILY_TIME             NUMERIC,
    WEEKLY_TOTAL           NUMERIC,
    WEEKLY_UPLOAD          NUMERIC,
    WEEKLY_DOWNLOAD        NUMERIC,
    WEEKLY_TIME            NUMERIC,
    BILLING_CYCLE_TOTAL    NUMERIC,
    BILLING_CYCLE_UPLOAD   NUMERIC,
    BILLING_CYCLE_DOWNLOAD NUMERIC,
    BILLING_CYCLE_TIME     NUMERIC,
    CUSTOM_TOTAL           NUMERIC,
    CUSTOM_UPLOAD          NUMERIC,
    CUSTOM_DOWNLOAD        NUMERIC,
    CUSTOM_TIME            NUMERIC,
    DAILY_RESET_TIME       TIMESTAMP,
    WEEKLY_RESET_TIME      TIMESTAMP,
    CUSTOM_RESET_TIME      TIMESTAMP,
    BILLING_CYCLE_RESET_TIME TIMESTAMP,
    LAST_UPDATE_TIME       TIMESTAMP,
    CONSTRAINT PK_USAGE_PRT PRIMARY KEY(ID)
   )                   
PARTITION BY HASH(ID)                                         
( PARTITION PART_TBLM_USAGE01       TABLESPACE TBS_TBLM_USAGE01,   
  PARTITION PART_TBLM_USAGE02       TABLESPACE TBS_TBLM_USAGE02,
  PARTITION PART_TBLM_USAGE03       TABLESPACE TBS_TBLM_USAGE03,
  PARTITION PART_TBLM_USAGE04       TABLESPACE TBS_TBLM_USAGE04, 
  PARTITION PART_TBLM_USAGE05       TABLESPACE TBS_TBLM_USAGE05,
  PARTITION PART_TBLM_USAGE06       TABLESPACE TBS_TBLM_USAGE06, 
  PARTITION PART_TBLM_USAGE07       TABLESPACE TBS_TBLM_USAGE07,
  PARTITION PART_TBLM_USAGE08       TABLESPACE TBS_TBLM_USAGE08   
)NOLOGGING;   


DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_USAGE') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_USAGE SELECT * FROM OLD_TBLM_USAGE';

      COMMIT;
	  dbms_output.put_line('TBLM_USAGE Data Imported');

	  END IF;
 END;
 /

--Disable Primary Key

ALTER TABLE TBLM_USAGE DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

CREATE UNIQUE INDEX PK_USAGE_PRT ON TBLM_USAGE (ID)
GLOBAL PARTITION BY HASH (ID)  
(
  PARTITION PART_IDX_USAGE01       TABLESPACE TBS_TBLM_USAGE01,   
  PARTITION PART_IDX_USAGE02       TABLESPACE TBS_TBLM_USAGE02,
  PARTITION PART_IDX_USAGE03       TABLESPACE TBS_TBLM_USAGE03,
  PARTITION PART_IDX_USAGE04       TABLESPACE TBS_TBLM_USAGE04, 
  PARTITION PART_IDX_USAGE05       TABLESPACE TBS_TBLM_USAGE05,
  PARTITION PART_IDX_USAGE06       TABLESPACE TBS_TBLM_USAGE06, 
  PARTITION PART_IDX_USAGE07       TABLESPACE TBS_TBLM_USAGE07,
  PARTITION PART_IDX_USAGE08       TABLESPACE TBS_TBLM_USAGE08 
)INITRANS 169 NOLOGGING;   

--Enable Primary key

ALTER TABLE TBLM_USAGE ENABLE PRIMARY KEY;

CREATE INDEX PPRT_USAGE_SID ON TBLM_USAGE (SUBSCRIBER_ID)
GLOBAL PARTITION BY HASH (SUBSCRIBER_ID)  
(
  PARTITION PART_IDX_USAGE01       TABLESPACE TBS_TBLM_USAGE01,   
  PARTITION PART_IDX_USAGE02       TABLESPACE TBS_TBLM_USAGE02,
  PARTITION PART_IDX_USAGE03       TABLESPACE TBS_TBLM_USAGE03,
  PARTITION PART_IDX_USAGE04       TABLESPACE TBS_TBLM_USAGE04, 
  PARTITION PART_IDX_USAGE05       TABLESPACE TBS_TBLM_USAGE05,
  PARTITION PART_IDX_USAGE06       TABLESPACE TBS_TBLM_USAGE06, 
  PARTITION PART_IDX_USAGE07       TABLESPACE TBS_TBLM_USAGE07,
  PARTITION PART_IDX_USAGE08       TABLESPACE TBS_TBLM_USAGE08 
)INITRANS 169 NOLOGGING; 


ALTER TABLE TBLM_USAGE INITRANS 169;

prompt --------------------DDL Change End for TBLM_USAGE-----------------------------

prompt --------------------DDL Change Start for TBLM_USAGE_HISTORY-------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_USAGE_HISTORY') INTO V_OBJ
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object TBLM_USAGE_HISTORY already exists, Drop TBLM_USAGE_HISTORY object then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_USAGE_HISTORY TO OLD_TBLM_USAGE_HISTORY';
	  dbms_output.put_line('TBLM_USAGE_HISTORY Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLM_USAGE_HISTORY TO OLD_TBLM_USAGE_HISTORY;

--table partitioning 

CREATE TABLE TBLM_USAGE_HISTORY
   (CREATE_DATE TIMESTAMP,
    ID VARCHAR2(36 BYTE), 
	SUBSCRIBER_ID VARCHAR2(255 BYTE), 
	PACKAGE_ID VARCHAR2(36 BYTE), 
	SUBSCRIPTION_ID VARCHAR2(36 BYTE), 
	QUOTA_PROFILE_ID VARCHAR2(36 BYTE), 
	SERVICE_ID VARCHAR2(36 BYTE), 
	DAILY_TOTAL NUMBER(*,0), 
	DAILY_UPLOAD NUMBER(*,0), 
	DAILY_DOWNLOAD NUMBER(*,0), 
	DAILY_TIME NUMBER(*,0), 
	WEEKLY_TOTAL NUMBER(*,0), 
	WEEKLY_UPLOAD NUMBER(*,0), 
	WEEKLY_DOWNLOAD NUMBER(*,0), 
	WEEKLY_TIME NUMBER(*,0), 
	BILLING_CYCLE_TOTAL NUMBER(*,0), 
	BILLING_CYCLE_UPLOAD NUMBER(*,0), 
	BILLING_CYCLE_DOWNLOAD NUMBER(*,0), 
	BILLING_CYCLE_TIME NUMBER(*,0), 
	CUSTOM_TOTAL NUMBER(*,0), 
	CUSTOM_UPLOAD NUMBER(*,0), 
	CUSTOM_DOWNLOAD NUMBER(*,0), 
	CUSTOM_TIME NUMBER(*,0), 
	DAILY_RESET_TIME TIMESTAMP (6), 
	WEEKLY_RESET_TIME TIMESTAMP (6), 
	CUSTOM_RESET_TIME TIMESTAMP (6), 
	BILLING_CYCLE_RESET_TIME TIMESTAMP (6), 
	LAST_UPDATE_TIME TIMESTAMP (6)
   )     
PARTITION BY RANGE (CREATE_DATE)
INTERVAL (NUMTODSINTERVAL(1,'day')) store in (tbs_usage_hist_01,tbs_usage_hist_02,tbs_usage_hist_03,tbs_usage_hist_04,tbs_usage_hist_05,tbs_usage_hist_06,tbs_usage_hist_07,tbs_usage_hist_08,tbs_usage_hist_09,tbs_usage_hist_10,tbs_usage_hist_11,tbs_usage_hist_12,tbs_usage_hist_13,tbs_usage_hist_14,tbs_usage_hist_15)
(PARTITION p_first VALUES LESS THAN (TO_DATE('18-12-2013', 'DD-MM-YYYY')))
NOLOGGING; 

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_USAGE_HISTORY') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_USAGE_HISTORY SELECT * FROM OLD_TBLM_USAGE_HISTORY';

      COMMIT;
	  dbms_output.put_line('TBLM_USAGE_HISTORY Data Imported');

	  END IF;
 END;
 /

 ALTER TABLE TBLM_USAGE_HISTORY	 INITRANS 169;
 
prompt --------------------DDL Change End for TBLM_USAGE_HISTORY-------------------


prompt --------------------Backup DBC After Change-------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OUTPUT CLOB:= NULL;
  BEGIN
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES 
               WHERE TABLE_NAME IN ('TBLM_SUBSCRIBER','TBLM_SUBSCRIPTION','TBLM_USAGE','TBLM_SUBSCRIPTION_HISTORY','TBLM_USAGE_HISTORY') ORDER BY TABLE_NAME)
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
