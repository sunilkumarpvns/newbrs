CREATE TABLE TBLM_SUBSCRIBER (
    USERNAME              VARCHAR(255),
    PASSWORD              VARCHAR(255),
    SUBSCRIBERIDENTITY 	  VARCHAR(255) NOT NULL,
    PARENTID              VARCHAR(255),
    GROUPNAME             VARCHAR(255),
    ENCRYPTIONTYPE        VARCHAR(255),
    CUSTOMERTYPE          VARCHAR(10),
    BILLINGDATE           INTEGER,
    EXPIRYDATE            TIMESTAMP,
    PRODUCT_OFFER         VARCHAR(100),
    IMSPACKAGE            VARCHAR(100),
    EMAIL                 VARCHAR(100),
    PHONE                 VARCHAR(20),
    SIPURL                VARCHAR(200),
    AREA                  VARCHAR(20),
    CITY                  VARCHAR(20),
    ZONE                  VARCHAR(20),
    COUNTRY               VARCHAR(55),
    BIRTHDATE             TIMESTAMP,
    ROLE                  VARCHAR(20),
    COMPANY               VARCHAR(512),
    DEPARTMENT            VARCHAR(20),
    CADRE                 VARCHAR(5),
    ARPU                  INTEGER,
    CUI                   VARCHAR(255),
    IMSI                  VARCHAR(100),
    MSISDN                VARCHAR(100),
    IMEI                  VARCHAR(100),
    ESN                   VARCHAR(100),
    MEID                  VARCHAR(100),
    MAC  		              VARCHAR(100),
    EUI64  		            VARCHAR(100),
    MODIFIED_EUI64  	    VARCHAR(100),
    SUBSCRIBERLEVELMETERING   VARCHAR(10) DEFAULT 'DISABLE',
    STATUS                VARCHAR(24),
    PASSWORD_CHECK        VARCHAR(5) DEFAULT '0',
    SY_INTERFACE          VARCHAR(5) DEFAULT '1',
    PAYG_INTL_DATA_ROAMING          VARCHAR(5) DEFAULT '1',
    CALLING_STATION_ID    VARCHAR(253),
    FRAMED_IP             VARCHAR(20),
    NAS_PORT_ID           VARCHAR(50),
    PARAM1   	            VARCHAR(10),
    PARAM2  	            VARCHAR(10),
    PARAM3   	            VARCHAR(10),
    PARAM4  	            VARCHAR(10),
    PARAM5  	            VARCHAR(10),
    BILLING_ACCOUNT_ID    VARCHAR(100),
	  SERVICE_INSTANCE_ID   VARCHAR(100),
    CREATED_DATE        TIMESTAMP,
    MODIFIED_DATE        TIMESTAMP,
     NEXTBILLDATE TIMESTAMP,
     BILLCHANGEDATE TIMESTAMP,
    CONSTRAINT PK_SUBSCRIBER PRIMARY KEY (SUBSCRIBERIDENTITY)
);

CREATE TABLE TBLT_USAGE (
    ID                     VARCHAR(36) NOT NULL,
    SUBSCRIBER_ID          VARCHAR(255) NOT NULL,
    PACKAGE_ID             VARCHAR(36),
    SUBSCRIPTION_ID        VARCHAR(36),
    QUOTA_PROFILE_ID       VARCHAR(36),
    SERVICE_ID             VARCHAR(36),
    DAILY_TOTAL            BIGINT,
    DAILY_UPLOAD           BIGINT,
    DAILY_DOWNLOAD         BIGINT,
    DAILY_TIME             BIGINT,
    WEEKLY_TOTAL           BIGINT,
    WEEKLY_UPLOAD          BIGINT,
    WEEKLY_DOWNLOAD        BIGINT,
    WEEKLY_TIME            BIGINT,
    BILLING_CYCLE_TOTAL    BIGINT,
    BILLING_CYCLE_UPLOAD   BIGINT,
    BILLING_CYCLE_DOWNLOAD BIGINT,
    BILLING_CYCLE_TIME     BIGINT,
    CUSTOM_TOTAL           BIGINT,
    CUSTOM_UPLOAD          BIGINT,
    CUSTOM_DOWNLOAD        BIGINT,
    CUSTOM_TIME            BIGINT,
    DAILY_RESET_TIME       TIMESTAMP,
    WEEKLY_RESET_TIME      TIMESTAMP,
    CUSTOM_RESET_TIME      TIMESTAMP,
    BILLING_CYCLE_RESET_TIME TIMESTAMP,
    LAST_UPDATE_TIME       TIMESTAMP,
    PRODUCT_OFFER_ID VARCHAR(36)
);

