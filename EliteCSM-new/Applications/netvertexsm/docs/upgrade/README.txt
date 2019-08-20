Linux - Server Manager - SM Upgrade :
=====================================

Pre-Requisites :
================
1.  Server Manager component is assumed to be installed and in use with tomcat. $SM_HOME & TOMCAT_HOME configured in '.bash_profile'/'.profile'

2.  Ensure that the compatible version of Server Manager was installed/upgraded properly previously.

3.  Note down existing Version, Revision & Build Date of Server Manager for emergency purpose :
   
4.  Go to : NetVertex Server Manager GUI and get above details by pressing STAR & note down 'UBIQUITOUS NETVERTEX SERVER MANAGER' information.

5.  Take the backup of the application before installing an upgrade.

    Note - There is no undo after upgrade process, so please take back up with the help of below procedures :

    - Please go to directory where all the 'to be backup directory' can be accessible.
    - Command : tar cvf <FileName_ClientName_Date_MajorChange>.tar <Directory_Name_To_Be_Backup> <Directory_2> <Directory_3>
    - e.g. tar cvf <Client_Name>_DD_MM_YYYY_<MajorPoint>.tar NetVertex/ SM/
    - Please take Database backup accordingly.


Upgrading Server Manager :
==========================
1.  Login into Machine.

2.  Make two directories 'source', 'source/sm' - (Please ignore if directories is available)

3.  Download / copy the upgrade file 'NetvertexSM-V<X.X.X.X>-upgrade-<Revision_Number>.tar.gz' to the 'source/sm' directory.

4.  Untar this upgrade file :

	e.g.
	------------------------------------------------------------
	gunzip NetvertexSM-V<X.X.X.X>-upgrade-<Revision_Number>.tar.gz    -    This will make file 'NetvertexSM-V<X.X.X.X>-upgrade-<Revision_Number>.tar'

	tar -xvf NetvertexSM-V<X.X.X.X>-upgrade-<Revision_Number>.tar      -    This will generate README.txt, release-notes.txt, upgrade.sh files
	------------------------------------------------------------


5.  Run the upgrade script for upgrading Server Manager :

    e.g.   sh upgrade.sh $SM_HOME

6.  Now, Database side upgrade : Go to '$SM_HOME/setup/database/upgrade'. There will be higher version SQL(s) from existing version

    e.g. Current version 6.X.X.2 and upgrading to 6.X.X.8 then 'Upgrade-NV-v6.X.X.4.sql' & 'Upgrade-NV-v6.X.X.6.sql' need to execute in oracle's existing Database User of NetVertex.

    Note : Above SQL file(s) for Major Release version only. If no SQL(s) in between then please make sure or get in touch with NetVertex's Responsible Team for any minor version SQL(s).

7.  Please copy file(s) in between current & upgrading version :

    e.g.  scp Upgrade-NV-v6.X.X.4.sql Upgrade-NV-v6.X.X.6.sql <DATABASE_SERVER_USER_NAME>@<ORACLE_DESTINATION_IP_HOST>:/<PATH_TO_BE_PASTE_SQL_FILE(S)>

8.  Connect to the Oracle server for executing the  Upgrade-NV-v6.X.X.4.sql & Upgrade-NV-v6.X.X.6.sql

9.  Go to the directory where we've copied the SQL(s) on the Oracle Database Server.

10. Please verify NetVertex's DB User :

	e.g  Open file $SM_HOME/WEB-INF/database.properties :
	
	#Database Connection Properties
	hibernate.connection.driver_class=oracle.jdbc.driver.OracleDriver
	hibernate.connection.url=jdbc:oracle:thin:@<ORACLE_IP_HOST>:<ORACLE_PORT>:<ORACLE_SID>
	hibernate.connection.username=netvertex_primary
	hibernate.connection.password=netvertex_primary
	hibernate.dialect=org.hibernate.dialect.Oracle9Dialect
	hibernate.show_sql=false

	#Quota Management Properties
	quotamgmt.connection.url=jdbc:oracle:thin:@<ORACLE_IP_HOST>:<ORACLE_PORT>:<ORACLE_SID>
	quotamgmt.connection.username=netvertex_primary
	quotamgmt.connection.password=netvertex_primary

12.	On Database Server, connect to sqlplus (from the pasted SQL(s) directory) as NetVertex's Database user and then fire the SQL(s) file as follows:

	------------------------------------------------------------------------------
	-bash-3.2$ sqlplus netvertex_primary/netvertex_primary@<ORACLE_SID>

	SQL*Plus: Release X.X.X.X.X Production on XXX XXX X XX:XX:XX XXXX

	Copyright (c) 1982, 2011, Oracle.  All rights reserved.

	Connected to:
	Oracle Database XXX XXXXXXXXXX Edition Release X.X.X.X.X  - Production
	With the Partitioning, OLAP, Data Mining and Real Application Testing options

	SQL> !ls
	Upgrade-NV-v6.X.X.4.sql Upgrade-NV-v6.X.X.6.sql
	
	SQL>@Upgrade-NV-v6.X.X.4.sql
	
	SQL>@Upgrade-NV-v6.X.X.6.sql

	SQL>commit;     -     If no auto 'Commit complete.' seen at last on screen.
	------------------------------------------------------------------------------
    
	Note : Execute all the SQL(s), if any.

13. Restart Tomcat Server after removing $TOMCAT_HOME/work directory - This will remove older Cache/Temporary File(s)/work of Tomcat Engine.

    e.g. $TOMCAT_HOME/bin
	sh shutdown.sh
	sh startup.sh; tail -f ../logs/catalina.out

14. Access Server Manager through following URL:
    
	URL : http://<NETVERTEX_IP_HOST>:<TOMCAT_PORT>/<CONTEXT_PATH> (If required, make host entry)
    
	Login through super user 'Admin' : User Name : admin, Password  : admin

15. Please verify successful upgrade, Go to : Server Manager GUI (by pressing STAR & get 'UBIQUITOUS NETVERTEX SERVER MANAGER' information)
	
    Note : Please perform the “Synchronize To” activity. This will send all the dbc changes into the physical server by NetVertex Restart.
	
Congratulation ! This completes the process of upgrading Server Manager component.

--------------------------------------------------------------
This product is registered to Elitecore Technologies Pvt. Ltd
Copyright (C) 2009, Elitecore Technologies Pvt. Ltd.
--------------------------------------------------------------