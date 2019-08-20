spool NETVERTEX_DBC_PARTITION_TBLTSYSTEMAUDIT.log
set serveroutput on
set timing on
set time on
SET long 1000000
set pagesize 0

prompt -------NETVERTEX-1975 DBC Enhancement for System Audit----------------------                                      
                                                                                                       
prompt -------create tablespaces for TBLTSYSTEMAUDIT---------                                      
                                                                                                       
prompt Enter the datafile location for tablespace of NETVERTEX - TABLE PARTITIONING OF TBLTSYSTEMAUDIT ===>
                                                                                                       
DEFINE db_datafile="&&dbf_storage_directory"                                                           
prompt &db_datafile                                                                                    

CREATE TABLESPACE TBS_TBLTSYSTEMAUDIT01 DATAFILE '&&db_datafile/TBS_TBLTSYSTEMAUDIT01.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLTSYSTEMAUDIT02 DATAFILE '&&db_datafile/TBS_TBLTSYSTEMAUDIT02.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLTSYSTEMAUDIT03 DATAFILE '&&db_datafile/TBS_TBLTSYSTEMAUDIT03.dbf' size 100M AUTOEXTEND ON NEXT 25M;
CREATE TABLESPACE TBS_TBLTSYSTEMAUDIT04 DATAFILE '&&db_datafile/TBS_TBLTSYSTEMAUDIT04.dbf' size 100M AUTOEXTEND ON NEXT 25M;
                                                                                                                           
CREATE TABLESPACE IDX_TBLTSYSTEMAUDIT01 DATAFILE '&&db_datafile/IDX_TBLTSYSTEMAUDIT01.dbf' size 100M AUTOEXTEND ON NEXT 25M;

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
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME IN ('TBLTSYSTEMAUDIT') ORDER BY TABLE_NAME)
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

prompt --------------------DDL Change Start for TBLTSYSTEMAUDIT-------------------


DECLARE
  V_OBJ NUMERIC:=0;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','TBLTSYSTEMAUDIT') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  execute immediate 'RENAME TBLTSYSTEMAUDIT TO OLD_TBLTSYSTEMAUDIT';
	  dbms_output.put_line('TBLTSYSTEMAUDIT Renamed');

	  END IF;
 END;
 /  

-- Table Partition

CREATE TABLE TBLTSYSTEMAUDIT 
   (SYSTEMAUDITID NUMERIC(20), 
	ACTIONID CHAR(8)		NOT NULL, 
	TRANSACTIONID CHAR(12), 
	AUDITDATE DATE			NOT NULL, 
	REMARK VARCHAR(4000), 
	SYSTEMUSERID NUMERIC(20)	NOT NULL, 
	SYSTEMUSERNAME VARCHAR(60) 	NOT NULL,
	CLIENTIP 	VARCHAR(64),
	CONSTRAINT PKB_TSYSTEMAUDIT PRIMARY KEY (SYSTEMAUDITID),
	CONSTRAINT FKB_TBLTSYSTEMAUDIT FOREIGN KEY (SYSTEMUSERID) REFERENCES TBLMSTAFF (STAFFID)
   )
PARTITION BY RANGE (AUDITDATE) INTERVAL (NUMTOYMINTERVAL(1,'MONTH')) STORE IN
 (TBS_TBLTSYSTEMAUDIT01,TBS_TBLTSYSTEMAUDIT02,TBS_TBLTSYSTEMAUDIT03,TBS_TBLTSYSTEMAUDIT04)
 (PARTITION P_FIRST  VALUES LESS THAN (TIMESTAMP' 2016-11-16 00:00:00')) NOLOGGING;

DECLARE
  V_OBJ NUMERIC:=NULL;
  BEGIN
  select FUNC_CHECK_OBJ('TABLE','OLD_TBLTSYSTEMAUDIT') INTO V_OBJ
  FROM DUAL;
	  IF V_OBJ > 0 THEN
	  
	  execute immediate 'INSERT INTO TBLTSYSTEMAUDIT SELECT * FROM OLD_TBLTSYSTEMAUDIT';

      COMMIT;
	  dbms_output.put_line('TBLTSYSTEMAUDIT Data Imported');

	  END IF;
 END;
 /
 
 SELECT COUNT(*) CNT_OF_TBLTSYSTEMAUDIT FROM TBLTSYSTEMAUDIT;

--Indexes 
 ALTER TABLE TBLTSYSTEMAUDIT DISABLE PRIMARY KEY;
 
 CREATE INDEX IDX_PK_SYSTEMAUDIT ON TBLTSYSTEMAUDIT(AUDITDATE) TABLESPACE IDX_TBLTSYSTEMAUDIT01 INITRANS 169 NOLOGGING;
 
 ALTER TABLE TBLTSYSTEMAUDIT ENABLE PRIMARY KEY;

 ALTER TABLE TBLTSYSTEMAUDIT INITRANS 169;

/*
 -- Data retention by default 365 days  
 create or replace function get_high_value_as_date(
  p_table_name     in varchar2,
  p_partition_name in varchar2
) return date as
  v_high_value varchar2(1024);
  v_date        date;
begin
  select high_value into v_high_value from user_tab_partitions
    where table_name = upper(p_table_name)
      and partition_name = upper(p_partition_name);
  execute immediate 'select ' || v_high_value || ' from dual' into v_date;
  return v_date;
end;
/

 create or replace procedure SP_PURGE_TBLTSYSTEMAUDIT
 as
   c_days_to_keep constant integer := 365;
   x_last_partition exception;
   pragma exception_init(x_last_partition, -14758);
 begin
   for rec in (select table_name, partition_name
     from user_tab_partitions
       where table_name = 'TBLTSYSTEMAUDIT' and partition_name != 'P_FIRST'
         and get_high_value_as_date(table_name, partition_name) <
         sysdate - c_days_to_keep) loop
     begin
      dbms_output.put_line('alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes');
       execute immediate 'alter table ' || rec.table_name || ' drop partition ' || rec.partition_name ||' update indexes';
     exception
       when x_last_partition then
         null;
     end;
   end loop;
 end;
 /
*/

prompt --------------------DDL Change End for TBLTSYSTEMAUDIT--------------------------

prompt --------------------Backup DBC After Change-------------------------------------

DECLARE
  V_OBJ NUMERIC:=0;
  V_OUTPUT CLOB:= NULL;
  BEGIN
  
  FOR REC  IN (SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME IN ('TBLTSYSTEMAUDIT') ORDER BY TABLE_NAME)
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

 

   