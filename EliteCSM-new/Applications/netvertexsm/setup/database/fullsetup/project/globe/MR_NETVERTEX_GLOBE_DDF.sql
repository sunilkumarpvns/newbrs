  --NETVERTEX DDF ARCHITECTURE DEPLOYMENT REFERENCE CODE
  
  CREATE OR REPLACE FORCE VIEW "RM"."VWVOLUMEUSAGE" ("USERID", "POLICYGROUPNAME", "UPLOADOCTETS", "DOWNLOADOCTETS", "TOTALOCTETS", "LASTUPDATETIME", "HSQVALUE", "DESCRIPTION") AS 
  SELECT U.SUBSCRIBER_ID,
	P.NAME,
	U.BILLING_CYCLE_UPLOAD,
	U.BILLING_CYCLE_DOWNLOAD,
	U.BILLING_CYCLE_TOTAL,
	U.LAST_UPDATE_TIME,
	(CASE
	 WHEN QD.TOTAL_UNIT = 'KB' THEN QD.TOTAL * 1024
	 WHEN QD.TOTAL_UNIT = 'MB' THEN QD.TOTAL * 1024 * 1024
	 WHEN QD.TOTAL_UNIT = 'GB' THEN QD.TOTAL * 1024 * 1024 * 1024
	 ELSE QD.TOTAL
	 END ) HSQVALUE,
	P.DESCRIPTION
	FROM TBLM_USAGE U,
		 TBLM_PACKAGE P,
		 TBLM_QUOTA_PROFILE QP,
		 TBLM_QUOTA_PROFILE_DETAIL QD
	WHERE U.PACKAGE_ID=P.ID
	AND   P.ID = QP.PACKAGE_ID
	AND   QP.ID = QD.QUOTA_PROFILE_ID
	AND   DECODE(QP.BALANCE_LEVEL,'HSQ',0,'FUP1',1,'FUP2',2) = QD.FUP_LEVEL;
	/
	
CREATE OR REPLACE FORCE VIEW "RM"."VWVODID" ("VODID", "VODNAME", "VALIDITYPERIOD", "UNIT", "HSQVALUE", "PRICE", "CREATEDATE") AS 
  SELECT P.ID,
P.NAME,
P.VALIDITY_PERIOD,
P.VALIDITY_PERIOD_UNIT,
(CASE 
 WHEN QD.TOTAL_UNIT = 'KB' THEN QD.TOTAL * 1024
 WHEN QD.TOTAL_UNIT = 'MB' THEN QD.TOTAL * 1024 * 1024
 WHEN QD.TOTAL_UNIT = 'GB' THEN QD.TOTAL * 1024 * 1024 * 1024
 ELSE QD.TOTAL 
 END ) HSQVALUE,
P.PRICE,
P.CREATED_DATE
FROM TBLM_PACKAGE P,TBLM_QUOTA_PROFILE QP,TBLM_QUOTA_PROFILE_DETAIL QD
WHERE P.ID=QP.PACKAGE_ID 
AND QP.ID=QD.QUOTA_PROFILE_ID 
AND P.TYPE='TOPUP' 
AND P.STATUS='ACTIVE'
AND p.PACKAGE_MODE IN ('LIVE','LIVE2');
/


  CREATE OR REPLACE FORCE VIEW "RM"."VWRADIUSPOLICY" ("PLANNAME", "DESCRIPTION", "CREATEDDATE", "HSQVALUE_IN_BYTES", "PCCRULE") AS 
  SELECT P.NAME,
P.DESCRIPTION,
TO_DATE(TO_CHAR(P.CREATED_DATE,'YYYY-MON-DD HH24:MI:SS'),'YYYY-MON-DD HH24:MI:SS') AS "CREATE DATE",
(CASE 
 WHEN QD.TOTAL_UNIT = 'KB' THEN QD.TOTAL * 1024
 WHEN QD.TOTAL_UNIT = 'MB' THEN QD.TOTAL * 1024 * 1024
 WHEN QD.TOTAL_UNIT = 'GB' THEN QD.TOTAL * 1024 * 1024 * 1024
 ELSE QD.TOTAL 
 END ) HSQVALUE,
