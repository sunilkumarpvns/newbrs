Elite Server Manager
====================

Server Manager is designed to support management of different entities of the servers. This version of SM provides management of entities related to RADIUS and RM server.

PRE-REQUISITES
==============

- Before upgrading Elite Server Manager, ensure that the latest upgrade of previous version is executed properly.
- Backup existing SM installation i.e. SM_HOME 


UPGRADE STEPS
=============

 - Create folder for Upgrade EliteAAA applications as:
        e.g. /opt/EliteAAA/upgrade
   
 - Copy EliteAAASM-Vx.x.x.x-upgrade-xxxx.tar.gz file to upgrade folder.

 - Untar the gz file, this will create a folder named EliteAAASM-Vx.x.x.x-upgrade-xxxx inside upgrade folder.
        gunzip EliteAAASM-Vx.x.x.x-upgrade-xxxx.tar.gz
        tar -xvf EliteAAASM-Vx.x.x.x-upgrade-xxxx.tar

 - Go to EliteAAASM-Vx.x.x.x-upgrade-xxxx folder and run upgrade script as below.
        sh upgrade.sh /opt/EliteAAA/SM
 
        Home folder of existing Server Manager need to be passed as argument.

 - Database Setup (Upgrade from 5.5.0.0 to 5.6.0.0)

	Execute appropriate upgrade SQL Script available at '$SM_HOME/setup/database/upgrade' folder in the existing EliteAAA schema.
	
	- upgrade-sm-to-5.6.0.0.sql	
	

 - Open file /home/sm/webroot/WEB-INF/classes/config/database.properties and modify database configuration details:

     	hibernate.connection.driver_class=oracle.jdbc.driver.OracleDriver
	hibernate.connection.url=jdbc:oracle:thin:@192.168.1.1:1521:orcl92
	hibernate.connection.username=ELITERADIUSDEV501
	hibernate.connection.password=ELITERADIUSDEV501
	hibernate.dialect=org.hibernate.dialect.Oracle9Dialect
	hibernate.show_sql=true


Accessing Server Manager GUI
============================

 - Open the jakarta-tomcat-5.0.XX/conf/server.xml file and check the following
   host entry:

		Example of a host entry made for the Server Manager:
		<Host name="elitesm.com" debug="0"
		                appBase="<SM_HOME>/webroot"
		                unpackWARs="false" autoDeploy="true">

		      <Logger className="org.apache.catalina.logger.FileLogger"
		                prefix="server-manager_log." suffix=".txt"
		                timestamp="true"/>

		     <Context path="" docBase="<SM_HOME>/webroot"
		               debug="0" reloadable="true" crossContext="true" />
		</Host>

 - Access server through following URL:
     URL : http://elitesm.com (If required, make host entry)

 - Login through super user 'Admin'
			User Name : admin
			Password  : admin
 	