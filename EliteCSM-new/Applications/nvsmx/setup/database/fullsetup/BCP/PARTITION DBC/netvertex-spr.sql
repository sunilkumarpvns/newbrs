spool netvertex-spr.log
set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

prompt -------NetVertex SPR ---------------------------------

prompt -------create tablespaces for netvertex spr module----

prompt Enter the datafile location for tablespace of NETVERTEX SPR - TABLE PARTITIONING ===>

DEFINE db_datafile_spr="&&subscriber_storage_dir"
prompt &db_datafile_spr                                          
                                          
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER01 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER02 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER03 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER04 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER05 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER06 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER07 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLM_SUBSCRIBER08 DATAFILE '&&db_datafile_spr/TBS_TBLM_SUBSCRIBER08.dbf' size 100M AUTOEXTEND ON NEXT 25M;

create tablespace TBS_IDX_SUBSCRIBER01 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER02 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER03 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER04 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER05 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER06 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER07 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create tablespace TBS_IDX_SUBSCRIBER08 datafile '&&db_datafile_spr/TBS_IDX_SUBSCRIBER08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLT_SUBSCRIPTION ===>

DEFINE db_datafile_addon="&&subscription_storage_dir"                                                           
prompt &db_datafile_addon 

CREATE TABLESPACE tbs_subscription01 DATAFILE '&&db_datafile_addon/tbs_subscription01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription02 DATAFILE '&&db_datafile_addon/tbs_subscription02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription03 DATAFILE '&&db_datafile_addon/tbs_subscription03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription04 DATAFILE '&&db_datafile_addon/tbs_subscription04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription05 DATAFILE '&&db_datafile_addon/tbs_subscription05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription06 DATAFILE '&&db_datafile_addon/tbs_subscription06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription07 DATAFILE '&&db_datafile_addon/tbs_subscription07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE tbs_subscription08 DATAFILE '&&db_datafile_addon/tbs_subscription08.dbf' size 100M AUTOEXTEND ON NEXT 25M;

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLT_SUBSCRIPTION_HISTORY ===>
                                                                                                       
DEFINE db_datafile_addon_hist="&&subscription_hist_storage_dir"                                                           
prompt &db_datafile_addon_hist                                                                                    

CREATE TABLESPACE TBS_SUBSCRIPTION_HIST01 DATAFILE '&&db_datafile_addon_hist/TBS_SUBSCRIPTION_HIST01.dbf' size 150M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_SUBSCRIPTION_HIST02 DATAFILE '&&db_datafile_addon_hist/TBS_SUBSCRIPTION_HIST02.dbf' size 150M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_SUBSCRIPTION_HIST03 DATAFILE '&&db_datafile_addon_hist/TBS_SUBSCRIPTION_HIST03.dbf' size 150M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_SUBSCRIPTION_HIST04 DATAFILE '&&db_datafile_addon_hist/TBS_SUBSCRIPTION_HIST04.dbf' size 150M AUTOEXTEND ON NEXT 25M;
                                                                                                                                
CREATE TABLESPACE IDX_SUBSCRIPTION_HIST01 DATAFILE '&&db_datafile_addon_hist/IDX_SUBSCRIPTION_HIST01.dbf' size 150M AUTOEXTEND ON NEXT 25M;

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLT_USAGE ===>

DEFINE db_datafile_usage="&&usage_storage_dir"
prompt &db_datafile_usage                                          
                                          
CREATE TABLESPACE TBS_TBLT_USAGE01 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLT_USAGE02 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLT_USAGE03 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLT_USAGE04 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLT_USAGE05 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLT_USAGE06 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLT_USAGE07 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLT_USAGE08 DATAFILE '&&db_datafile_usage/TBS_TBLT_USAGE08.dbf' size 100M AUTOEXTEND ON NEXT 25M;

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLT_USAGE_HISTORY ===>
DEFINE db_datafile_usage_hist="&&usage_hist_storage_dir"
prompt &db_datafile_usage_hist

CREATE TABLESPACE tbs_usage_hist_01   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_02   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_03   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_04   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_05   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_06   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_07   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_08   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_09   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_09.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_10   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_10.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_11   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_11.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_12   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_12.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_13   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_13.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_14   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_14.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE tbs_usage_hist_15   DATAFILE '&&db_datafile_usage_hist/tbs_usage_hist_15.dbf' size 100M AUTOEXTEND ON NEXT 25M; 


prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLM_DATA_BALANCE ===>
DEFINE db_datafile_data_balance="&&data_balance_storage_dir"
prompt &db_datafile_data_balance


CREATE TABLESPACE TS_TBLM_DATA_BALANCE01 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_DATA_BALANCE02 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_DATA_BALANCE03 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_DATA_BALANCE04 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_DATA_BALANCE05 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_DATA_BALANCE06 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_DATA_BALANCE07 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_DATA_BALANCE08 DATAFILE '&&db_datafile_data_balance/TBS_TBLM_DATA_BALANCE08.dbf' size 100M AUTOEXTEND ON NEXT 25M;
                   
create TABLESPACE TS_IDX_DATA_BALANCE01 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_DATA_BALANCE02 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_DATA_BALANCE03 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_DATA_BALANCE04 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_DATA_BALANCE05 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_DATA_BALANCE06 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_DATA_BALANCE07 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_DATA_BALANCE08 datafile '&&db_datafile_data_balance/TBS_IDX_DATA_BALANCE08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLM_MONETARY_BALANCE ===>
DEFINE db_datafile_monetary_balance="&&monetary_balance_storage_dir"
prompt &db_datafile_monetary_balance

CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE01 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE02 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE03 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE04 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE05 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE05.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE06 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE06.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE07 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE07.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TS_TBLM_MONETARY_BALANCE08 DATAFILE '&&monetary_balance_storage_dir/TBS_TBLM_MONETARY_BALANCE08.dbf' size 100M AUTOEXTEND ON NEXT 25M;

create TABLESPACE TS_IDX_MONETARY_BALANCE01 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_MONETARY_BALANCE02 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_MONETARY_BALANCE03 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_MONETARY_BALANCE04 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_MONETARY_BALANCE05 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_MONETARY_BALANCE06 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_MONETARY_BALANCE07 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
create TABLESPACE TS_IDX_MONETARY_BALANCE08 datafile '&&monetary_balance_storage_dir/TBS_IDX_MONETARY_BALANCE08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 


prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLT_data_balance_histORY ===>
DEFINE db_datafile_data_balance_hist="&&data_balance_hist_storage_dir"
prompt &db_datafile_data_balance_hist

CREATE TABLESPACE TS_data_balance_hist_01   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_02   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_03   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_04   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_05   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_06   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_07   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_08   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_09   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_09.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_10   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_10.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_11   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_11.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_12   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_12.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_13   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_13.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_14   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_14.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_data_balance_hist_15   DATAFILE '&&db_datafile_data_balance_hist/tbs_data_balance_hist_15.dbf' size 100M AUTOEXTEND ON NEXT 25M; 

prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLT_monetary_balance_histORY ===>
DEFINE db_datafile_monetary_balance_hist="&&monetary_balance_hist_storage_dir"
prompt &db_datafile_monetary_balance_hist

