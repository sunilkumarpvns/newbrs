spool ELITEAAA_DBC_PARTITION_TBLUSERSTATISTICS.log 
                                            
--create tablespaces for TBLMRADIUSCDR             
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile    


CREATE TABLESPACE tbsuserstats01 DATAFILE '&&db_datafile/tbsuserstats01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats02 DATAFILE '&&db_datafile/tbsuserstats02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats03 DATAFILE '&&db_datafile/tbsuserstats03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats04 DATAFILE '&&db_datafile/tbsuserstats04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats05 DATAFILE '&&db_datafile/tbsuserstats05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats06 DATAFILE '&&db_datafile/tbsuserstats06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats07 DATAFILE '&&db_datafile/tbsuserstats07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats08 DATAFILE '&&db_datafile/tbsuserstats08.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats09 DATAFILE '&&db_datafile/tbsuserstats09.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats10 DATAFILE '&&db_datafile/tbsuserstats10.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats11 DATAFILE '&&db_datafile/tbsuserstats11.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats12 DATAFILE '&&db_datafile/tbsuserstats12.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats13 DATAFILE '&&db_datafile/tbsuserstats13.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats14 DATAFILE '&&db_datafile/tbsuserstats14.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats15 DATAFILE '&&db_datafile/tbsuserstats15.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats16 DATAFILE '&&db_datafile/tbsuserstats16.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats17 DATAFILE '&&db_datafile/tbsuserstats17.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats18 DATAFILE '&&db_datafile/tbsuserstats18.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats19 DATAFILE '&&db_datafile/tbsuserstats19.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats20 DATAFILE '&&db_datafile/tbsuserstats20.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats21 DATAFILE '&&db_datafile/tbsuserstats21.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats22 DATAFILE '&&db_datafile/tbsuserstats22.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats23 DATAFILE '&&db_datafile/tbsuserstats23.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats24 DATAFILE '&&db_datafile/tbsuserstats24.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats25 DATAFILE '&&db_datafile/tbsuserstats25.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats26 DATAFILE '&&db_datafile/tbsuserstats26.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats27 DATAFILE '&&db_datafile/tbsuserstats27.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats28 DATAFILE '&&db_datafile/tbsuserstats28.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats29 DATAFILE '&&db_datafile/tbsuserstats29.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats30 DATAFILE '&&db_datafile/tbsuserstats30.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsuserstats31 DATAFILE '&&db_datafile/tbsuserstats31.dbf' size 100M AUTOEXTEND ON NEXT 25M;

CREATE TABLESPACE tbsidxusernamestatistics  DATAFILE '&&db_datafile/tbsidxusernamestatistics.dbf'  size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsidxcallingidstatistics DATAFILE '&&db_datafile/tbsidxcallingidstatistics.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsidxreplymsgstatistics  DATAFILE '&&db_datafile/tbsidxreplymsgstatistics.dbf'  size 100M AUTOEXTEND ON NEXT 25M;

--Enter Schema Name of EliteAAA Product
connect &&username/&&password@&&NET_STR

set long 10000
set pagesize 0
select dbms_metadata.get_ddl('TABLE','TBLUSERSTATISTICS') from dual;
select dbms_metadata.get_dependent_ddl('INDEX','TBLUSERSTATISTICS') from dual; 
  
                                                                   
  SELECT COUNT(1) FROM TBLUSERSTATISTICS;                                     
                                                                   
  RENAME TBLUSERSTATISTICS to OLD_TBLUSERSTATISTICS;     

