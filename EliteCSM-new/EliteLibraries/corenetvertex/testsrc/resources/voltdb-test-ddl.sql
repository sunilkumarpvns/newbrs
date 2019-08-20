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
    NEXTBILLDATE TIMESTAMP ,
    BILLCHANGEDATE TIMESTAMP ,
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
    PRODUCT_OFFER_ID       VARCHAR(36)
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
    PRODUCT_OFFER_ID       VARCHAR(36),
    TYPE                   VARCHAR(10)
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
    PRODUCT_OFFER_ID       VARCHAR(36),
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
CONSTRAINT PK_RESET_USAGE_REQ PRIMARY KEY(BILLING_CYCLE_ID)
);

CREATE TABLE TBLM_MONETARY_BALANCE(
  ID                      VARCHAR(36),
  SUBSCRIBER_ID           VARCHAR(255),
  SERVICE_ID              VARCHAR(36),
  AVAILABLE_BALANCE       FLOAT,
  INITIAL_BALANCE         FLOAT,
  TOTAL_RESERVATION       FLOAT,
  CREDIT_LIMIT       BIGINT,
  NEXT_BILL_CYCLE_CREDIT_LIMIT       BIGINT,
  CREDIT_LIMIT_UPDATE_TIME        TIMESTAMP,
  VALID_FROM_DATE        TIMESTAMP,
  VALID_TO_DATE          TIMESTAMP,
  CURRENCY            VARCHAR(5),
  TYPE                    VARCHAR(32),
  LAST_UPDATE_TIME          TIMESTAMP,
  PARAM1 VARCHAR(256),
  PARAM2 VARCHAR(256),
  CONSTRAINT PK_MONITORY_BALANCE PRIMARY KEY(ID)
);

CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberAddStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberAddWithUMStoredProcedure;
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
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceReserveStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceDirectDebitStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceReportAndReserveStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceSelectStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.MonetaryBalanceUpdateCreditLimitStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SubscriberChangeBillDayStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.RechargeMonetaryBalanceStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.RechargeMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.AddMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure;
CREATE PROCEDURE FROM CLASS com.elitecore.corenetvertex.spr.voltdb.storedprocedure.ChangeBaseProductOfferStoredProcedure;