spool NetVertexSchedulerStatistics.log
--TABLE FOR SCHEDULER KPI

CREATE TABLE TBLT_SCHEDULER_KPI
(	ID 		NUMERIC(*,0) PRIMARY KEY, 
	INST_ID 	NUMERIC(*,0), 
	RUN_TIMESTAMP 	TIMESTAMP (6), 
	JOB_NAME 	VARCHAR(255), 	
    EXECUTION_TIME VARCHAR(255),
	TOTAL_COUNT 	NUMERIC(*,0) DEFAULT 0, 
	TOTAL_SUCCESS 	NUMERIC(*,0) DEFAULT 0, 
	TOTAL_FAILURE 	NUMERIC(*,0) DEFAULT 0, 
	ADDITIONAL_INFO	VARCHAR(2000)
);


--COMMENTS FOR TABLE AND COLUMN

COMMENT ON TABLE TBLT_SCHEDULER_KPI IS 'Store the scheduler KPI';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.ID IS 'Store unque identity';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.INST_ID IS 'Store id of Job triggering instance';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.RUN_TIMESTAMP IS 'Store the date and time of execution';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.JOB_NAME IS 'Store name of the job';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.EXECUTION_TIME IS 'Store total execution time taken';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.TOTAL_COUNT IS 'Store total eligible records';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.TOTAL_SUCCESS IS 'Store total success records';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.TOTAL_FAILURE IS 'Store total failure records';
COMMENT ON COLUMN TBLT_SCHEDULER_KPI.ADDITIONAL_INFO IS 'Store reason for failure or success';

--SEQUENCE FOR AUTOGENERATE THE ID FOR SCHEDULER KPI TABLE

CREATE SEQUENCE SEQ_SCHEDULER_KPI
MINVALUE 1
INCREMENT BY 1
CACHE 20 NOORDER  NOCYCLE; 

--PACKAGE SPECIFICATION 
CREATE OR REPLACE PACKAGE PKG_PCRF_SCHEDULER_KPI
AS
  /*  ELITECSM NETVERTEX MODULE
  JIRA : NETVERTEX-2028 */
  PROCEDURE SP_SCHEDULER_EXECUTION_CHECK;
  PROCEDURE SP_SCHEDULER_GENERAL(
      CNT    IN NUMBER,
      SUC    IN NUMBER,
      INST   IN NUMBER,
      J_NAME IN VARCHAR2,
      E_TIME IN NUMBER,
      RES_T VARCHAR2);
  FUNCTION FUNC_CHECK_PARTITION(
      TNAME VARCHAR)
    RETURN NUMBER;
END PKG_PCRF_SCHEDULER_KPI;
/

