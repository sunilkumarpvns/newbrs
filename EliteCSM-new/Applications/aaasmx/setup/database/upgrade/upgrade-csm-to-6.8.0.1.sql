INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (SEQ_TBLMVERSION.nextval,'6.8.0.1','upgrade-csm-to-6.8.0.1','AAASMX',CURRENT_TIMESTAMP);
commit;

-- Introducing MSK Revalidation Time in EAP Configuration
ALTER TABLE TBLMEAPCONFIG ADD MSK_REVALIDATION_TIME	NUMERIC(10);

-- Configuration and implementation of historical password for user
ALTER TABLE TBLMPASSWORDPOLICY ADD MAXHISTORICALPASSWORDS NUMBER(1);
ALTER TABLE TBLMSTAFF ADD HISTORICALPASSWORD VARCHAR2(1550);
UPDATE TBLMPASSWORDPOLICY SET MAXHISTORICALPASSWORDS = 5 WHERE PASSWORDPOLICYID=1;
COMMIT;

--Introducing CDRTIMESTAMPHEADER and CDRTIMESTAMPPOSITION parameter in TBLMCLASSICCSVACCTDRIVER
ALTER TABLE TBLMCLASSICCSVACCTDRIVER
ADD (CDRTIMESTAMPHEADER VARCHAR2(64) DEFAULT 'CDRTimeStamp',CDRTIMESTAMPPOSITION VARCHAR2(64) DEFAULT 'SUFFIX'); 
COMMIT;

--Introduced TIMEBOUNDRY IN  TRANSACTIONLOGGER
ALTER TABLE TBLMTRANSACTIONLOGGER ADD TIMEBOUNDRY NUMBER(6);

--Added TGPP Command Code limit in SYSTEM PARAMETER 
Insert into TBLMSYSTEMPARAMETER (PARAMETERID,NAME,ALIAS,VALUE,SYSTEMGENERATED,DESCRIPTION) values (27,'TGPP Command Code Limit','TGPP_COMMAND_CODE_LIMIT','5','N','Specify to allow Command Code limit in TGPP AAA policy');
COMMIT;

update TBLMNETCONFIGURATIONPARAMETER set NAME='Application Id',DISPLAYNAME='Application Id',ALIAS='application-id',DESCRIPTION='Application Id' where PARAMETERID='APM001115';
COMMIT;

--TGPP AAA Policy - Manage Order
INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE)
VALUES('SBM00123','SEARCH_TGPP_AAA_SERVICE_POLICY','Search TGPP AAA Service Policy','N','E','STY00001','Search TGPP AAA Service Policy','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00033','SBM00123','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE)
values ('ACN00450','Manage TGPP AAA Service Policy','MANAGE_TGPP_AAA_SERVICE_POLICY_ORDER','Manage TGPP AAA Service Policy','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00450','SBM00123','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00450',1);

commit; 

--Plugin DBC for Changing Plugin Type In NAS, CC and EAP Policy
DELETE FROM TBLMEAPPOLICYPLUGINCONFIG;
ALTER TABLE TBLMEAPPOLICYPLUGINCONFIG MODIFY PLUGINTYPE VARCHAR(3);

DELETE FROM TBLMCCPOLICYPLUGINCONFIG;
ALTER TABLE TBLMCCPOLICYPLUGINCONFIG MODIFY PLUGINTYPE VARCHAR(3);

DROP TABLE TBLMNASPOLICYPLUGINCONFIG;

CREATE SEQUENCE SEQ_MNASPOLICYAUTHPLUGINCONFIG
    INCREMENT BY 1
    START WITH 20
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20
    NOORDER;

CREATE TABLE TBLMNASPOLICYAUTHPLUGINCONFIG(
    POLICYCONFID NUMERIC(20),
    PLUGINNAME VARCHAR2(200),
    PLUGINARGUMENT VARCHAR2(4000),
    NASPOLICYID NUMBER(20),
    PLUGINTYPE VARCHAR(3),
    CONSTRAINT PK_MNASPOLICYAUTHPLUGINCONFIG PRIMARY KEY (POLICYCONFID),
    CONSTRAINT FK1_MNASPOLICYAUTHPLUGINCONFIG  FOREIGN KEY (NASPOLICYID) REFERENCES TBLMNASPOLICY (NASPOLICYID)
);

CREATE SEQUENCE SEQ_MNASPOLICYACCTPLUGINCONFIG
    INCREMENT BY 1
    START WITH 20
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20
    NOORDER;

CREATE TABLE TBLMNASPOLICYACCTPLUGINCONFIG(
    POLICYCONFID NUMERIC(20),
    PLUGINNAME VARCHAR2(200),
    PLUGINARGUMENT VARCHAR2(4000),
    NASPOLICYID NUMBER(20),
    PLUGINTYPE VARCHAR(3),
    CONSTRAINT PK_MNASPOLICYACCTPLUGINCONFIG PRIMARY KEY (POLICYCONFID),
    CONSTRAINT FK1_MNASPOLICYACCTPLUGINCONFIG  FOREIGN KEY (NASPOLICYID) REFERENCES TBLMNASPOLICY (NASPOLICYID)
);

COMMIT; 

--Introduced TimeBasedRollingUnit in Transaction Logger
ALTER TABLE TBLMTRANSACTIONLOGGER DROP COLUMN TIMEBASEDROLLINGUNIT;
COMMIT;

--Create TGPP AAA Policy ACL
INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE)
VALUES('SBM00124','CREATE_TGPP_AAA_SERVICE_POLICY','Create TGPP AAA Service Policy','N','E','STY00001','Create TGPP AAA Service Policy','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00033','SBM00124','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE)
values ('ACN00451','Create TGPP AAA Service Policy','CREATE_TGPP_AAA_SERVICE_POLICY','Create TGPP AAA Service Policy','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00451','SBM00124','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00451',1);

--UPDATE TGPP AAA Policy ACL
Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE)
values ('ACN00452','Update TGPP AAA Service Policy','UPDATE_TGPP_AAA_SERVICE_POLICY','Update TGPP AAA Service Policy','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00452','SBM00123','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00452',1);

--SEARCH TGPP AAA Policy ACL
Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE)
values ('ACN00453','Search TGPP AAA Service Policy','SEARCH_TGPP_AAA_SERVICE_POLICY','Search TGPP AAA Service Policy','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00453','SBM00123','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00453',1);

--View TGPP AAA Policy ACL
Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE)
values ('ACN00454','View TGPP AAA Service Policy','VIEW_TGPP_AAA_SERVICE_POLICY','View TGPP AAA Service Policy','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00454','SBM00123','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00454',1);

--Delete TGPP AAA Policy ACL
Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE)
values ('ACN00455','Delete TGPP AAA Service Policy','DELETE_TGPP_AAA_SERVICE_POLICY','Delete TGPP AAA Service Policy','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00455','SBM00123','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00455',1);

COMMIT;