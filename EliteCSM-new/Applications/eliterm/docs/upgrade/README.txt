Elite Radius Server 
=================== 
 
Services/Functionalities: 
  ~ Authentication Service 
  ~ Accounting Service 
  ~ DynAuth Service 
   
  1. Authentication Service 
  ========================= 
    Authentication service based on Radius protocol. 
        
    Supports following authentication protocols : 
      PAP  ( Password Authentication Protocol )  
      CHAP (Challenge Handshake Authentication Protocol )  
      EAP  ( Extensible Authentication Protocol - Secure WLAN access protocol)  
        Supported EAP methods  
          a. EAP-MD5          b. EAP-TLS          c. EAP-TTLS 
      Digest (Radius Digest Authentication RFC - 5090 and draft-sterman-aaa-sip-01)       
    
   User profile datasource supported for authentication process. 
      1. LDAP Authentication (LDAP_AUTH_DRIVER) 
         Account (User Profile) information required for authentication process will be read  
         from configured LDAP server. 
           
      2. Open Database Authentication (OPENDB_AUTH_DRIVER) 
         Account information will be read from the configured database.  
          
      3. User File Authentication (USERFILE_AUTH_DRIVER) 
         Authentication information is read from XML file. 
      
      
 
  2. Accounting Service 
  ===================== 
     
    Stores usage information received. The service can be configured to store 
    the usage information to different formats and mediums.  
     
    Following drivers/plugins are supported in this release. 
        
      1. Open Database (OPENDB_ACCT_DRIVER) 
         The usage information of the user received in the Accounting-Request  
         packet will be stored in the configured database. 
 
      2. Flat File CSV Format (CLASSC_CSV_ACCT_DRIVER) 
         The usage information received will be stored stored in configured flat 
         file in coma seperated format. One usage information(CDR) on each line. 
 
      3. Flat File Detail Local Format (DETAIL_LOCAL_ACCT_DRIVER) 
         The usage information received will be stored stored in configured flat 
         file in Detail Local format. One usage information(CDR) on each line. 
          
             
  3. Dynamic Authoriztion Service 
  =============================== 
 
       The service can be configured to allow the administrator to change level of authorization 
       dynamically or to disconnect a particular use session.  
 
 
PRE-REQUISITES 
============== 
 
   - Verify environment variable JAVA_HOME that points to the home folder of JRE1.5.x/JDK1.5.x 
   - Before upgrading EliteRadius, ensure that the compatible version of EliteAAA Server Manager is upgraded properly. 
   - Before executing upgrade SQL scripts, upgrade SQL script for Elite Server Manager must be executed. 
   - Apply 'Synchronize From' action before upgrading Radius Server. Once it is upgraded, apply 'Synchronize To' action first.  
 
UPGRADE STEPS 
============= 
 
   - Create folder for Upgrade EliteAAA applications as: 
       e.g. /opt/EliteAAA/upgrade 
   
   - Copy EliteRadius-Vx.x.x.x-upgrade-xxxx.tar.gz file to upgrade folder. 
   
   - Untar the gz file, this will create a folder named EliteRadius-Vx.x.x.x-upgrade-xxxx inside upgrade folder. 
       gunzip EliteRadius-Vx.x.x.x-upgrade-xxxx.tar.gz 
       tar -xvf EliteRadius-Vx.x.x.x-upgrade-xxxx.tar 
 
   - Go to EliteRadius-Vx.x.x.x-upgrade-xxxx folder and run upgrade script as below. 
       sh upgrade.sh /opt/EliteAAA/EliteRadius 
      
       Home folder of existing EliteRadius server need to be passed as argument. 
    
   - Database Setup (Upgrading from 5.2.0.2 to 5.4.0.2) 
               
       Execute appropriate upgrade SQL (i.e. upgrade-radius-to-5.4.0.2.sql) Script available at '$RADIUS_HOME/setup/database/upgrade
