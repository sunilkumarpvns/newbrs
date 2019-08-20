Elite Resource manager Server=============================

==================PRE-REQUISITES==============   

- Verify environment variable JAVA_HOME that points to the home folder of JRE1.6.x/JDK1.6.x   

- Before upgrading EliteRM Server, ensure that compatible version of EliteAAA Server Manager is installed properly.   

==================INSTALLATION============   

- Create home directory for EliteRM applications.       

e.g. /opt/Setup/EliteRM	     

- Inside this home directory, create directory to maintain the release binary and all the future upgrades.       

e.g. opt/Setup/EliteRM/Setups   

- Copy eliterm-x.x.x.x.tar.gz file to setups directory.     

- Untar the gz file. This will create a directory named EliteRM-Vx.x.x.x inside setups directory.       

gunzip eliterm-x.x.x.x.tar.gz       

tar -xvf EliteRM-Vx.x.x.x.tar   

- Create home directory for EliteRM server.       

e.g. opt/Setup/EliteRM     

- Go to EliteRM-Vx.x.x.x directory and run install script as below.       

sh install.sh opt/Setup/EliteRM            

Home directory of EliteRM server need to be passed as argument.        

The directory structure will be as below:    

=================DIRECTORY STRUCTURE  ===================    

bin/                          Scripts for server management.    

lib/                          Library files required by Resource Manager server    

dictionary/                   Default EliteAAA dictionary files.         

conf/                         Folder that contains actual configuration. Do edit files manually. 
			      Always update configuration through Server Manager.    

system/                       System files. Never edit the files of this folder.        

logs/                         Destination directory for log files  

================Start Resource Manager Server=====================   

- Set EliteRM_HOME to the home folder of Resource Manager server installation.     

- Go to bin folder of server      

- Execute startup script       

eliterm.sh  (Unix)       

eliterm.bat (Windows)

================CONFIGURATION=============    

After starting Resource Manager Server, create an instance of Resource Manager Server that points to the physical installation of the server. 
    
Then within this server instance of Server Manager, the required services can be added and configured.       

- Create the Instance of Resource Manager Server through GUI.        

- Configure required parameters of server instance.        

- Add required service instances.        

- Configure required parameters of each service instance through GUI.        

- Synchronize the configuration from the GUI to physical server.        

- Restart Resource Manager server.

===============Stop the server===============      

- Go to bin folder of the server.     

- Execute the shell command as,     

cli.sh     (Unix)        

cli.bat    (Windows)           

- Issue "shutdown" command to stop the server.
