SET client_encoding TO 'UTF8';


\prompt 'Enter Application Username ' username

COPY (select 1) TO PROGRAM 'mkdir --mode=777 -p /opt/postgres/tbs_SUBSCRIPTION_EXPIRED01';
COPY (select 1) TO PROGRAM 'mkdir --mode=777 -p /opt/postgres/tbs_SUBSCRIPTION_RESET01';

CREATE TABLESPACE tbs_SUBSCRIPTION_EXPIRED01 OWNER :username LOCATION '/opt/postgres/tbs_SUBSCRIPTION_EXPIRED01';
CREATE TABLESPACE tbs_SUBSCRIPTION_RESET01 OWNER :username LOCATION '/opt/postgres/tbs_SUBSCRIPTION_RESET01';
\set ON_ERROR_STOP ON

GRANT ALL ON TABLESPACE tbs_SUBSCRIPTION_EXPIRED01 TO :username;
GRANT ALL ON TABLESPACE tbs_SUBSCRIPTION_RESET01 TO :username;

set role :username ;

set search_path = :username;
CREATE TABLE TBLT_SUBSCRIPTION_EXPIRED
   (
  SUBSCRIPTION_ID      VARCHAR(36),
  PACKAGE_ID           VARCHAR(36) NOT NULL,
  SUBSCRIBER_ID        VARCHAR(256) NOT NULL,
  CONSTRAINT PK_ADDONEXPIRED PRIMARY KEY(SUBSCRIPTION_ID))
  TABLESPACE tbs_SUBSCRIPTION_EXPIRED01;

CREATE TABLE TBLT_SUBSCRIPTION_RESET
   (
  SUBSCRIPTION_ID     VARCHAR(36),
  PACKAGE_ID          VARCHAR(36) NOT NULL,
  SUBSCRIBER_ID       VARCHAR(256) NOT NULL,
  ENDTIME             TIMESTAMP,
  USAGERESETDATE      TIMESTAMP,
  CONSTRAINT PK_MADDONUSAGERESET PRIMARY KEY(SUBSCRIPTION_ID))
   TABLESPACE tbs_SUBSCRIPTION_RESET01;


CREATE OR REPLACE FUNCTION sp_addon_ending_poff()
    RETURNS void
    LANGUAGE 'plpgsql'
