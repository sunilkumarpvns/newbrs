==============Elite Server Manager====================
Server Manager is designed to support management of different entities of the server instances. 

This version of SM provides management of entities related to AAA and RM server.


==============INSTALLATION============   

- Copy the server-manager-x.x.x.x.tar.gz to setups directory.

- Untar the gz file. This will create a directory named EliteAAASMX-Vx.x.x.x inside setups directory.     

 In Unix, gunzip server-manager-x.x.x.x.tar.gz      

 tar -xvf EliteAAASMX-Vx.x.x.x.tar

- Create a home directory for Server Manager.	

e.g. /opt/Setup/SM- Go to EliteAAASMX-Vx.x.x.x directory and run the installation script as below:	

	sh install.sh /opt/Setup/SM		     


- Now you need to create an EliteAAA User in the Oracle database and setup the database for Server Manager. You will find the 

following files in the “$SM_HOME/setup/database/fullsetup” directory.

• eliteaaa-schema.sql

• eliteaaa.sql

- Copy the above SQL script files available in “setup/database/fullsetup” folder to your Oracle OS user.

For Example:

	scp eliteaaa-schema.sql oracle@192.168.8.94:/home/oracle/eliteaaa

- The eliteaaa-schema.sql file comprises sql statements, which you may alter values (that are represented within <>), as per your 

needs. This query is used to create EliteAAA user in the database.

- On your database server, connect to sqlplus as “sys” user and execute the eliteaaa-schema.sql query as follows:

	sqlplus <sys-username>/<sys-password> as sysdba

	SQL>@eliteaaa-schema.sql

- Once the above-mentioned file is executed, a user is created. In this document, we assume that no updates are made in the 

eliteaaa-schema.sql file. Thus on execution of this file, a user named ELITEAAA is created.

- Copy the “eliteaaa.sql” file to your Oracle OS user.

For Example:

	scp eliteaaa.sql oracle@192.168.8.94:/home/oracle/eliteaaa

- Connect to the Oracle server for executing the eliteaaa.sql file.

For Example:

	sqlplus eliteaaa/eliteaaa

- Now go to the directory where you've copied the eliteaaa.sql on the Oracle database server. On your database server, connect 

to sqlplus (from the same directory) as eliteaaa user and then execute the eliteaaa.sql query as follows:

	sqlplus eliteaaa/eliteaaa@eliteaaa

where,
   eliteaaa/eliteaaa@eliteaaa represents username/password@ORACLE_SID
  
    SQL>@eliteaaa.sql


- Once done log out of the Oracle database.

- Log on to the system as the OS user you’ve created for installation of the server manager (elitesm as per our document).

- Open file /home/sm/WEB-INF/database.properties and update Database configuration details for:	

	hibernate.connection.driver_class=oracle.jdbc.driver.OracleDriver	

	hibernate.connection.url=jdbc:oracle:thin:@192.168.1.1:1521:orcl92	

	hibernate.connection.username=ELITERADIUSDEV501	

	hibernate.connection.password=ELITERADIUSDEV501	

	hibernate.dialect=org.hibernate.dialect.Oracle9Dialect	

	hibernate.show_sql=trueAccessing 

- Now do the web-hosting of the “EliteSM” directory in the $TOMCAT_HOME/conf/server.xml file as shown below:

	<Context path="" docBase="<SM_HOME>"

	debug="0" reloadable="true" crossContext="true" />

- This completes the process of installing the Server Manager.


==============Server Manager GUI============================

- Install TOMCAT 5.0.XX in the Home directory of the user.

- Edit the jakarta-tomcat-5.0.XX/conf/server.xml file and make the following   host entry:		

Example of a host entry made for the Server Manager:		
	<Host name="elitesm.com" debug="0" appBase="<SM_HOME>"		               
	 unpackWARs="false" autoDeploy="true">		      
	<Logger className="org.apache.catalina.logger.FileLogger"		                
	prefix="server-manager_log." suffix=".txt" timestamp="true"/>		     
	<Context path="" docBase="<SM_HOME>" debug="0" reloadable="true" crossContext="true" />		
	</Host> 

- Access server through following URL:   
  
URL : http://elitesm.com (If required, make host entry)

 - Login through super user 'Admin'			
	User Name : admin			
	Password  : admin  	. 	