CREATE TABLE TBLT_SUBSCRIPTION (
    SUBSCRIPTION_ID      VARCHAR(36) NOT NULL,
    SUBSCRIBER_ID        VARCHAR(256) NOT NULL,
    PACKAGE_ID           VARCHAR(36) NOT NULL,
    START_TIME           TIMESTAMP,
    END_TIME             TIMESTAMP,
    STATUS               TINYINT DEFAULT '0',
    SERVER_INSTANCE_ID   INTEGER,
    PARENT_IDENTITY      VARCHAR(256),
    REJECT_REASON        VARCHAR(1024),
    SUBSCRIPTION_TIME    TIMESTAMP,
    LAST_UPDATE_TIME     TIMESTAMP,
    PRIORITY             INTEGER,
    USAGE_RESET_DATE     TIMESTAMP,
    PARAM1               VARCHAR(256),
    PARAM2               VARCHAR(256),
    PRODUCT_OFFER_ID     VARCHAR(36),
    TYPE VARCHAR(10)
);

CREATE TABLE TBLT_USAGE_HISTORY (
	CREATED_DATE           TIMESTAMP,
    ID                     VARCHAR(36) NOT NULL,
    SUBSCRIBER_ID          VARCHAR(255) NOT NULL,
    PACKAGE_ID             VARCHAR(36),
    SUBSCRIPTION_ID        VARCHAR(36),
    QUOTA_PROFILE_ID       VARCHAR(36),
    SERVICE_ID             VARCHAR(36),
    DAILY_TOTAL            BIGINT,
    DAILY_UPLOAD           BIGINT,
    DAILY_DOWNLOAD         BIGINT,
    DAILY_TIME             BIGINT,
    WEEKLY_TOTAL           BIGINT,
    WEEKLY_UPLOAD          BIGINT,
    WEEKLY_DOWNLOAD        BIGINT,
    WEEKLY_TIME            BIGINT,
    BILLING_CYCLE_TOTAL    BIGINT,
    BILLING_CYCLE_UPLOAD   BIGINT,
    BILLING_CYCLE_DOWNLOAD BIGINT,
    BILLING_CYCLE_TIME     BIGINT,
    CUSTOM_TOTAL           BIGINT,
    CUSTOM_UPLOAD          BIGINT,
    CUSTOM_DOWNLOAD        BIGINT,
    CUSTOM_TIME            BIGINT,
    DAILY_RESET_TIME       TIMESTAMP,
    WEEKLY_RESET_TIME      TIMESTAMP,
    CUSTOM_RESET_TIME      TIMESTAMP,
    BILLING_CYCLE_RESET_TIME TIMESTAMP,
    LAST_UPDATE_TIME       TIMESTAMP,
    PRODUCT_OFFER_ID       VARCHAR(36)
);

CREATE TABLE TBLM_RESET_USAGE_REQ(
 BILLING_CYCLE_ID            VARCHAR(36),
 SUBSCRIBER_IDENTITY         VARCHAR(36),
 ALTERNATE_IDENTITY          VARCHAR(36) ,
 BILLING_CYCLE_DATE          TIMESTAMP(6),
 CREATED_DATE                TIMESTAMP(6),
 STATUS                      VARCHAR(1),
 SERVER_INSTANCE_ID          VARCHAR(8),
 PACKAGE_ID                  VARCHAR(36),
 RESET_REASON                VARCHAR(256),
 PARAM1                      VARCHAR(256),
 PARAM2                      VARCHAR(256),
 PARAM3                      VARCHAR(256),
 PRODUCT_OFFER_ID            VARCHAR(36),
);

