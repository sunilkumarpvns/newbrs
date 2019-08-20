spool ELITEAAA_DBC_PARTITION_TBLRADIUSCDR.log 
                                            
--create tablespaces for TBLRADIUSCDR             
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile                         
                                            

CREATE TABLESPACE tbsradiuscdr_01  DATAFILE '&&db_datafile/tbsradiuscdr_01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_02  DATAFILE '&&db_datafile/tbsradiuscdr_02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_03  DATAFILE '&&db_datafile/tbsradiuscdr_03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_04  DATAFILE '&&db_datafile/tbsradiuscdr_04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_05  DATAFILE '&&db_datafile/tbsradiuscdr_05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_06  DATAFILE '&&db_datafile/tbsradiuscdr_06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_07  DATAFILE '&&db_datafile/tbsradiuscdr_07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_08  DATAFILE '&&db_datafile/tbsradiuscdr_08.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_09  DATAFILE '&&db_datafile/tbsradiuscdr_09.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_10  DATAFILE '&&db_datafile/tbsradiuscdr_10.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsradiuscdr_11  DATAFILE '&&db_datafile/tbsradiuscdr_11.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsmradiuscdr_12 DATAFILE '&&db_datafile/tbsmradiuscdr_12.dbf' size 100M AUTOEXTEND ON NEXT 25M;

CREATE TABLESPACE tbsidxunameradiuscdr DATAFILE '&&db_datafile/tbsidxunameradiuscdr.dbf' size 100M AUTOEXTEND ON NEXT 25M;

CREATE TABLESPACE tbsidxsidradiuscdr DATAFILE '&&db_datafile/tbsidxsidradiuscdr.dbf' size 100M AUTOEXTEND ON NEXT 25M;

--Enter Schema Name of EliteAAA Product
connect &&username/&&password@&&NET_STR

                                                                   
set long 10000                                                     
set pagesize 0                                                     
select dbms_metadata.get_dependent_ddl('INDEX','TBLRADIUSCDR') from duAL;
                                                                   
SELECT DBMS_METADATA.GET_DDL('TABLE','TBLRADIUSCDR') FROM DUAL;          
                                                                   
  SELECT COUNT(1) FROM TBLRADIUSCDR;                                     
                                                                   
  RENAME TBLRADIUSCDR to OLD_TBLRADIUSCDR;                                     


CREATE TABLE TBLRADIUSCDR
   ( CDRID                  NUMERIC,
  CALL_START                TIMESTAMP,
  CALL_END                  TIMESTAMP,
  CREATE_DATE               TIMESTAMP,
  LAST_MODIFIED_DATE        TIMESTAMP,
  USER_NAME                 VARCHAR(253),
  NAS_IP_ADDRESS            VARCHAR(20),
  NAS_PORT                  NUMERIC(10),
  SERVICE_TYPE              VARCHAR(50),
  FRAMED_PROTOCOL           VARCHAR(50),
  FRAMED_IP_ADDRESS         VARCHAR(20),
  FRAMED_IP_NETMASK         VARCHAR(20),
  FRAMED_ROUTING            VARCHAR(50),
  FILTER_ID                 VARCHAR(50),
  FRAMED_MTU                NUMERIC(10),
  FRAMED_COMPRESSION        VARCHAR(50),
  LOGIN_IP_HOST             VARCHAR(20),
  LOGIN_SERVICE             VARCHAR(50),
  LOGIN_TCP_PORT            NUMERIC(10),
  CALLBACK_NUMBER           VARCHAR(50),
  CALLBACK_ID               VARCHAR(50),
  FRAMED_ROUTE              VARCHAR(50),
  FRAMED_IPX_NETWORK        VARCHAR(50),
  CLASS                     VARCHAR(253),
  VENDOR_SPECIFIC           VARCHAR(253),
  SESSION_TIMEOUT           NUMERIC(10),
  IDLE_TIMEOUT              NUMERIC(10),
  TERMINATION_ACTION        VARCHAR(50),
  CALLED_STATION_ID         VARCHAR(253),
  CALLING_STATION_ID        VARCHAR(253),
  NAS_IDENTIFIER            VARCHAR(50),
  PROXY_STATE               NUMERIC(10),
  LOGIN_LAT_SERVICE         VARCHAR(50),
  LOGIN_LAT_NODE            VARCHAR(50),
  LOGIN_LAT_GROUP           VARCHAR(50),
  FRAMED_APPLETALK_LINK     NUMERIC(10),
  FRAMED_APPLETALK_NETWORK  NUMERIC(10),
  FRAMED_APPLETALK_ZONE     VARCHAR(50),
  ACCT_STATUS_TYPE          VARCHAR(50),
  ACCT_DELAY_TIME           NUMERIC(10),
  ACCT_INPUT_OCTETS         NUMERIC(10),
  ACCT_OUTPUT_OCTETS        NUMERIC(10),
  ACCT_SESSION_ID           VARCHAR(253),
  ACCT_AUTHENTIC            VARCHAR(50),
  ACCT_SESSION_TIME         NUMERIC(10),
  ACCT_INPUT_PACKETS        NUMERIC(10),
  ACCT_OUTPUT_PACKETS       NUMERIC(10),
  ACCT_TERMINATE_CAUSE      VARCHAR(50),
  ACCT_MULTI_SESSION_ID     VARCHAR(253),
  ACCT_LINK_COUNT           NUMERIC(10),
  NAS_PORT_TYPE             VARCHAR(50),
  PORT_LIMIT                NUMERIC(10),
  LOGIN_LAT_PORT            NUMERIC(10),
  ACCT_INPUT_GIGAWORDS      NUMERIC(10),
  ACCT_OUTPUT_GIGAWORDS     NUMERIC(10),
  EVENT_TIMESTAMP           NUMERIC(14),
  NAS_PORT_ID               VARCHAR(50),
  CONNECT_INFO              VARCHAR(253),
  PARAM_STR0                VARCHAR(253),
  PARAM_STR1                VARCHAR(253),
  PARAM_STR2                VARCHAR(253),
  PARAM_STR3                VARCHAR(253),
  PARAM_STR4                VARCHAR(253),
  PARAM_STR5                VARCHAR(253),
  PARAM_STR6                VARCHAR(253),
  PARAM_STR7                VARCHAR(253),
  PARAM_STR8                VARCHAR(253),
  PARAM_STR9                VARCHAR(253),
  PARAM_INT0                NUMERIC,
  PARAM_INT1                NUMERIC,
  PARAM_INT2                NUMERIC,
  PARAM_INT3                NUMERIC,
  PARAM_INT4                NUMERIC,
  PARAM_DATE0               TIMESTAMP,
  PARAM_DATE1               TIMESTAMP,
  PARAM_DATE2               TIMESTAMP,
  GROUPNAME                 VARCHAR(60)) 
