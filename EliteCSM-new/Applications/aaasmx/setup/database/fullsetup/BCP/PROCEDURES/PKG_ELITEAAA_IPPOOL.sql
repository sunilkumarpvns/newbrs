spool PKG_ELITEAAA_IPPOOL_v6.6.x.log 

/*
Procedure Enhancement : v8
===========================
Release Notes:
1.) Introduce the Proper customized Logging functionality with LOG_LEVEL e.g. INFO,WARN
If IP Pool Stale or Reservation Cleanup exceeds the IPs 2000 then it will be consider WARN log
Below query will use in Manage Engine for proper monitoring of WARN Notification Events
SELECT * FROM TBL_IPPOOL_LOGGING WHERE LOG_LEVEL = 'WARN';
Table TBL_IPPOOL_LOGGING design in such a way to it's keep the history of last 48 hours only.
2.) Introduce the customized concurrency check functionality
Before executing the same procedure in another session it will check that whether same procedure are in runnable mode 
and if it found then next procedure will not start the execution from another session.
3.) Code change for looping code to correct the Loggin information
4.) Single Procedure for Business logic of SSR:Stale Session Removal Scheduler(21,41),
    RCS1 : Reservation Cleanup Scheduler1(11,31) and RCS2 : Reservation Cleanup Scheduler1(evey even minute)
5.) Single Procedure invoke on Every Minute
*/
	
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile 

CREATE TABLESPACE tbs_tbl_ippool_logging DATAFILE '&&db_datafile/tbs_tbl_ippool_logging.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_idx_ippool_logging DATAFILE '&&db_datafile/tbs_idx_ippool_logging.dbf' size 100M AUTOEXTEND ON NEXT 25M; 


--Enter Schema Name of EliteAAA Product                                     
connect &&username/&&password@&&NET_STR 

CREATE TABLE "TBL_IPPOOL_LOGGING" 
 ("CREATE_DATE" TIMESTAMP (6), 
  "PROC_NM" VARCHAR2(255 BYTE), 
  "DESCRIPTION" VARCHAR2(4000 BYTE), 
  "LOG_LEVEL" VARCHAR2(100 BYTE)
  )PARTITION BY RANGE (CREATE_DATE)
INTERVAL (NUMTODSINTERVAL(1,'day')) store in (tbs_tbl_ippool_logging)
(PARTITION p_first VALUES LESS THAN (TO_DATE('02-09-2015', 'DD-MM-YYYY')))INITRANS 100;

 CREATE INDEX "IDX_LOG_LEVEL" ON "TBL_IPPOOL_LOGGING" ("LOG_LEVEL") 
 tablespace tbs_idx_ippool_logging INITRANS 100;
  
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

create or replace procedure proc_aaa_cleanup_ip_logging
as
  c_days_to_keep constant integer := 2;
  x_last_partition exception;
  pragma exception_init(x_last_partition, -14758);
