SET client_encoding TO 'UTF8';

\set ON_ERROR_STOP ON



CREATE OR REPLACE FUNCTION sp_stale_sessions_subscriber()
    RETURNS void
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE

  CNT       bigint := 0;
  SUC       bigint := 0;
  STIME     bigint ;
  ETIME     bigint;
  TME_TAKEN bigint;
  INST      bigint := 1; -- := DBMS_UTILITY.CURRENT_INSTANCE;
  GX_CNT    bigint :=0;
  RX_CNT    bigint := 0;
  V_CNT		bigint := 0;
  REC RECORD;

BEGIN
  /*  EliteCSM NetVertex Module
  EliteCore Technologies Pvt. Ltd. */
  Select EXTRACT(EPOCH FROM clock_timestamp()) * 100 into STIME;
  FOR REC IN (SELECT CORESESSIONID
  FROM TBLMCORESESSIONS
  WHERE LASTUPDATETIME < CURRENT_TIMESTAMP - INTERVAL '24 HOURS'
  )
  LOOP
    CNT:=CNT+1;
    DELETE FROM TBLMSESSIONRULE R WHERE R.SESSIONID = REC.CORESESSIONID;
    DELETE FROM TBLMCORESESSIONS C WHERE C.CORESESSIONID = REC.CORESESSIONID;
    GET DIAGNOSTICS V_CNT = ROW_COUNT;
    SUC := SUC + V_CNT;
    --SUC:=SUC+SQL%ROWCOUNT;
    --COMMIT;
  END LOOP;
  --SELECT PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLMSESSIONRULE')  INTO RX_CNT;
--  SELECT PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLMCORESESSIONS')  INTO GX_CNT;
  --  ETIME    := DBMS_UTILITY.GET_TIME;
 -- TME_TAKEN:=ROUND((ETIME - STIME)/100,1);
  	Select EXTRACT(EPOCH FROM clock_timestamp()) * 100 into ETIME;
  	TME_TAKEN := ETIME - STIME;
  IF RX_CNT=1 AND GX_CNT=1 THEN
    RX_CNT :=1;
  ELSE
    RX_CNT :=0;
  END IF;
 perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_STALE_SESSIONS_SPR',TME_TAKEN,RX_CNT);
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
    1::integer, 'JOB_SP_STALE_SESSIONS_SPR'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'JOB_SP_STALE_SESSIONS_SPR-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '|| username ||';
select sp_stale_sessions_subscriber();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-21 15:52:36+05:30'::timestamp with time zone, '2020-07-21 15:52:32+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Hours
    ARRAY[false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Week days
    ARRAY[false, false, false, false, false, false, false]::boolean[],
    -- Month days
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Months
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false]::boolean[]
) RETURNING jscid INTO scid;
END
$$;