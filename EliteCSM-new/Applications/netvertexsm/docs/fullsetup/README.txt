Linux - Server Manager - SM Full Set-up :
=========================================

Pre-Requisites :
================
1. Linux Server, Data Source Server, JAVA 1.6 update 45, Tomcat 6.0.35 (or above) should be present with working condition.

2. Ensure that the compatible version of Server Manager will be taken.

Installation Server Manager :
=============================
1.  Login into Machine.

2.  Make two directories 'source'(Please ignore if source directory is available), 'sm', 'source/sm'

3.  Configure $SM_HOME (SM Full Path), $TOMCAT_HOME (Tomcat Full Path) configured in '.bash_profile'/'.profile'

4.  Download / copy the installation file 'NetvertexSM-V<X.X.X.X>.tar.gz' to the 'source/sm' directory.

5.  Untar this installation file :

	e.g.
	--------------------------------------
	gunzip NetvertexSM-V<X.X.X.X>.tar.gz    -    This will make file 'NetvertexSM-<X.X.X.X>.tar'

	tar -xvf NetvertexSM-V<X.X.X.X>.tar     -    This will generate install.sh, README.txt, server-manager-<X.X.X.X>.tar.gz files
	--------------------------------------

6. Run the installation script for installing Server Manager :

    e.g.   sh install.sh $SM_HOME
	
7.  This will create below directories - Please verify :

    1.  ckeditor
	2.  commons-logging.properties
	3.  css
	4.  favicon.ico
	5.  html
	6.  images
	7.  jquery
	8.  js
	9.  jsp
	10. Login.jsp
	11. META-INF
	12. setup
	13. simplelog.properties
	14. WEB-INF

8.  Now, Database side installation : Go to '$SM_HOME/setup/database/fullsetup'. Below files will be there :

    1.  netvertex-schema.sql
	2.  netvertex.sql
	
9.  Please copy all above files at Database Server :

    e.g.  scp netvertex-schema.sql netvertex.sql networkInformation.sql PROC_STALE_QUOTA_RELEASE_V1.sql PROC_STALE_QUOTA_RELEASE_V2.sql <DATABASE_SERVER_USER_NAME>@<ORACLE_DESTINATION_IP_HOST>:/<PATH_TO_BE_PASTE_SQL_FILE(S)>

10. Connect to the Database server for executing SQLs.

11. Go to the directory where we've copied the SQLs on the Database Server.

12. Please Edit 'netvertex-schema.sql', you may alter values (that are represented within <>), as per needs. This query is used to create NetVertex DB user in the database server.

    Syntax of Statements :
    ----------------------
	
	CREATE TABLESPACE <TABLE_SPACE_NAME> DATAFILE '<DATA_FILE_PATH>/<DATA_FILE_NAME>.dbf' size 100M AUTOEXTEND ON;
	CREATE USER <SCHEMA_USER_NAME> IDENTIFIED BY <SCHEMA_PASSWORD> DEFAULT TABLESPACE <TABLE_SPACE_NAME> TEMPORARY TABLESPACE temp QUOTA UNLIMITED ON <TABLE_SPACE_NAME>;
	GRANT CONNECT,RESOURCE, CREATE ANY VIEW TO <SCHEMA_USER_NAME>;
    GRANT CREATE JOB TO <SCHEMA_USER_NAME>;
	
	Default File Content :
	----------------------
	
	CREATE TABLESPACE netvertex DATAFILE '$ORACLE_BASE/oradata/$ORACLE_SID/netvertex.dbf' size 100M AUTOEXTEND ON;
	CREATE USER netvertex IDENTIFIED BY netvertex DEFAULT TABLESPACE netvertex TEMPORARY TABLESPACE temp QUOTA UNLIMITED ON netvertex;
	GRANT CONNECT,RESOURCE, CREATE ANY VIEW TO netvertex;
	GRANT CREATE JOB TO netvertex;

13. On Database Server, connect to sqlplus (from the pasted SQL(s) directory) as NetVertex's Database user and then fire the SQL(s) file as follows:

	-------------------------------------------------------------------------------------------------------------------------
	-bash-3.2$ sqlplus <sys-username>/<sys-password>@<ORACLE_SID>
	
	SQL*Plus: Release X.X.X.X.X Production on XXX XXX X XX:XX:XX XXXX

	Copyright (c) 1982, 2011, Oracle.  All rights reserved.

	Connected to:
	Oracle Database XXX XXXXXXXXXX Edition Release X.X.X.X.X  - Production
	With the Partitioning, OLAP, Data Mining and Real Application Testing options

	SQL> !ls
	netvertex-schema.sql    netvertex.sql
	
	SQL> @netvertex-schema.sql
	
	Tablespace created.

	User created.

	Grant succeeded.

	Grant succeeded.

	SQL> connect <netvertex>/<netvertex>
	Connected.
	
	SQL> show user
    USER is "NETVERTEX"
	
	SQL> @netvertex.sql
		
	SQL>commit;     -     If no auto 'Commit complete.' seen at last on screen.
	-------------------------------------------------------------------------------------------------------------------------

14. Log on to the NetVertex Server as Database activities completed.
	
15. Please Open file '$SM_HOME/WEB-INF/database.properties' and change according to system's current configuration :
	
	#Database Connection Properties
	hibernate.connection.driver_class=oracle.jdbc.driver.OracleDriver
	hibernate.connection.url=jdbc:oracle:thin:@<ORACLE_IP_HOST>:<ORACLE_PORT>:<ORACLE_SID>
	hibernate.connection.username=<netvertex>
	hibernate.connection.password=<netvertex>
	hibernate.dialect=org.hibernate.dialect.Oracle9Dialect
	hibernate.show_sql=false

	#Quota Management Properties
	quotamgmt.connection.url=jdbc:oracle:thin:@<ORACLE_IP_HOST>:<ORACLE_PORT>:<ORACLE_SID>
	quotamgmt.connection.username=<netvertex>
	quotamgmt.connection.password=<netvertex>

16. Please Open file '$TOMCAT_HOME/conf/server.xml' and add below line after '<Host name="localhost" ..... xmlNamespaceAware="false">'  :

    <Context path="</netvertex_sm>" docBase="<SM_FULL_PATH>" debug="0" reloadable="true" crossContext="true" />

17. Restart Tomcat Server after removing $TOMCAT_HOME/work directory - This will remove older Cache/Temporary File(s)/work of Tomcat Engine.

    e.g. $TOMCAT_HOME/bin
	sh shutdown.sh
	sh startup.sh; tail -f ../logs/catalina.out

18. Access Server Manager through following URL:
    
	URL : http://<NETVERTEX_IP_HOST>:<TOMCAT_PORT>/<CONTEXT_PATH> (If required, make host entry)
    
	Login through super user 'Admin' : User Name : admin, Password  : admin


Congratulation ! This completes the process of installation of Server Manager component.

--------------------------------------------------------------
This product is registered to Elitecore Technologies Pvt. Ltd
Copyright (C) 2009, Elitecore Technologies Pvt. Ltd.
--------------------------------------------------------------