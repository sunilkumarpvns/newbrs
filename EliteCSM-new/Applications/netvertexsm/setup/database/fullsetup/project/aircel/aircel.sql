create table tblstelcordiaerror (
errorid varchar2(20) primary key,
subscriberID varchar2(50),
servProv varchar2(50),
profileID varchar2(50),
voicePlan varchar2(50),
dateTime varchar2(50),
serviceLabel varchar2(50),
bonusCode varchar2(50),
reasonCode varchar2(50),
rechargeType varchar2(50),
errorreason varchar2(255)
);

CREATE SEQUENCE  SEQ_TELCORDIAERROR  MINVALUE 1 MAXVALUE 99999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 10 NOORDER  NOCYCLE ;

CREATE TABLE APIADDONHISTORY (
    SUBSCRIBERID VARCHAR2(128 BYTE) , 
    ADDONID NUMBER(*,0) , 
    STARTTIME TIMESTAMP (6), 
    ENDTIME TIMESTAMP (6), 
    LASTUPDATEIME TIMESTAMP (6), 
    STATUS VARCHAR2(32 BYTE) , 
    ADDONSUBSCRIPTIONID NUMBER(20,0), 
    PARENTIDENTITY VARCHAR2(256 BYTE), 
    SUBSCRIPTIONTIME TIMESTAMP (6), 
    REJECTREASON VARCHAR2(1024 BYTE)
  );
  
  create table tbltelprovhistory (
	id NUMBER(20) primary key,
	subscriberID varchar2(50),
	servProv varchar2(50),
	profileID varchar2(50),
	voicePlan varchar2(50),
	dateTime varchar2(50),
	serviceLabel varchar2(50),
	bonusCode varchar2(50),
	reasonCode varchar2(50),
	rechargeType varchar2(50),
	currenttimestamp timestamp default CURRENT_TIMESTAMP
);

CREATE SEQUENCE  seq_telprovhistory  MINVALUE 1 MAXVALUE 99999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 10 NOORDER  NOCYCLE ;

CREATE SEQUENCE  SEQ_TBLCIRCLEMRPMAPPING  MINVALUE 1 MAXVALUE 99999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 10 NOORDER  NOCYCLE;

create table TBLCIRCLEMRPMAPPING 
( mappingid number(20) not null,
  circle_name varchar2(3),
  plan_mrp varchar2(10),
  allow_multi_recharge number(1),
  addonId number not null,
   CONSTRAINT mappingid_pk PRIMARY KEY (mappingid),
   CONSTRAINT circle_mrp_addonid_unique UNIQUE (circle_name,plan_mrp,addonId)
);

ALTER TABLE APIADDONHISTORY ADD TOTALOCTETSBEFORERECHARGE NUMBER(20) DEFAULT 0;

ALTER TABLE tblstelcordiaerror ADD requesttimestamp timestamp default CURRENT_TIMESTAMP;

--REV 15133 - Changes for Subscription Management Screen

ALTER TABLE TBLCIRCLEMRPMAPPING ADD starttime timestamp DEFAULT TO_DATE('31/12/1950', 'dd/MM/yyyy') NOT NULL;

ALTER TABLE TBLCIRCLEMRPMAPPING ADD endtime timestamp DEFAULT TO_DATE('31/12/9999', 'dd/MM/yyyy') NOT NULL;

ALTER TABLE TBLCIRCLEMRPMAPPING ADD PARENTID NUMBER(20) DEFAULT 0;

ALTER TABLE TBLCIRCLEMRPMAPPING DROP CONSTRAINT circle_mrp_addonid_unique;

DROP INDEX circle_mrp_addonid_unique;

ALTER TABLE TBLCIRCLEMRPMAPPING ADD CONSTRAINT circle_mrp_addonid_unique UNIQUE (circle_name,plan_mrp,addonId, endtime);

ALTER TABLE TBLCIRCLEMRPMAPPING MODIFY CIRCLE_NAME NOT NULL;

ALTER TABLE TBLCIRCLEMRPMAPPING MODIFY PLAN_MRP NOT NULL;