CREATE TABLESPACE TS_monetary_balance_hist_01   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_01.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_02   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_02.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_03   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_03.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_04   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_04.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_05   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_05.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_06   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_06.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_07   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_07.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_08   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_08.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_09   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_09.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_10   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_10.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_11   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_11.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_12   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_12.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_13   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_13.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_14   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_14.dbf' size 100M AUTOEXTEND ON NEXT 25M; 
CREATE TABLESPACE TS_monetary_balance_hist_15   DATAFILE '&&monetary_balance_hist_storage_dir/tbs_monetary_balance_hist_15.dbf' size 100M AUTOEXTEND ON NEXT 25M; 


prompt Enter the username of NETVERTEX ===>
connect &&username/&&password@&&NET_STR
set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;
BEGIN

IF sys_context('userenv','current_schema') IN ('SYS','SYSTEM','OUTLN','DBSNMP','SYSMAN','ORACLE_OCM',
'DIP','APPQOSSYS','FLOWS_FILES','MDSYS','ORDSYS','SCOTT','HR','EXFSYS','WMSYS','XS$NULL','ORDDATA',
'MDDATA','CTXSYS','ANONYMOUS','XDB','APEX_PUBLIC_USER','SPATIAL_CSW_ADMIN_USR','SPATIAL_WFS_ADMIN_USR',
'ORDPLUGINS','OWBSYS','SI_INFORMTN_SCHEMA','OLAPSYS') THEN

 Raise_Application_Error (-20343, 'You are connected in Oracle Schemas ,Please switch the proper netvertex schema name.');

END IF;
END;
/

--Global Section FUNC_CHECK_OBJ
create or replace FUNCTION FUNC_CHECK_OBJ(OBJ_TYPE VARCHAR,
      OBJ_NAME VARCHAR)
    RETURN NUMBER
  AS
    CNT NUMBER;
  BEGIN
    IF Upper(OBJ_TYPE)	= 'TABLE' THEN
    SELECT COUNT(*) INTO CNT FROM USER_TABLES WHERE TABLE_NAME = ''||OBJ_NAME||'';
    RETURN CNT;
    ELSIF Upper(OBJ_TYPE)	= 'INDEX' THEN
    SELECT COUNT(*) INTO CNT FROM USER_INDEXES WHERE TABLE_NAME = ''||OBJ_NAME||'';
    RETURN CNT;
    ELSIF Upper(OBJ_TYPE)	= 'TRIGGER' THEN
    SELECT COUNT(*) INTO CNT FROM USER_TRIGGERS WHERE TRIGGER_NAME = ''||OBJ_NAME||'';
    RETURN CNT;
	ELSIF Upper(OBJ_TYPE) = 'SEQUENCE' THEN
	SELECT COUNT(*) INTO CNT FROM USER_SEQUENCES WHERE SEQUENCE_NAME = ''||OBJ_NAME||'';
    RETURN CNT;
	ELSE
	RETURN 0;
	END IF;
  END;
  /
  