PARTITION BY RANGE (CREATE_DATE)
INTERVAL (NUMTOYMINTERVAL(1,'month')) store in (tbsradiuscdr_01,tbsradiuscdr_02,tbsradiuscdr_03,tbsradiuscdr_04,tbsradiuscdr_05,tbsradiuscdr_06,tbsradiuscdr_07,tbsradiuscdr_08,tbsradiuscdr_09,tbsradiuscdr_10,tbsradiuscdr_11,tbsmradiuscdr_12)
(PARTITION p_first VALUES LESS THAN (TO_DATE('01-01-2015', 'DD-MM-YYYY')))
NOLOGGING; 
     

 ALTER TABLE TBLRADIUSCDR  INITRANS 169;

 CREATE INDEX MCDR_UNAME_IDX ON TBLRADIUSCDR (USER_NAME)
 TABLESPACE tbsidxunameradiuscdr NOLOGGING;
 
 CREATE INDEX MCDR_SID_IDX ON TBLRADIUSCDR (ACCT_SESSION_ID)
 TABLESPACE tbsidxsidradiuscdr NOLOGGING;
  
 ALTER INDEX MCDR_UNAME_IDX INITRANS 169;
 ALTER INDEX MCDR_SID_IDX INITRANS 169;
 
 --alter table TBLRADIUSCDR enable row movement; 
  
 ALTER SEQUENCE SEQ_TBLRADIUSCDR  CACHE 2000;
  
  create or replace function get_high_value_as_date(
  p_table_name     in varchar2,
  p_partition_name in varchar2
) return date as
  v_high_value varchar2(1024);
  v_date        date;
begin
  select high_value into v_high_value from user_tab_partitions
    where table_name = upper(p_table_name)
      and partition_name = upper(p_partition_name);
  execute immediate 'select ' || v_high_value || ' from dual' into v_date;
  return v_date;
end;
/

create or replace procedure proc_eliteaaa_rcdr_cln
as
  c_days_to_keep constant integer := 186;
  x_last_partition exception;
  pragma exception_init(x_last_partition, -14758);
begin
  for rec in (select table_name, partition_name
    from user_tab_partitions
      where table_name = 'TBLRADIUSCDR' and partition_name != 'P_FIRST'
        and get_high_value_as_date(table_name, partition_name) <
        sysdate - c_days_to_keep) loop
    begin
	  dbms_output.put_line('alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes');
      execute immediate 'alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes';
    exception
      when x_last_partition then
        null;
    end;
  end loop;
end;
/

BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.create_job (
    job_name => 'JOB_ELITEAAA_RCDR_CLN',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN PROC_ELITEAAA_RCDR_CLN(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=MONTHLY; BYMONTHDAY=2;BYHOUR=2;BYMINUTE=10;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined to Run on the second day of every month.');
END;
/ 
  
set long 10000
set pagesize 0
select dbms_metadata.get_dependent_ddl('INDEX','TBLRADIUSCDR') from duAL;

SELECT DBMS_METADATA.GET_DDL('TABLE','TBLRADIUSCDR') FROM DUAL;

SELECT TEXT FROM USER_SOURCE WHERE NAME='PROC_ELITEAAA_RCDR_CLN';
 
  
spool off;  
exit;