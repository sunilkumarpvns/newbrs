--creating schema for ippol
--DROP SCHEMA IF EXISTS PKG_ELITEAAA_IPPOOL CASCADE;
--CREATE SCHEMA PKG_ELITEAAA_IPPOOL;
--TABLE tbl_ippool_logging

CREATE TABLE tbl_ippool_logging
(
    create_date timestamp without time zone,
    proc_nm character varying(255) ,
    description character varying(4000) ,
    log_level character varying(100) 
);

CREATE INDEX idx_log_level ON tbl_ippool_logging ("log_level");

-- FUNCTION: pkg_eliteaaa_ippool_ippoollogging(timestamp with time zone, text, text, text)
CREATE OR REPLACE FUNCTION pkg_eliteaaa_ippool_ippoollogging(
	p_dat timestamp with time zone,
	p_proc_nm text,
	p_desc text,
	p_log_level text)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE 
AS $function$
BEGIN
  INSERT INTO TBL_IPPOOL_LOGGING VALUES (P_DAT,P_PROC_NM,P_DESC,P_LOG_LEVEL
    );
EXCEPTION
WHEN OTHERS THEN
  RAISE EXCEPTION '%', 'An error was encountered in - PROCEDURE - PKG_ELITEAAA_IPPOOL_IPPOOLLOGGING '
||SQLSTATE||' -ERROR- '||SQLERRM;
END;

$function$;

--PKG_IPPOOL_MGMT

-- FUNCTION: pkg_eliteaaa_ippool.sp_ippool_mgmt()

-- DROP FUNCTION pkg_eliteaaa_ippool.sp_ippool_mgmt();

CREATE OR REPLACE FUNCTION pkg_eliteaaa_ippool_sp_ippool_mgmt(
  )
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100.0
    VOLATILE 
AS $function$
DECLARE

  CNT            bigint;
  CNT_LMT        bigint;
  I_CNT          bigint;
  V_STATE        char(1);
  V_LST_UPD_TIME TIMESTAMP;
  REC_SSR RECORD;
  REC_RCS1 RECORD;
  REC_RCS2 RECORD;