QPCC.NAME  AS  "PCC Rule Name" 
FROM TBLM_PACKAGE P,     
	 TBLM_QUOTA_PROFILE QP,
	 TBLM_QUOTA_PROFILE_DETAIL QD,
	 TBLM_QOS_PROFILE QSP,
	 TBLM_QOS_PROFILE_DETAIL QSPD,
	 TBLM_QOS_PROFILE_PCC_RELATION QPR,
	 TBLM_PCC_RULE QPCC
WHERE P.ID = QP.PACKAGE_ID
AND p.PACKAGE_MODE IN ('LIVE','LIVE2')
AND   QP.ID = QD.QUOTA_PROFILE_ID
AND   DECODE(QP.BALANCE_LEVEL,'HSQ',0,'FUP1',1,'FUP2',2) = QD.FUP_LEVEL
AND QSP.QUOTA_PROFILE_ID = QP.ID
AND QSP.NAME = 'DEFAULT'
 AND QSP.ID = QSPD.QOS_PROFILE_ID
 AND QSPD.ID = QPR.QOS_PROFILE_DETAIL_ID
 AND QSPD.FUP_LEVEL=0
 AND QPR.PCC_RULE_ID = QPCC.ID
 AND QPCC.STATUS='ACTIVE';
 /
 
 
  CREATE OR REPLACE FORCE VIEW "RM"."VWPACKAGEDETAILS" ("PLANNAME", "DESCRIPTION", "CREATEDDATE", "BALANCE", "PCCRULE") AS 
  SELECT P.NAME,
P.DESCRIPTION,
TO_DATE(TO_CHAR(P.CREATED_DATE,'YYYY-MON-DD HH24:MI:SS'),'YYYY-MON-DD HH24:MI:SS') AS "CREATE DATE",TO_CHAR(QD.TOTAL||' '||QD.TOTAL_UNIT) AS "BALANCE",R.NAME "PCC Rule Name"
FROM TBLM_PACKAGE P,TBLM_PCC_RULE R,TBLM_QOS_PROFILE Q,TBLM_QOS_PROFILE_PCC_RELATION S,TBLM_QOS_PROFILE_DETAIL D,TBLM_QUOTA_PROFILE QP,TBLM_QUOTA_PROFILE_DETAIL QD
WHERE
P.ID=Q.PACKAGE_ID AND
Q.ID=D.QOS_PROFILE_ID AND
D.ID=R.QOS_PROFILE_DETAIL_ID AND
R.ID=S.PCC_RULE_ID  AND
P.ID=QP.PACKAGE_ID AND
QP.ID=QD.QUOTA_PROFILE_ID;
/


  CREATE OR REPLACE FORCE VIEW "RM"."VWDATAUSAGE" ("SUBSCRIBER_ID", "NAME", "LAST_UPDATE_TIME", "DAILY_DOWNLOAD", "DAILY_UPLOAD") AS 
  SELECT U.SUBSCRIBER_ID,P.NAME,
U.LAST_UPDATE_TIME,
U.DAILY_DOWNLOAD,
U.DAILY_UPLOAD
FROM TBLM_USAGE U,TBLM_PACKAGE P
WHERE U.PACKAGE_ID=P.ID;
/

  CREATE OR REPLACE FORCE VIEW "RM"."VWADDONDETAILS" ("ADDONID", "NAME", "VALIDITYPERIOD", "UNIT", "BALANCE", "PRICE", "CREATEDATE") AS 
  SELECT P.ID,
P.NAME,
P.VALIDITY_PERIOD,
P.VALIDITY_PERIOD_UNIT,
QD.TOTAL ||' '|| QD.TOTAL_UNIT,
P.PRICE,
P.CREATED_DATE
FROM TBLM_PACKAGE P,TBLM_QUOTA_PROFILE QP,TBLM_QUOTA_PROFILE_DETAIL QD
WHERE
P.ID=QP.PACKAGE_ID AND
QP.ID=QD.QUOTA_PROFILE_ID AND
P.TYPE='ADDON';
/

  CREATE OR REPLACE FORCE VIEW "RM"."GLOBE" ("PACKAGE_NAME", "QUOTA_PROFILE", "QOS_PROFILE", "PCC_RULE_NAME", "FUP_LEVEL", "TOTAL") AS 
  SELECT P.NAME AS PACKAGE_NAME,
