INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (CURRENT_TIMESTAMP,'6.10.0.1','upgrade-csm-to-6.10.0.1','AAASMX',CURRENT_TIMESTAMP);
commit;

-- ACL for Database Properties file
INSERT INTO TBLMBISMODULE (BUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,BISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
values ('BMO00059','DATABASEPROPERTIES','Database Properties','N','E','BTY00001','Database Properties','E');

Insert into TBLTBISMODELMODULEREL (BUSINESSMODELID,BUSINESSMODULEID,STATUS) values ('BM000004','BMO00059','E');

INSERT INTO TBLMSUBBISMODULE (SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) 
VALUES ('SBM00140','UPDATE_DATABASEPROPERTIESFILE','Update Database Properties file','N','E','STY00001','Update Database Properties file','E');

Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ('BMO00059','SBM00140','E');

Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) 
values ('ACN00480','Update Database Properties file','UPDATE_DATABASEPROPERTIESFILE','Update Database Properties file','ACT00','ACN00001',1,'N','SCR001','E','E');

Insert into tblmgroupactionrel (ACTIONID,GROUPID) values ('ACN00480',1);   

Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('ACN00480','SBM00140','E');

Insert into TBLSALERTTYPE (ALERTTYPEID,PARENTID,NAME,ALIAS,ENABLED,TYPE) values('AT000083',NULL,'IMDG','IMDG','Y','P');
Insert into TBLSALERTTYPE (ALERTTYPEID,PARENTID,NAME,ALIAS,ENABLED,TYPE) values ('AT000084','AT000083','InstanceStatus','INSTANCESTATUS','Y','L');
Insert into TBLSALERTTYPE (ALERTTYPEID,PARENTID,NAME,ALIAS,ENABLED,TYPE) values ('AT000085','AT000083','MemberStatus','MEMBERSTATUS','Y','L');
Insert into TBLSALERTTYPE (ALERTTYPEID,PARENTID,NAME,ALIAS,ENABLED,TYPE) values ('AT000086','AT000083','MigrationHealth','MIGRATIONHEALTH','Y','L');
commit;

alter table tblmmappinginstdetailrel add ORDERNUMBER NUMERIC(20);