prompt --------------------Backup DBC Before Change-------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OUTPUT CLOB:= NULL;
  BEGIN
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES 
               WHERE TABLE_NAME IN ('TBLM_SUBSCRIBER','TBLT_SUBSCRIPTION','TBLT_USAGE','TBLT_SUBSCRIPTION_HISTORY','TBLT_USAGE_HISTORY','TBLM_DATA_BALANCE','TBLM_MONETARY_BALANCE','TBLM_DATA_BALANCE_HISTORY','TBLM_MONETARY_BALANCE_HISTORY') ORDER BY TABLE_NAME)
  LOOP
     FOR REC_T IN (select FUNC_CHECK_OBJ('TABLE',REC.TABLE_NAME) V_OBJ FROM DUAL)
	 LOOP
	  IF REC_T.V_OBJ > 0 THEN
	  dbms_output.put_line('-----------------DDL FOR TABLE----------------------------------');
	  select dbms_metadata.get_ddl('TABLE',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
	 END LOOP; 
	 
  FOR REC_I IN (select FUNC_CHECK_OBJ('INDEX',REC.TABLE_NAME) V_OBJ FROM DUAL)
  LOOP
      IF REC_I.V_OBJ >0 THEN
	  dbms_output.put_line('-----------------DDL FOR INDEX----------------------------------');
	  select dbms_metadata.get_dependent_ddl('INDEX',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
  END LOOP;	  
  END LOOP;	  
END;
/

prompt --------------------DDL Change Start for TBLM_SUBSCRIBER------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_SUBSCRIBER'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIBER') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_SUBSCRIBER already exists, Drop OLD_TBLM_SUBSCRIBER object then execute DBC again');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_SUBSCRIBER TO OLD_TBLM_SUBSCRIBER';
	  dbms_output.put_line('TBLM_SUBSCRIBER Renamed');

	  END IF;
 END;
 / 
 
--RENAME TBLM_SUBSCRIBER TO OLD_TBLM_SUBSCRIBER;

CREATE TABLE TBLM_SUBSCRIBER(
    "USERNAME" VARCHAR2(255),
        "PASSWORD" VARCHAR2(255),
        "SUBSCRIBERIDENTITY" VARCHAR2(255),
        "PARENTID" VARCHAR2(255),
        "GROUPNAME" VARCHAR2(255),
        "ENCRYPTIONTYPE" VARCHAR2(255),
        "CUSTOMERTYPE" VARCHAR2(10),
        "BILLINGDATE" NUMBER(2,0),
        "EXPIRYDATE" TIMESTAMP (6),
        "PRODUCT_OFFER" VARCHAR2(100),
        "IMSPACKAGE" VARCHAR2(100),
        "EMAIL" VARCHAR2(100),
        "PHONE" VARCHAR2(20),
        "SIPURL" VARCHAR2(200),
        "AREA" VARCHAR2(20),
        "CITY" VARCHAR2(20),
        "ZONE" VARCHAR2(20),
        "COUNTRY" VARCHAR2(55),
        "BIRTHDATE" TIMESTAMP (6),
        "ROLE" VARCHAR2(20),
        "COMPANY" VARCHAR2(512),
        "DEPARTMENT" VARCHAR2(20),
        "CADRE" VARCHAR2(5),
        "ARPU" NUMBER(20,0),
        "CUI" VARCHAR2(255),
        "IMSI" VARCHAR2(100),
        "MSISDN" VARCHAR2(100),
        "IMEI" VARCHAR2(100),
        "ESN" VARCHAR2(100),
        "MEID" VARCHAR2(100),
        "MAC" VARCHAR2(100),
        "EUI64" VARCHAR2(100),
        "MODIFIED_EUI64" VARCHAR2(100),
        "SUBSCRIBERLEVELMETERING" VARCHAR2(10) DEFAULT 'DISABLE',
        "STATUS" VARCHAR2(24),
        "PASSWORD_CHECK" VARCHAR2(5) DEFAULT '0',
        "SY_INTERFACE" VARCHAR2(5) DEFAULT '1',
        "PAYG_INTL_DATA_ROAMING" VARCHAR2(5) DEFAULT '1',
        "CALLING_STATION_ID" VARCHAR2(253),
        "FRAMED_IP" VARCHAR2(20),
        "NAS_PORT_ID" VARCHAR2(50),
        "PARAM1" VARCHAR2(10),
        "PARAM2" VARCHAR2(10),
        "PARAM3" VARCHAR2(10),
        "PARAM4" VARCHAR2(10),
        "PARAM5" VARCHAR2(10),
        "BILLING_ACCOUNT_ID" VARCHAR2(100),
        "SERVICE_INSTANCE_ID" VARCHAR2(100),
        "CREATED_DATE" TIMESTAMP (6),
        "MODIFIED_DATE" TIMESTAMP (6),
        "NEXTBILLDATE" TIMESTAMP (6),
        "BILLCHANGEDATE" TIMESTAMP (6),
    CONSTRAINT PK_SUBSCRIBER_PRT PRIMARY KEY (SUBSCRIBERIDENTITY)
)                                                        
PARTITION BY HASH(SUBSCRIBERIDENTITY)                                         
( PARTITION PART_TBS_SUBSCRIBER01   TABLESPACE TS_TBLM_SUBSCRIBER01,   
  PARTITION PART_TBS_SUBSCRIBER02   TABLESPACE TS_TBLM_SUBSCRIBER02,
  PARTITION PART_TBS_SUBSCRIBER03   TABLESPACE TS_TBLM_SUBSCRIBER03,
  PARTITION PART_TBS_SUBSCRIBER04   TABLESPACE TS_TBLM_SUBSCRIBER04, 
  PARTITION PART_TBS_SUBSCRIBER05   TABLESPACE TS_TBLM_SUBSCRIBER05,
  PARTITION PART_TBS_SUBSCRIBER06   TABLESPACE TS_TBLM_SUBSCRIBER06,   
  PARTITION PART_TBS_SUBSCRIBER07   TABLESPACE TS_TBLM_SUBSCRIBER07,
  PARTITION PART_TBS_SUBSCRIBER08   TABLESPACE TS_TBLM_SUBSCRIBER08   
)NOLOGGING;   


DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_SUBSCRIBER') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_SUBSCRIBER SELECT * FROM OLD_TBLM_SUBSCRIBER';

      COMMIT;
	  dbms_output.put_line('TBLM_SUBSCRIBER Data Imported');

	  END IF;
 END;
 /

--Disable Primary Key

ALTER TABLE TBLM_SUBSCRIBER DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

  CREATE UNIQUE INDEX PK_SUBSCRIBER_PRT ON TBLM_SUBSCRIBER(SUBSCRIBERIDENTITY)
  GLOBAL PARTITION BY HASH (SUBSCRIBERIDENTITY)                                               
(PARTITION PRT_IDX_SUBSCRIBER01    TABLESPACE TS_IDX_SUBSCRIBER01 ,
 PARTITION PRT_IDX_SUBSCRIBER02    TABLESPACE TS_IDX_SUBSCRIBER02 ,
 PARTITION PRT_IDX_SUBSCRIBER03    TABLESPACE TS_IDX_SUBSCRIBER03 ,
 PARTITION PRT_IDX_SUBSCRIBER04    TABLESPACE TS_IDX_SUBSCRIBER04 ,
 PARTITION PRT_IDX_SUBSCRIBER05    TABLESPACE TS_IDX_SUBSCRIBER05 ,
 PARTITION PRT_IDX_SUBSCRIBER06    TABLESPACE TS_IDX_SUBSCRIBER06 ,
 PARTITION PRT_IDX_SUBSCRIBER07    TABLESPACE TS_IDX_SUBSCRIBER07 ,
 PARTITION PRT_IDX_SUBSCRIBER08    TABLESPACE TS_IDX_SUBSCRIBER08 )
 INITRANS 169 NOLOGGING;
 
--Enable Primary key

ALTER TABLE TBLM_SUBSCRIBER ENABLE PRIMARY KEY;

CREATE INDEX PIDX_SUBSCRIBER_STATUS ON TBLM_SUBSCRIBER(STATUS) INITRANS 169 NOLOGGING;

ALTER TABLE TBLM_SUBSCRIBER INITRANS 169;

DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('SEQUENCE','SEQ_MSUBSCRIBER') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  execute immediate 'ALTER SEQUENCE SEQ_MSUBSCRIBER CACHE 1000';
	  dbms_output.put_line('SEQUENCE Altered');
	  ELSE
	  execute immediate 'CREATE SEQUENCE SEQ_MSUBSCRIBER INCREMENT BY 1 START WITH 101 NOMAXVALUE NOMINVALUE NOCYCLE CACHE 1000 NOORDER';
	  dbms_output.put_line('SEQUENCE Created');

	  END IF;
 END;
 /

prompt --------------------DDL Change End for TBLM_SUBSCRIBER-----------------------------

prompt --------------------DDL Change Start for TBLT_SUBSCRIPTION-------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLT_SUBSCRIPTION'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SUBSCRIPTION') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLT_SUBSCRIPTION already exists, Drop OLD_TBLT_SUBSCRIPTION object then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLT_SUBSCRIPTION TO OLD_TBLT_SUBSCRIPTION';
	  dbms_output.put_line('TBLT_SUBSCRIPTION Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLT_SUBSCRIPTION TO OLD_TBLT_SUBSCRIPTION;

CREATE TABLE TBLT_SUBSCRIPTION 
   (
    "SUBSCRIPTION_ID" VARCHAR2(36),
        "SUBSCRIBER_ID" VARCHAR2(256) NOT NULL ENABLE,
        "PACKAGE_ID" VARCHAR2(36) NOT NULL ENABLE,
        "START_TIME" TIMESTAMP (6),
        "END_TIME" TIMESTAMP (6),
        "STATUS" CHAR(1) DEFAULT '0',
        "SERVER_INSTANCE_ID" NUMBER(*,0),
        "PARENT_IDENTITY" VARCHAR2(256),
        "REJECT_REASON" VARCHAR2(1024),
        "SUBSCRIPTION_TIME" TIMESTAMP (6),
        "LAST_UPDATE_TIME" TIMESTAMP (6),
        "PRIORITY" NUMBER(5,0),
        "USAGE_RESET_DATE" TIMESTAMP (6),
        "PARAM1" VARCHAR2(256),
        "PARAM2" VARCHAR2(256),
        "PRODUCT_OFFER_ID" VARCHAR2(36),
        "TYPE" VARCHAR2(10),
    CONSTRAINT PK_SUBSCRIPTION_PRT PRIMARY KEY(SUBSCRIPTION_ID)
   )
PARTITION BY HASH  (SUBSCRIBER_ID) 
(PARTITION PART_TBS_SUBSCRIBER1        tablespace ts_subscription01,
 PARTITION PART_TBS_SUBSCRIBER2        tablespace ts_subscription02,
 PARTITION PART_TBS_SUBSCRIBER3        tablespace ts_subscription03,
 PARTITION PART_TBS_SUBSCRIBER4        tablespace ts_subscription04,
 PARTITION PART_TBS_SUBSCRIBER5        tablespace ts_subscription05,
 PARTITION PART_TBS_SUBSCRIBER6        tablespace ts_subscription06,
 PARTITION PART_TBS_SUBSCRIBER7        tablespace ts_subscription07,
 PARTITION PART_TBS_SUBSCRIBER8        tablespace ts_subscription08 
)NOLOGGING;

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SUBSCRIPTION') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLT_SUBSCRIPTION SELECT * FROM OLD_TBLT_SUBSCRIPTION';

      COMMIT;
	  dbms_output.put_line('TBLT_SUBSCRIPTION Data Imported');

	  END IF;
 END;
 /

CREATE INDEX IDX_SUBSCRIBER_ID ON TBLT_SUBSCRIPTION (SUBSCRIBER_ID)     
GLOBAL PARTITION BY HASH (SUBSCRIBER_ID)                                                       
(PARTITION PART_TBS_SUBSCRIBER1        tablespace ts_subscription01,
 PARTITION PART_TBS_SUBSCRIBER2        tablespace ts_subscription02,
 PARTITION PART_TBS_SUBSCRIBER3        tablespace ts_subscription03,
 PARTITION PART_TBS_SUBSCRIBER4        tablespace ts_subscription04,
 PARTITION PART_TBS_SUBSCRIBER5        tablespace ts_subscription05,
 PARTITION PART_TBS_SUBSCRIBER6        tablespace ts_subscription06,
 PARTITION PART_TBS_SUBSCRIBER7        tablespace ts_subscription07,
 PARTITION PART_TBS_SUBSCRIBER8        tablespace ts_subscription08
)INITRANS 169 NOLOGGING;          

--Disable Primary Key

ALTER TABLE TBLT_SUBSCRIPTION DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

CREATE UNIQUE INDEX PK_SUBSCRIPTION_PRT ON TBLT_SUBSCRIPTION (SUBSCRIPTION_ID)           
GLOBAL PARTITION BY HASH (SUBSCRIPTION_ID)                                                       
(PARTITION PART_TBS_SUBSCRIBER1        tablespace ts_subscription01,
 PARTITION PART_TBS_SUBSCRIBER2        tablespace ts_subscription02,
 PARTITION PART_TBS_SUBSCRIBER3        tablespace ts_subscription03,
 PARTITION PART_TBS_SUBSCRIBER4        tablespace ts_subscription04,
 PARTITION PART_TBS_SUBSCRIBER5        tablespace ts_subscription05,
 PARTITION PART_TBS_SUBSCRIBER6        tablespace ts_subscription06,
 PARTITION PART_TBS_SUBSCRIBER7        tablespace ts_subscription07,
 PARTITION PART_TBS_SUBSCRIBER8        tablespace ts_subscription08
)INITRANS 169 NOLOGGING;

--Enable Primary key
ALTER TABLE TBLT_SUBSCRIPTION ENABLE PRIMARY KEY;

ALTER TABLE TBLT_SUBSCRIPTION INITRANS 169;

prompt --------------------DDL Change End for TBLT_SUBSCRIPTION-------------------------

prompt --------------------DDL Change Start for TBLT_SUBSCRIPTION_HISTORY-------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLT_SUBSCRIPTION_HISTORY'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SUBSCRIPTION_HISTORY') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLT_SUBSCRIPTION_HISTORY already exists, Drop OLD_TBLT_SUBSCRIPTION_HISTORY object then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	   execute immediate 'RENAME TBLT_SUBSCRIPTION_HISTORY TO OLD_TBLT_SUBSCRIPTION_HISTORY';
	  dbms_output.put_line('TBLT_SUBSCRIPTION_HISTORY Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLT_SUBSCRIPTION_HISTORY TO OLD_TBLT_SUBSCRIPTION_HISTORY;

CREATE TABLE TBLT_SUBSCRIPTION_HISTORY
(
   "SUBSCRIBER_ID" VARCHAR2(256) NOT NULL ENABLE,
   "PACKAGE_ID" VARCHAR2(36) NOT NULL ENABLE,
   "START_TIME" TIMESTAMP (6),
   "END_TIME" TIMESTAMP (6),
   "LAST_UPDATE_TIME" TIMESTAMP (6),
   "STATUS" VARCHAR2(32) NOT NULL ENABLE,
   "SUBSCRIPTION_ID" VARCHAR2(36),
   "PARENT_IDENTITY" VARCHAR2(256),
   "SUBSCRIPTION_TIME" TIMESTAMP (6),
   "REJECT_REASON" VARCHAR2(1024)
)PARTITION BY RANGE (START_TIME) INTERVAL (NUMTODSINTERVAL(1,'DAY')) STORE IN
 (TS_SUBSCRIPTION_HIST01,TS_SUBSCRIPTION_HIST02,TS_SUBSCRIPTION_HIST03,TS_SUBSCRIPTION_HIST04)
 (PARTITION P_PSHFIRST  VALUES LESS THAN (TIMESTAMP' 2014-07-03 00:00:00')) NOLOGGING;


DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLT_SUBSCRIPTION_HISTORY') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLT_SUBSCRIPTION_HISTORY SELECT * FROM OLD_TBLT_SUBSCRIPTION_HISTORY';

      COMMIT;
	  dbms_output.put_line('TBLT_SUBSCRIPTION_HISTORY Data Imported');

	  END IF;
 END;
 / 
 
CREATE INDEX IDX_SUBSCRIPTION_HIST_SID ON TBLT_SUBSCRIPTION_HISTORY(SUBSCRIPTION_ID) TABLESPACE TS_IDX_SUBSCRIPTION_HIST01 INITRANS 169 NOLOGGING;

ALTER TABLE TBLT_SUBSCRIPTION_HISTORY INITRANS 169;

--Added Procedure 22-09-2015

DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TRIGGER','TRG_SUBSCRIPTION_HISTORY_DEL') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  execute immediate 'DROP TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL';
	  dbms_output.put_line('DROP TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL');

	  END IF;
 END;
 /
 
--DROP TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL;

CREATE OR REPLACE TRIGGER TRG_SUBSCRIPTION_HISTORY_DEL
AFTER DELETE ON TBLT_SUBSCRIPTION FOR EACH ROW
BEGIN
    IF DELETING THEN
          INSERT INTO TBLT_SUBSCRIPTION_HISTORY
         (	SUBSCRIBER_ID      ,
          PACKAGE_ID           ,
          START_TIME           ,
		  END_TIME             ,
		  LAST_UPDATE_TIME     ,
		  STATUS               ,
		  SUBSCRIPTION_ID      ,
		  PARENT_IDENTITY      ,
		  SUBSCRIPTION_TIME    ,
		  REJECT_REASON 		  
	
)
		  VALUES( :OLD.SUBSCRIBER_ID      ,
				  :OLD.PACKAGE_ID           ,
				  :OLD.START_TIME         ,
				  :OLD.END_TIME           ,
				  :OLD.LAST_UPDATE_TIME   ,
				  :OLD.STATUS             ,
				  :OLD.SUBSCRIPTION_ID    ,
				  :OLD.PARENT_IDENTITY    ,
				  :OLD.SUBSCRIPTION_TIME  ,
				  :OLD.PARAM1
                );		  
    END IF;
	
END;
/
 
prompt --------------------DDL Change End for TBLT_SUBSCRIPTION_HISTORY-------------------------

prompt --------------------DDL Change Start for TBLT_USAGE--------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLT_USAGE'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLT_USAGE') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLT_USAGE already exists, Drop OLD_TBLT_USAGE then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLT_USAGE TO OLD_TBLT_USAGE';
	  dbms_output.put_line('TBLT_USAGE Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLT_USAGE TO OLD_TBLT_USAGE;

CREATE TABLE TBLT_USAGE
   (
     "ID" VARCHAR2(36),
     "SUBSCRIBER_ID" VARCHAR2(255),
     "PACKAGE_ID" VARCHAR2(36),
     "SUBSCRIPTION_ID" VARCHAR2(36),
     "QUOTA_PROFILE_ID" VARCHAR2(36),
     "SERVICE_ID" VARCHAR2(36),
     "DAILY_TOTAL" NUMBER(*,0),
     "DAILY_UPLOAD" NUMBER(*,0),
     "DAILY_DOWNLOAD" NUMBER(*,0),
     "DAILY_TIME" NUMBER(*,0),
     "WEEKLY_TOTAL" NUMBER(*,0),
     "WEEKLY_UPLOAD" NUMBER(*,0),
     "WEEKLY_DOWNLOAD" NUMBER(*,0),
     "WEEKLY_TIME" NUMBER(*,0),
     "BILLING_CYCLE_TOTAL" NUMBER(*,0),
     "BILLING_CYCLE_UPLOAD" NUMBER(*,0),
     "BILLING_CYCLE_DOWNLOAD" NUMBER(*,0),
     "BILLING_CYCLE_TIME" NUMBER(*,0),
     "CUSTOM_TOTAL" NUMBER(*,0),
     "CUSTOM_UPLOAD" NUMBER(*,0),
     "CUSTOM_DOWNLOAD" NUMBER(*,0),
     "CUSTOM_TIME" NUMBER(*,0),
     "DAILY_RESET_TIME" TIMESTAMP (6),
     "WEEKLY_RESET_TIME" TIMESTAMP (6),
     "CUSTOM_RESET_TIME" TIMESTAMP (6),
     "BILLING_CYCLE_RESET_TIME" TIMESTAMP (6),
     "LAST_UPDATE_TIME" TIMESTAMP (6),
     "PRODUCT_OFFER_ID" VARCHAR2(36),
    CONSTRAINT PK_USAGE_PRT PRIMARY KEY(ID)
   )                   
PARTITION BY HASH(ID)                                         
( PARTITION PART_TBLT_USAGE01       TABLESPACE TS_TBLT_USAGE01,   
  PARTITION PART_TBLT_USAGE02       TABLESPACE TS_TBLT_USAGE02,
  PARTITION PART_TBLT_USAGE03       TABLESPACE TS_TBLT_USAGE03,
  PARTITION PART_TBLT_USAGE04       TABLESPACE TS_TBLT_USAGE04, 
  PARTITION PART_TBLT_USAGE05       TABLESPACE TS_TBLT_USAGE05,
  PARTITION PART_TBLT_USAGE06       TABLESPACE TS_TBLT_USAGE06, 
  PARTITION PART_TBLT_USAGE07       TABLESPACE TS_TBLT_USAGE07,
  PARTITION PART_TBLT_USAGE08       TABLESPACE TS_TBLT_USAGE08   
)NOLOGGING;   


DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLT_USAGE') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLT_USAGE SELECT * FROM OLD_TBLT_USAGE';

      COMMIT;
	  dbms_output.put_line('TBLT_USAGE Data Imported');

	  END IF;
 END;
 /

--Disable Primary Key

ALTER TABLE TBLT_USAGE DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

CREATE UNIQUE INDEX PK_USAGE_PRT ON TBLT_USAGE (ID)
GLOBAL PARTITION BY HASH (ID)  
(
  PARTITION PART_IDX_USAGE01       TABLESPACE TS_TBLT_USAGE01,   
  PARTITION PART_IDX_USAGE02       TABLESPACE TS_TBLT_USAGE02,
  PARTITION PART_IDX_USAGE03       TABLESPACE TS_TBLT_USAGE03,
  PARTITION PART_IDX_USAGE04       TABLESPACE TS_TBLT_USAGE04, 
  PARTITION PART_IDX_USAGE05       TABLESPACE TS_TBLT_USAGE05,
  PARTITION PART_IDX_USAGE06       TABLESPACE TS_TBLT_USAGE06, 
  PARTITION PART_IDX_USAGE07       TABLESPACE TS_TBLT_USAGE07,
  PARTITION PART_IDX_USAGE08       TABLESPACE TS_TBLT_USAGE08 
)INITRANS 169 NOLOGGING;   

--Enable Primary key

ALTER TABLE TBLT_USAGE ENABLE PRIMARY KEY;

CREATE INDEX PPRT_USAGE_SID ON TBLT_USAGE (SUBSCRIBER_ID)
GLOBAL PARTITION BY HASH (SUBSCRIBER_ID)  
(
  PARTITION PART_IDX_USAGE01       TABLESPACE TS_TBLT_USAGE01,   
  PARTITION PART_IDX_USAGE02       TABLESPACE TS_TBLT_USAGE02,
  PARTITION PART_IDX_USAGE03       TABLESPACE TS_TBLT_USAGE03,
  PARTITION PART_IDX_USAGE04       TABLESPACE TS_TBLT_USAGE04, 
  PARTITION PART_IDX_USAGE05       TABLESPACE TS_TBLT_USAGE05,
  PARTITION PART_IDX_USAGE06       TABLESPACE TS_TBLT_USAGE06, 
  PARTITION PART_IDX_USAGE07       TABLESPACE TS_TBLT_USAGE07,
  PARTITION PART_IDX_USAGE08       TABLESPACE TS_TBLT_USAGE08 
)INITRANS 169 NOLOGGING; 


ALTER TABLE TBLT_USAGE INITRANS 169;

prompt --------------------DDL Change End for TBLT_USAGE-----------------------------

prompt --------------------DDL Change Start for TBLT_USAGE_HISTORY-------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLT_USAGE_HISTORY') INTO V_OBJ
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object TBLT_USAGE_HISTORY already exists, Drop TBLT_USAGE_HISTORY object then execute DBC again.');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLT_USAGE_HISTORY TO OLD_TBLT_USAGE_HISTORY';
	  dbms_output.put_line('TBLT_USAGE_HISTORY Renamed');

	  END IF;
 END;
 /
 
--RENAME TBLT_USAGE_HISTORY TO OLD_TBLT_USAGE_HISTORY;

--table partitioning 

CREATE TABLE TBLT_USAGE_HISTORY
   ( "CREATE_DATE" TIMESTAMP (6),
     "ID" VARCHAR2(36),
     "SUBSCRIBER_ID" VARCHAR2(255),
     "PACKAGE_ID" VARCHAR2(36),
     "SUBSCRIPTION_ID" VARCHAR2(36),
     "QUOTA_PROFILE_ID" VARCHAR2(36),
     "SERVICE_ID" VARCHAR2(36),
     "DAILY_TOTAL" NUMBER(*,0),
     "DAILY_UPLOAD" NUMBER(*,0),
     "DAILY_DOWNLOAD" NUMBER(*,0),
     "DAILY_TIME" NUMBER(*,0),
     "WEEKLY_TOTAL" NUMBER(*,0),
     "WEEKLY_UPLOAD" NUMBER(*,0),
     "WEEKLY_DOWNLOAD" NUMBER(*,0),
     "WEEKLY_TIME" NUMBER(*,0),
     "BILLING_CYCLE_TOTAL" NUMBER(*,0),
     "BILLING_CYCLE_UPLOAD" NUMBER(*,0),
     "BILLING_CYCLE_DOWNLOAD" NUMBER(*,0),
     "BILLING_CYCLE_TIME" NUMBER(*,0),
     "CUSTOM_TOTAL" NUMBER(*,0),
     "CUSTOM_UPLOAD" NUMBER(*,0),
     "CUSTOM_DOWNLOAD" NUMBER(*,0),
     "CUSTOM_TIME" NUMBER(*,0),
     "DAILY_RESET_TIME" TIMESTAMP (6),
     "WEEKLY_RESET_TIME" TIMESTAMP (6),
     "CUSTOM_RESET_TIME" TIMESTAMP (6),
     "BILLING_CYCLE_RESET_TIME" TIMESTAMP (6),
     "LAST_UPDATE_TIME" TIMESTAMP (6),
     "PRODUCT_OFFER_ID" VARCHAR2(36)
)PARTITION BY RANGE (CREATE_DATE)
INTERVAL (NUMTODSINTERVAL(1,'day')) store in (ts_usage_hist_01,ts_usage_hist_02,ts_usage_hist_03,ts_usage_hist_04,ts_usage_hist_05,ts_usage_hist_06,ts_usage_hist_07,ts_usage_hist_08,ts_usage_hist_09,ts_usage_hist_10,ts_usage_hist_11,ts_usage_hist_12,ts_usage_hist_13,ts_usage_hist_14,ts_usage_hist_15)
(PARTITION p_first VALUES LESS THAN (TO_DATE('18-12-2013', 'DD-MM-YYYY')))
NOLOGGING; 

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLT_USAGE_HISTORY') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLT_USAGE_HISTORY SELECT * FROM OLD_TBLT_USAGE_HISTORY';

      COMMIT;
	  dbms_output.put_line('TBLT_USAGE_HISTORY Data Imported');

	  END IF;
 END;
 /

 ALTER TABLE TBLT_USAGE_HISTORY	 INITRANS 169;
 
prompt --------------------DDL Change End for TBLT_USAGE_HISTORY-------------------

prompt --------------------DDL Change Start for TBLM_DATA_BALANCE------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_DATA_BALANCE'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_DATA_BALANCE') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_DATA_BALANCE already exists, Drop OLD_TBLM_DATA_BALANCE object then execute DBC again');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_DATA_BALANCE TO OLD_TBLM_DATA_BALANCE';
	  dbms_output.put_line('TBLM_DATA_BALANCE Renamed');

	  END IF;
 END;
 /
 
 
