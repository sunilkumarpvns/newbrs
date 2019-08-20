Insert into TBLMPLUGINVERSION values(seq_tblmversion.nextval,'6.0.0.0-r${eliteaaa.svn.rev}','FULLSETUP','Zero Session Time Filter Plugin',CURRENT_TIMESTAMP);


Insert into TBLMPLUGINTYPE
	(PLUGINTYPEID,NETSERVERTYPEID,NAME,ALIAS,DESCRIPTION,SYSTEMGENERATED,ENABLED,PROCESSINGTYPE,PLUGINVERSION)
Values
	('PGT0003','SRV0001','Zero Session Time Filter Plugin','ZERO_SESSION_TIME_FILTER_PLUGIN','Zero Session Time Filter Plugin','N','Y',1,'${plugin.version}');

Insert into TBLMNETCONFIGURATION
	(NETCONFIGID, SERIALNO, NAME, DISPLAYNAME, FILENAME,
	ALIAS, CONFIGVERSION)
Values
	('PGC0003', 3 ,'Zero Session Time Filter Plugin' , 'Zero Session Time Filter Plugin' , 'zero-session-time-filter-plugin' ,
	'ZERO_SESSION_TIME_FILTER_PLUGIN' , '1.0');

Insert into TBLMPLUGINCONFIGMAP (PLUGINTYPEID,NETCONFIGID) values ('PGT0003','PGC0003');

commit;
