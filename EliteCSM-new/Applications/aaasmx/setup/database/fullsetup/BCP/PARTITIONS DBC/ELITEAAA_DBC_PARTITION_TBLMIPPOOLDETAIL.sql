spool ELITEAAA_DBC_PARTITION_TBLMIPPOOLDETAIL.log 
                                            
--create tablespaces for TBLMCONCURRENTUSERS             
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile   

CREATE TABLESPACE tblmippooldetail01 DATAFILE '&&db_datafile/tblmippooldetail01.dbf' size 50M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tblmippooldetail02 DATAFILE '&&db_datafile/tblmippooldetail02.dbf' size 50M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tblmippooldetail03 DATAFILE '&&db_datafile/tblmippooldetail03.dbf' size 50M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tblmippooldetail04 DATAFILE '&&db_datafile/tblmippooldetail04.dbf' size 50M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tblmippooldetail05 DATAFILE '&&db_datafile/tblmippooldetail05.dbf' size 50M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tblmippooldetail06 DATAFILE '&&db_datafile/tblmippooldetail06.dbf' size 50M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tblmippooldetail07 DATAFILE '&&db_datafile/tblmippooldetail07.dbf' size 50M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tblmippooldetail08 DATAFILE '&&db_datafile/tblmippooldetail08.dbf' size 50M AUTOEXTEND ON NEXT 25M;
          
--Enter Schema Name of EliteAAA Product                                     
connect &&username/&&password@&&NET_STR                                             
set long 10000                                                                   
set pagesize 0                                                                   
select dbms_metadata.get_ddl('TABLE','TBLMIPPOOLDETAIL') from dual;           
select dbms_metadata.get_dependent_ddl('INDEX','TBLMIPPOOLDETAIL') from dual; 
                                                                      
                                                                            
  SELECT COUNT(1) FROM TBLMIPPOOLDETAIL;                                       
                                                                            
  RENAME TBLMIPPOOLDETAIL TO OLD_TBLMIPPOOLDETAIL;  
  
  CREATE TABLE TBLMIPPOOLDETAIL
    ( IPPOOLID      NUMERIC(20)                    NOT NULL,
	  SERIALNUMBER  NUMERIC(8)                       NOT NULL,
	  IPADDRESS     VARCHAR(50)               NOT NULL,
	  ASSIGNED      CHAR(1) DEFAULT 'N', 
	  USER_IDENTITY VARCHAR(100), 
	  LAST_UPDATED_TIME TIMESTAMP (6), 
	  NAS_IP_ADDRESS VARCHAR(50),
	  RESERVED CHAR(1) DEFAULT 'N',
	  IPADDRESSRANGE VARCHAR2(64),
	  IPADDRESSRANGEID VARCHAR2(64),
	  NAS_ID    VARCHAR2(100),         
	  CALLING_STATION_ID VARCHAR2(100),
	  CONSTRAINT PKIDX_MIPPOOLDETAIL PRIMARY KEY (IPPOOLID, SERIALNUMBER),
	  CONSTRAINT FKIDX1_MIPPOOLDETAIL FOREIGN KEY (IPPOOLID) REFERENCES TBLMIPPOOL (IPPOOLID)
   )  PARTITION BY HASH(IPPOOLID,SERIALNUMBER)                            
( PARTITION tblmippooldetail01       TABLESPACE tblmippooldetail01,                   
  PARTITION tblmippooldetail02       TABLESPACE tblmippooldetail02,                  
  PARTITION tblmippooldetail03       TABLESPACE tblmippooldetail03,                  
  PARTITION tblmippooldetail04       TABLESPACE tblmippooldetail04,                  
  PARTITION tblmippooldetail05       TABLESPACE tblmippooldetail05,                  
  PARTITION tblmippooldetail06       TABLESPACE tblmippooldetail06,                  
  PARTITION tblmippooldetail07       TABLESPACE tblmippooldetail07,                  
  PARTITION tblmippooldetail08       TABLESPACE tblmippooldetail08 
)INITRANS 169;            

INSERT INTO TBLMIPPOOLDETAIL
SELECT * FROM OLD_TBLMIPPOOLDETAIL;

COMMIT;

ALTER TABLE TBLMIPPOOLDETAIL DISABLE PRIMARY KEY;