CREATE TABLE "TBLM_DATA_BALANCE"
   (    "ID" VARCHAR2(36),
        "SUBSCRIBER_ID" VARCHAR2(255),
        "PACKAGE_ID" VARCHAR2(36),
        "SUBSCRIPTION_ID" VARCHAR2(36),
        "QUOTA_PROFILE_ID" VARCHAR2(36),
        "PRODUCT_OFFER_ID" VARCHAR2(36),
        "DATA_SERVICE_TYPE_ID" NUMBER(*,0),
        "RATING_GROUP_ID" NUMBER(*,0),
        "BALANCE_LEVEL" NUMBER(*,0),
        "QUOTA_EXPIRY_TIME" TIMESTAMP (6),
        "BILLING_CYCLE_TOTAL_VOLUME" NUMBER(*,0),
        "BILLING_CYCLE_AVAIL_VOLUME" NUMBER(*,0),
        "BILLING_CYCLE_TOTAL_TIME" NUMBER(*,0),
        "BILLING_CYCLE_AVAIL_TIME" NUMBER(*,0),
        "DAILY_RESET_TIME" TIMESTAMP (6),
        "DAILY_VOLUME" NUMBER(*,0),
        "DAILY_TIME" NUMBER(*,0),
        "WEEKLY_RESET_TIME" TIMESTAMP (6),
        "WEEKLY_VOLUME" NUMBER(*,0),
        "WEEKLY_TIME" NUMBER(*,0),
        "RESERVATION_VOLUME" NUMBER(*,0),
        "RESERVATION_TIME" NUMBER(*,0),
        "LAST_UPDATE_TIME" TIMESTAMP (6),
        "START_TIME" TIMESTAMP (6),
        "STATUS" VARCHAR2(36),
        "RENEWAL_INTERVAL" VARCHAR2(36),
         CONSTRAINT "PK__DATA_BALANCE" PRIMARY KEY ("ID")
)PARTITION BY HASH(ID)                                         
( PARTITION PART_TBLM_DATA_BALANCE01       TABLESPACE TS_TBLM_DATA_BALANCE01,   
  PARTITION PART_TBLM_DATA_BALANCE02       TABLESPACE TS_TBLM_DATA_BALANCE02,
  PARTITION PART_TBLM_DATA_BALANCE03       TABLESPACE TS_TBLM_DATA_BALANCE03,
  PARTITION PART_TBLM_DATA_BALANCE04       TABLESPACE TS_TBLM_DATA_BALANCE04, 
  PARTITION PART_TBLM_DATA_BALANCE05       TABLESPACE TS_TBLM_DATA_BALANCE05,
  PARTITION PART_TBLM_DATA_BALANCE06       TABLESPACE TS_TBLM_DATA_BALANCE06, 
  PARTITION PART_TBLM_DATA_BALANCE07       TABLESPACE TS_TBLM_DATA_BALANCE07,
  PARTITION PART_TBLM_DATA_BALANCE08       TABLESPACE TS_TBLM_DATA_BALANCE08   
)NOLOGGING; 

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_DATA_BALANCE') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_DATA_BALANCE SELECT * FROM OLD_TBLM_DATA_BALANCE';

      COMMIT;
	  dbms_output.put_line('TBLM_DATA_BALANCE Data Imported');

	  END IF;
 END;
 /