AS $BODY$
   DECLARE

     CNT       bigint := 0;
     SUC       bigint := 0;
     STIME     bigint ;
     ETIME     bigint;
     TME_TAKEN bigint;
     INST      bigint := 1; --DBMS_UTILITY.CURRENT_INSTANCE;
     T_CNT     bigint := 1;
     V_CNT     bigint := 0;
     V_ADDON_NM TBLM_PACKAGE.name%TYPE;
     REC RECORD;
     RC RECORD;

   BEGIN
     /*
     ON ADDON EXPIRY OR UNSUBCRIPTION
     1) DELETE ADDON SUBSCRIPTION
     2) INSERT USAGE IN TBLMSESSIONUSAGECDR
     3) DELETE USAGE FROM TBLMSESSIONUSAGESUMMERY
     JIRA : NETVERTEX-2028
     */
      SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into STIME;
     FOR REC IN (SELECT * FROM TBLT_SUBSCRIPTION_EXPIRED
      LIMIT 4999)
     LOOP
       CNT := CNT + 1;
       DELETE
       FROM TBLT_SUBSCRIPTION A
       WHERE A.SUBSCRIPTION_ID = REC.SUBSCRIPTION_ID;
       FOR RC IN (SELECT
         U.ID,
         U.SUBSCRIBER_ID,
         U.PACKAGE_ID,
         U.SUBSCRIPTION_ID,
         U.QUOTA_PROFILE_ID,
         U.SERVICE_ID,
         U.DAILY_TOTAL,
         U.DAILY_UPLOAD,
         U.DAILY_DOWNLOAD,
         U.DAILY_TIME,
         U.WEEKLY_TOTAL,
         U.WEEKLY_UPLOAD,
         U.WEEKLY_DOWNLOAD,
         U.WEEKLY_TIME,
         U.BILLING_CYCLE_TOTAL,
         U.BILLING_CYCLE_UPLOAD,
         U.BILLING_CYCLE_DOWNLOAD,
         U.BILLING_CYCLE_TIME,
         U.CUSTOM_TOTAL,
         U.CUSTOM_UPLOAD,
         U.CUSTOM_DOWNLOAD,
         U.CUSTOM_TIME,
         U.DAILY_RESET_TIME,
         U.WEEKLY_RESET_TIME,
         U.CUSTOM_RESET_TIME,
         U.BILLING_CYCLE_RESET_TIME,
         U.LAST_UPDATE_TIME
       FROM TBLT_USAGE U
       WHERE U.SUBSCRIBER_ID =REC.SUBSCRIBER_ID
       AND U.SUBSCRIPTION_ID =REC.SUBSCRIPTION_ID
       )
       LOOP
         INSERT
         INTO TBLT_USAGE_HISTORY(
             CREATE_DATE,
             ID,
             SUBSCRIBER_ID,
             PACKAGE_ID,
             SUBSCRIPTION_ID,
             QUOTA_PROFILE_ID,
             SERVICE_ID,
             DAILY_TOTAL,
             DAILY_UPLOAD,
             DAILY_DOWNLOAD,
             DAILY_TIME,
             WEEKLY_TOTAL,
             WEEKLY_UPLOAD,
             WEEKLY_DOWNLOAD,
             WEEKLY_TIME,
             BILLING_CYCLE_TOTAL,
             BILLING_CYCLE_UPLOAD,
             BILLING_CYCLE_DOWNLOAD,
             BILLING_CYCLE_TIME,
             CUSTOM_TOTAL,
             CUSTOM_UPLOAD,
             CUSTOM_DOWNLOAD,
             CUSTOM_TIME,
             DAILY_RESET_TIME,
             WEEKLY_RESET_TIME,
             CUSTOM_RESET_TIME,
             BILLING_CYCLE_RESET_TIME,
             LAST_UPDATE_TIME
           )
           VALUES (
             CURRENT_TIMESTAMP,
             RC.ID,
             RC.SUBSCRIBER_ID,
             RC.PACKAGE_ID,
             RC.SUBSCRIPTION_ID,
             RC.QUOTA_PROFILE_ID,
             RC.SERVICE_ID,
             RC.DAILY_TOTAL,
             RC.DAILY_UPLOAD,
             RC.DAILY_DOWNLOAD,
             RC.DAILY_TIME,
             RC.WEEKLY_TOTAL,
             RC.WEEKLY_UPLOAD,
             RC.WEEKLY_DOWNLOAD,
             RC.WEEKLY_TIME,
             RC.BILLING_CYCLE_TOTAL,
             RC.BILLING_CYCLE_UPLOAD,
             RC.BILLING_CYCLE_DOWNLOAD,
             RC.BILLING_CYCLE_TIME,
             RC.CUSTOM_TOTAL,
             RC.CUSTOM_UPLOAD,
             RC.CUSTOM_DOWNLOAD,
             RC.CUSTOM_TIME,
             RC.DAILY_RESET_TIME,
             RC.WEEKLY_RESET_TIME,
             RC.CUSTOM_RESET_TIME,
             RC.BILLING_CYCLE_RESET_TIME,
             RC.LAST_UPDATE_TIME
           );
         DELETE
         FROM TBLT_USAGE U
         WHERE U.SUBSCRIBER_ID = RC.SUBSCRIBER_ID
         AND U.SUBSCRIPTION_ID = RC.SUBSCRIPTION_ID;
       END LOOP;
       DELETE
       FROM TBLT_SUBSCRIPTION_EXPIRED E
       WHERE E.SUBSCRIPTION_ID = REC.SUBSCRIPTION_ID;
      GET DIAGNOSTICS V_CNT = ROW_COUNT;
      SUC := SUC + V_CNT;
       --COMMIT;
     END LOOP;
    SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into ETIME;
     TME_TAKEN := ETIME - STIME;
     perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_ADDON_ENDING_POFF',TME_TAKEN,T_CNT);
   END;
