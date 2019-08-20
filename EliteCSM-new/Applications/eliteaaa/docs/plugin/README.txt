Plugin Name : ${plugin.name}

===================PRE-REQUISITES==============  

- Before installing ${plugin.name} into EliteAAA Server, ensure that compatible version of EliteAAA Server should higher then ${application.major-version}.${application.minor-version}-r${eliteaaa.svn.rev} is installed properly.   


===================INSTALLATION============   

 
- Inside this $ELITEAAA_HOME directory, create directory to maintain the release binary and all the future upgrades.       

e.g. /opt/Setup/EliteAAA/Setups     

- Copy ${plugin.bundle-name}-Vx.x.tar.gz file to setups directory.     

- Untar the gz file. This will create a directory named ${plugin.bundle-name}-Vx.x.tar inside setups directory.       

gunzip ${plugin.bundle-name}-Vx.x.tar.gz by executing the tar command as:    

tar -xvf ${plugin.bundle-name}-Vx.x.tar   

- Go to ${plugin.bundle-name}-Vx.x directory and run the installation script as below:       

sh install.sh $ELITEAAA_HOME    
        
The Home directory of EliteAAA server need to be passed as argument.     

The directory structure will be as below.    

===================DIRECTORY STRUCTURE  ===================    

lib/                          Library files required by ${plugin.name}    

   In the lib dirctory verify following *.jar : ${plugin.jars}

sql/${plugin.bundle}/          SQL files required by ${plugin.name}    
   


===================Plugin in Effect===============      

- ${plugin.name} will comes into effect after the restart of the EliteAAA Server