--Disable Primary Key

ALTER TABLE TBLM_DATA_BALANCE DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

CREATE UNIQUE INDEX PK_DATA_BALANCE_PRT ON TBLM_DATA_BALANCE (ID)
GLOBAL PARTITION BY HASH (ID)  
(
  PARTITION PART_IDX_DATA_BALANCE01       TABLESPACE TS_IDX_DATA_BALANCE01,   
  PARTITION PART_IDX_DATA_BALANCE02       TABLESPACE TS_IDX_DATA_BALANCE02,
  PARTITION PART_IDX_DATA_BALANCE03       TABLESPACE TS_IDX_DATA_BALANCE03,
  PARTITION PART_IDX_DATA_BALANCE04       TABLESPACE TS_IDX_DATA_BALANCE04, 
  PARTITION PART_IDX_DATA_BALANCE05       TABLESPACE TS_IDX_DATA_BALANCE05,
  PARTITION PART_IDX_DATA_BALANCE06       TABLESPACE TS_IDX_DATA_BALANCE06, 
  PARTITION PART_IDX_DATA_BALANCE07       TABLESPACE TS_IDX_DATA_BALANCE07,
  PARTITION PART_IDX_DATA_BALANCE08       TABLESPACE TS_IDX_DATA_BALANCE08 
)INITRANS 169 NOLOGGING;   

