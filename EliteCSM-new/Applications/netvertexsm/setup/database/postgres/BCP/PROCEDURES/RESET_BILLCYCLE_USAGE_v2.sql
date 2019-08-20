SET client_encoding TO 'UTF8';

\set ON_ERROR_STOP ON



CREATE OR REPLACE FUNCTION sp_reset_billcycle_usage()
    RETURNS void
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE

/*
ETPL | CSM Dept
NETVERTEX-1614 : DB scheduler for Re-set billing cycle usage
*/
  CNT       bigint := 0;
  SUC       bigint := 0;
  STIME     bigint ; --:= DBMS_UTILITY.GET_TIME;
  ETIME     bigint;
  TME_TAKEN bigint;
  INST      bigint := 1; --DBMS_UTILITY.CURRENT_INSTANCE;
  P_CNT1    bigint := 0;
  P_CNT2    bigint := 0;
  V_CNT 	bigint := 0;
  REC_B RECORD;
  REC_U RECORD;

BEGIN
  -- JIRA : NETVERTEX-2028
  --select PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLM_USAGE_HISTORY') INTO P_CNT1;
  --select PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLM_USAGE') INTO P_CNT2;
  SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into STIME;
  FOR REC_B IN (SELECT * FROM TBLM_RESET_USAGE_REQ B
                WHERE B.BILLING_CYCLE_DATE <= CURRENT_TIMESTAMP
                AND B.STATUS ='P'
  )
  LOOP
  CNT := CNT + 1;
    FOR REC_U IN (SELECT    *
    FROM TBLM_USAGE U
    WHERE U.SUBSCRIBER_ID =REC_B.SUBSCRIBER_IDENTITY
    AND U.PACKAGE_ID=REC_B.PACKAGE_ID
    )
    LOOP
      INSERT
      INTO TBLM_USAGE_HISTORY VALUES (
          CURRENT_TIMESTAMP,
          REC_U.ID ,
          REC_U.SUBSCRIBER_ID ,
          REC_U.PACKAGE_ID ,
          REC_U.SUBSCRIPTION_ID ,
          REC_U.QUOTA_PROFILE_ID ,
          REC_U.SERVICE_ID ,
          REC_U.DAILY_TOTAL ,
          REC_U.DAILY_UPLOAD ,
          REC_U.DAILY_DOWNLOAD ,
          REC_U.DAILY_TIME ,
          REC_U.WEEKLY_TOTAL ,
          REC_U.WEEKLY_UPLOAD ,
          REC_U.WEEKLY_DOWNLOAD ,
          REC_U.WEEKLY_TIME ,
          REC_U.BILLING_CYCLE_TOTAL ,
          REC_U.BILLING_CYCLE_UPLOAD ,
          REC_U.BILLING_CYCLE_DOWNLOAD ,
          REC_U.BILLING_CYCLE_TIME ,
          REC_U.CUSTOM_TOTAL ,
          REC_U.CUSTOM_UPLOAD ,
          REC_U.CUSTOM_DOWNLOAD ,
          REC_U.CUSTOM_TIME ,
          REC_U.DAILY_RESET_TIME ,
          REC_U.WEEKLY_RESET_TIME ,
          REC_U.CUSTOM_RESET_TIME ,
          REC_U.BILLING_CYCLE_RESET_TIME,
          REC_U.LAST_UPDATE_TIME
        );
       IF REC_B.BILLING_CYCLE_DATE <= CURRENT_TIMESTAMP THEN
      UPDATE TBLM_USAGE
      SET BILLING_CYCLE_TOTAL  = 0,
        BILLING_CYCLE_UPLOAD   = 0,
        BILLING_CYCLE_DOWNLOAD = 0,
        BILLING_CYCLE_TIME     = 0,
		LAST_UPDATE_TIME = CURRENT_TIMESTAMP
      WHERE SUBSCRIBER_ID        = REC_U.SUBSCRIBER_ID
      AND PACKAGE_ID             = REC_U.PACKAGE_ID;
    ELSIF REC_B.BILLING_CYCLE_DATE IS NULL THEN
    DELETE from TBLM_USAGE D
    WHERE D.SUBSCRIBER_ID        = REC_U.SUBSCRIBER_ID
      AND D.PACKAGE_ID             = REC_U.PACKAGE_ID;
    END IF;
    END LOOP;
    UPDATE TBLM_RESET_USAGE_REQ
	SET STATUS  ='S'
	WHERE BILLING_CYCLE_ID = REC_B.BILLING_CYCLE_ID
	AND STATUS             ='P';
	GET DIAGNOSTICS V_CNT = ROW_COUNT;
    SUC := SUC + V_CNT;
 -- COMMIT;
  END LOOP;
  SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into ETIME;
  TME_TAKEN := ETIME - STIME;
  IF P_CNT1 = 1 AND P_CNT2 = 1 THEN
    P_CNT1 := 1;
  ELSE
    P_CNT1 := 0;
  END IF;
 	perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_RESET_BILLCYCLE_USAGE',TME_TAKEN,P_CNT1);
END;
$BODY$;

DO $$
DECLARE
    jid integer;
    scid integer;
	username varchar;
BEGIN
select user into username; --select current user(Application user)
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'JOB_SP_RESET_BILLCYCLE_USAGE'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'JOB_SP_RESET_BILLCYCLE_USAGE-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
     'set search_path = '|| username ||';
select sp_reset_billcycle_usage();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-21 15:49:48+05:30'::timestamp with time zone, '2020-07-21 15:49:44+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Hours
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true]::boolean[],
    -- Week days
    ARRAY[false, false, false, false, false, false, false]::boolean[],
    -- Month days
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Months
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false]::boolean[]
) RETURNING jscid INTO scid;
END
$$;
