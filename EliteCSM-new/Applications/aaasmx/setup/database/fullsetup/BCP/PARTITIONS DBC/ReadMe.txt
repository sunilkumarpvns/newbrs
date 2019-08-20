-------------------------------
EliteAAA BCP - PARTITION DBC
-------------------------------

Note: 

Copy BCP Scripts from Application Server to Database Server 

1. Go to the location Partition DBC folder
cd BCP\PARTITION DBC

2. Partition DBC should be execute from sys or system user.

3. Partition DBC will be ask for datafile location and application username respectively

1) Install Partition for CONCURRENTUSER_AUTH_BEHAVIOR

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_BCP_TBLMCONCURRENTUSERS_AUTH_BEHAVIOR.sql
SQL>exit

2) Install Partition for  DIAMETERCDR

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLDIAMETERCDR.sql
SQL>exit

3) Install Partition for  DIAMETERINTRIMCDR

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLDIAMETERINTERIMCDR.sql
SQL>exit

4) Install Partition for  DYNAUTHREQUESTS

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLDYNAUTHREQUESTS.sql
SQL>exit

5) Install Partition for CONCURRENTUSERS

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLMCONCURRENTUSERS.sql
SQL>exit

6) Install Partition for DIAMETERSESSIONDATA

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLMDIAMETERSESSIONDATA.sql
SQL>exit 

7) Install Partition for IPPOOLDETAIL

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLMIPPOOLDETAIL.sql
SQL>exit 

8) Install Partition for RADIUSINTRIMCDR

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLMRADIUSINTERIMCDR.sql
SQL>exit 

9) Install Partition for RADIUSCDR

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLRADIUSCDR.sql
SQL>exit 

10) Install Partition for RADIUSCUSTOMER

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLRADIUSCUSTOMER.sql
SQL>exit 

11) Install Partition for USERSTATISTICS

$sqlplus system/<sys password>@<service_name> 
SQL>@ELITEAAA_DBC_PARTITION_TBLUSERSTATISTICS.sql
SQL>exit 


Note:
Verify Below log files for success:
1.	ELITEAAA_BCP_TBLMCONCURRENTUSERS_AUTH_BEHAVIOR.log
2.	ELITEAAA_DBC_PARTITION_TBLDIAMETERCDR.log
3.	ELITEAAA_DBC_PARTITION_TBLDIAMETERINTERIMCDR.log
4.	ELITEAAA_DBC_PARTITION_TBLDYNAUTHREQUESTS.log
5.	ELITEAAA_DBC_PARTITION_TBLMCONCURRENTUSERS.log
6.	ELITEAAA_DBC_PARTITION_TBLMDIAMETERSESSIONDATA.log
7.	ELITEAAA_DBC_PARTITION_TBLMIPPOOLDETAIL.log
8.	ELITEAAA_DBC_PARTITION_TBLMRADIUSINTERIMCDR.log
9.	ELITEAAA_DBC_PARTITION_TBLRADIUSCDR.log
10.	ELITEAAA_DBC_PARTITION_TBLRADIUSCUSTOMER.log
11.	ELITEAAA_DBC_PARTITION_TBLUSERSTATISTICS.log


Important Note:
For Procedure of EliteAAA Module please go to PROCEDURES Folder
cd ..
cd PROCEDURES