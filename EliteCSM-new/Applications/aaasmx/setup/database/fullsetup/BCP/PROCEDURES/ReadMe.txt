-------------------------------
EliteAAA BCP - PROCEDURES DBC
-------------------------------

Note: 

Copy BCP Scripts from Application Server to Database Server 

1. Go to the location PROCEDURES DBC folder
cd BCP\PROCEDURES

2. Execute PKG_ELITEAAA_IPPOOL.sql procedure DBC
	
	Note: It will ask for Datafile directory loaction and username/password for EliteAAA
	$sqlplus system/<sys password>@<service name>
	SQL>@PKG_ELITEAAA_IPPOOL.sql
	SQL>exit
	
3. Execute SP_FREE_AAA_IP.sql procedure DBC

	$sqlplus <EliteAAA user>/<password>@<service name>
	SQl>@PKG_ELITEAAA_IPPOOL.sql
	
Note: Verify below log file

	1. PKG_ELITEAAA_IPPOOL_v6.6.x.log