begin
  for rec in (select table_name, partition_name
    from user_tab_partitions
      where table_name = 'TBL_IPPOOL_LOGGING' and partition_name != 'P_FIRST'
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
    job_name => 'JOB_AAA_CLEANUP_IP_LOGGING',
    job_type => 'PLSQL_BLOCK',
    job_action => 'BEGIN PROC_AAA_CLEANUP_IP_LOGGING(); END;',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=WEEKLY;BYHOUR=03;BYMINUTE=32;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined for JOB_AAA_CLEANUP_IP_LOGGING.');
END;
/
     
create or replace PACKAGE PKG_ELITEAAA_IPPOOL
IS
      /*  ELITECSM IP POOL MODULE
          ELITECORE TECHNOLOGIES PVT. LTD. */
PROCEDURE SP_IPPOOL_MGMT;
PROCEDURE IPPOOLLOGGING(P_DAT TIMESTAMP,P_PROC_NM VARCHAR,P_DESC VARCHAR,P_LOG_LEVEL VARCHAR);
END PKG_ELITEAAA_IPPOOL;
/

create or replace PACKAGE BODY PKG_ELITEAAA_IPPOOL
IS
  /*  ELITECSM IP POOL MODULE
  ELITECORE TECHNOLOGIES PVT. LTD. */
PROCEDURE SP_IPPOOL_MGMT
IS
  CNT            NUMBER;
  CNT_LMT        NUMBER;
  I_CNT          NUMBER;
  V_STATE        CHAR(1);
  V_LST_UPD_TIME TIMESTAMP;
BEGIN
  --DBMS_LOCK.SLEEP(5);
  SELECT (
    CASE
      WHEN NVL(COUNT(*),0) = 0
      THEN 'I'
      WHEN NVL(COUNT(*),0) > 1
      THEN 'R'
    END)
  INTO V_STATE
  FROM USER_SCHEDULER_RUNNING_JOBS
  WHERE JOB_NAME='JOB_PKG_ELITEAAA_IPPOOL_MGMT';
  
  IF V_STATE = 'R' THEN   
        IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.SP_IPPOOL_MGMT' ,'IP Pool Scheduler found Runnable:=>'||V_STATE,'WARN');
        NULL;
  END IF;
  
  IF TO_NUMBER(TO_CHAR(SYSDATE,'MI')) IN (21,41) THEN
    --SSR : IP Pool Stale Session Removal Scheduler
    DBMS_OUTPUT.PUT_LINE('IP Pool Scheduler Condition satisfied for SSR :=>>'||TO_CHAR(SYSDATE,'MI') );
    BEGIN
      /*
      NEW BUSINESS LOGIC: IP Pool Stale Session Removal Scheduler
      Remove Session which are not updated since 5401 Seconds (i.e. 3 Interim missed of 30 minutes each)
      Execute this Scheduler at every 21 and 41 minutes of every hour (2 execution per hour)
      PRODUCT QRY :
      UPDATE tblmippooldetail
      SET assigned                                   ='N',
      reserved                                     ='N',
      last_updated_time                            = systimestamp
      WHERE reserved                                 ='Y'
      AND assigned                                   ='Y'
      AND last_updated_time + interval '5401' second < SYSTIMESTAMP
      AND rownum                                    <= 50
      */
      
        SELECT COUNT(*)
        INTO CNT
        FROM tblmippooldetail a
        WHERE a.reserved                                 ='Y'
        AND a.assigned                                   ='Y'
        AND a.last_updated_time + interval '5401' second < SYSTIMESTAMP;
        IF CNT                                          >= 2000 THEN
          IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.SSR' ,'IP Pool Stale Session Removal Scheduler found total count:=>'||CNT,'WARN');
        ELSE
          IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.SSR' ,'IP Pool Stale Session Removal Scheduler found total count:=>'||CNT,'INFO');
        END IF;
        ---CNT_LMT                                         := ROUND(CNT / 100,0);
        CNT_LMT := 0;
        LOOP
          EXIT
        WHEN CNT_LMT >= CNT;
          I_CNT      :=0;
          FOR REC_SSR IN
          (SELECT a.ippoolid,
            a.serialnumber
          FROM tblmippooldetail a
          WHERE a.reserved                                 ='Y'
          AND a.assigned                                   ='Y'
          AND a.last_updated_time + interval '5401' second < SYSTIMESTAMP
          AND rownum                                      <= 100
          )
          LOOP
            UPDATE tblmippooldetail a
            SET a.assigned        ='N',
              a.reserved          ='N',
              a.last_updated_time = systimestamp
            WHERE a.ippoolid      = REC_SSR.ippoolid
            AND a.serialnumber    = REC_SSR.serialnumber;
            I_CNT                := I_CNT + 1;
          END LOOP;
          --Debug on
          DBMS_OUTPUT.PUT_LINE('IP POOL BATCH :=>'||CNT_LMT);
          COMMIT WORK WRITE NOWAIT;
          CNT_LMT := CNT_LMT + I_CNT;
          IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.SSR' ,'IP POOL Stale Session Removal count:=>'||CNT_LMT,'INFO');
        END LOOP;

    EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE(SQLCODE||':-'||SQLERRM);
    END;
  ELSIF TO_NUMBER(TO_CHAR(SYSDATE,'MI')) IN (11,31) THEN
    --RCS1 : IP Pool Reservation Cleanup Scheduler1
    DBMS_OUTPUT.PUT_LINE('IP Pool Scheduler Condition satisfied for RCS1 :=>>'||TO_CHAR(SYSDATE,'MI') );
    BEGIN
      /*
      NEW BUSINESS LOGIC:
      i. Scheduler One : IP Pool Reservation Cleanup Scheduler1
      1. Free Only Reserved but Not Assigned IP Addresses and not updated since last 30 Seconds (i.e. Accounting Requests not received for Reserved IP Address
      within 30 Seconds )
      2. Execute this Scheduler at every 11 and 31 minutes of every hour (2 execution per hour)
      PRODUCT QRY :
      UPDATE tblmippooldetail
      SET reserved                                 ='N',
      last_updated_time                          = systimestamp
      WHERE reserved                               ='Y'
      AND assigned                                 ='N'
      AND last_updated_time + interval '30' second < SYSTIMESTAMP
      AND rownum                                  <= 50
      */
   
        SELECT COUNT(*)
        INTO CNT
        FROM tblmippooldetail a
        WHERE a.reserved                               ='Y'
        AND a.assigned                                 ='N'
        AND a.last_updated_time + interval '30' second < SYSTIMESTAMP;
        IF CNT                                        >= 2000 THEN
          IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.RCS1' ,'IP Pool Reservation Cleanup Scheduler1 found total count:=>'||CNT,'WARN');
        ELSE
          IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.RCS1' ,'IP Pool Reservation Cleanup Scheduler1 found total count:=>'||CNT,'INFO');
        END IF;
        --CNT_LMT                                       := ROUND(CNT / 100,0);
        CNT_LMT := 0;
        LOOP
          EXIT
        WHEN CNT_LMT >= CNT;
          I_CNT      :=0;
          FOR REC_RCS1 IN
          (SELECT a.ippoolid,
            a.serialnumber
          FROM tblmippooldetail a
          WHERE a.reserved                               ='Y'
          AND a.assigned                                 ='N'
          AND a.last_updated_time + interval '30' second < SYSTIMESTAMP
          AND rownum                                    <= 100
          )
          LOOP
            UPDATE tblmippooldetail a
            SET a.reserved        ='N',
              a.last_updated_time = systimestamp
            WHERE a.ippoolid      = REC_RCS1.ippoolid
            AND a.serialnumber    = REC_RCS1.serialnumber;
            I_CNT                := I_CNT + 1;
          END LOOP;
          --Debug on
          DBMS_OUTPUT.PUT_LINE('IP POOL BATCH :=>'||CNT_LMT);
          COMMIT WORK WRITE NOWAIT;
          CNT_LMT := CNT_LMT + I_CNT;
          IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.RCS1' ,'IP POOL Reservation Cleanup Scheduler1 count:=>'||CNT_LMT,'INFO');
        END LOOP;
   
    EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE(SQLCODE||':-'||SQLERRM);
    END;
    --ELSIF TO_NUMBER(TO_CHAR(SYSDATE,'MI')) IN (2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60) THEN
  ELSIF MOD(TO_NUMBER(TO_CHAR(SYSDATE,'MI')),2) = 0 THEN
    --RCS2 : IP Pool  Reservation Cleanup Scheduler2
    DBMS_OUTPUT.PUT_LINE('IP Pool Scheduler Condition satisfied for RCS2 :=>>'||TO_CHAR(SYSDATE,'MI') );
    BEGIN
      /*
      ii. Scheduler Two: IP Pool  Reservation Cleanup Scheduler2
      1. Check Ratio of Free IP against Total IP in every Pool. If Ratio of Free IP is less than 20%(30% vishal)
      then Free up IP Address which are Reserved but Not Assigned IP Address within 30 Seconds of Last Update Time
      Else
      Do Nothing
      2. Execute this Scheduler at every 2 minutes (30 Executions per Hour)
      */
   
        FOR REC_RCS2 IN
        (SELECT IPPOOLID,
          (
          CASE
            WHEN COUNT(
              CASE
                WHEN NVL(RESERVED,'N')='N'
                THEN 'N'
              END)*100/COUNT(
              CASE
                WHEN NVL(RESERVED,'N') IN ('Y','N')
                THEN '1'
              END) <=20
            THEN 'T'
            ELSE 'F'
          END) IP_FREE_RATIO
        FROM TBLMIPPOOLDETAIL
        WHERE reserved ='Y'
        AND assigned   ='N'
        GROUP BY IPPOOLID
        )
        LOOP
          IF REC_RCS2.IP_FREE_RATIO = 'T' THEN
            --PKG_ELITEAAA_IPPOOL.RCS1;
            --Debug on
            UPDATE tblmippooldetail
            SET reserved                                 ='N',
              last_updated_time                          = systimestamp
            WHERE ippoolid                               = REC_RCS2.IPPOOLID
            AND reserved                                 ='Y'
            AND assigned                                 ='N'
            AND last_updated_time + interval '30' second < SYSTIMESTAMP;
            COMMIT WORK WRITE NOWAIT;
            DBMS_OUTPUT.PUT_LINE('IP_FREE_RATIO :=>'||REC_RCS2.IP_FREE_RATIO);
            IPPOOLLOGGING(systimestamp,'PKG_ELITEAAA_IPPOOL.RCS2' ,'IP Pool Reservation Cleanup Scheduler2 found ratio:=>'||REC_RCS2.IP_FREE_RATIO||' For IPPOOLID:=>'||REC_RCS2.IPPOOLID,'WARN');
          ELSE
            NULL;
            DBMS_OUTPUT.PUT_LINE('IP_FREE_RATIO :=>'||REC_RCS2.IP_FREE_RATIO);
          END IF;
        END LOOP;
    
    EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE(SQLCODE||':-'||SQLERRM);
    END;
  ELSE
    DBMS_OUTPUT.PUT_LINE('IP Pool Scheduler Condition not satisfied :=>>'||TO_CHAR(SYSDATE,'MI') );
    NULL;
    --IPPOOLLOGGING(systimestamp,'SP_IPPOOL_MGMT','IP Pool Scheduler Condition not satisfied :==>>'||TO_CHAR(SYSDATE,'MI')||' Minute','WARN');
  END IF;
EXCEPTION
WHEN OTHERS THEN
  raise_application_error(-20001,'An error was encountered in - PROCEDURE - PKG_ELITEAAA_IPPOOL.SP_IPPOOL_MGMT '||SQLCODE||' -ERROR- '||SQLERRM);
END;
PROCEDURE IPPOOLLOGGING(
    P_DAT       TIMESTAMP,
    P_PROC_NM   VARCHAR,
    P_DESC      VARCHAR,
    P_LOG_LEVEL VARCHAR)
IS
BEGIN
  INSERT INTO TBL_IPPOOL_LOGGING VALUES
    (P_DAT,P_PROC_NM,P_DESC,P_LOG_LEVEL
    );
  COMMIT WORK WRITE NOWAIT;
EXCEPTION
WHEN OTHERS THEN
  raise_application_error(-20001,'An error was encountered in - PROCEDURE - PKG_ELITEAAA_IPPOOL.IPPOOLLOGGING '||SQLCODE||' -ERROR- '||SQLERRM);
END;
END PKG_ELITEAAA_IPPOOL;
/

	
   BEGIN   
   DBMS_SCHEDULER.CREATE_JOB (
    JOB_NAME => 'JOB_PKG_ELITEAAA_IPPOOL_MGMT',
    JOB_TYPE => 'PLSQL_BLOCK',
    JOB_ACTION => 'begin PKG_ELITEAAA_IPPOOL.SP_IPPOOL_MGMT(); end;',
    START_DATE => trunc(SYSTIMESTAMP),
    REPEAT_INTERVAL => 'FREQ=MINUTELY;interval=1;',
    END_DATE => NULL,
    ENABLED => TRUE,
    COMMENTS => 'JOB DEFINED FOR PKG_ELITEAAA_IPPOOL.SP_IPPOOL_MGMT');
  END;
  /
  
--Generate statistics
begin 
         DBMS_STATS.GATHER_TABLE_STATS (
           ownname => UPPER('"&USERNAME"'),
           tabname => '"TBLMIPPOOLDETAIL"',
            cascade => true
           );
end;
/
  
set linesize 32767
set pagesize 10000
COLUMN JOB_NAME          FORMAT A25
COLUMN NEXT_RUN_DATE     FORMAT A20
COLUMN LAST_RUN_DURATION FORMAT A20
COLUMN MAX_RUN_DURATION  FORMAT A5
COLUMN RUN_COUNT         FORMAT 999999
COLUMN FAILURE_COUNT     FORMAT 999999
COLUMN COMMENTS          FORMAT A15
COLUMN JOB_ACTION        FORMAT A15
COLUMN ENABLED           FORMAT A15 
COLUMN REPEAT_INTERVAL      FORMAT A15
SELECT JOB_NAME,NEXT_RUN_DATE,LAST_RUN_DURATION,MAX_RUN_DURATION,RUN_COUNT,FAILURE_COUNT,JOB_ACTION,ENABLED,REPEAT_INTERVAL FROM  USER_SCHEDULER_JOBS;

spool off;