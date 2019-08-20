spool ELITEAAA_DBC_PARTITION_TBLMCONCURRENTUSERS.log
                                            
--create tablespaces for TBLMCONCURRENTUSERS             
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile   

CREATE TABLESPACE tbsconcurrentusers01 DATAFILE '&&db_datafile/tbsconcurrentusers01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsconcurrentusers02 DATAFILE '&&db_datafile/tbsconcurrentusers02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsconcurrentusers03 DATAFILE '&&db_datafile/tbsconcurrentusers03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsconcurrentusers04 DATAFILE '&&db_datafile/tbsconcurrentusers04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsconcurrentusers05 DATAFILE '&&db_datafile/tbsconcurrentusers05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsconcurrentusers06 DATAFILE '&&db_datafile/tbsconcurrentusers06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsconcurrentusers07 DATAFILE '&&db_datafile/tbsconcurrentusers07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbsconcurrentusers08 DATAFILE '&&db_datafile/tbsconcurrentusers08.dbf' size 100M AUTOEXTEND ON NEXT 25M;
          
--Enter Schema Name of EliteAAA Product                                     
connect &&username/&&password@&&NET_STR                                             
set long 10000                                                                   
set pagesize 0                                                                   
select dbms_metadata.get_ddl('TABLE','TBLMCONCURRENTUSERS') from dual;           
select dbms_metadata.get_dependent_ddl('INDEX','TBLMCONCURRENTUSERS') from dual; 
                                                                          
                                                                         
                                                                            
  SELECT COUNT(1) FROM TBLMCONCURRENTUSERS;                                       
                                                                            
  RENAME TBLMCONCURRENTUSERS TO OLD_TBLMCONCURRENTUSERS;                                           
  
  CREATE TABLE TBLMCONCURRENTUSERS                                
   ( CONCUSERID            		  NUMERIC,       
  CREATE_DATE           		  TIMESTAMP,   
  CALL_START            		  TIMESTAMP,   
  CALL_END              		  TIMESTAMP,   
  LAST_UPDATED_TIME     		  TIMESTAMP,   
  GROUPNAME             		  VARCHAR(60), 
  USER_IDENTITY 			  VARCHAR(253), 
  USER_NAME             		  VARCHAR(253),
  NAS_IP_ADDRESS        		  VARCHAR(20), 
  NAS_IDENTIFIER        		  VARCHAR(50), 
  NAS_PORT              		  NUMERIC(10), 
  NAS_PORT_TYPE         		  VARCHAR(50), 
  NAS_PORT_ID           		  VARCHAR(50), 
  SERVICE_TYPE          		  VARCHAR(50), 	
  SESSION_TIMEOUT       		  NUMERIC(10), 	
  IDLE_TIMEOUT          		  NUMERIC(10), 	
  CALLED_STATION_ID     		  VARCHAR(253),
  CALLING_STATION_ID    		  VARCHAR(253), 
  CLASS                 		  VARCHAR(253), 
  VENDOR_SPECIFIC       		  VARCHAR(253), 
  TERMINATION_ACTION    		  VARCHAR(50), 
  PROXY_STATE           		  NUMERIC(10), 
  CALLBACK_NUMBER       		  VARCHAR(50), 
  CALLBACK_ID           		  VARCHAR(50), 
  PORT_LIMIT            		  NUMERIC(10), 
  ACCT_INPUT_GIGAWORDS  		  NUMERIC(10), 
  ACCT_OUTPUT_GIGAWORDS 		  NUMERIC(10), 
  EVENT_TIMESTAMP       		  NUMERIC(14), 
  CONNECT_INFO          		  VARCHAR(253), 
  FRAMED_PROTOCOL       		  VARCHAR(50), 
  FRAMED_IP_ADDRESS     		  VARCHAR(20), 
  FRAMED_IP_NETMASK     		  VARCHAR(20), 
  FRAMED_ROUTING        		  VARCHAR(50), 
  FILTER_ID             		  VARCHAR(50), 
  FRAMED_MTU            		  NUMERIC(10), 
  ACCT_STATUS_TYPE      		  VARCHAR(50), 
  ACCT_INPUT_OCTETS     		  NUMERIC(10), 
  ACCT_OUTPUT_OCTETS    		  NUMERIC(10), 
  ACCT_SESSION_ID       		  VARCHAR(253), 													
  ACCT_SESSION_TIME     		  NUMERIC(10),   
  ACCT_INPUT_PACKETS    		  NUMERIC(10), 	
  ACCT_OUTPUT_PACKETS   		  NUMERIC(10), 	
  ACCT_MULTI_SESSION_ID 		  VARCHAR(253), 													
  ACCT_LINK_COUNT       		  NUMERIC(10), 
  PROTOCOLTYPE 					  VARCHAR(30),  
  PARAM_STR0            		  VARCHAR(253), 													
  PARAM_STR1            		  VARCHAR(253), 													
  PARAM_STR2            		  VARCHAR(253), 													
  PARAM_STR3            		  VARCHAR(253), 													
  PARAM_STR4            		  VARCHAR(253), 															
  PARAM_STR5            		  VARCHAR(253), 													
  PARAM_STR6            		  VARCHAR(253),										
  PARAM_STR7            		  VARCHAR(253),										
  PARAM_STR8            		  VARCHAR(253),										
  PARAM_STR9            		  VARCHAR(253),										
  PARAM_INT0            		  NUMERIC,     										
  PARAM_INT1            		  NUMERIC,     										
  PARAM_INT2            		  NUMERIC,     										
  PARAM_INT3            		  NUMERIC,     										
  PARAM_INT4            		  NUMERIC,     										
  PARAM_DATE0           		  TIMESTAMP,
  PARAM_DATE1           		  TIMESTAMP,
  PARAM_DATE2           		  TIMESTAMP,
  SESSION_CLOSE_REQUEST 		  CHAR(1)        DEFAULT 'N'                   NOT NULL,
  START_TIME            		  TIMESTAMP,
  AUTO_SESSION_AGENT_ID 		  VARCHAR(100),
  UID_POLICY_KEY        		  VARCHAR(252),
  LAST_AUTO_CLOSER_INVOKE_TIME 		  TIMESTAMP,
  ESN_MEID 				  VARCHAR(16),
  CUI 	 				  VARCHAR(64),
  HA_IP					  VARCHAR(64),
  BS_ID					  VARCHAR(64),
  PCF_SGSN_AGW 				  VARCHAR(64),
  NAP_OPERATOR_CARRIER 		          VARCHAR(64),
  LOCATION			          VARCHAR(255),
  TRANSPORT_IP_ADDRESS		          VARCHAR(64),
  SESSION_STATUS				VARCHAR(10),
  DEVICEVENDOR          VARCHAR(255),
  DEVICENAME            VARCHAR(255),
  DEVICEPORT            VARCHAR(255),
  GEOLOCATION           VARCHAR(255),
  DEVICEVLAN            VARCHAR(255),
  AAA_ID VARCHAR(60)
 )
  PARTITION BY HASH(ACCT_SESSION_ID)                            
