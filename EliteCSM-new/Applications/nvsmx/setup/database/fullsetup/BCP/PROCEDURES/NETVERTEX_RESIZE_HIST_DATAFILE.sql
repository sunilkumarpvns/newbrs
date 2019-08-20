spool resize_hist_datafiles.log

REM NETVERTEX-2284 Daily Partition growth in TBLT_USAGE_HISTORY is almost equal to total count of TBLT_USAGE	

set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

prompt Enter the password of [SYS]		
connect sys/&&password@&&NET_STR AS SYSDBA

CREATE TABLE TBT_SHRINK_FILE_LOG (SHRINK_DATAFILES VARCHAR2(4000), EXECUTION_DATE DATE) TABLESPACE &APP_TABLESPACE;
COMMENT ON TABLE TBT_SHRINK_FILE_LOG IS 'Store history of resize datafiles';
COMMENT ON COLUMN TBT_SHRINK_FILE_LOG.SHRINK_DATAFILES IS 'Name of the datafile';
COMMENT ON COLUMN TBT_SHRINK_FILE_LOG.EXECUTION_DATE IS 'Date and Time of the datafile resize';

CREATE OR REPLACE PROCEDURE SP_PCRF_RESIZE_HIST_DATAFILES AS
  V_SQL VARCHAR2(4000);
BEGIN
  FOR I IN (SELECT 'ALTER DATABASE DATAFILE '''
  || FILE_NAME
  || ''' RESIZE '
  || CEIL( ((NVL(HWM,1) * 8192 )/1024/1024)+1 )
  || 'M' SHRINK_DATAFILES
FROM
  (SELECT *
  FROM DBA_DATA_FILES
  WHERE TABLESPACE_NAME IN
    (SELECT TABLESPACE_NAME
    FROM DBA_SEGMENTS
    WHERE OWNER     = UPPER('&APP_USERNAME')
    AND SEGMENT_NAME IN ('TBLT_USAGE_HISTORY','TBLT_SUBSCRIPTION_HISTORY')
    )
  ) DBADF,
  (SELECT FILE_ID,
    MAX(BLOCK_ID+BLOCKS-1) HWM
  FROM DBA_EXTENTS
  GROUP BY FILE_ID
  ) DBAFS
WHERE DBADF.FILE_ID                                                      = DBAFS.FILE_ID(+)
AND CEIL(BLOCKS * 8192 /1024/1024)- CEIL((NVL(HWM,1)* 8192 )/1024/1024 ) > 0
AND FILE_NAME LIKE '+%.dbf%') LOOP
      V_SQL:=I.SHRINK_DATAFILES;
      BEGIN
        EXECUTE IMMEDIATE V_SQL;       
        INSERT INTO TBT_SHRINK_FILE_LOG (SHRINK_DATAFILES,EXECUTION_DATE) VALUES(V_SQL,CURRENT_TIMESTAMP);
        COMMIT;
      EXCEPTION WHEN OTHERS THEN
        NULL;
      END;
  END LOOP;
END; 
/

---DB scheduler Configuration
--DEFINE JOB_CLASS='DEFAULT_JOB_CLASS'
BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   JOB_NAME           =>  'JOB_PCRF_RESIZE_HIST_DATAFILES',
   JOB_TYPE           =>  'STORED_PROCEDURE',
   JOB_ACTION         =>  'SP_PCRF_RESIZE_HIST_DATAFILES',
   START_DATE         =>  SYSTIMESTAMP,
   REPEAT_INTERVAL    =>  'FREQ=DAILY; BYHOUR=06; BYMINUTE=06;',   
   END_DATE           =>   NULL,
   ENABLED            =>   TRUE,
   JOB_CLASS          =>  '"&JOB_CLASS"',
   COMMENTS           =>  'JOB_PCRF_RESIZE_HIST_DATAFILES....');
END;
/

spool off;
exit;