' folder in the existing EliteAAA schema. 
          
 
  This will install Elite Radius at existing home location.  
  The directory structure will be majorly same as existing installation; however the following modifications need to be made: 
 
  - Go to $RADIUS_HOME/dictionary directory of server 
        1. Create a directory, namely "radius" at this location. 
        2. Move all dictionaries to "radius" directory.  
 
  - Go to $RADIUS_HOME/system/cert directory of server 
          
 
  This will install Elite Radius at existing home location.  
  The directory structure will be majorly same as existing installation; however the following modifications need to be made: 
 
  - Go to $RADIUS_HOME/dictionary directory of server 
        1. Create a directory, namely "radius" at this location. 
        2. Move all dictionaries to "radius" directory.  
 
  - Go to $RADIUS_HOME/system/cert directory of server 

A
   - Before executing upgrade SQL scripts, upgrade SQL script for Elite Server Manager must be executed. 
   - Apply 'Synchronize From' action before upgrading Radius Server. Once it is upgraded, apply 'Synchronize To' action first.  
 
UPGRADE STEPS 
============= 
 
   - Create folder for Upgrade EliteAAA applications as: 
       e.g. /opt/EliteAAA/upgrade 
   
   - Copy EliteRadius-Vx.x.x.x-upgrade-xxxx.tar.gz file to upgrade folder. 
   
   - Untar the gz file, this will create a folder named EliteRadius-Vx.x.x.x-upgrade-xxxx inside upgrade folder. 
       gunzip EliteRadius-Vx.x.x.x-upgrade-xxxx.tar.gz 
       tar -xvf EliteRadius-Vx.x.x.x-upgrade-xxxx.tar 
 
   - Go to EliteRadius-Vx.x.x.x-upgrade-xxxx folder and run upgrade script as below. 
       sh upgrade.sh /opt/EliteAAA/EliteRadius 
      
       Home folder of existing EliteRadius server need to be passed as argument. 
 
 - Go to $RADIUS_HOME/default/config/session/plugins
 	1. Remove following files if exist.
 		- DBPlugin.xml 
 		- InMemoryPlugin.xml
     
    
   - Database Setup (Upgrading from 5.2.0.2 to 5.4.0.2) 
               
       Execute appropriate upgrade SQL (i.e. upgrade-radius-to-5.4.0.2.sql) Script available at '$RADIUS_HOME/setup/database/upgrade
' folder in the existing EliteAAA schema. 
          
 
  This will install Elite Radius at existing home location.  
  The directory structure will be majorly same as existing installation; however the following modifications need to be made: 
 
  - Go to $RADIUS_HOME/dictionary directory of server 
        1. Create a directory, namely "radius" at this location. 
        2. Move all dictionaries to "radius" directory.  
 
  - Go to $RADIUS_HOME/system/cert directory of server 
        1. Create a directory, namely "trustedcertificates" at this location. 
        2. Move all available CA certificates from this location to "trustedcertificates" directory. Make sure that at least the cer
tificate file configured as CA-Certificate in EAP configuration MUST be relocated to "trustedcertificates" directory. 
   
-Go to $RADIUS_HOME/bin add 
         JAR_HOME5=../lib/json 
             
      for i in ${JAR_HOME5}/*.* ; do
           SERVERJARS=${SERVERJARS}:$i
       done
in cli.sh and startServer.sh
 
- Go to $RADIUS_HOME/system directory of server 
        1. Create a directory, namely "misc" at this location. 
        2. Create a file "misc-config.xml" with following contents: 
 
        <?xml version="1.0" encoding="UTF-8"?> 
        <miscellaneous-data> 
                <param name="authorize-only-cdr-enabled" value="false"/> 
                <param name="spi-key-config" value="spi-key-config.xml"/> 
                <param name="NUL-char-support-in-PAP-password" value="false"/> 
        </miscellaneous-data> 
 
        3. Create a file "spi-key-config.xml" with following contents: 
 
        <?xml version="1.0" encoding="UTF-8"?> 
        <spi-group-list> 
        <spi-group> 
                <ha-ip-address-list>0.0.0.0</ha-ip-address-list> 
                <spi-key-pair-list>              
                        <spi-key-pair> 
                                        <spi>273</spi> <!--273 (Decimal) = 111 (Hex) --> 
                                 <key>elitecore</key>         
                        </spi-key-pair> 
                </spi-key-pair-list> 