--Enable Primary key
ALTER TABLE TBLM_DATA_BALANCE ENABLE PRIMARY KEY;


CREATE INDEX PPRT_DATA_BALANCE_SID ON TBLM_DATA_BALANCE (SUBSCRIBER_ID)
GLOBAL PARTITION BY HASH (SUBSCRIBER_ID)  
(
  PARTITION PART_IDX_DATA_BALANCE01       TABLESPACE TS_IDX_DATA_BALANCE01,   
  PARTITION PART_IDX_DATA_BALANCE02       TABLESPACE TS_IDX_DATA_BALANCE02,
  PARTITION PART_IDX_DATA_BALANCE03       TABLESPACE TS_IDX_DATA_BALANCE03,
  PARTITION PART_IDX_DATA_BALANCE04       TABLESPACE TS_IDX_DATA_BALANCE04, 
  PARTITION PART_IDX_DATA_BALANCE05       TABLESPACE TS_IDX_DATA_BALANCE05,
  PARTITION PART_IDX_DATA_BALANCE06       TABLESPACE TS_IDX_DATA_BALANCE06, 
  PARTITION PART_IDX_DATA_BALANCE07       TABLESPACE TS_IDX_DATA_BALANCE07,
  PARTITION PART_IDX_DATA_BALANCE08       TABLESPACE TS_IDX_DATA_BALANCE08 
)INITRANS 169 NOLOGGING; 