CREATE TABLE TBLUSERSTATISTICS
   (    USERSTATISTICSID NUMBER(*,0),
        CREATE_DATE TIMESTAMP (6),
        USER_NAME VARCHAR2(253),
        REPLY_MESSAGE VARCHAR2(253),
        NAS_IP_ADDRESS VARCHAR2(20),
        NAS_PORT NUMBER(10,0),
        SERVICE_TYPE VARCHAR2(50),
        FRAMED_PROTOCOL VARCHAR2(50),
        FRAMED_IP_ADDRESS VARCHAR2(20),
        FRAMED_IP_NETMASK VARCHAR2(20),
        FRAMED_ROUTING VARCHAR2(50),
        FILTER_ID VARCHAR2(50),
        FRAMED_MTU NUMBER(10,0),
        CALLBACK_NUMBER VARCHAR2(50),
        CALLBACK_ID VARCHAR2(50),
        FRAMED_ROUTE VARCHAR2(50),
        SESSION_TIMEOUT NUMBER(10,0),
        IDLE_TIMEOUT NUMBER(10,0),
        TERMINATION_ACTION VARCHAR2(50),
        CALLED_STATION_ID VARCHAR2(253),
        CALLING_STATION_ID VARCHAR2(253),
        NAS_IDENTIFIER VARCHAR2(50),
        PROXY_STATE NUMBER(10,0),
        NAS_PORT_TYPE VARCHAR2(50),
        PORT_LIMIT NUMBER(10,0),
        EVENT_TIMESTAMP NUMBER(14,0),
        NAS_PORT_ID VARCHAR2(50),
        CONNECT_INFO VARCHAR2(253),
        PARAM_STR0 VARCHAR2(253),
        PARAM_STR1 VARCHAR2(253),
        PARAM_STR2 VARCHAR2(253),
        PARAM_STR3 VARCHAR2(253),
        PARAM_STR4 VARCHAR2(253),
        PARAM_STR5 VARCHAR2(253),
        PARAM_STR6 VARCHAR2(253),
        PARAM_STR7 VARCHAR2(253),
        PARAM_STR8 VARCHAR2(253),
        PARAM_STR9 VARCHAR2(253),
        PARAM_INT0 NUMBER(*,0),
        PARAM_INT1 NUMBER(*,0),
        PARAM_INT2 NUMBER(*,0),
        PARAM_INT3 NUMBER(*,0),
        PARAM_INT4 NUMBER(*,0),
        PARAM_DATE0 TIMESTAMP (6),
        PARAM_DATE1 TIMESTAMP (6),
        PARAM_DATE2 TIMESTAMP (6),
        GROUPNAME VARCHAR2(60),
        USER_IDENTITY VARCHAR2(100),
        ESN_MEID VARCHAR2(16),
        CUI VARCHAR2(64),
        HA_IP VARCHAR2(64),
        BS_ID VARCHAR2(64),
        PCF_SGSN_AGW VARCHAR2(64),
        NAP_OPERATOR_CARRIER VARCHAR2(64),
        LOCATION VARCHAR2(255)
   )
PARTITION BY RANGE (CREATE_DATE)                                                                                   
INTERVAL (NUMTODSINTERVAL(1,'day')) store in (tbsuserstats01,tbsuserstats02,tbsuserstats03,tbsuserstats04,tbsuserstats05,tbsuserstats06,tbsuserstats07,tbsuserstats08,tbsuserstats09,tbsuserstats10,tbsuserstats11,tbsuserstats12,tbsuserstats13,tbsuserstats14,tbsuserstats15,tbsuserstats16,tbsuserstats17,tbsuserstats18,tbsuserstats19,tbsuserstats20,tbsuserstats21,tbsuserstats22,tbsuserstats23,tbsuserstats24,tbsuserstats25,tbsuserstats26,tbsuserstats27,tbsuserstats28,tbsuserstats29,tbsuserstats30,tbsuserstats31) 
(PARTITION p_first VALUES LESS THAN (TO_DATE('01-01-2015', 'DD-MM-YYYY'))) NOLOGGING;                                                                                                                                                                                                                                          
  
ALTER TABLE TBLUSERSTATISTICS INITRANS 169;         

CREATE INDEX IDX_UNAME_STAT ON TBLUSERSTATISTICS (USER_NAME) TABLESPACE tbsidxusernamestatistics NOLOGGING;
 
CREATE INDEX IDX_CALLING_ID_STAT ON TBLUSERSTATISTICS (CALLING_STATION_ID) TABLESPACE tbsidxcallingidstatistics NOLOGGING;

CREATE INDEX IDX_REPLYMSG_STAT ON TBLUSERSTATISTICS (REPLY_MESSAGE) TABLESPACE tbsidxreplymsgstatistics NOLOGGING;

ALTER INDEX IDX_UNAME_STAT INITRANS 169;

ALTER INDEX IDX_CALLING_ID_STAT INITRANS 169;

ALTER INDEX IDX_REPLYMSG_STAT INITRANS 169;

alter table TBLUSERSTATISTICS enable row movement;	

ALTER SEQUENCE SEQ_USERSTATISTICS CACHE 2000;

set long 10000
set pagesize 0
select dbms_metadata.get_ddl('TABLE','TBLUSERSTATISTICS') from dual;
select dbms_metadata.get_dependent_ddl('INDEX','TBLUSERSTATISTICS') from dual; 

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

create or replace procedure proc_eliteaaa_userstats_cln
as
  c_days_to_keep constant integer := 186;
  x_last_partition exception;
  pragma exception_init(x_last_partition, -14758);
begin
  for rec in (select table_name, partition_name
    from user_tab_partitions
      where table_name = 'TBLUSERSTATISTICS' and partition_name != 'P_FIRST'
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
    job_name => 'JOB_ELITEAAA_USERSTATS_CLN',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN PROC_ELITEAAA_USERSTATS_CLN(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=MONTHLY; BYMONTHDAY=2;BYHOUR=2;BYMINUTE=30;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined to Run on the second day of every month.');
END;
/  

select dbms_metadata.get_ddl('TABLE','TBLUSERSTATISTICS') from dual;
select dbms_metadata.get_dependent_ddl('INDEX','TBLUSERSTATISTICS') from dual;
select text from user_source where name='PROC_ELITEAAA_USERSTATS_CLN';

spool off;
exit;
