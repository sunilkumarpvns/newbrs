INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (SEQ_TBLMVERSION.nextval,'6.6.0.4','upgrade-csm-to-6.6.0.4','AAASMX',CURRENT_TIMESTAMP);
Commit;

alter table tblmdynauthservicepolicy ADD (DBFAILUREACTION VARCHAR(10));

UPDATE TBLMNETCONFIGURATIONPARAMETER SET DEFAULTVALUE = '0.0.0.0:1161' WHERE PARAMETERID = 'APM000014';
UPDATE TBLMNETCONFIGURATIONPARAMETER SET DEFAULTVALUE = '0.0.0.0:1161' WHERE PARAMETERID = 'APM000440';
commit;

ALTER TABLE TBLMSMCONFIGINSTANCE ADD SESSIONSTOPACTION VARCHAR(10) DEFAULT 'DELETE';

--change default value of batch update interval in session manager
update tblmsmconfiginstance set batchupdateinterval= batchupdateinterval*1000;

COMMIT;

-- IMSI BASED ROUTING TABLE
ALTER TABLE TBLMPEERGROUPREL DROP CONSTRAINT FK1_MPEERGROUPREL;
ALTER TABLE TBLMHSSPEERGROUPREL DROP CONSTRAINT FK1_MHSSPEERGROUPREL;
ALTER TABLE TBLMPEER DROP CONSTRAINT PK_MPEER;

ALTER TABLE TBLMPEER ADD CONSTRAINT PK_MPEER PRIMARY KEY (PEERID,PEERNAME);
ALTER TABLE TBLMPEERGROUPREL ADD PEERNAME VARCHAR(60);
UPDATE TBLMPEERGROUPREL A SET A.PEERNAME = (SELECT B.PEERNAME FROM TBLMPEER B WHERE A.PEERID = B.PEERID);
ALTER TABLE TBLMPEERGROUPREL ADD CONSTRAINT FK1_MPEERGROUPREL FOREIGN KEY (PEERID,PEERNAME) REFERENCES TBLMPEER (PEERID,PEERNAME);

ALTER TABLE TBLMHSSPEERGROUPREL ADD PEERNAME VARCHAR(60);
UPDATE TBLMHSSPEERGROUPREL A SET A.PEERNAME = (SELECT B.PEERNAME FROM TBLMPEER B WHERE A.PEERID = B.PEERID);
ALTER TABLE TBLMHSSPEERGROUPREL ADD CONSTRAINT FK1_MHSSPEERGROUPREL FOREIGN KEY (PEERID,PEERNAME) REFERENCES TBLMPEER (PEERID,PEERNAME);

CREATE SEQUENCE SEQ_MIMSIBASEDROUTINGTABLE 
    INCREMENT BY 1
    START WITH 20
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20
    NOORDER;     

CREATE TABLE TBLMIMSIBASEDROUTINGTABLE(
	ROUTINGTABLEID NUMBER(20,0),
	ROUTINGTABLENAME VARCHAR2(128),
	IMSIIDENTITYATTRIBUTES VARCHAR2(256),
	AUDIT_UID NUMBER(20,0),
	CONSTRAINT PK_MIMSIBASEDROUTINGTABLE PRIMARY KEY (ROUTINGTABLEID),
	CONSTRAINT UK1_MIMSIBASEDROUTINGTABLE UNIQUE (ROUTINGTABLENAME)
);

CREATE SEQUENCE SEQ_MIMSIFIELDMAP
      INCREMENT BY 1
      START WITH 20
      NOMAXVALUE
      NOMINVALUE
      NOCYCLE
      CACHE 20
      NOORDER;

CREATE TABLE TBLMIMSIFIELDMAP
(
	IMSIFIELDMAPID NUMERIC(10),
	ROUTINGTABLEID NUMERIC(10),
	IMSIRANGE VARCHAR2(256),
	PRIMARYPEERNAME  VARCHAR(60),
	SECONDARYPEERNAME VARCHAR(60),
	PRIMARYPEERID NUMERIC(10),
	SECONDARYPEERID NUMERIC(10),
	TAG VARCHAR(50),
	CONSTRAINT PK_MIMSIFIELDMAP PRIMARY KEY (IMSIFIELDMAPID),
	CONSTRAINT FK1_MIMSIFIELDMAP  FOREIGN KEY (ROUTINGTABLEID) REFERENCES TBLMIMSIBASEDROUTINGTABLE (ROUTINGTABLEID),
	CONSTRAINT FK2_MIMSIFIELDMAP  FOREIGN KEY (PRIMARYPEERID,PRIMARYPEERNAME) REFERENCES TBLMPEER (PEERID,PEERNAME),
	CONSTRAINT FK3_MIMSIFIELDMAP  FOREIGN KEY (SECONDARYPEERID,SECONDARYPEERNAME) REFERENCES TBLMPEER (PEERID,PEERNAME)
);