QP.NAME AS QUOTA_PROFILE,QSP.NAME AS QOS_PROFILE,QPCC.NAME  AS  PCC_Rule_Name,QD.FUP_LEVEL,
(CASE 
WHEN QD.TOTAL_UNIT = 'KB' THEN QD.TOTAL * 1024
WHEN QD.TOTAL_UNIT = 'MB' THEN QD.TOTAL * 1024 * 1024
WHEN QD.TOTAL_UNIT = 'GB' THEN QD.TOTAL * 1024 * 1024 * 1024
ELSE QD.TOTAL 
END ) TOTAL
FROM TBLM_PACKAGE P,
TBLM_QUOTA_PROFILE QP,
TBLM_QUOTA_PROFILE_DETAIL QD,
TBLM_QOS_PROFILE QSP,
TBLM_QOS_PROFILE_DETAIL QSPD,
TBLM_QOS_PROFILE_PCC_RELATION QPR,
TBLM_PCC_RULE QPCC
WHERE P.ID               = QP.PACKAGE_ID
AND QP.ID                = QD.QUOTA_PROFILE_ID
AND QSP.QUOTA_PROFILE_ID = QP.ID
AND QSP.ID               = QSPD.QOS_PROFILE_ID
AND QSPD.ID              = QPR.QOS_PROFILE_DETAIL_ID
AND QPR.PCC_RULE_ID      = QPCC.ID
AND DECODE(QP.BALANCE_LEVEL,'HSQ',0,'FUP1',1) =  QSPD.FUP_LEVEL
AND QD.FUP_LEVEL IN (0,1)
AND QSPD.FUP_LEVEL IN (0,1);
/



---Default Node-SETUP3

create or replace PROCEDURE PROC_DAILY_PACKAGE_RESET
as
BEGIN
FOR REC IN (SELECT P.NAME,
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
FROM TBLM_USAGE U,
  TBLM_QUOTA_PROFILE P
WHERE U.QUOTA_PROFILE_ID = P.ID)
LOOP

  IF REC.NAME LIKE '%_DAILY' THEN

  UPDATE TBLM_USAGE R
  SET R.BILLING_CYCLE_TIME=0,
      R.BILLING_CYCLE_TOTAL=0,
	  R.BILLING_CYCLE_UPLOAD=0,
	  R.BILLING_CYCLE_DOWNLOAD=0,
	  R.LAST_UPDATE_TIME = CURRENT_TIMESTAMP
  WHERE R.ID = REC.ID;

  INSERT INTO TBLM_USAGE_HISTORY
  VALUES(CURRENT_TIMESTAMP,
  REC.ID,
  REC.SUBSCRIBER_ID,
  REC.PACKAGE_ID,
  REC.SUBSCRIPTION_ID,
  REC.QUOTA_PROFILE_ID,
  REC.SERVICE_ID,
  REC.DAILY_TOTAL,
  REC.DAILY_UPLOAD,
  REC.DAILY_DOWNLOAD,
  REC.DAILY_TIME,
  REC.WEEKLY_TOTAL,
  REC.WEEKLY_UPLOAD,
  REC.WEEKLY_DOWNLOAD,
  REC.WEEKLY_TIME,
  REC.BILLING_CYCLE_TOTAL,
  REC.BILLING_CYCLE_UPLOAD,
  REC.BILLING_CYCLE_DOWNLOAD,
  REC.BILLING_CYCLE_TIME,
  REC.CUSTOM_TOTAL,
  REC.CUSTOM_UPLOAD,
  REC.CUSTOM_DOWNLOAD,
  REC.CUSTOM_TIME,
  REC.DAILY_RESET_TIME,
  REC.WEEKLY_RESET_TIME,
  REC.CUSTOM_RESET_TIME,
  REC.BILLING_CYCLE_RESET_TIME,
  REC.LAST_UPDATE_TIME,
  REC.NAME,
  REC.PACKAGE_ID,
  'D',
  'D');
   
  COMMIT;
  
  END IF;
END LOOP;
END;
/