( PARTITION tbsconcurrentusers01       TABLESPACE tbsconcurrentusers01,                   
  PARTITION tbsconcurrentusers02       TABLESPACE tbsconcurrentusers02,                  
  PARTITION tbsconcurrentusers03       TABLESPACE tbsconcurrentusers03,                  
  PARTITION tbsconcurrentusers04       TABLESPACE tbsconcurrentusers04,                  
  PARTITION tbsconcurrentusers05       TABLESPACE tbsconcurrentusers05,                  
  PARTITION tbsconcurrentusers06       TABLESPACE tbsconcurrentusers06,                  
  PARTITION tbsconcurrentusers07       TABLESPACE tbsconcurrentusers07,                  
  PARTITION tbsconcurrentusers08       TABLESPACE tbsconcurrentusers08 
)NOLOGGING;            

CREATE INDEX IDX_SESSION_ID
ON TBLMCONCURRENTUSERS
(
  ACCT_SESSION_ID 
)
GLOBAL
PARTITION BY HASH(ACCT_SESSION_ID)
(
  PARTITION tbsconcurrentusers01       TABLESPACE tbsconcurrentusers01,                   
  PARTITION tbsconcurrentusers02       TABLESPACE tbsconcurrentusers02,                  
  PARTITION tbsconcurrentusers03       TABLESPACE tbsconcurrentusers03,                  
  PARTITION tbsconcurrentusers04       TABLESPACE tbsconcurrentusers04,                  
  PARTITION tbsconcurrentusers05       TABLESPACE tbsconcurrentusers05,                  
  PARTITION tbsconcurrentusers06       TABLESPACE tbsconcurrentusers06,                  
  PARTITION tbsconcurrentusers07       TABLESPACE tbsconcurrentusers07,                  
  PARTITION tbsconcurrentusers08       TABLESPACE tbsconcurrentusers08
)NOLOGGING;

