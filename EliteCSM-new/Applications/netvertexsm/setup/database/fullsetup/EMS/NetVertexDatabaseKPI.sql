--NETVERTEX-1446 NetVertex EMS : KPIs for Database

#Create Tables in Application Schema
SET SERVEROUTPUT ON
BEGIN

EXECUTE IMMEDIATE 'CREATE TABLE TBL_DATAPUMP_HIST
(TYPE VARCHAR2(50),
 DAT  TIMESTAMP,
 STATUS VARCHAR2(15)
)';


EXECUTE IMMEDIATE 'CREATE TABLE TBL_SHRINK_OPS_HIST
(TB_NAME   VARCHAR2(255),
 DAT       TIMESTAMP(6),
 STATUS    VARCHAR2(255))';

END;
/

--SQL Query Monitor1 - DB Schedulers Monitoring
SELECT MAX(LOG_DATE),JOB_NAME,STATUS
FROM USER_SCHEDULER_JOB_LOG
WHERE TO_CHAR(LOG_DATE,'DD-MON-RRRR') = TO_CHAR(SYSDATE,'DD-MON-RRRR')
GROUP BY JOB_NAME,STATUS; 

--SQL Query Monitor2 - DB Health Script checkup Monitoring
SELECT MAX(DAT),TB_NAME,STATUS FROM TBL_SHRINK_OPS_HIST
WHERE TO_CHAR(DAT,'DD-MON-RRRR') = TO_CHAR(SYSDATE,'DD-MON-RRRR')
GROUP BY TB_NAME,STATUS;

--SQL Query Monitor3 - NetVertex Tables Capacity Reached
SELECT SPR_CNT,
USAGE_CNT,
ADDON_CNT,
CoreSESS_CNT,
RuleSESS_CNT,
DYNSPR_CNT,
USAGECDR_CNT,
PROMOTIONALSUB_CNT 
FROM 
(SELECT (CASE WHEN COUNT(*)< 5000000 THEN 'SUCCEEDED' WHEN COUNT(*)> 5000000 THEN 'FAILED' END) SPR_CNT from TBLNETVERTEXCUSTOMER),
(SELECT (CASE WHEN COUNT(*)< 5000000 THEN 'SUCCEEDED' WHEN COUNT(*)> 5000000 THEN 'FAILED' END) USAGE_CNT FROM TBLMSESSIONUSAGESUMMARY),
(SELECT (CASE WHEN COUNT(*)< 5000000 THEN 'SUCCEEDED' WHEN COUNT(*)> 5000000 THEN 'FAILED' END) ADDON_CNT FROM TBLMADDONSUBSCRIBERREL),
(SELECT (CASE WHEN COUNT(*)< 400000 THEN 'SUCCEEDED' WHEN COUNT(*)> 400000 THEN 'FAILED' END) CoreSESS_CNT FROM TBLMCORESESSIONS),
(SELECT (CASE WHEN COUNT(*)< 400000 THEN 'SUCCEEDED' WHEN COUNT(*)> 400000 THEN 'FAILED' END) RuleSESS_CNT FROM TBLMSESSIONRULE),
(SELECT (CASE WHEN COUNT(*)< 500000 THEN 'SUCCEEDED' WHEN COUNT(*)> 400000 THEN 'FAILED' END) DYNSPR_CNT FROM TBLMDYNASPR),
(SELECT (CASE WHEN COUNT(*)< 500000 THEN 'SUCCEEDED' WHEN COUNT(*)> 400000 THEN 'FAILED' END) USAGECDR_CNT FROM TBLMSESSIONUSAGECDR),
(SELECT (CASE WHEN COUNT(*)< 500000 THEN 'SUCCEEDED' WHEN COUNT(*)> 400000 THEN 'FAILED' END) PROMOTIONALSUB_CNT FROM TBLMPROMOTIONALSUBHISTORY),
(SELECT (CASE WHEN COUNT(*)< 500000 THEN 'SUCCEEDED' WHEN COUNT(*)> 400000 THEN 'FAILED' END) CDR_CNT FROM TBLCDR);
--Note: This query may revise as per project deployment 

--SQL Query Monitor4 - DB Instance Availability Health
SELECT INSTANCE_NAME,TO_CHAR(STARTUP_TIME,'DD-MM-YYYY HH24:MI:SS')UPTIME, 
STATUS as DB_STATUS,DECODE(STATUS,'OPEN','SUCCEEDED','FAILED') STATUS from GV$INSTANCE;

