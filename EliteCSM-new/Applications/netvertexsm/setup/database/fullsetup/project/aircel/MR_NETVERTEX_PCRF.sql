spool MR_NETVERTEX_PCRF.log

--code redesign for removeSubscriberSessions
CREATE OR REPLACE PROCEDURE SP_STALE_SESSIONS_SUBSCRIBER 
as
  BEGIN
  
      /*  EliteCSM NetVertex Module 
          EliteCore Technologies Pvt. Ltd. */
      
    FOR REC IN (SELECT CORESESSIONID FROM TBLMCORESESSIONS WHERE LASTUPDATETIME < SYSTIMESTAMP - INTERVAL '24' HOUR)
    LOOP
    
    DELETE FROM TBLMSESSIONRULE R WHERE R.CORESESSIONID = REC.CORESESSIONID;
   
    DELETE FROM TBLMCORESESSIONS C WHERE C.CORESESSIONID = REC.CORESESSIONID;
    
    COMMIT;    
    
    END LOOP;
   
END;
/

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_STALE_SESSIONS_SPR',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'SP_STALE_SESSIONS_SUBSCRIBER',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=DAILY; BYHOUR=5; BYMINUTE=4;',   
   end_date           =>   NULL,
    enabled           =>   TRUE,
   comments           =>  'Deleting Sbbscriber session which are older than 24 hours....');
END;
/

--change the repeat_interval for cc
/*
BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.drop_job (job_name => 'JOB_RESET_BOD_STATUS');
END;
/ 						 

BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.CREATE_JOB (
    job_name => 'JOB_RESET_BOD_STATUS',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN PROC_RESET_BOD_STATUS(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=MINUTELY;INTERVAL=41;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined entirely by the CREATE JOB procedure.');
END;
/
*/

--change the repeat_interval for JOB_RESET_ADDON_STATUS
/*
--NA PROCEDURE						 
BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.drop_job (job_name => 'JOB_RESET_ADDON_STATUS');
END;
/ 


BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.create_job (
    job_name => 'JOB_RESET_ADDON_STATUS',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN PROC_RESET_ADDON_STATUS(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=MINUTELY;INTERVAL=31;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined entirely by the CREATE JOB procedure.');
END;
/ 
*/
---table design for NOTIFICATION HISTORY
  create table TBLMNOTIFICATIONHISTORY
  as
  SELECT * FROM TBLMNOTIFICATIONQUEUE
  WHERE 1 = 2;
    
---procedure for tuning workaround   nv_notification_queue
create or replace procedure sp_nv_notificationhistory
  as
  begin
  
  /*  EliteCSM - NetVertex Module  */
  
  FOR REC IN (SELECT  /*+ index(B IDX_NOTICATIONQUEUE_SELECT ) */ NOTIFICATIONID FROM TBLMNOTIFICATIONQUEUE B WHERE B.EMAILSTATUS != 'PENDING' AND SMSSTATUS != 'PENDING')
  LOOP
  
  INSERT INTO  TBLMNOTIFICATIONHISTORY
  SELECT * FROM TBLMNOTIFICATIONQUEUE I WHERE I.NOTIFICATIONID =  REC.NOTIFICATIONID;
  
  DELETE FROM TBLMNOTIFICATIONQUEUE D WHERE D.NOTIFICATIONID =  REC.NOTIFICATIONID;
  
  END LOOP;
  COMMIT; 
  
  end;
/
    
BEGIN
    DBMS_SCHEDULER.drop_job (job_name => 'JOB_SP_NV_NOTIFICATIONHISTORY');
END;
/  
	
-- Scheduler for sp_nv_notificationhistory  
BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.create_job (
    job_name => 'JOB_SP_NV_NOTIFICATIONHISTORY',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN SP_NV_NOTIFICATIONHISTORY(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=HOURLY;BYMINUTE=28;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined entirely by the CREATE JOB procedure.');
END;
/ 

-- PURGEING FOR NOTIFICATIONHISTORY 

Create or replace Procedure SP_PURGE_NOTIFICATIONHISTORY
as
begin

      /*  EliteCSM NetVertex Module 
          EliteCore Technologies Pvt. Ltd. */
		  
--Purge the Data Which is Older than 15 days
DELETE FROM TBLMNOTIFICATIONHISTORY WHERE TIMESTAMP + 15 < SYSDATE;

commit;

end;
/

BEGIN
    DBMS_SCHEDULER.drop_job (job_name => 'JOB_PURGE_NOTIFICATIONHISTORY');
END;
/  

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_PURGE_NOTIFICATIONHISTORY',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'SP_PURGE_NOTIFICATIONHISTORY',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=DAILY;BYHOUR=4;BYMINUTE=30;',   
   end_date           =>   NULL,
   enabled           =>   TRUE,
   comments           =>  'JOB_PURGE_NOTIFICATIONHISTORY...');
END;
/


-- PURGING THE CDR PARTITION CONFIGURATION

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

create or replace procedure proc_pcrf_cleanup_cdr
as
  c_days_to_keep constant integer := 15;
  x_last_partition exception;
  pragma exception_init(x_last_partition, -14758);
begin
  for rec in (select table_name, partition_name
    from user_tab_partitions
      where table_name = 'TBLCDR' and partition_name != 'P_FIRST'
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
    DBMS_SCHEDULER.drop_job (job_name => 'JOB_PCRF_CLEANUP_CDR');
END;
/  

BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.create_job (
    job_name => 'JOB_PCRF_CLEANUP_CDR',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN PROC_PCRF_CLEANUP_CDR(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=DAILY;BYHOUR=03;BYMINUTE=32;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined for JOB_PCRF_CLEANUP_CDR.');
END;
/ 


-- Monitoring Command for sp_nv_notificationhistory
set linesize 32767
set pagesize 10000
COLUMN JOB_NAME          FORMAT A25
COLUMN NEXT_RUN_DATE     FORMAT A20
COLUMN LAST_RUN_DURATION FORMAT A20
COLUMN MAX_RUN_DURATION  FORMAT A5
COLUMN RUN_COUNT         FORMAT 999999
COLUMN COMMENTS          FORMAT A15
SELECT JOB_NAME,NEXT_RUN_DATE,LAST_RUN_DURATION,MAX_RUN_DURATION,RUN_COUNT,COMMENTS FROM  USER_SCHEDULER_JOBS;

-- Check Invalid Objects
SELECT OBJECT_NAME FROM USER_OBJECTS WHERE STATUS != 'VALID';

spool off;


