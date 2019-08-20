spool ELITEAAA_BCP_TBLMCONCURRENTUSERS_AUTH_BEHAVIOR.log

prompt In case of Session manager auth behaviour CALLING_STATION_ID

--Enter Schema Name of EliteAAA Product                                     
connect &&username/&&password@&&NET_STR                                             
set long 10000                                                                   
set pagesize 0                                                                   
select dbms_metadata.get_ddl('TABLE','TBLMCONCURRENTUSERS') from dual;           
select dbms_metadata.get_dependent_ddl('INDEX','TBLMCONCURRENTUSERS') from dual; 

CREATE INDEX IDXP_CALLING_SID
ON TBLMCONCURRENTUSERS
(
 CALLING_STATION_ID
)
GLOBAL
PARTITION BY HASH(CALLING_STATION_ID)
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

ALTER INDEX IDXP_CALLING_SID INITRANS 169;


select dbms_metadata.get_ddl('TABLE','TBLMCONCURRENTUSERS') from dual;           
select dbms_metadata.get_dependent_ddl('INDEX','TBLMCONCURRENTUSERS') from dual; 

spool off;
exit;