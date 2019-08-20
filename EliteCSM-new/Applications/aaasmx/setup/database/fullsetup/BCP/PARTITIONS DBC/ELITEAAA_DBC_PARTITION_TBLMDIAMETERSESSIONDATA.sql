spool ELITEAAA_DBC_PARTITION_TBLMDIAMETERSESSIONDATA.log
                                           
--create tablespaces for TBLMDIAMETERSESSIONDATA             
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile   

CREATE TABLESPACE tbsdiametersesdata01 DATAFILE '&&db_datafile/tbsdiametersesdata01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsdiametersesdata02 DATAFILE '&&db_datafile/tbsdiametersesdata02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsdiametersesdata03 DATAFILE '&&db_datafile/tbsdiametersesdata03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsdiametersesdata04 DATAFILE '&&db_datafile/tbsdiametersesdata04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsdiametersesdata05 DATAFILE '&&db_datafile/tbsdiametersesdata05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsdiametersesdata06 DATAFILE '&&db_datafile/tbsdiametersesdata06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsdiametersesdata07 DATAFILE '&&db_datafile/tbsdiametersesdata07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsdiametersesdata08 DATAFILE '&&db_datafile/tbsdiametersesdata08.dbf' size 100M AUTOEXTEND ON NEXT 25M;
          
--Enter Schema Name of EliteAAA Product                                     
connect &&username/&&password@&&NET_STR                                             
set long 10000                                                                   
 set pagesize 0                                                                   
 select dbms_metadata.get_ddl('TABLE','TBLMDIAMETERSESSIONDATA') from dual;           
 select dbms_metadata.get_dependent_ddl('INDEX','TBLMDIAMETERSESSIONDATA') from dual; 
                                                                            
 SELECT COUNT(1) FROM TBLMDIAMETERSESSIONDATA;                                       
                                                                            
 RENAME TBLMDIAMETERSESSIONDATA TO OLD_TBLMDIAMETERSESSIONDATA;                                           
  
 CREATE TABLE TBLMDIAMETERSESSIONDATA(
	CONCUSERID	        VARCHAR(36),
	START_TIME			TIMESTAMP,
	LAST_UPDATED_TIME 	TIMESTAMP,
    SESSION_ID        	VARCHAR(253),
    FRAMED_IP_ADDRESS 	VARCHAR(20),
	SESSION_TIMEOUT    	NUMERIC(10),
    AAA_ID 			    VARCHAR(60),
    CLIENT_IP_ADDRESS 	VARCHAR(20),
    SUBSCRIBER_ID    	VARCHAR(253),
    CREATE_DATE 		TIMESTAMP,
    CALL_START  		TIMESTAMP,
    CALL_END 			TIMESTAMP,
    GEOLOCATION         VARCHAR(255),
    APPLICATION_ID      VARCHAR(30),
    PDP_TYPE			VARCHAR(30),
    PEER_ID             VARCHAR(30),
    CONCURRENCY_ID      VARCHAR(30),
    SESSION_STATUS		VARCHAR(30),
    GROUPNAME 			VARCHAR(60),
    PROTOCOLTYPE 		VARCHAR(30),
	PARAM_STR0			VARCHAR(25),
	PARAM_STR1			VARCHAR(25),
	PARAM_STR2			VARCHAR(25),
	PARAM_STR3			VARCHAR(25),
	PARAM_STR4			VARCHAR(25),
	PARAM_STR5			VARCHAR(25),
	PARAM_STR6			VARCHAR(25),
	PARAM_STR7			VARCHAR(25),
	PARAM_STR8			VARCHAR(25),
	PARAM_STR9			VARCHAR(25)
) PARTITION BY HASH(SESSION_ID)                            
( PARTITION prtdiametersesdata01       TABLESPACE tbsdiametersesdata01,                   
  PARTITION prtdiametersesdata02       TABLESPACE tbsdiametersesdata02,                  
  PARTITION prtdiametersesdata03       TABLESPACE tbsdiametersesdata03,                  
  PARTITION prtdiametersesdata04       TABLESPACE tbsdiametersesdata04,                  
  PARTITION prtdiametersesdata05       TABLESPACE tbsdiametersesdata05,                  
  PARTITION prtdiametersesdata06       TABLESPACE tbsdiametersesdata06,                  
  PARTITION prtdiametersesdata07       TABLESPACE tbsdiametersesdata07,                  
  PARTITION prtdiametersesdata08       TABLESPACE tbsdiametersesdata08 
)NOLOGGING;            

CREATE INDEX IDX_DSESSION_ID
ON TBLMDIAMETERSESSIONDATA
(
  SESSION_ID
)
GLOBAL
PARTITION BY HASH(SESSION_ID)
(
  PARTITION prtdiametersesdata01       TABLESPACE tbsdiametersesdata01,
  PARTITION prtdiametersesdata02       TABLESPACE tbsdiametersesdata02,
  PARTITION prtdiametersesdata03       TABLESPACE tbsdiametersesdata03,
  PARTITION prtdiametersesdata04       TABLESPACE tbsdiametersesdata04,
  PARTITION prtdiametersesdata05       TABLESPACE tbsdiametersesdata05,
  PARTITION prtdiametersesdata06       TABLESPACE tbsdiametersesdata06,
  PARTITION prtdiametersesdata07       TABLESPACE tbsdiametersesdata07,
  PARTITION prtdiametersesdata08       TABLESPACE tbsdiametersesdata08 
)NOLOGGING;

/*
CREATE INDEX IDX_CON_ID
ON TBLMDIAMETERSESSIONDATA
(
  CONCURRENCY_ID
)
GLOBAL
PARTITION BY HASH(CONCURRENCY_ID)
(
  PARTITION prtdiametersesdata01       TABLESPACE tbsdiametersesdata01,
  PARTITION prtdiametersesdata02       TABLESPACE tbsdiametersesdata02,
  PARTITION prtdiametersesdata03       TABLESPACE tbsdiametersesdata03,
  PARTITION prtdiametersesdata04       TABLESPACE tbsdiametersesdata04,
  PARTITION prtdiametersesdata05       TABLESPACE tbsdiametersesdata05,
  PARTITION prtdiametersesdata06       TABLESPACE tbsdiametersesdata06,
  PARTITION prtdiametersesdata07       TABLESPACE tbsdiametersesdata07,
  PARTITION prtdiametersesdata08       TABLESPACE tbsdiametersesdata08
)NOLOGGING;
*/

ALTER TABLE TBLMDIAMETERSESSIONDATA INITRANS 169;

ALTER INDEX IDX_DSESSION_ID INITRANS 169;

--ALTER INDEX IDX_CON_ID INITRANS 169;

ALTER SEQUENCE SEQ_MDIAMETERSESSIONDATA CACHE 4000;
                            
select dbms_metadata.get_ddl('TABLE','TBLMDIAMETERSESSIONDATA') from dual;           
select dbms_metadata.get_dependent_ddl('INDEX','TBLMDIAMETERSESSIONDATA') from dual; 

spool off;
exit;