--SQL Query Monitor5 - DB Backup Monitoring
SELECT MAX(DAT),TYPE,STATUS FROM TBL_DATAPUMP_HIST
WHERE TO_CHAR(DAT,'DD-MON-RRRR') = TO_CHAR(SYSDATE,'DD-MON-RRRR')
GROUP BY TYPE,STATUS;

--SQL Query Monitor6 - DB Size Monitoring
SELECT 'DB_SIZE' as  NAME,
(CASE 
WHEN SUM(BYTES)/1024/1024/1024 <= 80 THEN 'SUCCEEDED'
WHEN SUM(BYTES)/1024/1024/1024 > 80 THEN 'FAILED'
END) STATUS FROM DBA_DATA_FILES
UNION
SELECT TABLESPACE_NAME as NAME,
(CASE 
WHEN SUM(BYTES)/1024/1024/1024 <= 25 THEN 'SUCCEEDED'
WHEN SUM(BYTES)/1024/1024/1024 > 25 THEN 'FAILED'
END) STATUS  FROM DBA_DATA_FILES
group by TABLESPACE_NAME
UNION
SELECT 'TEMP' as NAME,
(CASE 
WHEN SUM(BYTES)/1024/1024/1024 <= 25 THEN 'SUCCEEDED'
WHEN SUM(BYTES)/1024/1024/1024 > 25 THEN 'FAILED'
END) STATUS  FROM GV$TEMPFILE
group by NAME;

--SQL Query Monitor7 - Oracle Metric Monitor-1
SELECT 1 AS inst_id,Session_Count,Process_Limit,Open_Cursors_Count,DB_TPS,SQL_Response_Time,AAS FROM
(select  (case when max(value) < 1000 then 'SUCCEEDED' when max(value) > 1000 then 'FAILED' end) Session_Count from gv$sysmetric where inst_id = 1 and metric_name='Session Count'),
(select (case when max(value) < 1000 then 'SUCCEEDED' when max(value) > 1000 then 'FAILED' end) Process_Limit from gv$sysmetric where inst_id = 1 and metric_name='Process Limit %'),
(select (case when max(value) < 2000 then 'SUCCEEDED' when max(value) > 2000 then 'FAILED' end) Open_Cursors_Count from gv$sysmetric where inst_id = 1 and metric_name='Current Open Cursors Count'),
(select (case when max(value) < 2000 then 'SUCCEEDED' when max(value) > 2000 then 'FAILED' end) DB_TPS from gv$sysmetric where inst_id = 1 and metric_name='User Transaction Per Sec'),
(select (case when max(value) < 10 then 'SUCCEEDED' when max(value) > 11 then 'FAILED' end) SQL_Response_Time from gv$sysmetric where inst_id = 1 and metric_name='SQL Service Response Time'),
(select (case when max(value) < 10 then 'SUCCEEDED' when max(value) > 11 then 'FAILED' end) AAS from gv$sysmetric where inst_id = 1 and metric_name='Average Active Sessions')
UNION
SELECT 2 AS inst_id,Session_Count,Process_Limit,Open_Cursors_Count,DB_TPS,SQL_Response_Time,AAS FROM
(select (case when max(value) < 1000 then 'SUCCEEDED' when max(value) > 1000 then 'FAILED' end) Session_Count from gv$sysmetric where inst_id = 2 and metric_name='Session Count'),
(select (case when max(value) < 1000 then 'SUCCEEDED' when max(value) > 1000 then 'FAILED' end) Process_Limit from gv$sysmetric where inst_id = 2 and metric_name='Process Limit %'),
(select (case when max(value) < 2000 then 'SUCCEEDED' when max(value) > 2000 then 'FAILED' end) Open_Cursors_Count from gv$sysmetric where inst_id = 2 and metric_name='Current Open Cursors Count'),
(select (case when max(value) < 2000 then 'SUCCEEDED' when max(value) > 2000 then 'FAILED' end) DB_TPS from gv$sysmetric where inst_id = 2 and metric_name='User Transaction Per Sec'),
(select (case when max(value) < 10 then 'SUCCEEDED' when max(value) > 11 then 'FAILED' end) SQL_Response_Time from gv$sysmetric where inst_id = 2 and metric_name='SQL Service Response Time'),
(select (case when max(value) < 10 then 'SUCCEEDED' when max(value) > 11 then 'FAILED' end) AAS from gv$sysmetric where inst_id = 2 and metric_name='Average Active Sessions');

--SQL Query Monitor8 - Database FRA monitoring OR Monitor RAC Interconnect Latency
--Database FRA monitoring