ALTER TABLE TBLM_DATA_BALANCE INITRANS 169;


 
prompt --------------------DDL Change Start for TBLM_MONETARY_BALANCE------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_MONETARY_BALANCE'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_MONETARY_BALANCE') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_MONETARY_BALANCE already exists, Drop OLD_TBLM_MONETARY_BALANCE object then execute DBC again');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_MONETARY_BALANCE TO OLD_TBLM_MONETARY_BALANCE';
	  dbms_output.put_line('TBLM_MONETARY_BALANCE Renamed');

	  END IF;
 END;
 /
 
 
CREATE TABLE "TBLM_MONETARY_BALANCE"
   (    "ID" VARCHAR2(36),
        "SUBSCRIBER_ID" VARCHAR2(255),
        "SERVICE_ID" VARCHAR2(36),
        "AVAILABLE_BALANCE" NUMBER(20,6),
        "INITIAL_BALANCE" NUMBER(20,6),
        "TOTAL_RESERVATION" NUMBER(20,6),
        "CREDIT_LIMIT" NUMBER(20,6),
        "NEXT_BILL_CYCLE_CREDIT_LIMIT" NUMBER(20,6),
        "CREDIT_LIMIT_UPDATE_TIME" TIMESTAMP (6),
        "VALID_FROM_DATE" TIMESTAMP (6),
        "VALID_TO_DATE" TIMESTAMP (6),
        "CURRENCY" VARCHAR2(5),
        "TYPE" VARCHAR2(32),
        "LAST_UPDATE_TIME" TIMESTAMP (6),
        "PARAM1" VARCHAR2(256),
        "PARAM2" VARCHAR2(256),
         CONSTRAINT "PK__MONITORY_BALANCE" PRIMARY KEY ("ID")
 )PARTITION BY HASH(ID)                                         
( PARTITION PART_TBLM_MONETARY_BALANCE01       TABLESPACE TS_TBLM_MONETARY_BALANCE01,   
  PARTITION PART_TBLM_MONETARY_BALANCE02       TABLESPACE TS_TBLM_MONETARY_BALANCE02,
  PARTITION PART_TBLM_MONETARY_BALANCE03       TABLESPACE TS_TBLM_MONETARY_BALANCE03,
  PARTITION PART_TBLM_MONETARY_BALANCE04       TABLESPACE TS_TBLM_MONETARY_BALANCE04, 
  PARTITION PART_TBLM_MONETARY_BALANCE05       TABLESPACE TS_TBLM_MONETARY_BALANCE05,
  PARTITION PART_TBLM_MONETARY_BALANCE06       TABLESPACE TS_TBLM_MONETARY_BALANCE06, 
  PARTITION PART_TBLM_MONETARY_BALANCE07       TABLESPACE TS_TBLM_MONETARY_BALANCE07,
  PARTITION PART_TBLM_MONETARY_BALANCE08       TABLESPACE TS_TBLM_MONETARY_BALANCE08   
)NOLOGGING; 

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_MONETARY_BALANCE') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_MONETARY_BALANCE SELECT * FROM OLD_TBLM_MONETARY_BALANCE';

      COMMIT;
	  dbms_output.put_line('TBLM_MONETARY_BALANCE Data Imported');

	  END IF;
 END;
 /


--Disable Primary Key

ALTER TABLE TBLM_MONETARY_BALANCE DISABLE PRIMARY KEY;

--Create Global Hash Unique Index

CREATE UNIQUE INDEX PK_MONITORY_BALANCE_PRT ON TBLM_MONETARY_BALANCE (ID)
GLOBAL PARTITION BY HASH (ID)  
(
  PARTITION PART_IDX_MONETARY_BALANCE01       TABLESPACE TS_IDX_MONETARY_BALANCE01,   
  PARTITION PART_IDX_MONETARY_BALANCE02       TABLESPACE TS_IDX_MONETARY_BALANCE02,
  PARTITION PART_IDX_MONETARY_BALANCE03       TABLESPACE TS_IDX_MONETARY_BALANCE03,
  PARTITION PART_IDX_MONETARY_BALANCE04       TABLESPACE TS_IDX_MONETARY_BALANCE04, 
  PARTITION PART_IDX_MONETARY_BALANCE05       TABLESPACE TS_IDX_MONETARY_BALANCE05,
  PARTITION PART_IDX_MONETARY_BALANCE06       TABLESPACE TS_IDX_MONETARY_BALANCE06, 
  PARTITION PART_IDX_MONETARY_BALANCE07       TABLESPACE TS_IDX_MONETARY_BALANCE07,
  PARTITION PART_IDX_MONETARY_BALANCE08       TABLESPACE TS_IDX_MONETARY_BALANCE08 
)INITRANS 169 NOLOGGING;   

--Enable Primary key
ALTER TABLE TBLM_MONETARY_BALANCE ENABLE PRIMARY KEY;


CREATE INDEX PPRT_MONETARY_BALANCE_SID ON TBLM_MONETARY_BALANCE (SUBSCRIBER_ID)
GLOBAL PARTITION BY HASH (SUBSCRIBER_ID)  
(
  PARTITION PART_IDX_MONETARY_BALANCE01       TABLESPACE TS_IDX_MONETARY_BALANCE01,   
  PARTITION PART_IDX_MONETARY_BALANCE02       TABLESPACE TS_IDX_MONETARY_BALANCE02,
  PARTITION PART_IDX_MONETARY_BALANCE03       TABLESPACE TS_IDX_MONETARY_BALANCE03,
  PARTITION PART_IDX_MONETARY_BALANCE04       TABLESPACE TS_IDX_MONETARY_BALANCE04, 
  PARTITION PART_IDX_MONETARY_BALANCE05       TABLESPACE TS_IDX_MONETARY_BALANCE05,
  PARTITION PART_IDX_MONETARY_BALANCE06       TABLESPACE TS_IDX_MONETARY_BALANCE06, 
  PARTITION PART_IDX_MONETARY_BALANCE07       TABLESPACE TS_IDX_MONETARY_BALANCE07,
  PARTITION PART_IDX_MONETARY_BALANCE08       TABLESPACE TS_IDX_MONETARY_BALANCE08 
)INITRANS 169 NOLOGGING; 

prompt --------------------DDL Change Start for TBLM_DATA_BALANCE_HISTORY------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_DATA_BALANCE_HISTORY'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_DATA_BALANCE_HISTORY') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_DATA_BALANCE_HISTORY already exists, Drop OLD_TBLM_DATA_BALANCE_HISTORY object then execute DBC again');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_DATA_BALANCE_HISTORY TO OLD_TBLM_DATA_BALANCE_HISTORY';
	  dbms_output.put_line('TBLM_DATA_BALANCE_HISTORY Renamed');

	  END IF;
 END;
 /