$BODY$;


--Job for sp_addon_ending_poff start

DO $$
DECLARE
    jid integer;
    scid integer;
	username varchar;
BEGIN
	select user into username;
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'JOB_SP_ADDON_ENDING_POFF'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'JOB_SP_ADDON_ENDING_POFF-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '||username||';
select sp_addon_ending_poff();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-25 16:05:34+05:30'::timestamp with time zone, '2020-07-25 16:05:34+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false]::boolean[],
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

--Job for sp_addon_ending_poff end



CREATE OR REPLACE FUNCTION sp_load_addonexpired()
    RETURNS void
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE

  CNT       bigint := 0;
  SUC       bigint := 0;
  STIME     bigint ;
  ETIME     bigint;
  TME_TAKEN bigint;
 INST      bigint := 1; -- DBMS_UTILITY.CURRENT_INSTANCE;
  T_CNT     bigint := 1;
  V_CNT bigint := 0;
  REC RECORD;

BEGIN
  /* DATA PROCESS ONCE A DAY
  JIRA : NETVERTEX-2028 */
   SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into STIME;
  FOR REC IN (SELECT SUBSCRIPTION_ID,
    PACKAGE_ID,
    SUBSCRIBER_ID
  FROM TBLT_SUBSCRIPTION
  WHERE END_TIME < CURRENT_TIMESTAMP
  OR STATUS     IN ('5','7')
  )
  LOOP
    CNT := CNT +1;
    INSERT
    INTO TBLT_SUBSCRIPTION_EXPIRED(
        SUBSCRIPTION_ID,
        PACKAGE_ID,
        SUBSCRIBER_ID
      )
      VALUES (
        REC.SUBSCRIPTION_ID,
        REC.PACKAGE_ID,
        REC.SUBSCRIBER_ID
      );
    UPDATE TBLT_SUBSCRIPTION
    SET SERVER_INSTANCE_ID = 9999
    WHERE SUBSCRIPTION_ID  = REC.SUBSCRIPTION_ID;
    GET DIAGNOSTICS v_cnt = ROW_COUNT;
    SUC := SUC + V_CNT;
  END LOOP;
  --COMMIT;
 SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into ETIME;
  TME_TAKEN := ETIME - STIME;
  perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_LOAD_ADDONEXPIRED',TME_TAKEN,T_CNT);
END;

$BODY$;

--Job for sp_load_addonexpired start
DO $$
DECLARE
    jid integer;
    scid integer;
	username varchar;
BEGIN
	select user into username;
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'JOB_SP_LOAD_ADDONEXPIRED'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'JOB_SP_LOAD_ADDONEXPIRED-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '||username||';
select sp_load_addonexpired();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-21 15:44:05+05:30'::timestamp with time zone, '2020-07-21 15:43:54+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Hours
    ARRAY[false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Week days
    ARRAY[true, true, true, true, true, true, true]::boolean[],
    -- Month days
    ARRAY[true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]::boolean[],
    -- Months
    ARRAY[true, true, true, true, true, true, true, true, true, true, true, true]::boolean[]
) RETURNING jscid INTO scid;
END
$$;
--Job for sp_load_addonexpired end

CREATE OR REPLACE FUNCTION sp_load_addonusagereset(
	)
    RETURNS void
    LANGUAGE 'plpgsql'

AS $BODY$
DECLARE

  CNT       bigint := 0;
  SUC       bigint := 0;
  STIME     bigint ;
  ETIME     bigint;
  TME_TAKEN bigint;
  INST      bigint := 1;--DBMS_UTILITY.CURRENT_INSTANCE;
  T_CNT     bigint := 1;
  V_CNT 	bigint := 0;
  REC RECORD;

