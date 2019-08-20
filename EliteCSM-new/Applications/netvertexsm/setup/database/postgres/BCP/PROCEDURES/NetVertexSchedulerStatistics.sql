SET client_encoding TO 'UTF8';

\set ON_ERROR_STOP ON

CREATE TABLE tblt_scheduler_kpi (
        id NUMERIC,
        inst_id NUMERIC,
        run_timestamp timestamp,
        job_name varchar(255),
        execution_time varchar(255),
        total_count NUMERIC DEFAULT 0,
        total_success NUMERIC DEFAULT 0,
        total_failure NUMERIC DEFAULT 0,
        additional_info varchar(2000)
) ;

--COMMENTS FOR TABLE AND COLUMN

COMMENT ON TABLE tblt_scheduler_kpi IS E'Store the scheduler KPI';
COMMENT ON COLUMN tblt_scheduler_kpi.inst_id IS E'Store id of Job triggering instance';
COMMENT ON COLUMN tblt_scheduler_kpi.run_timestamp IS E'Store the date and time of execution';
COMMENT ON COLUMN tblt_scheduler_kpi.job_name IS E'Store name of the job';
COMMENT ON COLUMN tblt_scheduler_kpi.additional_info IS E'Store reason for failure or success';
COMMENT ON COLUMN tblt_scheduler_kpi.id IS E'Store unque identity';
COMMENT ON COLUMN tblt_scheduler_kpi.total_count IS E'Store total eligible records';
COMMENT ON COLUMN tblt_scheduler_kpi.total_success IS E'Store total success records';
COMMENT ON COLUMN tblt_scheduler_kpi.total_failure IS E'Store total failure records';
COMMENT ON COLUMN tblt_scheduler_kpi.execution_time IS E'Store total execution time taken';

--SEQUENCE FOR AUTOGENERATE THE ID FOR SCHEDULER KPI TABLE

CREATE SEQUENCE seq_scheduler_kpi
INCREMENT 1
MINVALUE 1
NO MAXVALUE
CACHE 20;

-- Oracle package 'pkg_pcrf_hist' declaration, please edit to match PostgreSQL syntax.
-- PostgreSQL does not recognize PACKAGES, using SCHEMA instead.
--DROP SCHEMA IF EXISTS PKG_PCRF_HIST CASCADE;
--CREATE SCHEMA PKG_PCRF_HIST;


/*
/* ELITECSM NETVERTEX MODULE
  JIRA : NETVERTEX-2028 */
CREATE OR REPLACE FUNCTION PKG_PCRF_HIST_sp_purge_tbltsystemaudit () RETURNS VOID AS $body$
DECLARE
    CNT              bigint := 0;
    SUC              bigint := 0;
    STIME            bigint; -- := DBMS_UTILITY.GET_TIME;
    ETIME            bigint;
    TME_TAKEN        bigint;
	PRE_CNT          bigint           := 0;
    POST_CNT         bigint           := 0;
    INST             bigint           := 1 --DBMS_UTILITY.CURRENT_INSTANCE;
    P_CNT            bigint           := 0;
    C_DAYS_TO_KEEP   CONSTANT numeric := 365;
    X_LAST_PARTITION EXCEPTION;
    PRAGMA EXCEPTION_INIT(X_LAST_PARTITION, -14758);
  REC RECORD;