COMMIT;


--ADD ACL for IMSI Based Routing table
INSERT INTO TBLMBISMODULE (BUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,BISMODULETYPEID,DESCRIPTION,FREEZEPROFILE)
VALUES ('BMO00046','IMSI_BASED_ROUTING_TABLE','IMSI Based Routing Table','N','E','BTY00001','IMSI Based Routing Table','E');	

INSERT INTO TBLTBISMODELMODULEREL (BUSINESSMODELID,BUSINESSMODULEID,STATUS) VALUES ('BM000010','BMO00046','E');

INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
VALUES ('SBM00108','CREATE_IMSI_BASED_ROUTING_TABLE','Create IMSI Based Routing Table','N','E','STY00001','Create IMSI Based Routing Table','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00046','SBM00108','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00412','Create IMSI Based Routing Table','CREATE_IMSI_BASED_ROUTING_TABLE','Create IMSI Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00412','SBM00108','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00412',1);
--SEarch

INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
VALUES ('SBM00109','SEARCH_IMSI_BASED_ROUTING_TABLE','Search IMSI Based Routing Table','N','E','STY00001','Search IMSI Based Routing Table','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00046','SBM00109','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00413','Search IMSI Based Routing Table','SEARCH_IMSI_BASED_ROUTING_TABLE','Search IMSI Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00413','SBM00109','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00413',1);

--update and delete
Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00414','Update IMSI Based Routing Table','UPDATE_IMSI_BASED_ROUTING_TABLE','Update IMSI Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00415','Delete IMSI Based Routing Table','DELETE_IMSI_BASED_ROUTING_TABLE','Delete IMSI Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00416','View IMSI Based Routing Table','VIEW_IMSI_BASED_ROUTING_TABLE','View IMSI Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00414','SBM00109','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00415','SBM00109','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00416','SBM00109','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00414',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00415',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00416',1);

COMMIT;

ALTER TABLE TBLMROUTINGCONFIG ADD IMSIBASEDROUTINGTABLEID NUMERIC(20,0);
ALTER TABLE TBLMROUTINGCONFIG ADD CONSTRAINT FK5_MROUTINGCONFIG FOREIGN KEY (IMSIBASEDROUTINGTABLEID) REFERENCES TBLMIMSIBASEDROUTINGTABLE(ROUTINGTABLEID);


-- EliteKPI Prerequisites for Database Package Module --

ALTER TABLE JVMTHREADINSTANCETABLE
ADD (JVMTHREADINSTCPUTIMENS_MINVAL NUMBER(20,0),  
     JVMTHREADINSTCPUTIMENS_MAXVAL NUMBER(20,0)); 
                                              

ALTER TABLE JVMMEMPOOLTABLE
ADD (JVMSYSTEMLOADAVERAGE_MINVAL NUMBER(20,0),
     JVMSYSTEMLOADAVERAGE_MAXVAL NUMBER(20,0));


ALTER TABLE JVMMEMORY
ADD (JVMMEMORYHEAPUSED_MINVAL NUMBER(20,0),
     JVMMEMORYHEAPUSED_MAXVAL NUMBER(20,0),
	 JVMMEMORYNONHEAPUSED_MINVAL NUMBER(20,0),
	 JVMMEMORYNONHEAPUSED_MAXVAL NUMBER(20,0));	 	 

--MSISDN Based Routing Table
	 
CREATE SEQUENCE SEQ_MMSISDNBASEDROUTINGTABLE 
    INCREMENT BY 1
    START WITH 20
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20
    NOORDER; 
	
CREATE SEQUENCE SEQ_MMSISDNFIELDMAP
      INCREMENT BY 1
      START WITH 20
      NOMAXVALUE
      NOMINVALUE
      NOCYCLE
      CACHE 20
      NOORDER;
      
