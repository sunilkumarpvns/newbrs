spool ELITEAAA_DBC_PARTITION_TBLDYNAUTHREQUESTS.log 
                                            
--create tablespaces for TBLDYNAUTHREQUESTS             
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile                         

CREATE TABLESPACE tbldynauthrequests_01  DATAFILE '&&db_datafile/tbldynauthrequests_01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbldynauthrequests_02  DATAFILE '&&db_datafile/tbldynauthrequests_02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbldynauthrequests_03  DATAFILE '&&db_datafile/tbldynauthrequests_03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbldynauthrequests_04  DATAFILE '&&db_datafile/tbldynauthrequests_04.dbf' size 100M AUTOEXTEND ON NEXT 25M;

--Enter Schema Name of EliteAAA Product
connect &&username/&&password@&&NET_STR
                                                                   
set long 10000                                                     
set pagesize 0                                                     
select dbms_metadata.get_dependent_ddl('INDEX','TBLDYNAUTHREQUESTS') from duAL;
                                                                   
SELECT DBMS_METADATA.GET_DDL('TABLE','TBLDYNAUTHREQUESTS') FROM DUAL;          
                                                                   
  SELECT COUNT(1) FROM TBLDYNAUTHREQUESTS;                                     
                                                                   
  RENAME TBLDYNAUTHREQUESTS to OLD_TBLDYNAUTHREQUESTS;                                     


CREATE TABLE TBLDYNAUTHREQUESTS
(
  SLNO NUMERIC PRIMARY KEY,
  PACKETTYPE INT NOT NULL,
  USERID VARCHAR(50),
  ACCTSESSIONID VARCHAR(50),
  NASIPADDRESS VARCHAR(50),
  AVPSTRINGPAIR VARCHAR(2000),
  RESPONSECODE NUMERIC(10) default -1,
  TRIEDCOUNT NUMERIC(10) default 0,
  STARTTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ENDTIME TIMESTAMP
)
PARTITION BY RANGE (ENDTIME)
INTERVAL (NUMTOYMINTERVAL(1,'month')) store in (tbldynauthrequests_01,tbldynauthrequests_02,tbldynauthrequests_03,tbldynauthrequests_04)
(PARTITION p_first VALUES LESS THAN (TO_DATE('01-01-2015', 'DD-MM-YYYY')))
NOLOGGING; 
     
ALTER TABLE TBLDYNAUTHREQUESTS DISABLE PRIMARY KEY;

CREATE UNIQUE INDEX IDX_PK_SLNO ON TBLDYNAUTHREQUESTS(SLNO) INITRANS 169 NOLOGGING; 

ALTER TABLE TBLDYNAUTHREQUESTS ENABLE PRIMARY KEY;

 ALTER TABLE TBLDYNAUTHREQUESTS  INITRANS 169;

 alter table TBLDYNAUTHREQUESTS enable row movement; 
 
 CREATE SEQUENCE  SEQ_DYNAUTHREQUESTS  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 20 CACHE 20 NOORDER  NOCYCLE ;
 
 ALTER SEQUENCE SEQ_DYNAUTHREQUESTS CACHE 1000;
  
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

create or replace procedure proc_eliteaaa_dynauthreq_cln
as
  c_days_to_keep constant integer := 30;
  x_last_partition exception;
  pragma exception_init(x_last_partition, -14758);
begin
  for rec in (select table_name, partition_name
    from user_tab_partitions
      where table_name = 'TBLDYNAUTHREQUESTS' and partition_name != 'P_FIRST'
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
    job_name => 'JOB_ELITEAAA_DYNAUTHREQ_CLN',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN PROC_ELITEAAA_DYNAUTHREQ_CLN(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=DAILY;BYHOUR=4;BYMINUTE=16;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined to Run on the second day of every month.');
END;
/ 
  
set long 10000
set pagesize 0
select dbms_metadata.get_dependent_ddl('INDEX','TBLDYNAUTHREQUESTS') from duAL;

SELECT DBMS_METADATA.GET_DDL('TABLE','TBLDYNAUTHREQUESTS') FROM DUAL;

SELECT TEXT FROM USER_SOURCE WHERE NAME='PROC_ELITEAAA_DYNAUTHREQ_CLN';
 
  
spool off;  
exit;