BEGIN
    SELECT EXTRACT(EPOCH FROM current_time) into STIME;
    SELECT PKG_PCRF_SCHEDULER_KPI_FUNC_CHECK_PARTITION('TBLTSYSTEMAUDIT')
    INTO P_CNT;
    IF P_CNT    = 0 THEN
      SELECT EXTRACT(EPOCH FROM current_time) into ETIME;
      --ETIME    := DBMS_UTILITY.GET_TIME;
      TME_TAKEN:=ROUND((ETIME - STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_PURGE_TBLTSYSTEMAUDIT',TME_TAKEN,P_CNT);
    ELSE
	SELECT COUNT(*)
      INTO PRE_CNT
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME ='TBLTSYSTEMAUDIT';
      FOR REC IN (SELECT TABLE_NAME,
        PARTITION_NAME
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME                                                     = 'TBLTSYSTEMAUDIT'
      AND PARTITION_NAME                                                  != 'P_FIRST'
      AND pkg_pcrf_hist.get_high_value_as_date(TABLE_NAME, PARTITION_NAME) < clock_timestamp() - C_DAYS_TO_KEEP
      )
      LOOP
        BEGIN
          CNT:=CNT+1;
          --DBMS_OUTPUT.PUT_LINE('alter table ' || REC.TABLE_NAME || ' drop partition ' || REC.PARTITION_NAME ||' update indexes');
          EXECUTE 'alter table ' || REC.TABLE_NAME || ' drop partition ' || REC.PARTITION_NAME ||' update indexes';
          --SUC:= SUC + SQL%ROWCOUNT;
		  SELECT COUNT(*)
          INTO POST_CNT
          FROM USER_TAB_PARTITIONS
          WHERE TABLE_NAME ='TBLTSYSTEMAUDIT';
		  SUC := PRE_CNT - POST_CNT;
        EXCEPTION
        WHEN X_LAST_PARTITION THEN
          NULL;
        END;
      END LOOP;
      SELECT EXTRACT(EPOCH FROM current_time) into ETIME;
      TME_TAKEN:=ROUND((ETIME - STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_PURGE_TBLTSYSTEMAUDIT',TME_TAKEN,P_CNT);
    END IF;
  END;
$body$
LANGUAGE PLPGSQL
;
*/
-- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.sp_purge_tbltsystemaudit () FROM PUBLIC;

CREATE OR REPLACE FUNCTION pkg_pcrf_hist_sp_nv_notificationhist(
	)
    RETURNS void
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE

    CNT       bigint := 0;
    SUC       bigint := 0;
    STIME     bigint ; --:= DBMS_UTILITY.GET_TIME;
    ETIME     bigint;
    TME_TAKEN bigint;
    INST      bigint := 1; --DBMS_UTILITY.CURRENT_INSTANCE;
    T_CNT     bigint := 0;
    V_CNT     bigint := 0;
  REC RECORD;

BEGIN
    /*  EliteCSM - NetVertex Module
    JIRA : NETVERTEX-2028  */
    SELECT EXTRACT(EPOCH FROM current_time) * 100 into STIME;
    SELECT pkg_pcrf_hist_func_check_table('TBLMNOTIFICATIONHISTORY')
    INTO T_CNT;
    IF T_CNT    = 0 THEN
        SELECT EXTRACT(EPOCH FROM current_time) * 100 into ETIME;
    --  ETIME    :=DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ETIME - STIME;
      perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_NV_NOTIFICATIONHIST',TME_TAKEN,T_CNT);
    ELSE
      FOR REC IN (SELECT
        /*+ index(B IDX_NOTICATIONQUEUE_SELECT ) */
        NOTIFICATIONID
      FROM TBLMNOTIFICATIONQUEUE B
      WHERE B.EMAILSTATUS != 'PENDING'
      AND SMSSTATUS       != 'PENDING'
      )
      LOOP
        CNT:=CNT+1;
        INSERT INTO TBLMNOTIFICATIONHISTORY
        SELECT *
        FROM TBLMNOTIFICATIONQUEUE I
        WHERE I.NOTIFICATIONID = REC.NOTIFICATIONID;
        DELETE
        FROM TBLMNOTIFICATIONQUEUE D
        WHERE D.NOTIFICATIONID = REC.NOTIFICATIONID;
        GET DIAGNOSTICS V_CNT = ROW_COUNT;
        SUC := SUC + V_CNT;
      END LOOP;
      --COMMIT;
        SELECT EXTRACT(EPOCH FROM current_time) * 100 into ETIME;
      --ETIME    :=DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ETIME - STIME;
      perform PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_NV_NOTIFICATIONHIST',TME_TAKEN,T_CNT);
    END IF;
  END;

$BODY$;


-- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.sp_nv_notificationhist () FROM PUBLIC;
/*
CREATE OR REPLACE FUNCTION PKG_PCRF_HIST_sp_purge_notificationhist () RETURNS VOID AS $body$
DECLARE

    CNT       bigint := 0;
    SUC       bigint := 0;
    STIME     bigint ; --:= DBMS_UTILITY.GET_TIME;
    ETIME     bigint;
    TME_TAKEN bigint;
    INST      bigint := 1;-- DBMS_UTILITY.CURRENT_INSTANCE;
    T_CNT     bigint := 1;
    V_CNT     bigint := 0;
BEGIN
    /*  EliteCSM NetVertex Module
    EliteCore Technologies Pvt. Ltd.
    JIRA : NETVERTEX-2028 */
	/*
    SELECT PKG_PCRF_HIST.FUNC_CHECK_TABLE('TBLMNOTIFICATIONHISTORY')
    INTO T_CNT
    FROM DUAL;
    IF T_CNT     = 0 THEN
      ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_NOTIFICATIONHIST',TME_TAKEN,T_CNT);
    ELSE

      CNT:=CNT+1;
	*/
    SELECT EXTRACT(EPOCH FROM current_time) into STIME;
	  SELECT COUNT(*) INTO CNT FROM TBLMNOTIFICATIONHISTORY
	  WHERE TIMESTAMP          + interval '15' < current_timestamp;
      --Purge the Data Which is Older than 15 days
      DELETE
      FROM TBLMNOTIFICATIONHISTORY
      WHERE TIMESTAMP          + interval '15' < current_timestamp;
      GET DIAGNOSTICS V_CNT := ROW_COUNT;
      SUC := SUC + V_CNT;
      --SUC                 :=SUC+SQL%ROWCOUNT;
      --COMMIT;
      SELECT EXTRACT(EPOCH FROM current_time) into ETIME;
      --ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ETIME - STIME;
      PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_NOTIFICATIONHIST',TME_TAKEN,T_CNT);
    --END IF;
  END;
$body$
LANGUAGE PLPGSQL
 STABLE;
*/
 -- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.sp_purge_notificationhist () FROM PUBLIC;
/*
CREATE OR REPLACE FUNCTION PKG_PCRF_HIST_sp_purge_subscription_hist () RETURNS VOID AS $body$
DECLARE

    /* ELITECSM NETEVRTEX MODULE
    JIRA : NETVERTEX-2028 */
    CNT              bigint := 0;
    SUC              bigint := 0;
    STIME            bigint ; --:= DBMS_UTILITY.GET_TIME;
    ETIME            bigint;
    TME_TAKEN        bigint;
    INST             bigint  := 1; -- DBMS_UTILITY.CURRENT_INSTANCE;
    P_CNT            bigint  := 0;
    PRE_CNT          bigint  := 0;
    POST_CNT         bigint  := 0;
    c_days_to_keep   CONSTANT numeric := 15;
    x_last_partition EXCEPTION;
    pragma exception_init(x_last_partition, -14758);
  rec RECORD;

BEGIN
    SELECT EXTRACT(EPOCH FROM current_time) into STIME;
    SELECT PKG_PCRF_SCHEDULER_KPI_FUNC_CHECK_PARTITION('TBLM_SUBSCRIPTION_HISTORY')
    INTO P_CNT;
    IF P_CNT     = 0 THEN
      SELECT EXTRACT(EPOCH FROM current_time) into ETIME;
      --ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ETIME - STIME;
      PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_SUBSCRIPTION_HIST',TME_TAKEN,P_CNT);
    ELSE
      SELECT COUNT(*)
      INTO PRE_CNT
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME ='TBLM_SUBSCRIPTION_HISTORY';
      FOR rec IN (SELECT table_name,
        partition_name
      FROM user_tab_partitions
      WHERE table_name                                                     = 'TBLM_SUBSCRIPTION_HISTORY'
      AND partition_name                                                  != 'P_PSHFIRST'
      AND pkg_pcrf_hist.get_high_value_as_date(table_name, partition_name) < clock_timestamp() - c_days_to_keep
      )
      LOOP
        BEGIN
          CNT := CNT+1;
          --dbms_output.put_line('alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes');
          EXECUTE 'alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes';
          SELECT COUNT(*)
          INTO POST_CNT
          FROM USER_TAB_PARTITIONS
          WHERE TABLE_NAME ='TBLM_SUBSCRIPTION_HISTORY';
          SUC             := PRE_CNT - POST_CNT;
        EXCEPTION
        WHEN x_last_partition THEN
          NULL;
        END;
      END LOOP;
      SELECT EXTRACT(EPOCH FROM current_time) into ETIME;
      --ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ETIME - STIME;
    END IF;
    PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_SUBSCRIPTION_HIST',TME_TAKEN,P_CNT);
  END;
$body$
LANGUAGE PLPGSQL
 STABLE;
-- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.sp_purge_subscription_hist () FROM PUBLIC;
/*
CREATE OR REPLACE FUNCTION PKG_PCRF_HIST_sp_purge_cln_usage_hist () RETURNS VOID AS $body$
DECLARE

    /*ELITECSM NETEVRTEX MODULE
    JIRA : NETVERTEX-2028 */
    CNT              bigint := 0;
    SUC              bigint := 0;
    STIME            bigint; -- := DBMS_UTILITY.GET_TIME;
    ETIME            bigint;
    TME_TAKEN        bigint;
    INST             bigint := 1; --:= DBMS_UTILITY.CURRENT_INSTANCE;
    P_CNT            bigint           := 0;
    PRE_CNT          bigint           := 0;
    POST_CNT         bigint           := 0;
    V_CNT            bigint := 0;
    c_days_to_keep   CONSTANT numeric := 2;
    x_last_partition EXCEPTION;
    pragma exception_init(x_last_partition, -14758);
  rec RECORD;

BEGIN
    SELECT EXTRACT(EPOCH FROM current_time) into STIME;
    SELECT PKG_PCRF_SCHEDULER_KPI_FUNC_CHECK_PARTITION('TBLM_USAGE_HISTORY')
    INTO P_CNT;
    IF P_CNT     = 0 THEN
      ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ETIME - STIME;
      PKG_PCRF_SCHEDULER_KPI_SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_CLN_USAGE_HIST',TME_TAKEN,P_CNT);
    ELSE
      SELECT COUNT(*)
      INTO PRE_CNT
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME ='TBLM_USAGE_HISTORY';
      FOR rec IN (SELECT table_name,
        partition_name
      FROM user_tab_partitions
      WHERE table_name                                                     = 'TBLM_USAGE_HISTORY'
      AND partition_name                                                  != 'P_FIRST'
      AND pkg_pcrf_hist_get_high_value_as_date(table_name, partition_name) < clock_timestamp() - c_days_to_keep
      )
      LOOP
        BEGIN
          CNT := CNT + 1;
          --dbms_output.put_line('alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes');
          EXECUTE 'alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes';
          SELECT COUNT(*)
          INTO POST_CNT
          FROM USER_TAB_PARTITIONS
          WHERE TABLE_NAME ='TBLM_USAGE_HISTORY';
          GET DIAGNOSTICS V_CNT := ROW_COUNT;
          SUC := SUC + V_CNT;
          --SUC             := PRE_CNT - POST_CNT;
        EXCEPTION
        WHEN x_last_partition THEN
          NULL;
        END;
      END LOOP;
      SELECT EXTRACT(EPOCH FROM current_time) into ETIME;
      --ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ETIME - STIME;
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_CLN_USAGE_HIST',TME_TAKEN,P_CNT);
    END IF;
  END;
$body$
LANGUAGE PLPGSQL
 STABLE;
-- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.sp_purge_cln_usage_hist () FROM PUBLIC;
/*
CREATE OR REPLACE FUNCTION PKG_PCRF_HIST_sp_main () RETURNS VOID AS $body$
BEGIN
    SP_PURGE_TBLTSYSTEMAUDIT;
    SP_NV_NOTIFICATIONHIST;
    SP_PURGE_NOTIFICATIONHIST;
    SP_PURGE_SUBSCRIPTION_HIST;
    SP_PURGE_CLN_USAGE_HIST;
  END;
$body$
LANGUAGE PLPGSQL
 STABLE;
-- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.sp_main () FROM PUBLIC;
*/
CREATE OR REPLACE FUNCTION pkg_pcrf_hist_func_check_table(
	tname text)
    RETURNS bigint
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE

    CNT bigint;
BEGIN
     select count(table_name) from information_schema."tables" where table_schema=current_schema and table_name= ''||lower(tname)||''  into cnt ;
    RETURN CNT;
  END;

$BODY$;


-- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.func_check_table ( TNAME text) FROM PUBLIC;
/*
CREATE OR REPLACE FUNCTION PKG_PCRF_HIST_get_high_value_as_date ( p_table_name text, p_partition_name text ) RETURNS timestamp AS $body$
DECLARE

    v_high_value varchar(1024);
    v_date       timestamp;
BEGIN
    SELECT high_value
    INTO v_high_value
    FROM user_tab_partitions
    WHERE table_name   = upper(p_table_name)
    AND partition_name = upper(p_partition_name);
    EXECUTE 'select ' || v_high_value || ' from dual' INTO v_date;
    RETURN v_date;
  END;
END;

$body$
LANGUAGE PLPGSQL
 STABLE;
*/
 -- REVOKE ALL ON FUNCTION PKG_PCRF_HIST.get_high_value_as_date ( p_table_name text, p_partition_name text ) FROM PUBLIC;
-- End of Oracle package 'pkg_pcrf_hist' declaration

-- Oracle package 'pkg_pcrf_scheduler_kpi' declaration, please edit to match PostgreSQL syntax.
-- PostgreSQL does not recognize PACKAGES, using SCHEMA instead.



/* ELITECSM NETVERTEX MODULE
  JIRA : NETVERTEX-2028 */
CREATE OR REPLACE FUNCTION pkg_pcrf_scheduler_kpi_sp_scheduler_execution_check()
    RETURNS void
    LANGUAGE 'plpgsql'

AS $BODY$
DECLARE

  REC RECORD;

BEGIN
    FOR REC IN (select job.jobname,js.* from pgagent.pga_jobsteplog js, pgagent.pga_job job
					where js.jslstatus='f'
					and js.jslstart > CURRENT_TIMESTAMP - interval '2 day'
					and not EXISTS(select * from tblt_scheduler_kpi kpi
									WHERE TO_CHAR(kpi.RUN_TIMESTAMP,'DD-MON-RRRR HH24:MI:SS') = TO_CHAR(js.jslstart,'DD-MON-RRRR HH24:MI:SS'))
					and js.jsljstid=job.jobid
      )
      LOOP
      INSERT
      INTO TBLT_SCHEDULER_KPI(
          ID,
          INST_ID,
          RUN_TIMESTAMP,
          JOB_NAME,
          EXECUTION_TIME,
          TOTAL_COUNT,
          TOTAL_SUCCESS,
          TOTAL_FAILURE,
          ADDITIONAL_INFO
        )
        VALUES (
          nextval('seq_scheduler_kpi'),
          1,
          --  REC.INSTANCE_ID,
          REC.JSLSTART,
          REC.JOBNAME,
          REC.JSLDURATION,
          0,
          0,
          0,
          REC.JSLOUTPUT
        );
      --COMMIT;
    END LOOP;
  END;

$BODY$;

DO $$
DECLARE
    jid integer;
    scid integer;
BEGIN
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'JOB_SCHEDULER_EXECUTION_CHECK'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'Step1-JOB_SCHEDULER_EXECUTION_CHECK'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '||username||';
select pkg_pcrf_scheduler_kpi_sp_scheduler_execution_check();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche-1'::text, ''::text, true,
    '2017-10-04 12:31:37+05:30'::timestamp with time zone, '3017-10-29 12:31:28+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Hours
    ARRAY[false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Week days
    ARRAY[false, false, false, false, false, false, false]::boolean[],
    -- Month days
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Months
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false]::boolean[]
) RETURNING jscid INTO scid;
END
$$;


CREATE OR REPLACE FUNCTION PKG_PCRF_SCHEDULER_KPI_sp_scheduler_general ( CNT bigint, SUC bigint, INST bigint, J_NAME text, E_TIME bigint, RES_T text ) RETURNS VOID AS $body$
DECLARE

    TDATE TIMESTAMP := CURRENT_DATE;
    RESLT varchar(2000);
    FAIL  bigint := 0;
BEGIN
    FAIL    := CNT - SUC;
    IF RES_T =0 THEN
      RESLT :='Table is not partitioned';
    ELSE
      IF SUC     = CNT AND CNT != 0 THEN
        RESLT   := 'Complete Batch Excecuted';
      ELSIF FAIL = CNT AND CNT != 0 THEN
        RESLT   := 'Complete Batch Failed';
      ELSIF CNT  > SUC AND SUC != 0 THEN
        RESLT   := 'Total Count: '|| CNT ||'Total Failed: '|| FAIL;
      --ELSIF SUC  =0 AND FAIL=0 AND CNT >0 THEN
      --RESLT   := 'Total Count not Eligible: '|| CNT ;
      ELSE
        RESLT := 'No Data Found for Processing ';
      END IF;
    END IF;
    INSERT
    INTO TBLT_SCHEDULER_KPI(
        ID,
        INST_ID,
        RUN_TIMESTAMP,
        JOB_NAME,
        EXECUTION_TIME,
        TOTAL_COUNT,
        TOTAL_SUCCESS,
        TOTAL_FAILURE,
        ADDITIONAL_INFO
      )
      VALUES (
        nextval('seq_scheduler_kpi'),
        INST,
        TDATE,
        J_NAME,
        E_TIME,
        CNT,
        SUC,
        FAIL,
        RESLT
      );
    COMMIT;
  END;
$body$
LANGUAGE PLPGSQL
 STABLE;
-- REVOKE ALL ON FUNCTION PKG_PCRF_SCHEDULER_KPI.sp_scheduler_general ( CNT bigint, SUC bigint, INST bigint, J_NAME text, E_TIME bigint, RES_T text ) FROM PUBLIC;
/*
CREATE OR REPLACE FUNCTION PKG_PCRF_SCHEDULER_KPI_func_check_partition ( TNAME text ) RETURNS bigint AS $body$
DECLARE

    CNT bigint;
BEGIN
    SELECT COUNT(*)
    INTO CNT
    FROM USER_TAB_PARTITIONS
    WHERE TABLE_NAME =''
      ||TNAME
      ||'';
    IF CNT = 0 THEN
      RETURN 0;
    ELSE
      RETURN 1;
    END IF;
  END;
END;
;
$body$
LANGUAGE PLPGSQL
 STABLE;
-- REVOKE ALL ON FUNCTION PKG_PCRF_SCHEDULER_KPI.func_check_partition ( TNAME text ) FROM PUBLIC;
-- End of Oracle package 'pkg_pcrf_scheduler_kpi' declaration
*/