CREATE TABLE TBLMMSISDNBASEDROUTINGTABLE(
	ROUTINGTABLEID NUMBER(20,0),
	ROUTINGTABLENAME VARCHAR2(128),
	MSISDNIDENTITYATTRIBUTES VARCHAR2(256) default '21067:65599.21067:65600',
	MSISDN_LENGTH NUMBER(2,0) default 10,
	MCC VARCHAR2(10),
	AUDIT_UID NUMBER(20,0),
	CONSTRAINT PK_MMSISDNBASEDROUTINGTABLE PRIMARY KEY (ROUTINGTABLEID),
	CONSTRAINT UK1_MMSISDNBASEDROUTINGTABLE UNIQUE (ROUTINGTABLENAME)
);

CREATE TABLE TBLMMSISDNFIELDMAP(
	MSISDNFIELDMAPID NUMERIC(10),
	ROUTINGTABLEID NUMERIC(20),
	MSISDNRANGE VARCHAR2(256),
	PRIMARYPEERNAME  VARCHAR(60),
	SECONDARYPEERNAME VARCHAR(60),
	PRIMARYPEERID NUMERIC(10),
	SECONDARYPEERID NUMERIC(10),
	TAG VARCHAR(50),
	CONSTRAINT PK_MMSISDNFIELDMAP PRIMARY KEY (MSISDNFIELDMAPID),
	CONSTRAINT FK1_MMSISDNFIELDMAP  FOREIGN KEY (ROUTINGTABLEID) REFERENCES TBLMMSISDNBASEDROUTINGTABLE (ROUTINGTABLEID),
	CONSTRAINT FK2_MMSISDNFIELDMAP  FOREIGN KEY (PRIMARYPEERID,PRIMARYPEERNAME) REFERENCES TBLMPEER (PEERID,PEERNAME),
	CONSTRAINT FK3_MMSISDNFIELDMAP  FOREIGN KEY (SECONDARYPEERID,SECONDARYPEERNAME) REFERENCES TBLMPEER (PEERID,PEERNAME)
);

COMMIT;


--ADD ACL for MSISDN Based Routing table --

--Create Module ACL
INSERT INTO TBLMBISMODULE (BUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,BISMODULETYPEID,DESCRIPTION,FREEZEPROFILE)
VALUES ('BMO00047','MSISDN_BASED_ROUTING_TABLE','MSISDN Based Routing Table','N','E','BTY00001','MSISDN Based Routing Table','E');	

INSERT INTO TBLTBISMODELMODULEREL (BUSINESSMODELID,BUSINESSMODULEID,STATUS) VALUES ('BM000010','BMO00047','E');

INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
VALUES ('SBM00110','CREATE_MSISDN_BASED_ROUTING_TABLE','Create MSISDN Based Routing Table','N','E','STY00001','Create MSISDN Based Routing Table','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00047','SBM00110','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00417','Create MSISDN Based Routing Table','CREATE_MSISDN_BASED_ROUTING_TABLE','Create MSISDN Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00417','SBM00110','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00417',1);

--SEARCH MODULE ACL
INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
VALUES ('SBM00111','SEARCH_MSISDN_BASED_ROUTING_TABLE','Search MSISDN Based Routing Table','N','E','STY00001','Search MSISDN Based Routing Table','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00047','SBM00111','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00418','Search MSISDN Based Routing Table','SEARCH_MSISDN_BASED_ROUTING_TABLE','Search MSISDN Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00418','SBM00111','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00418',1);

--UPDATE and DELETE Module ACL
Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00419','Update MSISDN Based Routing Table','UPDATE_MSISDN_BASED_ROUTING_TABLE','Update MSISDN Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00420','Delete MSISDN Based Routing Table','DELETE_MSISDN_BASED_ROUTING_TABLE','Delete MSISDN Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00421','View MSISDN Based Routing Table','VIEW_MSISDN_BASED_ROUTING_TABLE','View MSISDN Based Routing Table','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00419','SBM00111','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00420','SBM00111','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00421','SBM00111','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00419',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00420',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00421',1);

COMMIT;

--Subscriber Routing1, SunscriberRouting2 and SubscriberMode Configuration parameter
ALTER TABLE TBLMROUTINGCONFIG ADD MSISDNBASEDROUTINGTABLEID NUMERIC(20,0);
ALTER TABLE TBLMROUTINGCONFIG ADD CONSTRAINT FK6_MROUTINGCONFIG FOREIGN KEY (MSISDNBASEDROUTINGTABLEID) REFERENCES TBLMMSISDNBASEDROUTINGTABLE(ROUTINGTABLEID);
ALTER TABLE TBLMROUTINGCONFIG ADD SUBSCRIBERMODE VARCHAR2(12);

COMMIT;