CREATE TABLE IS_AVAILABLE(ID VARCHAR(36));

CREATE TABLE DUAL (ID INTEGER);
INSERT INTO DUAL VALUES(1);

CREATE TABLE TBLM_MONETARY_BALANCE(
  ID                      VARCHAR(36),
  SUBSCRIBER_ID           VARCHAR(255),
  SERVICE_ID              VARCHAR(36),
  AVAILABLE_BALANCE       FLOAT,
  INITIAL_BALANCE           FLOAT,
  TOTAL_RESERVATION       FLOAT,
  CREDIT_LIMIT       BIGINT,
  NEXT_BILL_CYCLE_CREDIT_LIMIT       BIGINT,
  CREDIT_LIMIT_UPDATE_TIME        TIMESTAMP,
  VALID_FROM_DATE        TIMESTAMP,
  VALID_TO_DATE          TIMESTAMP,
  CURRENCY            VARCHAR(5),
  TYPE                    VARCHAR(32),
  METADATA             VARCHAR(2000),
  LAST_UPDATE_TIME          TIMESTAMP,
  PARAM1 VARCHAR(256),
  PARAM2 VARCHAR(256),
  CONSTRAINT PK_MONITORY_BALANCE PRIMARY KEY(ID)
);

CREATE TABLE TBLT_SESSION (
  CS_ID                       VARCHAR(36),
  CORE_SESSION_ID             VARCHAR(255),
  SESSION_ID                  VARCHAR(255),
  GATEWAY_ADDRESS             VARCHAR(50),
  SESSION_MANAGER_ID          VARCHAR(50),
  SESSION_IP_V4               VARCHAR(35),
  SESSION_IP_V6               VARCHAR(35),
  ACCESS_NETWORK              VARCHAR(20),
  GATEWAY_REALM               VARCHAR(50),
  SESSION_TYPE                VARCHAR(20),
  START_TIME                  TIMESTAMP,
  LAST_UPDATE_TIME            TIMESTAMP,
  TIME_ZONE                   VARCHAR(35),
  QOS_PROFILE                 VARCHAR(255),
  SUBSCRIBER_IDENTITY         VARCHAR(255),
  SOURCE_GATEWAY              VARCHAR(64),
  SY_SESSION_ID               VARCHAR(255),
  GATEWAY_NAME                VARCHAR(40),
  SY_GATEWAY_NAME             VARCHAR(40),
  CONGESTION_STATUS           FLOAT,
  IMSI                        VARCHAR(50),
  MSISDN                      VARCHAR(50),
  NAI                         VARCHAR(50),
  NAI_REALM                   VARCHAR(50),
  NAI_USER_NAME               VARCHAR(50),
  SIP_URL                     VARCHAR(50),
  PCC_RULES                   VARCHAR(1000),
  REQUESTED_QOS               VARCHAR(500),
  SESSION_USAGE               VARCHAR(150),
  REQUEST_NUMBER              VARCHAR(10),
  USAGE_RESERVATION           VARCHAR(1000),
  CHARGING_RULE_BASE_NAMES    VARCHAR(1000),
  PACKAGE_USAGE               VARCHAR(2000),
  CALLING_STATION_ID          VARCHAR(253),
  CALLED_STATION_ID           VARCHAR(253),
  QUOTA_RESERVATION           VARCHAR(4000),
  PCC_PROFILE_SELECTION_STATE VARCHAR(1000),
  UNACCOUNTED_QUOTA           VARCHAR(4000),
  SGSN_MCC_MNC                VARCHAR(6),
  LOCATION                    VARCHAR(2000),
  SERVICE                     VARCHAR(100),
  PARAM1                      VARCHAR(255),
  PARAM2                      VARCHAR(255),
  PARAM3                      VARCHAR(255),
  PARAM4                      VARCHAR(255),
  PARAM5                      VARCHAR(255)
);
CREATE INDEX IX_TBLT_SESSION_CORE_SESSION_ID ON TBLT_SESSION(CORE_SESSION_ID);
CREATE INDEX IX_TBLT_SESSION_SESSION_ID ON TBLT_SESSION(SESSION_ID);
CREATE INDEX IX_TBLT_SESSION_SUBSCRIBER_IDENTITY ON TBLT_SESSION(SUBSCRIBER_IDENTITY);
CREATE INDEX IX_TBLT_SESSION_SESSION_IP_V4 ON TBLT_SESSION(SESSION_IP_V4);
CREATE INDEX IX_TBLT_SESSION_SESSION_IP_V6 ON TBLT_SESSION(SESSION_IP_V6);
CREATE INDEX IX_TBLT_SESSION_SY_SESSION_ID ON TBLT_SESSION(SY_SESSION_ID);
CREATE INDEX IX_TBLT_SESSION_GATEWAY_ADDRESS ON TBLT_SESSION(GATEWAY_ADDRESS);


