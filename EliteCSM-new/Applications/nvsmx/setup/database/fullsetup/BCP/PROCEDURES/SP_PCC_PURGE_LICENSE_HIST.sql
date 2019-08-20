create or replace PROCEDURE SP_PURGE_LICENSE_72_HOURS
AS
BEGIN
  /*  EliteCSM NetVertex Module
  Sterlite Tech Ltd. */
  FOR REC IN
  (SELECT ID
  FROM TBLM_LICENSE
  WHERE LAST_UPDATE_TIME < SYSTIMESTAMP - INTERVAL '72' HOUR
  )
  LOOP
    DELETE FROM TBLM_LICENSE R WHERE R.ID = REC.ID;
    COMMIT;
  END LOOP;
END;
/

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name           =>  'JOB_SP_PURGE_LICENSE_72_HOURS',
   job_type           =>  'STORED_PROCEDURE',
   job_action         =>  'SP_PURGE_LICENSE_72_HOURS',
   start_date         =>  SYSTIMESTAMP,
   repeat_interval    =>  'FREQ=DAILY; BYHOUR=1; BYMINUTE=10;',   
   end_date           =>   NULL,
    enabled           =>   TRUE,
   comments           =>  'Purge license data older than 72 hours.....');
END;
/