CREATE UNIQUE INDEX PKIDX_MIPPOOLDETAIL
ON TBLMIPPOOLDETAIL
(
  IPPOOLID,SERIALNUMBER 
)
GLOBAL
PARTITION BY HASH(IPPOOLID,SERIALNUMBER)
(
  PARTITION tblmippooldetail01       TABLESPACE tblmippooldetail01,                   
  PARTITION tblmippooldetail02       TABLESPACE tblmippooldetail02,                  
  PARTITION tblmippooldetail03       TABLESPACE tblmippooldetail03,                  
  PARTITION tblmippooldetail04       TABLESPACE tblmippooldetail04,                  
  PARTITION tblmippooldetail05       TABLESPACE tblmippooldetail05,                  
  PARTITION tblmippooldetail06       TABLESPACE tblmippooldetail06,                  
  PARTITION tblmippooldetail07       TABLESPACE tblmippooldetail07,                  
  PARTITION tblmippooldetail08       TABLESPACE tblmippooldetail08 
)INITRANS 169;

ALTER TABLE TBLMIPPOOLDETAIL ENABLE PRIMARY KEY;

CREATE INDEX IDXP_CALLING_ID
ON TBLMIPPOOLDETAIL
(
  CALLING_STATION_ID 
)
GLOBAL
PARTITION BY HASH(CALLING_STATION_ID)
(
PARTITION tblmippooldetail01       TABLESPACE tblmippooldetail01,                   
  PARTITION tblmippooldetail02       TABLESPACE tblmippooldetail02,                  
  PARTITION tblmippooldetail03       TABLESPACE tblmippooldetail03,                  
  PARTITION tblmippooldetail04       TABLESPACE tblmippooldetail04,                  
  PARTITION tblmippooldetail05       TABLESPACE tblmippooldetail05,                  
  PARTITION tblmippooldetail06       TABLESPACE tblmippooldetail06,                  
  PARTITION tblmippooldetail07       TABLESPACE tblmippooldetail07,                  
  PARTITION tblmippooldetail08       TABLESPACE tblmippooldetail08 
)INITRANS 169;
  
CREATE INDEX IDXP_IP_RESERVED
ON TBLMIPPOOLDETAIL
(
  RESERVED 
)
GLOBAL
PARTITION BY HASH(RESERVED)
(
PARTITION tblmippooldetail01       TABLESPACE tblmippooldetail01,                   
  PARTITION tblmippooldetail02       TABLESPACE tblmippooldetail02,                  
  PARTITION tblmippooldetail03       TABLESPACE tblmippooldetail03,                  
  PARTITION tblmippooldetail04       TABLESPACE tblmippooldetail04,                  
  PARTITION tblmippooldetail05       TABLESPACE tblmippooldetail05,                  
  PARTITION tblmippooldetail06       TABLESPACE tblmippooldetail06,                  
  PARTITION tblmippooldetail07       TABLESPACE tblmippooldetail07,                  
  PARTITION tblmippooldetail08       TABLESPACE tblmippooldetail08 
)INITRANS 169;

CREATE INDEX IDXP_IP_IPADDRESS
ON TBLMIPPOOLDETAIL
(
  IPADDRESS
)
GLOBAL
PARTITION BY HASH(IPADDRESS)
(
PARTITION tblmippooldetail01       TABLESPACE tblmippooldetail01,                   
  PARTITION tblmippooldetail02       TABLESPACE tblmippooldetail02,                  
  PARTITION tblmippooldetail03       TABLESPACE tblmippooldetail03,                  
  PARTITION tblmippooldetail04       TABLESPACE tblmippooldetail04,                  
  PARTITION tblmippooldetail05       TABLESPACE tblmippooldetail05,                  
  PARTITION tblmippooldetail06       TABLESPACE tblmippooldetail06,                  
  PARTITION tblmippooldetail07       TABLESPACE tblmippooldetail07,                  
  PARTITION tblmippooldetail08       TABLESPACE tblmippooldetail08 
)INITRANS 169;

select dbms_metadata.get_ddl('TABLE','TBLMIPPOOLDETAIL') from dual;           
select dbms_metadata.get_dependent_ddl('INDEX','TBLMIPPOOLDETAIL') from dual;

spool off;
exit;