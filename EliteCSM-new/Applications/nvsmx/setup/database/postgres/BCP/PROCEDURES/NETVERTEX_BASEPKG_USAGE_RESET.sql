\set ON_ERROR_STOP ON

CREATE OR REPLACE FUNCTION sp_pcrf_basepkg_reset_trn(sys_day bigint)
    RETURNS void
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE

  CNT       bigint := 0;
  SUC       bigint := 0;
  STIME     bigint;
  ETIME     bigint;
  TME_TAKEN bigint;
  INST      bigint := 1; --DBMS_UTILITY.CURRENT_INSTANCE;
  P_CNT1    bigint := 0;
  P_CNT2    bigint := 0;
  V_CNT		bigint := 0;
  REC_S RECORD;
  REC_U RECORD;

BEGIN
 SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into STIME;
  /* JIRA : NETVERTEX-2028 */
   FOR REC_S IN (SELECT SUBSCRIBERIDENTITY,BILLINGDATE FROM TBLM_SUBSCRIBER  WHERE BILLINGDATE = SYS_DAY)
    LOOP
      CNT := CNT + 1;
      FOR REC_U IN (SELECT ID ,
          SUBSCRIBER_ID ,
          PACKAGE_ID ,
          SUBSCRIPTION_ID ,
          QUOTA_PROFILE_ID ,
          SERVICE_ID ,
      DAILY_TOTAL      ,
        DAILY_UPLOAD     ,
          DAILY_DOWNLOAD   ,
          DAILY_TIME       ,
          WEEKLY_TOTAL     ,
          WEEKLY_UPLOAD    ,
          WEEKLY_DOWNLOAD  ,
          WEEKLY_TIME      ,
          BILLING_CYCLE_TOTAL ,
          BILLING_CYCLE_UPLOAD ,
          BILLING_CYCLE_DOWNLOAD ,
          BILLING_CYCLE_TIME ,
      DAILY_RESET_TIME ,
          WEEKLY_RESET_TIME,
      BILLING_CYCLE_RESET_TIME,
          LAST_UPDATE_TIME
        FROM TBLT_USAGE U
        WHERE U.SUBSCRIBER_ID  = REC_S.SUBSCRIBERIDENTITY
        AND U.SUBSCRIPTION_ID IS NULL
      )
      LOOP
        INSERT
        INTO TBLT_USAGE_HISTORY(
            CREATE_DATE,
            ID ,
            SUBSCRIBER_ID ,
            PACKAGE_ID ,
            SUBSCRIPTION_ID ,
            QUOTA_PROFILE_ID ,
            SERVICE_ID ,
      DAILY_TOTAL      ,
          DAILY_UPLOAD     ,
          DAILY_DOWNLOAD   ,
          DAILY_TIME       ,
          WEEKLY_TOTAL     ,
          WEEKLY_UPLOAD    ,
          WEEKLY_DOWNLOAD  ,
          WEEKLY_TIME      ,
          BILLING_CYCLE_TOTAL ,
          BILLING_CYCLE_UPLOAD ,
          BILLING_CYCLE_DOWNLOAD ,
          BILLING_CYCLE_TIME ,
      DAILY_RESET_TIME ,
          WEEKLY_RESET_TIME,
      BILLING_CYCLE_RESET_TIME,
          LAST_UPDATE_TIME    
          )
          VALUES (
            CURRENT_TIMESTAMP,
            REC_U.ID ,
            REC_U.SUBSCRIBER_ID ,
            REC_U.PACKAGE_ID ,
            REC_U.SUBSCRIPTION_ID ,
            REC_U.QUOTA_PROFILE_ID ,
            REC_U.SERVICE_ID ,
      REC_U.DAILY_TOTAL      ,
          REC_U.DAILY_UPLOAD     ,
          REC_U.DAILY_DOWNLOAD   ,
          REC_U.DAILY_TIME       ,
          REC_U.WEEKLY_TOTAL     ,
          REC_U.WEEKLY_UPLOAD    ,
          REC_U.WEEKLY_DOWNLOAD  ,
          REC_U.WEEKLY_TIME      ,
          REC_U.BILLING_CYCLE_TOTAL ,
          REC_U.BILLING_CYCLE_UPLOAD ,
          REC_U.BILLING_CYCLE_DOWNLOAD ,
          REC_U.BILLING_CYCLE_TIME ,
      REC_U.DAILY_RESET_TIME ,
          REC_U.WEEKLY_RESET_TIME,
      REC_U.BILLING_CYCLE_RESET_TIME,            
            REC_U.LAST_UPDATE_TIME      
          );
        UPDATE TBLT_USAGE 
        SET DAILY_TOTAL          = 0,
        DAILY_UPLOAD           = 0,
          DAILY_DOWNLOAD         = 0,
          DAILY_TIME             = 0,
          WEEKLY_TOTAL           = 0,
          WEEKLY_UPLOAD          = 0,
          WEEKLY_DOWNLOAD        = 0,
          WEEKLY_TIME            = 0,
		  BILLING_CYCLE_TOTAL    = 0,
          BILLING_CYCLE_UPLOAD   = 0,
          BILLING_CYCLE_DOWNLOAD = 0,
          BILLING_CYCLE_TIME     = 0,       
          DAILY_RESET_TIME       = current_date + time '11:59:59 PM',       
         WEEKLY_RESET_TIME      = current_date - cast(extract(dow from current_date) as int) + 6 + time '11:59:59 PM',
      LAST_UPDATE_TIME       = CURRENT_TIMESTAMP      
        WHERE SUBSCRIBER_ID      = REC_U.SUBSCRIBER_ID
    AND SUBSCRIPTION_ID IS NULL;
    GET DIAGNOSTICS V_CNT = ROW_COUNT;
    SUC := SUC + V_CNT;
    --  COMMIT;
      END LOOP;
END LOOP;
  SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into ETIME;
  TME_TAKEN := ETIME - STIME;
  IF P_CNT1 = 1 AND P_CNT2 = 1 THEN
    P_CNT1 := 1;
  ELSE
    P_CNT1 := 0;
  END IF;
    perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PCRF_BASEPKG_RESET_TRN',TME_TAKEN,P_CNT1);
END;

$BODY$;


-- Job for Netvertex_basepkg_usage_reset

DO $$
DECLARE
    jid integer;
    scid integer;
    username varchar;
BEGIN
-- Creating a new job
	select user into username; --select current user(Application user)
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
     1::integer, 'JOB_SP_PCRF_BASEPKG_RESET_TRN'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'JOB_SP_PCRF_BASEPKG_RESET_TRN-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '|| username ||';
select SP_PCRF_BASEPKG_RESET_TRN(extract(day from current_timestamp)::bigint);'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-25 16:05:34+05:30'::timestamp with time zone, '2100-07-25 16:05:34+05:30'::timestamp with time zone,
     -- Minutes
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Hours
    ARRAY[true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Week days
    ARRAY[false, false, false, false, false, false, false]::boolean[],
    -- Month days
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Months
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false]::boolean[]
) RETURNING jscid INTO scid;
END
$$;
