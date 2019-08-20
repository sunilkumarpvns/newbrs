INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (SEQ_TBLMVERSION.nextval,'6.8.0.6','upgrade-csm-to-6.8.0.6','AAASMX',CURRENT_TIMESTAMP);

--ELITEAAA-3309 This entry was missing while you install full setup so by default you do not have rights in admin acces group for creating any operation in Radius Service Group. 
INSERT INTO TBLMGROUPACTIONREL (ACTIONID,GROUPID) VALUES ('ACN00422',1);
COMMIT;

--ELITEAAA-3314 Introducing new module "Radius ESI Group"
CREATE SEQUENCE SEQ_MRADIUSESIGROUP
    INCREMENT BY 1
    START WITH 20
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20
    NOORDER;

CREATE TABLE TBLMRADIUSESIGROUP(
 ESIGROUPID 				NUMBER(20,0),
 ESIGROUPNAME				VARCHAR(200) NOT NULL,
 DESCRIPTION 				VARCHAR(255),
 PRIMARYESIID				NUMBER(20,0),
 PRIMARYESINAME 			VARCHAR2(200 BYTE),
 SECONDARYESIID 			NUMBER(20,0),
 SECONDARYESINAME			VARCHAR2(200 BYTE),
 AUDIT_UID 					NUMBER(20,0),
 CONSTRAINT PK_MRADIUSESIGROUP 	PRIMARY KEY (ESIGROUPID),
 CONSTRAINT UK_MRADIUSESIGROUP 	UNIQUE 		(ESIGROUPNAME),
 CONSTRAINT FK1_MRADIUSESIGROUP 	FOREIGN KEY (PRIMARYESIID) REFERENCES TBLMESIINSTANCE (ESIINSTANCEID),
 CONSTRAINT FK2_MRADIUSESIGROUP 	FOREIGN KEY (SECONDARYESIID) REFERENCES TBLMESIINSTANCE (ESIINSTANCEID)
);

--ACL for Radius ESI Group

INSERT INTO TBLMBISMODULE
   (BUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,BISMODULETYPEID,DESCRIPTION,FREEZEPROFILE)
VALUES
   ('BMO00053','RADIUS_ESI_GROUP','Radius ESI Group','N','E','BTY00001','Radius ESI Group','E');

INSERT INTO TBLTBISMODELMODULEREL (BUSINESSMODELID,BUSINESSMODULEID,STATUS) VALUES ('BM000001','BMO00053','E');
   
INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
VALUES('SBM00125','SEARCH_RADIUS_ESI_GROUP','Search Radius ESI Group','N','E','STY00001','Search Radius ESI Group','E');

INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
VALUES('SBM00126','CREATE_RADIUS_ESI_GROUP','Create Radius ESI Group','N','E','STY00001','Create Radius ESI Group','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00053','SBM00125','E');
Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00053','SBM00126','E');


Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00456','Update Radius ESI Group','UPDATE_RADIUS_ESI_GROUP','Update Radius ESI Group','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00457','Search Radius ESI Group','SEARCH_RADIUS_ESI_GROUP','Search Radius ESI Group','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00458','View Radius ESI Group','VIEW_RADIUS_ESI_GROUP','View Radius ESI Group','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00459','Delete Radius ESI Group','DELETE_RADIUS_ESI_GROUP','Delete Radius ESI Group','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00460','Create Radius ESI Group','CREATE_RADIUS_ESI_GROUP','Create Radius ESI Group','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00456','SBM00125','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00457','SBM00125','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00458','SBM00125','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00459','SBM00125','E');
Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00460','SBM00126','E');

Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00456',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00457',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00458',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00459',1);
Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('ACN00460',1);

commit;

--Added new column for sequencing support in  TBLMTRANSACTIONLOGGER table

ALTER TABLE TBLMTRANSACTIONLOGGER ADD (RANGE VARCHAR2(40));

ALTER TABLE TBLMTRANSACTIONLOGGER ADD (PATTERN VARCHAR2(16) DEFAULT 'suffix');

ALTER TABLE TBLMTRANSACTIONLOGGER ADD (GLOBALIZATION VARCHAR2(8) DEFAULT 'false');