CREATE TABLE TBLT_SUB_SESSION (
	SR_ID                        VARCHAR(36),
	SESSION_ID                   VARCHAR(255),
	GATEWAY_ADDRESS 			       VARCHAR(64),
	AF_SESSION_ID             	 VARCHAR(255),
  MEDIA_TYPE                   VARCHAR(36),
	PCC_RULE                     VARCHAR(255),
	MEDIA_COMPONENT_NUMBER       VARCHAR(9),
	FLOW_NUMBER        			     VARCHAR(9),
	UPLINK_FLOW					         VARCHAR(255),
	DOWNLINK_FLOW				         VARCHAR(255),
	START_TIME                   TIMESTAMP,
  LAST_UPDATE_TIME             TIMESTAMP,
	ADDITIONAL_PARAMETER         VARCHAR(4000)
);
CREATE INDEX IX_TBLT_SUB_SESSION_SESSION_ID ON TBLT_SUB_SESSION(SESSION_ID);
CREATE INDEX IX_TBLT_SUB_SESSION_AF_SESSION_ID ON TBLT_SUB_SESSION(AF_SESSION_ID);


load classes NetVertex-PCC-StoredProcedure.jar;

CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberAddStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberAddWithUMStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberAddWithMonetaryBalanceProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberChangeDataPackageStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberChangeDataPackageAndNewUsageInsertStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberChangeDataPackageAndScheduleOldUsageDeleteStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberChangeDataPackageAndScheduleOldUsageDeleteAndNewUsageInsertStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberFetchDeletedMarkProfileStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberMarkDeleteStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberPurgeAndDeleteUsageStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberRestoreStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberSelectStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberUpdateStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageAddToExistingStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageDeleteByPackageIdStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageDeleteStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageGetStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageHistoryInsertStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageInsertStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageReplaceStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageResetStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UsageScheduleResetStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.ChangeIMSpackageStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionSelectStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionAddStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionAddWithUmStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionCheckExistsStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionSelectBySubscriptionIdStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionUnsubscribeStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionUpdateDetailStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriptionUpdateStatusStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceAddStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceUpdateStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceSelectStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceReserveStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceDirectDebitStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceReportAndReserveStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.IsVoltDBAvailable;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceUpdateCreditLimitStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberChangeBillDayStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.RechargeMonetaryBalanceStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.RechargeMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.AddMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure;

--For TBLT_SESSION
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SaveCoreSessionStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UpdateCoreSessionStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SelectCoreSessionsBySingleKeyValueStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SelectCoreSessionsByCriteriaStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.DeleteCoreSessionsBySingleKeyValueStoredProcedure;

--For TBLT_SUB_SESSION
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SaveSubSessionStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UpdateSubSessionStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SelectSessionRulesBySingleKeyValueStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.DeleteSessionRulesBySingleKeyValueStoredProcedure;
