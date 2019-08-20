Linux - NetVertex Server Upgrade :
==================================

Pre-Requisites :
================
1. NetVertex component is assumed to be installed and in use. $NETVERTEX_HOME configured in '.bash_profile'/'.profile'

2. Ensure that the compatible version of NetVertex was installed/upgraded properly previously.

3. Note down existing Version, Revision & Build Date of NetVertex Server for emergency purpose :
   
4. Go to : $NETVERTEX_HOME/lib - fire 'java -jar netvertex.jar' command, it will show like :
    
    ---------------------------
    Elitecore Technologies Ltd.
    Module    : netvertex
    Version   : X.X.X.XX
    Revision  : XXXX
    Date      : XXXX XX XXXX
    ---------------------------
	
5. Take the backup of the application before Upgrading.

	Note - There is no undo after upgrade process, so please take back up with the help of below procedures :

	- Please go to directory where all the 'to be backup directory' can be accessible.
	- Command : tar cvf <FileName_ClientName_Date_MajorChange>.tar <Directory_Name_To_Be_Backup> <Directory_2> <Directory_3>
	- e.g. tar cvf <Client_Name>_DD_MM_YYYY_<MajorPoint>.tar <NetVertex> <SM>
	- Please take Database backup accordingly.


Upgrading NetVertex Server:
===========================
1. Login into Machine.

2. Make two directories 'source', 'source/netvertex' - (Please ignore if directories are available)

3. Download / copy the upgrade file 'Netvertex-V<X.X.X.X>-upgrade-<Revision_Number>.tar.gz' to the 'source/netvertex' directory.

4. Untar this upgrade file :

	e.g.
	------------------------------------------------------------
	gunzip Netvertex-V<X.X.X.X>-upgrade-<Revision_Number>.tar.gz    -    This will make file 'Netvertex-V<X.X.X.X>-upgrade-<Revision_Number>.tar'

	tar -xvf Netvertex-V<X.X.X.X>-upgrade-<Revision_Number>.tar     -    This will generate README.txt, release-notes.txt, upgrade.sh files
	------------------------------------------------------------


5. Run the upgrade script for upgrading NetVertex :

	e.g.   sh upgrade.sh $NETVERTEX_HOME

6. Go to : $NETVERTEX_HOME/lib and fire 'java -jar netvertex.jar'. Please verify upgraded Version, Revision. 

7. Please restart NetVertex

8. Make sure NetVertex Server shows service(s) RUNNING in CLI.

	e.g. 

	[netvertex@csm-build bin]$ sh cli.sh 

	************************************************
	*  netvertex [X.X.X.XX]            *
	*                                              *
	*  Elitecore Technologies Pvt. Ltd.            *
	************************************************

	Enter ? or help for list of commands supported.

	NVX> services 
	---------------------------------------------------------------------------------------------------------
	|     Service Name     |        Start Date         |    Status    |               Remarks               |
	---------------------------------------------------------------------------------------------------------
	| PCRF                 | XX XXX XXXX XX:XX:XX      | Running      |  --                                 |
	| BOD                  | XX XXX XXXX XX:XX:XX      | Running      |  --                                 |
	| PROMOTIONAL          | XX XXX XXXX XX:XX:XX      | Running      |  --                                 |
	| NOTIFICATION         | XX XXX XXXX XX:XX:XX      | Running      |  --                                 |
	---------------------------------------------------------------------------------------------------------

Congratulation ! This completes the process of upgrading NetVertex component.

--------------------------------------------------------------
This product is registered to Elitecore Technologies Pvt. Ltd
Copyright (C) 2009, Elitecore Technologies Pvt. Ltd.
--------------------------------------------------------------