create or replace PROCEDURE  PROC_MONTHLY_PACKAGE_RESET
as
BEGIN
FOR REC IN (SELECT P.NAME,
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
FROM TBLM_USAGE U,
  TBLM_QUOTA_PROFILE P
WHERE U.QUOTA_PROFILE_ID = P.ID)
LOOP

  IF REC.NAME NOT LIKE '%_DAILY' THEN

  UPDATE TBLM_USAGE R
  SET R.BILLING_CYCLE_TIME=0,
      R.BILLING_CYCLE_TOTAL=0,
	  R.BILLING_CYCLE_UPLOAD=0,
	  R.BILLING_CYCLE_DOWNLOAD=0,
	  R.LAST_UPDATE_TIME = CURRENT_TIMESTAMP
  WHERE R.ID = REC.ID;

  INSERT INTO TBLM_USAGE_HISTORY
  VALUES(CURRENT_TIMESTAMP,
  REC.ID,
  REC.SUBSCRIBER_ID,
  REC.PACKAGE_ID,
  REC.SUBSCRIPTION_ID,
  REC.QUOTA_PROFILE_ID,
  REC.SERVICE_ID,
  REC.DAILY_TOTAL,
  REC.DAILY_UPLOAD,
  REC.DAILY_DOWNLOAD,
  REC.DAILY_TIME,
  REC.WEEKLY_TOTAL,
  REC.WEEKLY_UPLOAD,
  REC.WEEKLY_DOWNLOAD,
  REC.WEEKLY_TIME,
  REC.BILLING_CYCLE_TOTAL,
  REC.BILLING_CYCLE_UPLOAD,
  REC.BILLING_CYCLE_DOWNLOAD,
  REC.BILLING_CYCLE_TIME,
  REC.CUSTOM_TOTAL,
  REC.CUSTOM_UPLOAD,
  REC.CUSTOM_DOWNLOAD,
  REC.CUSTOM_TIME,
  REC.DAILY_RESET_TIME,
  REC.WEEKLY_RESET_TIME,
  REC.CUSTOM_RESET_TIME,
  REC.BILLING_CYCLE_RESET_TIME,
  REC.LAST_UPDATE_TIME,
  REC.NAME,
  REC.PACKAGE_ID,
  'M',
  'D');
   
  COMMIT;

  END IF;
END LOOP;
END;
/