CREATE INDEX IDX_GRP_NAME
ON TBLMCONCURRENTUSERS
(
  GROUPNAME 
)
GLOBAL
PARTITION BY HASH(GROUPNAME)
(
  PARTITION tbsconcurrentusers01       TABLESPACE tbsconcurrentusers01,                   
  PARTITION tbsconcurrentusers02       TABLESPACE tbsconcurrentusers02,                  
  PARTITION tbsconcurrentusers03       TABLESPACE tbsconcurrentusers03,                  
  PARTITION tbsconcurrentusers04       TABLESPACE tbsconcurrentusers04,                  
  PARTITION tbsconcurrentusers05       TABLESPACE tbsconcurrentusers05,                  
  PARTITION tbsconcurrentusers06       TABLESPACE tbsconcurrentusers06,                  
  PARTITION tbsconcurrentusers07       TABLESPACE tbsconcurrentusers07,                  
  PARTITION tbsconcurrentusers08       TABLESPACE tbsconcurrentusers08
)NOLOGGING;

CREATE INDEX IDX_USER_NAME
ON TBLMCONCURRENTUSERS
(
  USER_NAME
)
GLOBAL
PARTITION BY HASH(USER_NAME)
(
  PARTITION tbsconcurrentusers01       TABLESPACE tbsconcurrentusers01,
  PARTITION tbsconcurrentusers02       TABLESPACE tbsconcurrentusers02,
  PARTITION tbsconcurrentusers03       TABLESPACE tbsconcurrentusers03,
  PARTITION tbsconcurrentusers04       TABLESPACE tbsconcurrentusers04,
  PARTITION tbsconcurrentusers05       TABLESPACE tbsconcurrentusers05,
  PARTITION tbsconcurrentusers06       TABLESPACE tbsconcurrentusers06,
  PARTITION tbsconcurrentusers07       TABLESPACE tbsconcurrentusers07,
  PARTITION tbsconcurrentusers08       TABLESPACE tbsconcurrentusers08
)NOLOGGING;


CREATE INDEX IDX_FRAMED_IP_ADDR
ON TBLMCONCURRENTUSERS
(
  FRAMED_IP_ADDRESS
)
GLOBAL
PARTITION BY HASH(FRAMED_IP_ADDRESS)
(
  PARTITION tbsconcurrentusers01       TABLESPACE tbsconcurrentusers01,
  PARTITION tbsconcurrentusers02       TABLESPACE tbsconcurrentusers02,
  PARTITION tbsconcurrentusers03       TABLESPACE tbsconcurrentusers03,
  PARTITION tbsconcurrentusers04       TABLESPACE tbsconcurrentusers04,
  PARTITION tbsconcurrentusers05       TABLESPACE tbsconcurrentusers05,
  PARTITION tbsconcurrentusers06       TABLESPACE tbsconcurrentusers06,
  PARTITION tbsconcurrentusers07       TABLESPACE tbsconcurrentusers07,
  PARTITION tbsconcurrentusers08       TABLESPACE tbsconcurrentusers08
)NOLOGGING;