BEGIN
  /*
  SELECT(
    CASE
      WHEN coalesce(COUNT(*),0) = 0
      THEN 'I'
      WHEN coalesce(COUNT(*),0) > 1
      THEN 'R'
    END)
  INTO V_STATE
  FROM USER_SCHEDULER_RUNNING_JOBS
  WHERE JOB_NAME='JOB_PKG_ELITEAAA_IPPOOL_MGMT';
  
  IF V_STATE = 'R' THEN   
        perform pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.SP_IPPOOL_MGMT' ,'IP Pool Scheduler found Runnable:=>'||V_STATE,'WARN');
        NULL;
  END IF;
  */
  IF (TO_CHAR(clock_timestamp(),'MI'))::integer IN (21,41) THEN
    --SSR : IP Pool Stale Session Removal Scheduler
    RAISE NOTICE 'IP Pool Scheduler Condition satisfied for SSR :=>>%', TO_CHAR(clock_timestamp(),'MI');
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
        AND a.last_updated_time + interval '5401' second < CURRENT_TIMESTAMP;
        IF CNT                                          >= 2000 THEN
          perform  pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.SSR' ,'IP Pool Stale Session Removal Scheduler found total count:=>'||CNT,'WARN');
        ELSE
          perform  pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.SSR' ,'IP Pool Stale Session Removal Scheduler found total count:=>'||CNT,'INFO');
        END IF;
        ---CNT_LMT                                         := ROUND(CNT / 100,0);
        CNT_LMT := 0;
        LOOP
          EXIT
        WHEN CNT_LMT >= CNT;
          I_CNT      :=0;
          FOR REC_SSR IN (SELECT a.ippoolid,
            a.serialnumber
          FROM tblmippooldetail a
          WHERE a.reserved                                 ='Y'
          AND a.assigned                                   ='Y'
          AND a.last_updated_time + interval '5401' second < CURRENT_TIMESTAMP 
           LIMIT 100)
          LOOP
            UPDATE tblmippooldetail 
            SET assigned        ='N',
              reserved          ='N',
              last_updated_time = CURRENT_TIMESTAMP
            WHERE ippoolid      = REC_SSR.ippoolid
            AND serialnumber    = REC_SSR.serialnumber;
            I_CNT                := I_CNT + 1;
          END LOOP;
          --Debug on
          RAISE NOTICE 'IP POOL BATCH :=>%', CNT_LMT;
          CNT_LMT := CNT_LMT + I_CNT;
            perform pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.SSR' ,'IP POOL Stale Session Removal count:=>'||CNT_LMT,'INFO');
        END LOOP;

    EXCEPTION
    WHEN OTHERS THEN
      RAISE NOTICE '%:-%', SQLSTATE, SQLERRM;
    END;
  ELSIF (TO_CHAR(clock_timestamp(),'MI'))::integer IN (11,31) THEN
    --RCS1 : IP Pool Reservation Cleanup Scheduler1
    RAISE NOTICE 'IP Pool Scheduler Condition satisfied for RCS1 :=>>%', TO_CHAR(clock_timestamp(),'MI');
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
        AND a.last_updated_time + interval '30' second < CURRENT_TIMESTAMP;
        IF CNT                                        >= 2000 THEN
            perform pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.RCS1' ,'IP Pool Reservation Cleanup Scheduler1 found total count:=>'||CNT,'WARN');
        ELSE
            perform pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.RCS1' ,'IP Pool Reservation Cleanup Scheduler1 found total count:=>'||CNT,'INFO');
        END IF;
        --CNT_LMT                                       := ROUND(CNT / 100,0);
        CNT_LMT := 0;
        LOOP
          EXIT
        WHEN CNT_LMT >= CNT;
          I_CNT      :=0;
          FOR REC_RCS1 IN (SELECT a.ippoolid,
            a.serialnumber
          FROM tblmippooldetail a
          WHERE a.reserved                               ='Y'
          AND a.assigned                                 ='N'
          AND a.last_updated_time + interval '30' second < CURRENT_TIMESTAMP 
           LIMIT 100)
          LOOP
            UPDATE tblmippooldetail 
            SET reserved        ='N',
              last_updated_time = CURRENT_TIMESTAMP
            WHERE ippoolid      = REC_RCS1.ippoolid
            AND serialnumber    = REC_RCS1.serialnumber;
            I_CNT                := I_CNT + 1;
          END LOOP;
          --Debug on
          RAISE NOTICE 'IP POOL BATCH :=>%', CNT_LMT;
          CNT_LMT := CNT_LMT + I_CNT;
          select pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.RCS1' ,'IP POOL Reservation Cleanup Scheduler1 count:=>'||CNT_LMT,'INFO');
        END LOOP;
   
    EXCEPTION
    WHEN OTHERS THEN
      RAISE NOTICE '%:-%', SQLSTATE, SQLERRM;
    END;
    --ELSIF TO_NUMBER(TO_CHAR(SYSDATE,'MI')) IN (2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60) THEN
  ELSIF MOD((TO_CHAR(clock_timestamp(),'MI'))::integer,2) = 0 THEN
    --RCS2 : IP Pool  Reservation Cleanup Scheduler2
    RAISE NOTICE 'IP Pool Scheduler Condition satisfied for RCS2 :=>>%', TO_CHAR(clock_timestamp(),'MI');
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
                WHEN coalesce(RESERVED,'N')='N'
                THEN 'N'
              END)*100/COUNT(
              CASE
                WHEN coalesce(RESERVED,'N') IN ('Y','N')
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
              last_updated_time                          = CURRENT_TIMESTAMP
            WHERE ippoolid                               = REC_RCS2.IPPOOLID
            AND reserved                                 ='Y'
            AND assigned                                 ='N'
            AND last_updated_time + interval '30' second < CURRENT_TIMESTAMP;
           
            RAISE NOTICE 'IP_FREE_RATIO :=>%', REC_RCS2.IP_FREE_RATIO;
           perform  pkg_eliteaaa_ippool_ippoollogging(CURRENT_TIMESTAMP,'PKG_ELITEAAA_IPPOOL.RCS2' ,'IP Pool Reservation Cleanup Scheduler2 found ratio:=>'||REC_RCS2.IP_FREE_RATIO||' For IPPOOLID:=>'||REC_RCS2.IPPOOLID,'WARN');
          ELSE
            NULL;
            RAISE NOTICE 'IP_FREE_RATIO :=>%', REC_RCS2.IP_FREE_RATIO;
          END IF;
        END LOOP;
    
    EXCEPTION
    WHEN OTHERS THEN
      RAISE NOTICE '%:-%', SQLSTATE, SQLERRM;
    END;
  ELSE
    RAISE NOTICE 'IP Pool Scheduler Condition not satisfied :=>>%', TO_CHAR(clock_timestamp(),'MI');
    NULL;
    --pkg_eliteaaa_ippool_ippoollogging(systimestamp,'SP_IPPOOL_MGMT','IP Pool Scheduler Condition not satisfied :==>>'||TO_CHAR(SYSDATE,'MI')||' Minute','WARN');
  END IF;
EXCEPTION
WHEN OTHERS THEN
  RAISE EXCEPTION '%', 'An error was encountered in - PROCEDURE - PKG_ELITEAAA_IPPOOL.SP_IPPOOL_MGMT '
||SQLSTATE||' -ERROR- '||SQLERRM;
END;

$function$;

--Scheduler for IPPOOL

DO $$
DECLARE
    jid integer;
    scid integer;
BEGIN
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'JOB_PKG_ELITEAAA_IPPOOL_MGMT'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'Step1'::text, true, 's'::character(1),
    ''::text, 'eliteaaa'::name, 'f'::character(1),
    'select pkg_eliteaaa_ippool_SP_IPPOOL_MGMT();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'sche1'::text, ''::text, true,
    CURRENT_TIMESTAMP, null,
    -- Minutes
    ARRAY[true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]::boolean[],
    -- Hours
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Week days
    ARRAY[false, false, false, false, false, false, false]::boolean[],
    -- Month days
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Months
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false]::boolean[]
) RETURNING jscid INTO scid;
END
$$;

update pgagent.pga_jobstep set jstdbname=(select current_database());

COMMIT;