---SPR Node1-SETUP1
  CREATE DATABASE LINK "SETUP3N6"
   CONNECT TO "RM" IDENTIFIED BY VALUES '065C395842C8D4FB0E5DD48C655911FABF8C6D21677A2DFA70E1CD4E414C5CF930AF8FA0DCC4A7F41EC6368D32B14E4A7553D89C67DF45ED7A701F15B214B5611053C2D5508A2AD17B9F50256A3FFC0D4A1059029E32405525DA8B5FB081AF4EF2123FAB8E67A1A8B36D2341963D29C9E93A51CD4A540F88BB1EBC15A77D85BB'
   USING 'setup3';
   
  CREATE MATERIALIZED VIEW "RM"."TBLM_QUOTA_PROFILE_DETAIL" ("ID", "FUP_LEVEL", "QUOTA_PROFILE_ID", "SERVICE_TYPE_ID", "AGGREGATION_KEY", "TOTAL", "TOTAL_UNIT", "UPLOAD", "UPLOAD_UNIT", "DOWNLOAD", "DOWNLOAD_UNIT", "TIME", "TIME_UNIT")
  ORGANIZATION HEAP PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  BUILD IMMEDIATE
  USING INDEX 
  REFRESH COMPLETE ON DEMAND
  WITH PRIMARY KEY USING DEFAULT LOCAL ROLLBACK SEGMENT
  USING ENFORCED CONSTRAINTS DISABLE QUERY REWRITE
  AS SELECT "TBLM_QUOTA_PROFILE_DETAIL"."ID" "ID","TBLM_QUOTA_PROFILE_DETAIL"."FUP_LEVEL" "FUP_LEVEL","TBLM_QUOTA_PROFILE_DETAIL"."QUOTA_PROFILE_ID" "QUOTA_PROFILE_ID","TBLM_QUOTA_PROFILE_DETAIL"."SERVICE_TYPE_ID" "SERVICE_TYPE_ID","TBLM_QUOTA_PROFILE_DETAIL"."AGGREGATION_KEY" "AGGREGATION_KEY","TBLM_QUOTA_PROFILE_DETAIL"."TOTAL" "TOTAL","TBLM_QUOTA_PROFILE_DETAIL"."TOTAL_UNIT" "TOTAL_UNIT","TBLM_QUOTA_PROFILE_DETAIL"."UPLOAD" "UPLOAD","TBLM_QUOTA_PROFILE_DETAIL"."UPLOAD_UNIT" "UPLOAD_UNIT","TBLM_QUOTA_PROFILE_DETAIL"."DOWNLOAD" "DOWNLOAD","TBLM_QUOTA_PROFILE_DETAIL"."DOWNLOAD_UNIT" "DOWNLOAD_UNIT","TBLM_QUOTA_PROFILE_DETAIL"."TIME" "TIME","TBLM_QUOTA_PROFILE_DETAIL"."TIME_UNIT" "TIME_UNIT" FROM "TBLM_QUOTA_PROFILE_DETAIL"@"SETUP3N6" "TBLM_QUOTA_PROFILE_DETAIL";

   COMMENT ON MATERIALIZED VIEW "RM"."TBLM_QUOTA_PROFILE_DETAIL"  IS 'snapshot table for snapshot RM.TBLM_QUOTA_PROFILE_DETAIL';

 CREATE MATERIALIZED VIEW "RM"."TBLM_QUOTA_PROFILE" ("ID", "NAME", "STATUS", "PACKAGE_ID", "DESCRIPTION", "USAGE_AVAILABILITY", "BALANCE_LEVEL", "CREATED_DATE", "CREATED_BY_STAFF_ID", "MODIFIED_DATE", "MODIFIED_BY_STAFF_ID")
  ORGANIZATION HEAP PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  BUILD IMMEDIATE
  USING INDEX 
  REFRESH COMPLETE ON DEMAND
  WITH PRIMARY KEY USING DEFAULT LOCAL ROLLBACK SEGMENT
  USING ENFORCED CONSTRAINTS DISABLE QUERY REWRITE
  AS SELECT "TBLM_QUOTA_PROFILE"."ID" "ID","TBLM_QUOTA_PROFILE"."NAME" "NAME","TBLM_QUOTA_PROFILE"."STATUS" "STATUS","TBLM_QUOTA_PROFILE"."PACKAGE_ID" "PACKAGE_ID","TBLM_QUOTA_PROFILE"."DESCRIPTION" "DESCRIPTION","TBLM_QUOTA_PROFILE"."USAGE_AVAILABILITY" "USAGE_AVAILABILITY","TBLM_QUOTA_PROFILE"."BALANCE_LEVEL" "BALANCE_LEVEL","TBLM_QUOTA_PROFILE"."CREATED_DATE" "CREATED_DATE","TBLM_QUOTA_PROFILE"."CREATED_BY_STAFF_ID" "CREATED_BY_STAFF_ID","TBLM_QUOTA_PROFILE"."MODIFIED_DATE" "MODIFIED_DATE","TBLM_QUOTA_PROFILE"."MODIFIED_BY_STAFF_ID" "MODIFIED_BY_STAFF_ID" FROM "TBLM_QUOTA_PROFILE"@"SETUP3N6" "TBLM_QUOTA_PROFILE";

   COMMENT ON MATERIALIZED VIEW "RM"."TBLM_QUOTA_PROFILE"  IS 'snapshot table for snapshot RM.TBLM_QUOTA_PROFILE';
   
  CREATE MATERIALIZED VIEW "RM"."TBLM_PACKAGE" ("ID", "NAME", "STATUS", "DESCRIPTION", "TYPE", "PACKAGE_MODE", "VALIDITY_PERIOD", "VALIDITY_PERIOD_UNIT", "PRICE", "USAGE_RESET_INTERVAL", "EXCLUSIVE_ADDON", "MULTIPLE_SUBSCRIPTION", "REPLACEABLE_BY_ADDON", "AVAILABILITY_START_DATE", "AVAILABILITY_END_DATE", "QUOTA_PROFILE_TYPE", "GRACE_PERIOD", "GRACE_PERIOD_UNIT", "GROUPS", "ORDER_NO", "CREATED_DATE", "CREATED_BY_STAFF_ID", "MODIFIED_DATE", "MODIFIED_BY_STAFF_ID")
  ORGANIZATION HEAP PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  BUILD IMMEDIATE
  USING INDEX 
  REFRESH COMPLETE ON DEMAND
  WITH PRIMARY KEY USING DEFAULT LOCAL ROLLBACK SEGMENT
  USING ENFORCED CONSTRAINTS DISABLE QUERY REWRITE
  AS SELECT "TBLM_PACKAGE"."ID" "ID","TBLM_PACKAGE"."NAME" "NAME","TBLM_PACKAGE"."STATUS" "STATUS","TBLM_PACKAGE"."DESCRIPTION" "DESCRIPTION","TBLM_PACKAGE"."TYPE" "TYPE","TBLM_PACKAGE"."PACKAGE_MODE" "PACKAGE_MODE","TBLM_PACKAGE"."VALIDITY_PERIOD" "VALIDITY_PERIOD","TBLM_PACKAGE"."VALIDITY_PERIOD_UNIT" "VALIDITY_PERIOD_UNIT","TBLM_PACKAGE"."PRICE" "PRICE","TBLM_PACKAGE"."USAGE_RESET_INTERVAL" "USAGE_RESET_INTERVAL","TBLM_PACKAGE"."EXCLUSIVE_ADDON" "EXCLUSIVE_ADDON","TBLM_PACKAGE"."MULTIPLE_SUBSCRIPTION" "MULTIPLE_SUBSCRIPTION","TBLM_PACKAGE"."REPLACEABLE_BY_ADDON" "REPLACEABLE_BY_ADDON","TBLM_PACKAGE"."AVAILABILITY_START_DATE" "AVAILABILITY_START_DATE","TBLM_PACKAGE"."AVAILABILITY_END_DATE" "AVAILABILITY_END_DATE","TBLM_PACKAGE"."QUOTA_PROFILE_TYPE" "QUOTA_PROFILE_TYPE","TBLM_PACKAGE"."GRACE_PERIOD" "GRACE_PERIOD","TBLM_PACKAGE"."GRACE_PERIOD_UNIT" "GRACE_PERIOD_UNIT","TBLM_PACKAGE"."GROUPS" "GROUPS","TBLM_PACKAGE"."ORDER_NO" "ORDER_NO","TBLM_PACKAGE"."CREATED_DATE" "CREATED_DATE","TBLM_PACKAGE"."CREATED_BY_STAFF_ID" "CREATED_BY_STAFF_ID","TBLM_PACKAGE"."MODIFIED_DATE" "MODIFIED_DATE","TBLM_PACKAGE"."MODIFIED_BY_STAFF_ID" "MODIFIED_BY_STAFF_ID" FROM "TBLM_PACKAGE"@"SETUP3N6" "TBLM_PACKAGE";

   COMMENT ON MATERIALIZED VIEW "RM"."TBLM_PACKAGE"  IS 'snapshot table for snapshot RM.TBLM_PACKAGE';
   