BEGIN
  /* DATA PROCESS ONCE A DAY
  JIRA : NETVERTEX-2028 */
   SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into STIME;
  FOR REC IN (SELECT U.SUBSCRIBER_ID,
    U.PACKAGE_ID,
    U.SUBSCRIPTION_ID,
    U.END_TIME,
    U.USAGE_RESET_DATE
  FROM TBLT_SUBSCRIPTION U
  WHERE U.USAGE_RESET_DATE <= clock_timestamp()
  AND U.USAGE_RESET_DATE    < U.END_TIME
  )
  LOOP
  CNT := CNT + 1;
    INSERT
    INTO TBLT_SUBSCRIPTION_RESET(
        SUBSCRIPTION_ID,
        PACKAGE_ID,
        SUBSCRIBER_ID,
        ENDTIME,
        USAGERESETDATE
      )
      VALUES (
        REC.SUBSCRIPTION_ID,
        REC.PACKAGE_ID,
        REC.SUBSCRIBER_ID,
        REC.END_TIME,
        REC.USAGE_RESET_DATE
      );
      GET DIAGNOSTICS V_CNT = ROW_COUNT;
  	 SUC := SUC + V_CNT;
  END LOOP;
  --COMMIT;
   SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into ETIME;
  TME_TAKEN := ETIME - STIME;
  perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_LOAD_ADDONUSAGERESET',TME_TAKEN,T_CNT);
END;

$BODY$;
--Job for sp_load_addonusagereset start

DO $$
DECLARE
    jid integer;
    scid integer;
	username varchar;
BEGIN
	select user into username;
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'JOB_SP_LOAD_ADDONUSAGERESET'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'JOB_SP_LOAD_ADDONUSAGERESET-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '||username||';
select sp_load_addonusagereset();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-21 15:41:25+05:30'::timestamp with time zone, '2020-07-21 15:41:25+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Hours
    ARRAY[false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Week days
    ARRAY[true, true, true, true, true, true, true]::boolean[],
    -- Month days
    ARRAY[true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]::boolean[],
    -- Months
    ARRAY[true, true, true, true, true, true, true, true, true, true, true, true]::boolean[]
) RETURNING jscid INTO scid;
END
$$;

--Job for sp_load_addonusagereset end