CREATE TABLE "TBLM_DATA_BALANCE_HISTORY"
   (    "ID" VARCHAR2(36),
        "SUBSCRIBER_ID" VARCHAR2(255),
        "PACKAGE_ID" VARCHAR2(36),
        "SUBSCRIPTION_ID" VARCHAR2(36),
        "QUOTA_PROFILE_ID" VARCHAR2(36),
        "PRODUCT_OFFER_ID" VARCHAR2(36),
        "DATA_SERVICE_TYPE_ID" NUMBER(*,0),
        "RATING_GROUP_ID" NUMBER(*,0),
        "BALANCE_LEVEL" NUMBER(*,0),
        "QUOTA_EXPIRY_TIME" TIMESTAMP (6),
        "BILLING_CYCLE_TOTAL_VOLUME" NUMBER(*,0),
        "BILLING_CYCLE_AVAIL_VOLUME" NUMBER(*,0),
        "BILLING_CYCLE_TOTAL_TIME" NUMBER(*,0),
        "BILLING_CYCLE_AVAIL_TIME" NUMBER(*,0),
        "DAILY_RESET_TIME" TIMESTAMP (6),
        "DAILY_VOLUME" NUMBER(*,0),
        "DAILY_TIME" NUMBER(*,0),
        "WEEKLY_RESET_TIME" TIMESTAMP (6),
        "WEEKLY_VOLUME" NUMBER(*,0),
        "WEEKLY_TIME" NUMBER(*,0),
        "RESERVATION_VOLUME" NUMBER(*,0),
        "RESERVATION_TIME" NUMBER(*,0),
        "LAST_UPDATE_TIME" TIMESTAMP (6),
        "START_TIME" TIMESTAMP (6),
        "STATUS" VARCHAR2(36),
        "RENEWAL_INTERVAL" VARCHAR2(36),
        "CREATE_DATE" TIMESTAMP (6)
)PARTITION BY RANGE (CREATE_DATE)
INTERVAL (NUMTODSINTERVAL(1,'day')) store in (TS_data_balance_hist_01,TS_data_balance_hist_02,TS_data_balance_hist_03,TS_data_balance_hist_04,TS_data_balance_hist_05,TS_data_balance_hist_06,TS_data_balance_hist_07,TS_data_balance_hist_08,TS_data_balance_hist_09,TS_data_balance_hist_10,TS_data_balance_hist_11,TS_data_balance_hist_12,TS_data_balance_hist_13,TS_data_balance_hist_14,TS_data_balance_hist_15)
(PARTITION p_first VALUES LESS THAN (TO_DATE('18-12-2013', 'DD-MM-YYYY')))
NOLOGGING;

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_DATA_BALANCE_HISTORY') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_DATA_BALANCE_HISTORY SELECT * FROM OLD_TBLM_DATA_BALANCE_HISTORY';

      COMMIT;
	  dbms_output.put_line('TBLM_DATA_BALANCE_HISTORY Data Imported');

	  END IF;
 END;
 /


ALTER TABLE TBLM_DATA_BALANCE_HISTORY INITRANS 169;



prompt --------------------DDL Change Start for TBLM_MONETARY_BALANCE_HISTORY------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OLD NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLM_MONETARY_BALANCE_HISTORY'),
         FUNC_CHECK_OBJ('TABLE','OLD_TBLM_MONETARY_BALANCE_HISTORY') INTO V_OBJ,V_OLD
  FROM DUAL;
    IF V_OLD > 0 THEN
      Raise_Application_Error(-20010,'Object OLD_TBLM_MONETARY_BALANCE_HISTORY already exists, Drop OLD_TBLM_MONETARY_BALANCE_HISTORY object then execute DBC again');
	  ELSIF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLM_MONETARY_BALANCE_HISTORY TO OLD_TBLM_MONETARY_BALANCE_HISTORY';
	  dbms_output.put_line('TBLM_MONETARY_BALANCE_HISTORY Renamed');

	  END IF;
 END;
 /

CREATE TABLE "TBLM_MONETARY_BALANCE_HISTORY"
   (    "ID" VARCHAR2(36),
        "SUBSCRIBER_ID" VARCHAR2(255),
        "SERVICE_ID" VARCHAR2(36),
        "AVAILABLE_BALANCE" NUMBER(20,6),
        "INITIAL_BALANCE" NUMBER(20,6),
        "TOTAL_RESERVATION" NUMBER(20,6),
        "CREDIT_LIMIT" NUMBER(20,6),
        "NEXT_BILL_CYCLE_CREDIT_LIMIT" NUMBER(20,6),
        "CREDIT_LIMIT_UPDATE_TIME" TIMESTAMP (6),
        "VALID_FROM_DATE" TIMESTAMP (6),
        "VALID_TO_DATE" TIMESTAMP (6),
        "CURRENCY" VARCHAR2(5),
        "TYPE" VARCHAR2(32),
        "LAST_UPDATE_TIME" TIMESTAMP (6),
        "PARAM1" VARCHAR2(256),
        "PARAM2" VARCHAR2(256),
        "CREATE_DATE" TIMESTAMP (6)
)PARTITION BY RANGE (CREATE_DATE)
INTERVAL (NUMTODSINTERVAL(1,'day')) store in (TS_monetary_balance_hist_01,TS_monetary_balance_hist_02,TS_monetary_balance_hist_03,TS_monetary_balance_hist_04,TS_monetary_balance_hist_05,TS_monetary_balance_hist_06,TS_monetary_balance_hist_07,TS_monetary_balance_hist_08,TS_monetary_balance_hist_09,TS_monetary_balance_hist_10,TS_monetary_balance_hist_11,TS_monetary_balance_hist_12,TS_monetary_balance_hist_13,TS_monetary_balance_hist_14,TS_monetary_balance_hist_15)
(PARTITION p_first VALUES LESS THAN (TO_DATE('18-12-2013', 'DD-MM-YYYY')))
NOLOGGING;

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLM_MONETARY_BALANCE_HISTORY') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLM_MONETARY_BALANCE_HISTORY SELECT * FROM OLD_TBLM_MONETARY_BALANCE_HISTORY';

      COMMIT;
	  dbms_output.put_line('TBLM_MONETARY_BALANCE_HISTORY Data Imported');

	  END IF;
 END;
 /

ALTER TABLE TBLM_MONETARY_BALANCE_HISTORY INITRANS 169;


prompt --------------------Backup DBC After Change-------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OUTPUT CLOB:= NULL;
  BEGIN
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES 
               WHERE TABLE_NAME IN ('TBLM_SUBSCRIBER','TBLT_SUBSCRIPTION','TBLT_USAGE','TBLT_SUBSCRIPTION_HISTORY','TBLT_USAGE_HISTORY','TBLM_DATA_BALANCE','TBLM_MONETARY_BALANCE','TBLM_DATA_BALANCE_HISTORY','TBLM_MONETARY_BALANCE_HISTORY') ORDER BY TABLE_NAME)
  LOOP
     FOR REC_T IN (select FUNC_CHECK_OBJ('TABLE',REC.TABLE_NAME) V_OBJ FROM DUAL)
	 LOOP
	  IF REC_T.V_OBJ > 0 THEN
	  dbms_output.put_line('-----------------DDL FOR TABLE----------------------------------');
	  select dbms_metadata.get_ddl('TABLE',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
	 END LOOP; 
	 
  FOR REC_I IN (select FUNC_CHECK_OBJ('INDEX',REC.TABLE_NAME) V_OBJ FROM DUAL)
  LOOP
      IF REC_I.V_OBJ >0 THEN
	  dbms_output.put_line('-----------------DDL FOR INDEX----------------------------------');
	  select dbms_metadata.get_dependent_ddl('INDEX',REC.TABLE_NAME) INTO V_OUTPUT from dual;
	  dbms_output.put_line(V_OUTPUT);	  
	  END IF;
  END LOOP;	  
  END LOOP;	  
END;
/

spool off;
exit;