select case when (SPACE_USED/1024/1024/1024) >((SPACE_LIMIT/1024/1024/1024 *80) /100)
then 'FAILED'
else 'SUCCEEDED'
end "80%"
from v$RECOVERY_FILE_DEST
--Note: This will only required if the database if archive log mode.

--Monitor RAC Interconnect Latency

	SELECT b1.inst_id, b2.VALUE "GCS CR BLOCKS RECEIVED",
		 b1.VALUE "GCS CR BLOCK RECEIVE TIME",
		 ((b1.VALUE / b2.VALUE) * 10) "AVG CR BLOCK RECEIVE TIME (ms)",
     (case
     when ((b1.VALUE / b2.VALUE) * 10) >= 1 then 'FAILED'
     when ((b1.VALUE / b2.VALUE) * 10) < 1 then 'SUCCEEDED'
     end) STATUS
		 FROM gv$sysstat b1, gv$sysstat b2
		 WHERE b1.name = 'global cache cr block receive time'
		 AND b2.name = 'global cache cr blocks received'
		 AND b1.inst_id = b2.inst_id OR b1.name = 'gc cr block receive time'
		 AND b2.name = 'gc cr blocks received'
		 AND b1.inst_id = b2.inst_id;

--SQL Query Monitor9 - Applications Query Monitoring – Alarm

SELECT INST_ID,executions "Executions",(elapsed_time     /1000000) "Elapsed_Seconds",
((elapsed_time    /1000000)*1000) "Elapsed_MiliSeconds",
ROUND(executions  /((elapsed_time/1000000)*1000),2) "Single_QRY_Elapsed_MiliSeconds",
(case 
when ROUND(executions  /((elapsed_time/1000000)*1000),2) < 100 then 'SUCCEEDED'
when ROUND(executions  /((elapsed_time/1000000)*1000),2) > 100 then 'FAILED'
end) status,
  SUBSTR(sql_text,1,500) "SQL",
  module "Module",
  SQL_ID
FROM gv$sql s
WHERE s.PARSING_SCHEMA_ID IN
  (SELECT user_id
  FROM dba_users
  WHERE ACCOUNT_STATUS           ='OPEN'
  AND USERNAME NOT IN ('SYS','SYSTEM','OUTLN','DBSNMP','SYSMAN','ORACLE_OCM',
'DIP','APPQOSSYS','FLOWS_FILES','MDSYS','ORDSYS','SCOTT','HR','EXFSYS','WMSYS','XS$NULL','ORDDATA',
'MDDATA','CTXSYS','ANONYMOUS','XDB','APEX_PUBLIC_USER','SPATIAL_CSW_ADMIN_USR','SPATIAL_WFS_ADMIN_USR',
'ORDPLUGINS','OWBSYS','SI_INFORMTN_SCHEMA','OLAPSYS'))
and sql_id IN
  ( SELECT DISTINCT sql_id
  FROM
    ( WITH sql_class AS
    (SELECT sql_id,
      state,
      COUNT(*) occur
    FROM
      (SELECT sql_id ,
        CASE
          WHEN session_state = 'ON CPU'
          THEN 'CPU'
          WHEN session_state = 'WAITING'
          AND wait_class    IN ('User I/O')
          THEN 'IO'
          ELSE 'WAIT'
        END state
      FROM gv$active_session_history
      WHERE session_type                         IN ( 'FOREGROUND')
      AND sample_time BETWEEN TRUNC(sysdate,'MI') - 15/24/60 AND TRUNC(sysdate,'MI')
      )
    GROUP BY sql_id,
      state
    ),
    ranked_sqls AS
    (SELECT sql_id,
      SUM(occur) sql_occur ,
      rank () over (order by SUM(occur)DESC) xrank
    FROM sql_class
    GROUP BY sql_id
    )
  SELECT sc.sql_id,
    state,
    occur
  FROM sql_class sc,
    ranked_sqls rs
  WHERE rs.sql_id = sc.sql_id
    --and rs.xrank <= :top_n
  ORDER BY xrank,
    sql_id,
    state
    )
  )ORDER BY elapsed_time DESC nulls last;

 --SQL Query Monitor10 - Monitor blocking session and deadlock
 SELECT 'Check Blocking Session'
   ,DECODE(count(*),0,'SUCCEEDED','FAILED')
FROM
   gv$session s
WHERE
   blocking_session IS NOT NULL
UNION   
select 'Check Deadlock'
   ,DECODE(count(*),0,'SUCCEEDED','FAILED')
from gv$lock where (ID1,ID2,TYPE) in
(select ID1,ID2,TYPE from gv$lock where request>0);

 