CREATE OR REPLACE FUNCTION sp_addon_usage_reset()
    RETURNS void
    LANGUAGE 'plpgsql'

  AS $BODY$
    DECLARE

     CNT       bigint := 0;
     SUC       bigint := 0;
     STIME     bigint;
     ETIME     bigint;
     TME_TAKEN bigint;
     INST      bigint := 1; -- DBMS_UTILITY.CURRENT_INSTANCE;
     T_CNT     bigint := 1;
     V_ADDON_NM TBLM_PACKAGE.NAME%TYPE;
   --  V_USAGERESETINTERVAL TBLM_PACKAGE.USAGE_RESET_INTERVAL%TYPE;
     V_VALIDITYPERIODUNIT TBLM_PACKAGE.VALIDITY_PERIOD_UNIT%TYPE;
     V_RESETDATE TIMESTAMP;
     USAGERESETDATE TIMESTAMP;
     V_CNT numeric := 0;
     REC RECORD;
     RC RECORD;

   BEGIN
     /*
     ON ADDON USAGE RESET
     1) UPDATE USAGERESETDATE IN TBLM_SUBSCRIPTION
     2) INSERT USAGE IN TBLMSESSIONUSAGECDR
     3) RESET USAGE IN TBLMSESSIONUSAGESUMMERY
     JIRA : NETVERTEX-2028
     */
      SELECT EXTRACT(EPOCH FROM clock_timestamp()) * 100 into STIME;
     FOR REC IN (SELECT * FROM TBLT_SUBSCRIPTION_RESET
      LIMIT 4999)
     LOOP
     CNT := CNT + 1;
       BEGIN
         SELECT NAME,
         --  USAGE_RESET_INTERVAL,
           (
           CASE VALIDITY_PERIOD_UNIT
             WHEN 'MIDNIGHT'
             THEN 'DAY'
             WHEN 'DAY'
             THEN 'DAY'
             WHEN 'HOUR'
             THEN 'HOUR'
             WHEN 'MINUTE'
             THEN 'MINUTE'
           END)
         INTO  V_ADDON_NM,
          -- V_USAGERESETINTERVAL,
           V_VALIDITYPERIODUNIT
         FROM TBLM_PACKAGE
         WHERE ID=REC.PACKAGE_ID;
       --  AND coalesce(USAGE_RESET_INTERVAL,0) > 0;
        EXCEPTION
       WHEN NO_DATA_FOUND THEN
         V_RESETDATE := REC.ENDTIME;
         UPDATE TBLT_SUBSCRIPTION R
         SET R.USAGE_RESET_DATE  = V_RESETDATE,
           R.LAST_UPDATE_TIME    = CURRENT_TIMESTAMP
         WHERE R.SUBSCRIPTION_ID = REC.SUBSCRIPTION_ID;

       END;
       /* ADD LOOP DT: 01-OCT-2014 */
 	 /*	loop
        	EXECUTE 'select '|| REC.USAGERESETDATE || '+ interval '|| V_USAGERESETINTERVAL || V_VALIDITYPERIODUNIT||' INTO'|| V_RESETDATE ||'';
        end loop;
     */
     --EXECUTE 'SELECT $1::timestamp + INTERVAL '''|| V_USAGERESETINTERVAL ||'' || V_VALIDITYPERIODUNIT ||'''' INTO V_RESETDATE using USAGERESETDATE;

      LOOP
		 EXECUTE 'SELECT $1 + INTERVAL '''|| V_USAGERESETINTERVAL ||'' || V_VALIDITYPERIODUNIT ||'''' INTO V_RESETDATE USING REC.USAGERESETDATE;
    exit WHEN REC.USAGERESETDATE <= CURRENT_TIMESTAMP;
       END LOOP;
       --DBMS_OUTPUT.PUT_LINE('ADDON NAME:-'|| V_ADDON_NM ||' | ' ||V_RESETDATE);
       IF REC.ENDTIME < V_RESETDATE THEN
         V_RESETDATE := REC.ENDTIME;
       END IF;
       UPDATE TBLT_SUBSCRIPTION R
       SET USAGE_RESET_DATE  = V_RESETDATE,
         LAST_UPDATE_TIME    = CURRENT_TIMESTAMP
       WHERE R.SUBSCRIPTION_ID = REC.SUBSCRIPTION_ID;
       FOR RC IN (SELECT
         U.ID,
         U.SUBSCRIBER_ID,
         U.PACKAGE_ID,
         U.SUBSCRIPTION_ID,
         U.QUOTA_PROFILE_ID,
         U.SERVICE_ID,
         U.DAILY_TOTAL,
         U.DAILY_UPLOAD,
         U.DAILY_DOWNLOAD,
         U.DAILY_TIME,
         U.WEEKLY_TOTAL,
         U.WEEKLY_UPLOAD,
         U.WEEKLY_DOWNLOAD,
         U.WEEKLY_TIME,
         U.BILLING_CYCLE_TOTAL,
         U.BILLING_CYCLE_UPLOAD,
         U.BILLING_CYCLE_DOWNLOAD,
         U.BILLING_CYCLE_TIME,
         U.CUSTOM_TOTAL,
         U.CUSTOM_UPLOAD,
         U.CUSTOM_DOWNLOAD,
         U.CUSTOM_TIME,
         U.DAILY_RESET_TIME,
         U.WEEKLY_RESET_TIME,
         U.CUSTOM_RESET_TIME,
         U.BILLING_CYCLE_RESET_TIME,
         U.LAST_UPDATE_TIME
       FROM TBLT_USAGE U
       WHERE U.SUBSCRIBER_ID     =REC.SUBSCRIBER_ID
       AND U.SUBSCRIPTION_ID     =REC.SUBSCRIPTION_ID
       AND (U.DAILY_TOTAL       != 0
       OR U.DAILY_TIME          != 0
       OR U.WEEKLY_TOTAL        != 0
       OR U.WEEKLY_TIME         != 0
       OR U.BILLING_CYCLE_TOTAL != 0
       OR U.BILLING_CYCLE_TIME  != 0
       OR U.CUSTOM_TOTAL        != 0
       OR U.CUSTOM_TIME         != 0 )
       )
       /*
       AND (TOTALOCTETS!=0
       OR USAGETIME!    =0)
       */
       LOOP
         INSERT
         INTO TBLT_USAGE_HISTORY(
             CREATE_DATE,
             ID,
             SUBSCRIBER_ID,
             PACKAGE_ID,
             SUBSCRIPTION_ID,
             QUOTA_PROFILE_ID,
             SERVICE_ID,
             DAILY_TOTAL,
             DAILY_UPLOAD,
             DAILY_DOWNLOAD,
             DAILY_TIME,
             WEEKLY_TOTAL,
             WEEKLY_UPLOAD,
             WEEKLY_DOWNLOAD,
             WEEKLY_TIME,
             BILLING_CYCLE_TOTAL,
             BILLING_CYCLE_UPLOAD,
             BILLING_CYCLE_DOWNLOAD,
             BILLING_CYCLE_TIME,
             CUSTOM_TOTAL,
             CUSTOM_UPLOAD,
             CUSTOM_DOWNLOAD,
             CUSTOM_TIME,
             DAILY_RESET_TIME,
             WEEKLY_RESET_TIME,
             CUSTOM_RESET_TIME,
             BILLING_CYCLE_RESET_TIME,
             LAST_UPDATE_TIME
           )
           VALUES (
             CURRENT_TIMESTAMP,
             RC.ID,
             RC.SUBSCRIBER_ID,
             RC.PACKAGE_ID,
             RC.SUBSCRIPTION_ID,
             RC.QUOTA_PROFILE_ID,
             RC.SERVICE_ID,
             RC.DAILY_TOTAL,
             RC.DAILY_UPLOAD,
             RC.DAILY_DOWNLOAD,
             RC.DAILY_TIME,
             RC.WEEKLY_TOTAL,
             RC.WEEKLY_UPLOAD,
             RC.WEEKLY_DOWNLOAD,
             RC.WEEKLY_TIME,
             RC.BILLING_CYCLE_TOTAL,
             RC.BILLING_CYCLE_UPLOAD,
             RC.BILLING_CYCLE_DOWNLOAD,
             RC.BILLING_CYCLE_TIME,
             RC.CUSTOM_TOTAL,
             RC.CUSTOM_UPLOAD,
             RC.CUSTOM_DOWNLOAD,
             RC.CUSTOM_TIME,
             RC.DAILY_RESET_TIME,
             RC.WEEKLY_RESET_TIME,
             RC.CUSTOM_RESET_TIME,
             RC.BILLING_CYCLE_RESET_TIME,
             RC.LAST_UPDATE_TIME
           );
         UPDATE TBLT_USAGE U
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
           CUSTOM_TOTAL           = 0,
           CUSTOM_UPLOAD          = 0,
           CUSTOM_DOWNLOAD        = 0,
           LAST_UPDATE_TIME       =CURRENT_TIMESTAMP
         WHERE U.SUBSCRIBER_ID      = RC.SUBSCRIBER_ID
         AND U.SUBSCRIPTION_ID      = RC.SUBSCRIPTION_ID;
       END LOOP;
       DELETE
       FROM TBLT_SUBSCRIPTION_RESET U
       WHERE U.SUBSCRIPTION_ID = REC.SUBSCRIPTION_ID;
       GET DIAGNOSTICS v_cnt = ROW_COUNT;
      	SUC  := SUC + V_CNT;
      -- COMMIT;
     END LOOP;
    SELECT EXTRACT(EPOCH FROM clock_timestamp()) *100 into ETIME;
     TME_TAKEN := ETIME - STIME;
     perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_ADDON_USAGE_RESET',TME_TAKEN,T_CNT);
   END;

$BODY$;

--Job for sp_addon_usage_reset start
DO $$
DECLARE
    jid integer;
    scid integer;
	username varchar;
BEGIN
	select user into username;
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'JOB_SP_ADDON_USAGE_RESET'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'JOB_SP_ADDON_USAGE_RESET-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '||username||';
select SP_ADDON_USAGE_RESET();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-21 17:09:11+05:30'::timestamp with time zone, '2020-07-21 17:09:08+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false]::boolean[],
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
--Job for sp_addon_usage_reset end