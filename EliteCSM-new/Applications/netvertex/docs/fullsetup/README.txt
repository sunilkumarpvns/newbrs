Linux - NetVertex Server Full Set-up :
======================================

Pre-Requisites :
================
1. Linux Server, Data Source Server, JAVA 1.6 update 45, Tomcat 6.0.35 (or above) should be present with working condition.

2. Ensure that the compatible version of NetVertex Server will be taken.

Installation NetVertex Server :
===============================
1.  Login into Machine.

2.  Make two directories 'source'(Please ignore if source directory is available), 'netvertex', 'source/netvertex'

3.  Configure $NETVERTEX_HOME (NetVertex Full Path), $TOMCAT_HOME (Tomcat Full Path) configured in '.bash_profile'/'.profile'

4.  Download / copy the installation file 'Netvertex-V<X.X.X.X>.tar.gz' to the 'source/netvertex' directory.

5.  Untar this installation file :

	e.g.
	--------------------------------------
	gunzip Netvertex-V<X.X.X.X>.tar.gz    -    This will make file 'Netvertex-<X.X.X.X>.tar'

	tar -xvf Netvertex-V<X.X.X.X>.tar     -    This will generate install.sh, README.txt, release-notes.txt files
	--------------------------------------

6.  Now run the installation script for installing NetVertex Server :

    e.g.   sh install.sh $NETVERTEX_HOME
	
7.  This will create below directories - Please verify :

	bin/                          Scripts for server management.
	lib/                          Library files required by NetVertex server.
	dictionary/                   Default NetVertex dictionary files. 
	conf/                         Dictionary that contains actual configuration.
	system/                       System files. -  Note : Please don't edit these files.
	logs/                         Destination directory for log files.

8.  Start NetVertex Server :

    Go to : $NETVERTEX_HOME/bin
	sh netvertex.sh

Congratulation ! This completes the process of installation of NetVertex Server component.

--------------------------------------------------------------
This product is registered to Elitecore Technologies Pvt. Ltd
Copyright (C) 2009, Elitecore Technologies Pvt. Ltd.
--------------------------------------------------------------