CREATE INDEX PIDX_SESS_STATUS
ON TBLMCONCURRENTUSERS
(
  session_status
)
GLOBAL
PARTITION BY HASH(session_status)
(
  PARTITION tbsconcurrentusers01       TABLESPACE tbsconcurrentusers01,
  PARTITION tbsconcurrentusers02       TABLESPACE tbsconcurrentusers02,
  PARTITION tbsconcurrentusers03       TABLESPACE tbsconcurrentusers03,
  PARTITION tbsconcurrentusers04       TABLESPACE tbsconcurrentusers04,
  PARTITION tbsconcurrentusers05       TABLESPACE tbsconcurrentusers05,
  PARTITION tbsconcurrentusers06       TABLESPACE tbsconcurrentusers06,
  PARTITION tbsconcurrentusers07       TABLESPACE tbsconcurrentusers07,
  PARTITION tbsconcurrentusers08       TABLESPACE tbsconcurrentusers08
)NOLOGGING;

CREATE UNIQUE INDEX IDX_CONCUSERID
ON TBLMCONCURRENTUSERS
(
  CONCUSERID
)
GLOBAL
PARTITION BY HASH(CONCUSERID)
(
  PARTITION tbsconcurrentusers01       TABLESPACE tbsconcurrentusers01,
  PARTITION tbsconcurrentusers02       TABLESPACE tbsconcurrentusers02,
  PARTITION tbsconcurrentusers03       TABLESPACE tbsconcurrentusers03,
  PARTITION tbsconcurrentusers04       TABLESPACE tbsconcurrentusers04,
  PARTITION tbsconcurrentusers05       TABLESPACE tbsconcurrentusers05,
  PARTITION tbsconcurrentusers06       TABLESPACE tbsconcurrentusers06,
  PARTITION tbsconcurrentusers07       TABLESPACE tbsconcurrentusers07,
  PARTITION tbsconcurrentusers08       TABLESPACE tbsconcurrentusers08
)NOLOGGING;

ALTER TABLE TBLMCONCURRENTUSERS INITRANS 169;

ALTER INDEX IDX_SESSION_ID INITRANS 169;

ALTER INDEX IDX_GRP_NAME INITRANS 169;

ALTER INDEX IDX_USER_NAME INITRANS 169;

ALTER INDEX IDX_FRAMED_IP_ADDR INITRANS 169;

ALTER INDEX PIDX_SESS_STATUS INITRANS 169;

ALTER INDEX IDX_CONCUSERID INITRANS 169;

ALTER SEQUENCE SEQ_TBLMCONCURRENTUSERS CACHE 4000;
                            
CREATE OR REPLACE PROCEDURE PROC_AUTO_SESSION_CLOSER
AS
BEGIN
		  /*  ELITECSM ELITEAAA ADDON MODULE
		  ELITECORE TECHNOLOGIES PVT. LTD. */
	
FOR REC IN (SELECT ACCT_SESSION_ID FROM TBLMCONCURRENTUSERS WHERE LAST_UPDATED_TIME + 1 < SYSDATE)
LOOP
		
	DELETE FROM TBLMCONCURRENTUSERS S WHERE S.ACCT_SESSION_ID = REC.ACCT_SESSION_ID;
		
	commit;
			
END LOOP;	

END;
/


select dbms_metadata.get_ddl('TABLE','TBLMCONCURRENTUSERS') from dual;           
select dbms_metadata.get_dependent_ddl('INDEX','TBLMCONCURRENTUSERS') from dual; 
select text from user_source where name='PROC_AUTO_SESSION_CLOSER';

spool off;
exit;