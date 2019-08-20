Elite AAA Server

===================PRE-REQUISITES==============  

 - Verify environment variable JAVA_HOME that points to the home folder of JRE1.6.x/JDK1.6.x   

- Before upgrading EliteAAA Server, ensure that compatible version of EliteAAA Server Manager is installed properly.   

===================INSTALLATION============   

- Create home directory for EliteAAA applications.      

 e.g. /opt/Setup/EliteAAA
   
- Inside this home directory, create directory to maintain the release binary and all the future upgrades.       

e.g. /opt/Setup/EliteAAA/Setups     

- Copy eliteaaa-x.x.x.x.tar.gz file to setups directory.     

- Untar the gz file. This will create a directory named EliteAAA-Vx.x.x.x.tar inside setups directory.       

gunzip eliteaaa-x.x.x.x.tar.gz by executing the tar command as:    

tar -xvf EliteAAA-Vx.x.x.x.tar   

- Create home directory for EliteAAA server(ELITEAAA_HOME).       

e.g. /opt/Setup/EliteAAA     

- Go to EliteAAA-Vx.x.x.x directory and run the installation script as below:       

sh install.sh /opt/Setup/EliteAAA    
        

The Home directory of EliteAAA server need to be passed as argument.     

The directory structure will be as below.    

===================DIRECTORY STRUCTURE  ===================    


bin/                          Scripts for server management.    

lib/                          Library files required by EliteAAA server    

dictionary/                   Default EliteAAA dictionary files.         

conf/                         Folder that contains actual configuration. Do edit files manually. Always update     			      		      configuration through Server Manager.    

system/                       System files. Never edit the files of this folder.        

system/certs                  Folder used to store the certificates used for EAP authentication.      

logs/                         Destination directory for log files  

====================Start AAA Server=====================   

- Set ELITEAAA_HOME to the home folder of AAA server installation.     

- Go to bin folder of server      

- Execute startup script       
eliteaaa.sh  (Unix)       
eliteaaa.bat (Windows)

====================CONFIGURATION=============    

After starting EliteAAA Server, create an instance of EliteAAA Server that points to the physical installation of the server. 
Then within this server instance    of Server Manager, the required services can be added and configured.        

- Create the Instance of EliteAAA Server through GUI.        

- Configure required parameters of server instance.        

- Add required service instances.        

- Configure required parameters of each service instance through GUI.        

- Synchronize the configuration from the GUI to physical server.        

- Restart EliteAAA server.

===================Stop the server===============      

- Go to bin folder of the server.      

- Execute the shell command        
cli.sh     (Unix)        
cli.bat    (Windows)          

- Execute "shutdown" command to stop the server.