---SPR Node2-SETUP2
  CREATE DATABASE LINK "SETUP3N6"
   CONNECT TO "RM" IDENTIFIED BY VALUES '065C395842C8D4FB0E5DD48C655911FABF8C6D21677A2DFA70E1CD4E414C5CF930AF8FA0DCC4A7F41EC6368D32B14E4A7553D89C67DF45ED7A701F15B214B5611053C2D5508A2AD17B9F50256A3FFC0D4A1059029E32405525DA8B5FB081AF4EF2123FAB8E67A1A8B36D2341963D29C9E93A51CD4A540F88BB1EBC15A77D85BB'
   USING 'setup3';
   
  CREATE MATERIALIZED VIEW "RM"."TBLM_PACKAGE" ("ID", "NAME", "STATUS", "DESCRIPTION", "TYPE", "PACKAGE_MODE", "VALIDITY_PERIOD", "VALIDITY_PERIOD_UNIT", "PRICE", "USAGE_RESET_INTERVAL", "EXCLUSIVE_ADDON", "MULTIPLE_SUBSCRIPTION", "REPLACEABLE_BY_ADDON", "AVAILABILITY_START_DATE", "AVAILABILITY_END_DATE", "QUOTA_PROFILE_TYPE", "GRACE_PERIOD", "GRACE_PERIOD_UNIT", "GROUPS", "ORDER_NO", "CREATED_DATE", "CREATED_BY_STAFF_ID", "MODIFIED_DATE", "MODIFIED_BY_STAFF_ID")
  ORGANIZATION HEAP PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  BUILD IMMEDIATE
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  REFRESH COMPLETE ON DEMAND START WITH sysdate+0 NEXT ( SYSDATE + 1/24 )
  WITH PRIMARY KEY USING DEFAULT LOCAL ROLLBACK SEGMENT
  USING ENFORCED CONSTRAINTS DISABLE QUERY REWRITE
  AS SELECT "TBLM_PACKAGE"."ID" "ID","TBLM_PACKAGE"."NAME" "NAME","TBLM_PACKAGE"."STATUS" "STATUS","TBLM_PACKAGE"."DESCRIPTION" "DESCRIPTION","TBLM_PACKAGE"."TYPE" "TYPE","TBLM_PACKAGE"."PACKAGE_MODE" "PACKAGE_MODE","TBLM_PACKAGE"."VALIDITY_PERIOD" "VALIDITY_PERIOD","TBLM_PACKAGE"."VALIDITY_PERIOD_UNIT" "VALIDITY_PERIOD_UNIT","TBLM_PACKAGE"."PRICE" "PRICE","TBLM_PACKAGE"."USAGE_RESET_INTERVAL" "USAGE_RESET_INTERVAL","TBLM_PACKAGE"."EXCLUSIVE_ADDON" "EXCLUSIVE_ADDON","TBLM_PACKAGE"."MULTIPLE_SUBSCRIPTION" "MULTIPLE_SUBSCRIPTION","TBLM_PACKAGE"."REPLACEABLE_BY_ADDON" "REPLACEABLE_BY_ADDON","TBLM_PACKAGE"."AVAILABILITY_START_DATE" "AVAILABILITY_START_DATE","TBLM_PACKAGE"."AVAILABILITY_END_DATE" "AVAILABILITY_END_DATE","TBLM_PACKAGE"."QUOTA_PROFILE_TYPE" "QUOTA_PROFILE_TYPE","TBLM_PACKAGE"."GRACE_PERIOD" "GRACE_PERIOD","TBLM_PACKAGE"."GRACE_PERIOD_UNIT" "GRACE_PERIOD_UNIT","TBLM_PACKAGE"."GROUPS" "GROUPS","TBLM_PACKAGE"."ORDER_NO" "ORDER_NO","TBLM_PACKAGE"."CREATED_DATE" "CREATED_DATE","TBLM_PACKAGE"."CREATED_BY_STAFF_ID" "CREATED_BY_STAFF_ID","TBLM_PACKAGE"."MODIFIED_DATE" "MODIFIED_DATE","TBLM_PACKAGE"."MODIFIED_BY_STAFF_ID" "MODIFIED_BY_STAFF_ID" FROM "TBLM_PACKAGE"@"SETUP3N6" "TBLM_PACKAGE";

   COMMENT ON MATERIALIZED VIEW "RM"."TBLM_PACKAGE"  IS 'snapshot table for snapshot RM.TBLM_PACKAGE';
   
   
  CREATE MATERIALIZED VIEW "RM"."TBLM_QUOTA_PROFILE" ("ID", "NAME", "STATUS", "PACKAGE_ID", "DESCRIPTION", "USAGE_AVAILABILITY", "BALANCE_LEVEL", "CREATED_DATE", "CREATED_BY_STAFF_ID", "MODIFIED_DATE", "MODIFIED_BY_STAFF_ID")
  ORGANIZATION HEAP PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  BUILD IMMEDIATE
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  REFRESH COMPLETE ON DEMAND START WITH sysdate+0 NEXT ( SYSDATE + 1/24 )
  WITH PRIMARY KEY USING DEFAULT LOCAL ROLLBACK SEGMENT
  USING ENFORCED CONSTRAINTS DISABLE QUERY REWRITE
  AS SELECT "TBLM_QUOTA_PROFILE"."ID" "ID","TBLM_QUOTA_PROFILE"."NAME" "NAME","TBLM_QUOTA_PROFILE"."STATUS" "STATUS","TBLM_QUOTA_PROFILE"."PACKAGE_ID" "PACKAGE_ID","TBLM_QUOTA_PROFILE"."DESCRIPTION" "DESCRIPTION","TBLM_QUOTA_PROFILE"."USAGE_AVAILABILITY" "USAGE_AVAILABILITY","TBLM_QUOTA_PROFILE"."BALANCE_LEVEL" "BALANCE_LEVEL","TBLM_QUOTA_PROFILE"."CREATED_DATE" "CREATED_DATE","TBLM_QUOTA_PROFILE"."CREATED_BY_STAFF_ID" "CREATED_BY_STAFF_ID","TBLM_QUOTA_PROFILE"."MODIFIED_DATE" "MODIFIED_DATE","TBLM_QUOTA_PROFILE"."MODIFIED_BY_STAFF_ID" "MODIFIED_BY_STAFF_ID" FROM "TBLM_QUOTA_PROFILE"@"SETUP3N6" "TBLM_QUOTA_PROFILE";

   COMMENT ON MATERIALIZED VIEW "RM"."TBLM_QUOTA_PROFILE"  IS 'snapshot table for snapshot RM.TBLM_QUOTA_PROFILE';
   
 CREATE MATERIALIZED VIEW "RM"."TBLM_PACKAGE" ("ID", "NAME", "STATUS", "DESCRIPTION", "TYPE", "PACKAGE_MODE", "VALIDITY_PERIOD", "VALIDITY_PERIOD_UNIT", "PRICE", "USAGE_RESET_INTERVAL", "EXCLUSIVE_ADDON", "MULTIPLE_SUBSCRIPTION", "REPLACEABLE_BY_ADDON", "AVAILABILITY_START_DATE", "AVAILABILITY_END_DATE", "QUOTA_PROFILE_TYPE", "GRACE_PERIOD", "GRACE_PERIOD_UNIT", "GROUPS", "ORDER_NO", "CREATED_DATE", "CREATED_BY_STAFF_ID", "MODIFIED_DATE", "MODIFIED_BY_STAFF_ID")
  ORGANIZATION HEAP PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  BUILD IMMEDIATE
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "RM" 
  REFRESH COMPLETE ON DEMAND START WITH sysdate+0 NEXT ( SYSDATE + 1/24 )
  WITH PRIMARY KEY USING DEFAULT LOCAL ROLLBACK SEGMENT
  USING ENFORCED CONSTRAINTS DISABLE QUERY REWRITE
  AS SELECT "TBLM_PACKAGE"."ID" "ID","TBLM_PACKAGE"."NAME" "NAME","TBLM_PACKAGE"."STATUS" "STATUS","TBLM_PACKAGE"."DESCRIPTION" "DESCRIPTION","TBLM_PACKAGE"."TYPE" "TYPE","TBLM_PACKAGE"."PACKAGE_MODE" "PACKAGE_MODE","TBLM_PACKAGE"."VALIDITY_PERIOD" "VALIDITY_PERIOD","TBLM_PACKAGE"."VALIDITY_PERIOD_UNIT" "VALIDITY_PERIOD_UNIT","TBLM_PACKAGE"."PRICE" "PRICE","TBLM_PACKAGE"."USAGE_RESET_INTERVAL" "USAGE_RESET_INTERVAL","TBLM_PACKAGE"."EXCLUSIVE_ADDON" "EXCLUSIVE_ADDON","TBLM_PACKAGE"."MULTIPLE_SUBSCRIPTION" "MULTIPLE_SUBSCRIPTION","TBLM_PACKAGE"."REPLACEABLE_BY_ADDON" "REPLACEABLE_BY_ADDON","TBLM_PACKAGE"."AVAILABILITY_START_DATE" "AVAILABILITY_START_DATE","TBLM_PACKAGE"."AVAILABILITY_END_DATE" "AVAILABILITY_END_DATE","TBLM_PACKAGE"."QUOTA_PROFILE_TYPE" "QUOTA_PROFILE_TYPE","TBLM_PACKAGE"."GRACE_PERIOD" "GRACE_PERIOD","TBLM_PACKAGE"."GRACE_PERIOD_UNIT" "GRACE_PERIOD_UNIT","TBLM_PACKAGE"."GROUPS" "GROUPS","TBLM_PACKAGE"."ORDER_NO" "ORDER_NO","TBLM_PACKAGE"."CREATED_DATE" "CREATED_DATE","TBLM_PACKAGE"."CREATED_BY_STAFF_ID" "CREATED_BY_STAFF_ID","TBLM_PACKAGE"."MODIFIED_DATE" "MODIFIED_DATE","TBLM_PACKAGE"."MODIFIED_BY_STAFF_ID" "MODIFIED_BY_STAFF_ID" FROM "TBLM_PACKAGE"@"SETUP3N6" "TBLM_PACKAGE";

   COMMENT ON MATERIALIZED VIEW "RM"."TBLM_PACKAGE"  IS 'snapshot table for snapshot RM.TBLM_PACKAGE';
     
   
