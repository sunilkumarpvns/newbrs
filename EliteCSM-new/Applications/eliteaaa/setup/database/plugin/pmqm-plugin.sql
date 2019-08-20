
Insert into TBLMPLUGINVERSION values(seq_tblmversion.nextval,'6.0.0.0-r${eliteaaa.svn.rev}','FULLSETUP','PMQM Plugin',CURRENT_TIMESTAMP);

Insert into TBLMPLUGINTYPE
	(PLUGINTYPEID,NETSERVERTYPEID,NAME,ALIAS,DESCRIPTION,SYSTEMGENERATED,ENABLED,PROCESSINGTYPE,PLUGINVERSION)
Values
	('PGT0006','SRV0001','PMQM Plugin','PMQM_ACCT_PLUGIN','PMQM Plugin','N','Y',3,'${plugin.version}');

Insert into TBLMNETCONFIGURATION
	(NETCONFIGID, SERIALNO, NAME, DISPLAYNAME, FILENAME,
	ALIAS, CONFIGVERSION)
Values
	('PGC0006', 6 ,'PMQM Plugin' , 'PMQM Plugin' , 'pmqm-acct-plugin' ,
	'PMQM_ACCT_PLUGIN' , '1.0');

Insert into TBLMPLUGINCONFIGMAP (PLUGINTYPEID,NETCONFIGID) values ('PGT0006','PGC0006');

commit;


INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000036' , 'PGC0006' , 1 , 'PMQM Plugin' , 'PMQM Plugin' ,
	'pmqm-acct-plugin' , 'PMQM Plugin' , 'P' , 'regexp' , 1 , 'N' ,
	NULL , NULL , 0 , 'N' ,
	'SMS0164' , 'Y' , 'N');

INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000037' , 'PGC0006' , 1 , 'WebServer Properties' , 'WebServer Properties' ,
	'webserver-properties' , 'WebServer Properties' , 'P' , 'regexp' , 1 , 'N' ,
	'PGP000036' , NULL , 0 , 'Y' ,
	'SMS0164' , 'Y' , 'N');

INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000038' , 'PGC0006' , 1 , 'URL' , 'URL' ,
	'url' , 'Specify Web Service URL' , 'L' , 'regexp' , 1 , 'N' ,
	'PGP000037' , 'http://10.104.1.20/psssc/services/SSSCSCEService' , 500 , 'y' ,
	'SMS0164' , 'Y' , 'N');	
	
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000039' , 'PGC0006' , 2 , 'PM Field Mapping' , 'PM Field Mapping' ,
	'pm-field-mapping' , 'PM Field Mapping' , 'P' , 'regexp' , 1 , 'N' ,
	'PGP000036' , NULL , 0 , 'Y' ,
	'SMS0164' , 'Y' , 'N');	
	
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000040' , 'PGC0006' , 1 , 'Account Field Mapping' , 'Account Field Mapping' ,
	'acct-field-mapping' , 'Account Field Mapping' , 'P' , 'regexp' , 1 , 'N' ,
	'PGP000039' , NULL , 0 , 'Y' ,
	'SMS0164' , 'Y' , 'N');	
	
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000041' , 'PGC0006' , 1 , 'Attribute' , 'Attribute' ,
	'attribute' , 'Attribute' , 'P' , 'regexp' , 100 , 'Y' ,
	'PGP000040' , NULL , 0 , 'Y' ,
	'SMS0164' , 'Y' , 'N');		
	
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000042' , 'PGC0006' , 1 , 'Acct Attribute Id' , 'Acct Attribute Id' ,
	'acct-attribute-id' , 'Accountt Attribute Id' , 'L' , 'regexp' , 1 , 'N' ,
	'PGP000041' , '0:1' , 255 , 'Y' ,
	'SMS0164' , 'Y' , 'N');		
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000043' , 'PGC0006' , 2 , 'PM Field' , 'PM Field' ,
	'pm-field' , 'PM Field' , 'L' , 'regexp' , 1 , 'N' ,
	'PGP000041' , 'USER_NAME' , 255 , 'Y' ,
	'SMS0164' , 'Y' , 'N');
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000044' , 'PGC0006' , 3 , 'Default Value' , 'Default Value' ,
	'default-value' , 'Default Value' , 'L' , 'regexp' , 1 , 'N' ,
	'PGP000041' , null , 255 , 'Y' ,
	'SMS0164' , 'Y' , 'N');	

INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000045' , 'PGC0006' , 4 , 'Value Mapping' , 'Value Mapping' ,
	'value-mapping' , 'Value Mapping' , 'P' , 'regexp' , 1 , 'N' ,
	'PGP000041' , NULL , 0 , 'Y' ,
	'SMS0164' , 'Y' , 'N');		
	
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000046' , 'PGC0006' , 1 , 'Value' , 'Value' ,
	'value' , 'Value' , 'P' , 'regexp' , 1 , 'N' ,
	'PGP000045' , NULL , 0 , 'Y' ,
	'SMS0164' , 'Y' , 'N');		
	

INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000047' , 'PGC0006' , 2 , 'Radius' , 'Radius' ,
	'radius' , 'Radius' , 'L' , 'regexp' , 1 , 'N' ,
	'PGP000046' , NULL , 255 , 'Y' ,
	'SMS0164' , 'Y' , 'N');	
INSERT INTO TBLMNETCONFIGURATIONPARAMETER
	(PARAMETERID, CONFIGID, SERIALNO, NAME, DISPLAYNAME, 
	ALIAS, DESCRIPTION, TYPE, REGEXP, MAXINSTANCES,MULTIPLEINSTANCES, 
	PARENTPARAMETERID, DEFAULTVALUE, MAXLENGTH, EDITABLE,
	STARTUPMODE, STATUS, ISNOTNULL )
VALUES
	('PGP000048' , 'PGC0006' , 2 , 'PM' , 'PM' ,
	'pm' , 'PM' , 'L' , 'regexp' , 1 , 'N' ,
	'PGP000046' , NULL , 255 , 'Y' ,
	'SMS0164' , 'Y' , 'N');		

commit;