--PACKAGE BODY
CREATE OR REPLACE PACKAGE BODY PKG_PCRF_SCHEDULER_KPI
AS
  /* ELITECSM NETVERTEX MODULE
  JIRA : NETVERTEX-2028 */
  PROCEDURE SP_SCHEDULER_EXECUTION_CHECK
  AS
  BEGIN
    FOR REC IN
    (SELECT  *
    FROM USER_SCHEDULER_JOB_RUN_DETAILS M
    WHERE M.STATUS          ='FAILED'
    AND M.ACTUAL_START_DATE > SYSTIMESTAMP -INTERVAL '2' DAY
    AND NOT EXISTS
      (SELECT *
      FROM TBLT_SCHEDULER_KPI I
      WHERE TO_CHAR(I.RUN_TIMESTAMP,'DD-MON-RRRR HH24:MI:SS') = TO_CHAR(M.ACTUAL_START_DATE,'DD-MON-RRRR HH24:MI:SS') and
      I.JOB_NAME=M.JOB_NAME      
      )
    )
    LOOP
      INSERT
      INTO TBLT_SCHEDULER_KPI
        (
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
        VALUES
        (
          SEQ_SCHEDULER_KPI.NEXTVAL,
          REC.INSTANCE_ID,
          REC.ACTUAL_START_DATE,
          REC.JOB_NAME,
          REC.RUN_DURATION,
          0,
          0,
          0,
          REC.ADDITIONAL_INFO
        );
      COMMIT;
    END LOOP;
  END;
  PROCEDURE SP_SCHEDULER_GENERAL
    (
      CNT    IN NUMBER,
      SUC    IN NUMBER,
      INST   IN NUMBER,
      J_NAME IN VARCHAR2,
      E_TIME IN NUMBER,
      RES_T VARCHAR2
    )
  AS
    TDATE TIMESTAMP := CURRENT_DATE;
    RESLT VARCHAR2(2000);
    FAIL  NUMBER := 0;
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
    INTO TBLT_SCHEDULER_KPI
      (
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
      VALUES
      (
        SEQ_SCHEDULER_KPI.NEXTVAL,
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
  FUNCTION FUNC_CHECK_PARTITION
    (
      TNAME VARCHAR
    )
    RETURN NUMBER
  AS
    CNT NUMBER;
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
END PKG_PCRF_SCHEDULER_KPI;
/
--SCHEDULER FOR SP_SCHEDULER_EXECUTION_CHECK PROCEDURE

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_SCHEDULER_EXECUTION_CHECK',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_EXECUTION_CHECK',
   start_date         =>   SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=DAILY;BYHOUR=7;BYMINUTE=30;',   
   end_date           =>   NULL,
   enabled           =>    TRUE,
   comments           =>  'JOB FOR POPULATING TECHNICAL FAILURE TO SCHEDULER KPI TABLE');
END;
/

--PKG_PCRF_HIST: Package Speification--
CREATE OR REPLACE PACKAGE PKG_PCRF_HIST
AS
  /*  ELITECSM NETVERTEX MODULE */
  PROCEDURE SP_PURGE_TBLTSYSTEMAUDIT;
  PROCEDURE SP_NV_NOTIFICATIONHIST;
  PROCEDURE SP_PURGE_NOTIFICATIONHIST;
  PROCEDURE SP_PURGE_SUBSCRIPTION_HIST;
  PROCEDURE SP_PURGE_CLN_USAGE_HIST;
  PROCEDURE SP_MAIN;
  FUNCTION GET_HIGH_VALUE_AS_DATE(
      p_table_name     IN VARCHAR2,
      p_partition_name IN VARCHAR2)
    RETURN DATE;
  FUNCTION FUNC_CHECK_TABLE(
      TNAME VARCHAR)
    RETURN NUMBER;
END PKG_PCRF_HIST;
/

--PKG_PCRF_HIST: Package Body--
CREATE OR REPLACE PACKAGE BODY PKG_PCRF_HIST
AS
  /* ELITECSM NETVERTEX MODULE
  JIRA : NETVERTEX-2028 */
  PROCEDURE SP_PURGE_TBLTSYSTEMAUDIT
  AS
    CNT              NUMBER := 0;
    SUC              NUMBER := 0;
    STIME            NUMBER := DBMS_UTILITY.GET_TIME;
    ETIME            NUMBER;
    TME_TAKEN        NUMBER;
	PRE_CNT          NUMBER           := 0;    
    POST_CNT         NUMBER           := 0;
    INST             NUMBER           := DBMS_UTILITY.CURRENT_INSTANCE;
    P_CNT            NUMBER           := 0;
    C_DAYS_TO_KEEP   CONSTANT INTEGER := 365;
    X_LAST_PARTITION EXCEPTION;
    PRAGMA EXCEPTION_INIT(X_LAST_PARTITION, -14758);
  BEGIN
    SELECT PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLTSYSTEMAUDIT')
    INTO P_CNT
    FROM DUAL;
    IF P_CNT    = 0 THEN
      ETIME    := DBMS_UTILITY.GET_TIME;
      TME_TAKEN:=ROUND((ETIME - STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL (CNT,SUC,INST,'JOB_PURGE_TBLTSYSTEMAUDIT',TME_TAKEN,P_CNT);
    ELSE
	SELECT COUNT(*)
      INTO PRE_CNT
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME ='TBLTSYSTEMAUDIT';
      FOR REC IN
      (SELECT TABLE_NAME,
        PARTITION_NAME
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME                                                     = 'TBLTSYSTEMAUDIT'
      AND PARTITION_NAME                                                  != 'P_FIRST'
      AND PKG_PCRF_HIST.GET_HIGH_VALUE_AS_DATE(TABLE_NAME, PARTITION_NAME) < SYSDATE - C_DAYS_TO_KEEP
      )
      LOOP
        BEGIN
          CNT:=CNT+1;
          --DBMS_OUTPUT.PUT_LINE('alter table ' || REC.TABLE_NAME || ' drop partition ' || REC.PARTITION_NAME ||' update indexes');
          EXECUTE IMMEDIATE 'alter table ' || REC.TABLE_NAME || ' drop partition ' || REC.PARTITION_NAME ||' update indexes';
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
      ETIME    := DBMS_UTILITY.GET_TIME;
      TME_TAKEN:=ROUND((ETIME - STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL (CNT,SUC,INST,'JOB_PURGE_TBLTSYSTEMAUDIT',TME_TAKEN,P_CNT);
    END IF;
  END;
  PROCEDURE SP_NV_NOTIFICATIONHIST
  AS
    CNT       NUMBER := 0;
    SUC       NUMBER := 0;
    STIME     NUMBER := DBMS_UTILITY.GET_TIME;
    ETIME     NUMBER;
    TME_TAKEN NUMBER;
    INST      NUMBER := DBMS_UTILITY.CURRENT_INSTANCE;
    T_CNT     NUMBER := 0;
  BEGIN
    /*  EliteCSM - NetVertex Module
    JIRA : NETVERTEX-2028  */
    SELECT PKG_PCRF_HIST.FUNC_CHECK_TABLE('TBLMNOTIFICATIONHISTORY')
    INTO T_CNT
    FROM DUAL;
    IF T_CNT    = 0 THEN
      ETIME    :=DBMS_UTILITY.GET_TIME;
      TME_TAKEN:=ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_NV_NOTIFICATIONHIST',TME_TAKEN,T_CNT);
    ELSE
       FOR REC IN
		(SELECT
			/*+ index(B IDX_NOTICATIONQUEUE_SELECT ) */
			NOTIFICATIONID
			FROM TBLMNOTIFICATIONQUEUE B
			WHERE B.EMAILSTATUS IN ('SENT','FAILED')
			AND B.SMSSTATUS     IN('SENT','FAILED')
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
        SUC                   :=SUC+SQL%ROWCOUNT;
      END LOOP;
      COMMIT;
      ETIME    :=DBMS_UTILITY.GET_TIME;
      TME_TAKEN:=ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_NV_NOTIFICATIONHIST',TME_TAKEN,T_CNT);
    END IF;
  END;
  PROCEDURE SP_PURGE_NOTIFICATIONHIST
  AS
    CNT       NUMBER := 0;
    SUC       NUMBER := 0;
    STIME     NUMBER := DBMS_UTILITY.GET_TIME;
    ETIME     NUMBER;
    TME_TAKEN NUMBER;
    INST      NUMBER := DBMS_UTILITY.CURRENT_INSTANCE;
    T_CNT     NUMBER := 1;
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
	  SELECT COUNT(*) INTO CNT FROM TBLMNOTIFICATIONHISTORY
	  WHERE TIMESTAMP          + 15 < SYSDATE;
      --Purge the Data Which is Older than 15 days
      DELETE
      FROM TBLMNOTIFICATIONHISTORY
      WHERE TIMESTAMP          + 15 < SYSDATE;
      SUC                 :=SUC+SQL%ROWCOUNT;
      COMMIT;
      ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_NOTIFICATIONHIST',TME_TAKEN,T_CNT);
    --END IF;
  END;
  PROCEDURE SP_PURGE_SUBSCRIPTION_HIST
  AS
    /* ELITECSM NETEVRTEX MODULE
    JIRA : NETVERTEX-2028 */
    CNT              NUMBER := 0;
    SUC              NUMBER := 0;
    STIME            NUMBER := DBMS_UTILITY.GET_TIME;
    ETIME            NUMBER;
    TME_TAKEN        NUMBER;
    INST             NUMBER           := DBMS_UTILITY.CURRENT_INSTANCE;
    P_CNT            NUMBER           := 0;
    PRE_CNT          NUMBER           := 0;
    POST_CNT         NUMBER           := 0;
    c_days_to_keep   CONSTANT INTEGER := 15;
    x_last_partition EXCEPTION;
    pragma exception_init(x_last_partition, -14758);
  BEGIN
    SELECT PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLM_SUBSCRIPTION_HISTORY')
    INTO P_CNT
    FROM DUAL;
    IF P_CNT     = 0 THEN
      ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_SUBSCRIPTION_HIST',TME_TAKEN,P_CNT);
    ELSE
      SELECT COUNT(*)
      INTO PRE_CNT
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME ='TBLM_SUBSCRIPTION_HISTORY';
      FOR rec IN
      (SELECT table_name,
        partition_name
      FROM user_tab_partitions
      WHERE table_name                                                     = 'TBLM_SUBSCRIPTION_HISTORY'
      AND partition_name                                                  != 'P_PSHFIRST'
      AND PKG_PCRF_HIST.GET_HIGH_VALUE_AS_DATE(table_name, partition_name) < sysdate - c_days_to_keep
      )
      LOOP
        BEGIN
          CNT := CNT+1;
          --dbms_output.put_line('alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes');
          EXECUTE immediate 'alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes';
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
      ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_SUBSCRIPTION_HIST',TME_TAKEN,P_CNT);
    END IF;
  END;
  PROCEDURE SP_PURGE_CLN_USAGE_HIST
  AS
    /*ELITECSM NETEVRTEX MODULE
    JIRA : NETVERTEX-2028 */
    CNT              NUMBER := 0;
    SUC              NUMBER := 0;
    STIME            NUMBER := DBMS_UTILITY.GET_TIME;
    ETIME            NUMBER;
    TME_TAKEN        NUMBER;
    INST             NUMBER           := DBMS_UTILITY.CURRENT_INSTANCE;
    P_CNT            NUMBER           := 0;
    PRE_CNT          NUMBER           := 0;
    POST_CNT         NUMBER           := 0;
    c_days_to_keep   CONSTANT INTEGER := 2;
    x_last_partition EXCEPTION;
    pragma exception_init(x_last_partition, -14758);
  BEGIN
    SELECT PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLM_USAGE_HISTORY')
    INTO P_CNT
    FROM DUAL;
    IF P_CNT     = 0 THEN
      ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_CLN_USAGE_HIST',TME_TAKEN,P_CNT);
    ELSE
      SELECT COUNT(*)
      INTO PRE_CNT
      FROM USER_TAB_PARTITIONS
      WHERE TABLE_NAME ='TBLM_USAGE_HISTORY';
      FOR rec IN
      (SELECT table_name,
        partition_name
      FROM user_tab_partitions
      WHERE table_name                                                     = 'TBLM_USAGE_HISTORY'
      AND partition_name                                                  != 'P_FIRST'
      AND PKG_PCRF_HIST.GET_HIGH_VALUE_AS_DATE(table_name, partition_name) < sysdate - c_days_to_keep
      )
      LOOP
        BEGIN
          CNT := CNT + 1;
          --dbms_output.put_line('alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes');
          EXECUTE immediate 'alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes';
          SELECT COUNT(*)
          INTO POST_CNT
          FROM USER_TAB_PARTITIONS
          WHERE TABLE_NAME ='TBLM_USAGE_HISTORY';
          SUC             := PRE_CNT - POST_CNT;
        EXCEPTION
        WHEN x_last_partition THEN
          NULL;
        END;
      END LOOP;
      ETIME     := DBMS_UTILITY.GET_TIME;
      TME_TAKEN := ROUND((ETIME-STIME)/100,1);
      PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PURGE_CLN_USAGE_HIST',TME_TAKEN,P_CNT);
    END IF;
  END;
  PROCEDURE SP_MAIN
  AS
  BEGIN
    SP_PURGE_TBLTSYSTEMAUDIT;
    SP_NV_NOTIFICATIONHIST;
    SP_PURGE_NOTIFICATIONHIST;
    SP_PURGE_SUBSCRIPTION_HIST;
    SP_PURGE_CLN_USAGE_HIST;
  END;
  FUNCTION FUNC_CHECK_TABLE(
      TNAME VARCHAR)
    RETURN NUMBER
  AS
    CNT NUMBER;
  BEGIN
    SELECT COUNT(*) INTO CNT FROM USER_TABLES WHERE TABLE_NAME = ''||TNAME||'';
    RETURN CNT;
  END;
  FUNCTION GET_HIGH_VALUE_AS_DATE(
      p_table_name     IN VARCHAR2,
      p_partition_name IN VARCHAR2 )
    RETURN DATE
  AS
    v_high_value VARCHAR2(1024);
    v_date       DATE;
  BEGIN
    SELECT high_value
    INTO v_high_value
    FROM user_tab_partitions
    WHERE table_name   = upper(p_table_name)
    AND partition_name = upper(p_partition_name);
    EXECUTE immediate 'select ' || v_high_value || ' from dual' INTO v_date;
    RETURN v_date;
  END;
END PKG_PCRF_HIST;
/
--Scheduler configuration--

--Scheduler for sp_nv_notificationhistory--

--creating new job
BEGIN
  -- Job defined entirely by the CREATE JOB procedure.
  DBMS_SCHEDULER.create_job (
    job_name => 'JOB_SP_NV_NOTIFICATIONHIST',
    job_type => 'STORED_PROCEDURE',
    job_action => 'PKG_PCRF_HIST.SP_NV_NOTIFICATIONHIST',
    START_DATE => SYSTIMESTAMP,
    repeat_interval => 'FREQ=HOURLY;BYMINUTE=28;',
    end_date => NULL,
    enabled => TRUE,
    comments => 'Job defined entirely by the CREATE JOB procedure.');
END;
/ 

--Scheduler for SP_PURGE_NOTIFICATIONHIST

--creating new job
BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_SP_PURGE_NOTIFICATIONHIST',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'PKG_PCRF_HIST.SP_PURGE_NOTIFICATIONHIST',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=DAILY;BYHOUR=4;BYMINUTE=30;',   
   end_date           =>   NULL,
   enabled           =>   TRUE,
   comments           =>  'JOB_PURGE_NOTIFICATIONHISTORY...');
END;
/

--Scheduler for SP_PURGE_TBLTSYSTEMAUDIT

--Creating new job
BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_SP_PURGE_TBLTSYSTEMAUDIT',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'PKG_PCRF_HIST.SP_PURGE_TBLTSYSTEMAUDIT',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=MONTHLY;BYMONTHDAY=1;BYDAY=MON,TUE,WED,THU,FRI,SAT,SUN;BYHOUR=04;BYMINUTE=45;',   
   end_date           =>   NULL,
   enabled           =>   TRUE,
   comments           =>  'Purge NVTX SYSTEMAUDIT older than 365 days...');
END;
/
--Scheduler for SP_PURGE_SUBSCRIPTION_HIST

--creating new job
BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_SP_PURGE_SUBSCRIPTION_HIST',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'PKG_PCRF_HIST.SP_PURGE_SUBSCRIPTION_HIST',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=DAILY;BYHOUR=4;BYMINUTE=48;',   
   end_date           =>   NULL,
   enabled           =>   TRUE,
   comments           =>  'Purge SUBSCRIPTION HISTORY older than 15 days...');
END;
/

--Scheduler for SP_PURGE_CLN_USAGE_HIST

--creating new job
BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_SP_PURGE_CLN_USAGE_HIST',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'PKG_PCRF_HIST.SP_PURGE_CLN_USAGE_HIST',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=DAILY;BYHOUR=4;BYMINUTE=52;',   
   end_date           =>   NULL,
   enabled           =>   TRUE,
   comments           =>  'Purge USAGE_HIST older than 2 days...');
END;
/

spool off;
