spool NETVERTEX_BASEPKG_USAGE_RESET.log

prompt Enter the credential of NETVERTEX 
connect &&username/&&password@&&NET_STR

create or replace PROCEDURE SP_PCRF_BASEPKG_RESET_TRN(SYS_DAY NUMBER)
AS
  CNT       NUMBER := 0;
  SUC       NUMBER := 0;
  STIME     NUMBER := DBMS_UTILITY.GET_TIME;
  ETIME     NUMBER;
  TME_TAKEN NUMBER;
  INST      NUMBER := DBMS_UTILITY.CURRENT_INSTANCE;
  P_CNT1    NUMBER := 0;
  P_CNT2    NUMBER := 0;
BEGIN
  /* JIRA : NETVERTEX-2028 */
  select PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLM_USAGE_HISTORY') INTO P_CNT1 from dual;
  select PKG_PCRF_SCHEDULER_KPI.FUNC_CHECK_PARTITION('TBLM_USAGE') INTO P_CNT2 from dual;
    FOR REC_S IN (SELECT R.SUBSCRIBERIDENTITY,R.BILLINGDATE FROM TBLM_SUBSCRIBER R WHERE R.BILLINGDATE = SYS_DAY)
    LOOP
      CNT := CNT + 1;
      FOR REC_U IN
      (SELECT ID ,
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
        FROM TBLM_USAGE U
        WHERE U.SUBSCRIBER_ID  = REC_S.SUBSCRIBERIDENTITY
        AND U.SUBSCRIPTION_ID IS NULL
      )
      LOOP
        INSERT
        INTO TBLM_USAGE_HISTORY
          (
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
          VALUES
          (
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
        UPDATE TBLM_USAGE I
        SET I.DAILY_TOTAL          = 0,
        I.DAILY_UPLOAD           = 0,
          I.DAILY_DOWNLOAD         = 0,
          I.DAILY_TIME             = 0,
          I.WEEKLY_TOTAL           = 0,
          I.WEEKLY_UPLOAD          = 0,
          I.WEEKLY_DOWNLOAD        = 0,
          I.WEEKLY_TIME            = 0,
      I.BILLING_CYCLE_TOTAL    = 0,
          I.BILLING_CYCLE_UPLOAD   = 0,
          I.BILLING_CYCLE_DOWNLOAD = 0,
          I.BILLING_CYCLE_TIME     = 0,       
          I.DAILY_RESET_TIME       = TRUNC(SYSDATE)+86399/86400,       
          I.WEEKLY_RESET_TIME      = NEXT_DAY(TRUNC(SYSDATE),'SATURDAY')+86399/86400,
      I.LAST_UPDATE_TIME       = CURRENT_TIMESTAMP      
        WHERE I.SUBSCRIBER_ID      = REC_U.SUBSCRIBER_ID
    AND I.SUBSCRIPTION_ID IS NULL;
      SUC := SUC + SQL%ROWCOUNT;
      COMMIT;
      END LOOP;

END LOOP;
  ETIME := DBMS_UTILITY.GET_TIME;
  TME_TAKEN := ROUND((ETIME-STIME)/100,1);
  IF P_CNT1 = 1 AND P_CNT2 = 1 THEN
    P_CNT1 := 1;
  ELSE
    P_CNT1 := 0;
  END IF;
  PKG_PCRF_SCHEDULER_KPI.SP_SCHEDULER_GENERAL(CNT,SUC,INST,'JOB_SP_PCRF_BASEPKG_RESET_TRN',TME_TAKEN,P_CNT1);
END;
/

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   JOB_NAME           =>  'JOB_SP_PCRF_BASEPKG_RESET_TRN',
   JOB_TYPE           =>  'PLSQL_BLOCK',
   JOB_ACTION         =>  'BEGIN SP_PCRF_BASEPKG_RESET_TRN(TO_NUMBER(TO_CHAR(SYSDATE, ''DD''))); END;',
   START_DATE         =>  SYSTIMESTAMP,
   REPEAT_INTERVAL    =>  'FREQ=DAILY;BYHOUR=00;BYMINUTE=45;',   
   END_DATE           =>   NULL,
   ENABLED            =>   TRUE,
   COMMENTS           =>  'This base pkg reset scheduler will execute on daily 00:45 hour');
END;
/

