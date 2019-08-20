spool NETVERTEX_6.4.8_UTILITY_TO_EXPORT_JSON_v4.log
remark ========NETVERTEX-1773 SPR Migration Support from NetVertex 6.4.8 to NetVertex v6.8.x============

SET SERVEROUTPUT ON
SET TIMING ON
SET TIME ON
prompt "Creatint Logical Directory where you want to generate JSON Dump"
CREATE DIRECTORY DB_JSON AS '&location';

prompt "Enter the username of NetVertex Application"
GRANT READ,WRITE ON DIRECTORY DB_JSON TO &USERNAME;
grant analyze any to &&USERNAME;

prompt "Creatint Tablespace for Migration Staging Table"
DEFINE db_datafile="&&dbf_storage_directory"
prompt &db_datafile
create tablespace tbs_proc1 DATAFILE '&&db_datafile/tbs_proc1.dbf' size 100M AUTOEXTEND ON NEXT 25M;
create tablespace tbs_proc2 DATAFILE '&&db_datafile/tbs_proc2.dbf' size 100M AUTOEXTEND ON NEXT 25M;
create tablespace tbs_proc3 DATAFILE '&&db_datafile/tbs_proc3.dbf' size 100M AUTOEXTEND ON NEXT 25M;
create tablespace tbs_proc4 DATAFILE '&&db_datafile/tbs_proc4.dbf' size 100M AUTOEXTEND ON NEXT 25M;

prompt "Enter the Username and Password of NetVertex Application"
connect &USERNAME/&PASSWORD

Prompt "Create the Migration Staging table"
--truncate table TBL_CONV_SPR;
--drop INDEX IDX_CONV_SPR;

  CREATE TABLE TBL_CONV_SPR (
   SPRID NUMBER,
   SUBSCRIBERID varchar2(255), 
   STATUS char(1)
   ) 
PARTITION BY RANGE(SPRID) (
  partition P1 values less than (150000)     tablespace tbs_proc1, 
  partition P2 values less than (300000)     tablespace tbs_proc2, 
  partition P3 values less than (450000)     tablespace tbs_proc3, 
  partition P4 values less than (550000)     tablespace tbs_proc4  
) INITRANS 169; 


prompt "Creating package for PKG_PCRF_ORA_JSON_EXPORT"

create or replace PACKAGE PKG_PCRF_ORA_JSON_EXPORT AS

  /* TODO enter package declarations (types, exceptions, methods etc) here */
    /*  ELITECSM NETVERTEX USAGE MODULE
        ELITECORE TECHNOLOGIES PVT. LTD.

        Product Jira: NETVERTEX-1773 SPR Migration Support from NetVertex 6.4.8 to NetVertex v6.6.x/v6.8.x
        Date:12/05/2016
    */

   FUNCTION CONVERT_INTO_JSON_SPR(QUERY_FUNC VARCHAR2) RETURN CLOB;
   FUNCTION CONVERT_INTO_JSON_ADDON(QUERY_FUNC VARCHAR2) RETURN CLOB;
   FUNCTION CONVERT_INTO_JSON_BASEPKG(QUERY_FUNC VARCHAR2) RETURN CLOB;

   FUNCTION FUNC_GET_DATA_SPR(P_SUBSCRIBERID varchar2) RETURN SYS_REFCURSOR;
   FUNCTION FUNC_GET_DATA_ADDON(P_SUBSCRIBERID varchar2) RETURN SYS_REFCURSOR;
   FUNCTION FUNC_GET_DATA_BASEPKG(P_SUBSCRIBERID varchar2) RETURN SYS_REFCURSOR;

   FUNCTION FUNC_GET_DATA_MAIN(P_SUBSCRIBERID varchar2) RETURN clob;
   PROCEDURE PROC_GEN_JSON_DUMP_P1;
   PROCEDURE PROC_GEN_JSON_DUMP_P2;
   PROCEDURE PROC_GEN_JSON_DUMP_P3;
   PROCEDURE PROC_GEN_JSON_DUMP_P4;
   PROCEDURE PROC_LOAD_CID;

END PKG_PCRF_ORA_JSON_EXPORT;
/

create or replace PACKAGE BODY PKG_PCRF_ORA_JSON_EXPORT AS

    /*  ELITECSM NETVERTEX USAGE MODULE
        ELITECORE TECHNOLOGIES PVT. LTD.

        Product Jira: NETVERTEX-1773 SPR Migration Support from NetVertex 6.4.8 to NetVertex v6.6.x/v6.8.x
        Date:12/05/2016
    */

  FUNCTION CONVERT_INTO_JSON_SPR(QUERY_FUNC VARCHAR2) RETURN CLOB AS
  QUERY 		VARCHAR2(250);
	DATA_HEADER 	CLOB;
	CURSOR_DESC 	NUMBER;
 	COL_NUM   	NUMBER;
	ROW_DATA 	VARCHAR2(100);
	DATA_XML 	XMLTYPE;
	HTMLOUTPUT 	XMLTYPE;
	DATA_XSL 	LONG;
	MAIN_DATA 	CLOB;
	QUERY_FUNC_ACT  SYS_REFCURSOR;
	DESC_TAB  	dbms_sql.desc_tab2;
	CONTENT 	dbms_xmlgen.ctxhandle;
  BEGIN
    -- TODO: Implementation required for FUNCTION PKG_PCRF_ORA_JSON_EXPORT.CONVERT_INTO_JSON_SPR

    QUERY := 'SELECT ' || QUERY_FUNC || ' FROM DUAL';

        EXECUTE IMMEDIATE QUERY
                INTO QUERY_FUNC_ACT;

   	DATA_HEADER:= '{ "Header" :[';
        CURSOR_DESC   := dbms_sql.to_cursor_number(QUERY_FUNC_ACT);

        dbms_sql.describe_columns2(CURSOR_DESC
                                  ,COL_NUM
                                  ,DESC_TAB);

        FOR i IN 1 .. COL_NUM
        LOOP
                CASE
                        WHEN DESC_TAB(i).col_type IN (2
                                         ,8) THEN
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"number"},';
                        WHEN DESC_TAB(i).col_type = 12 THEN
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"date"},';
                        ELSE
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"text"},';
                END CASE;
                dbms_lob.writeappend(DATA_HEADER
                                    ,length(ROW_DATA)
                                    ,ROW_DATA);
        END LOOP;
        DATA_HEADER := rtrim(DATA_HEADER
                              ,',') || '],"Data":';

	EXECUTE IMMEDIATE QUERY INTO QUERY_FUNC_ACT;

	DATA_HEADER := '"SPR":';

	CONTENT := dbms_xmlgen.newcontext(QUERY_FUNC_ACT);
        dbms_xmlgen.setnullhandling(CONTENT,1);
       	DATA_XML := dbms_xmlgen.getxmltype(CONTENT,dbms_xmlgen.none);

       	DATA_XSL := '<?xml version="1.0" encoding="ISO-8859-1"?>
	<xsl:stylesheet version="1.0"
	  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:template match="/">[<xsl:for-each select="/ROWSET/*">{<xsl:for-each select="./*">"<xsl:value-of select="name()"/>":"<xsl:value-of select="text()"/>"<xsl:choose><xsl:when test="position()!= last()">,</xsl:when></xsl:choose></xsl:for-each>}<xsl:choose><xsl:when test="position() != last()">,</xsl:when></xsl:choose></xsl:for-each>]</xsl:template></xsl:stylesheet>';

        HTMLOUTPUT := DATA_XML.transform(XMLTYPE(DATA_XSL));
        MAIN_DATA  := HTMLOUTPUT.getclobval();
        MAIN_DATA  := REPLACE(MAIN_DATA,'_x0020_',' ');
        dbms_lob.writeappend(DATA_HEADER,length(MAIN_DATA),MAIN_DATA);
 

	RETURN  DATA_HEADER;
   EXCEPTION
        WHEN OTHERS THEN      
                RETURN NULL;

  END CONVERT_INTO_JSON_SPR;

  FUNCTION CONVERT_INTO_JSON_ADDON(QUERY_FUNC VARCHAR2) RETURN CLOB AS
  QUERY 		VARCHAR2(250);
	DATA_HEADER 	CLOB;
	CURSOR_DESC 	NUMBER;
 	COL_NUM   	NUMBER;
	ROW_DATA 	VARCHAR2(100);
	DATA_XML 	XMLTYPE;
	HTMLOUTPUT 	XMLTYPE;
	DATA_XSL 	LONG;
	MAIN_DATA 	CLOB;
	QUERY_FUNC_ACT  SYS_REFCURSOR;
	DESC_TAB  	dbms_sql.desc_tab2;
	CONTENT 	dbms_xmlgen.ctxhandle;
  BEGIN
    -- TODO: Implementation required for FUNCTION PKG_PCRF_ORA_JSON_EXPORT.CONVERT_INTO_JSON_ADDON
       QUERY := 'SELECT ' || QUERY_FUNC || ' FROM DUAL';

        EXECUTE IMMEDIATE QUERY
                INTO QUERY_FUNC_ACT;

   	DATA_HEADER:= '{ "Header" :[';
        CURSOR_DESC   := dbms_sql.to_cursor_number(QUERY_FUNC_ACT);

        dbms_sql.describe_columns2(CURSOR_DESC
                                  ,COL_NUM
                                  ,DESC_TAB);

        FOR i IN 1 .. COL_NUM
        LOOP
                CASE
                        WHEN DESC_TAB(i).col_type IN (2
                                         ,8) THEN
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"number"},';
                        WHEN DESC_TAB(i).col_type = 12 THEN
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"date"},';
                        ELSE
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"text"},';
                END CASE;
                dbms_lob.writeappend(DATA_HEADER
                                    ,length(ROW_DATA)
                                    ,ROW_DATA);
        END LOOP;
        DATA_HEADER := rtrim(DATA_HEADER
                              ,',') || '],"Data":';

	EXECUTE IMMEDIATE QUERY INTO QUERY_FUNC_ACT;

	DATA_HEADER := '"SUBSCRIPTION ADDONS":';

	CONTENT := dbms_xmlgen.newcontext(QUERY_FUNC_ACT);
        dbms_xmlgen.setnullhandling(CONTENT,1);
       	DATA_XML := dbms_xmlgen.getxmltype(CONTENT,dbms_xmlgen.none);

       	DATA_XSL := '<?xml version="1.0" encoding="ISO-8859-1"?>
	<xsl:stylesheet version="1.0"
	  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/><xsl:template match="/">[<xsl:for-each select="/ROWSET/*">{<xsl:for-each select="./*">"<xsl:value-of select="name()"/>":"<xsl:value-of select="text()"/>"<xsl:choose><xsl:when test="position()!= last()">,</xsl:when> </xsl:choose></xsl:for-each>}<xsl:choose><xsl:when test="position() != last()">,</xsl:when></xsl:choose></xsl:for-each>]</xsl:template></xsl:stylesheet>';

        HTMLOUTPUT := DATA_XML.transform(XMLTYPE(DATA_XSL));
        MAIN_DATA  := HTMLOUTPUT.getclobval();
        MAIN_DATA  := REPLACE(MAIN_DATA,'_x0020_',' ');
        dbms_lob.writeappend(DATA_HEADER,length(MAIN_DATA),MAIN_DATA);

	RETURN  DATA_HEADER;
   EXCEPTION
        WHEN OTHERS THEN      
                RETURN NULL;


  END CONVERT_INTO_JSON_ADDON;

  FUNCTION CONVERT_INTO_JSON_BASEPKG(QUERY_FUNC VARCHAR2) RETURN CLOB AS
  QUERY 		VARCHAR2(250);
	DATA_HEADER 	CLOB;
	CURSOR_DESC 	NUMBER;
 	COL_NUM   	NUMBER;
	ROW_DATA 	VARCHAR2(100);
	DATA_XML 	XMLTYPE;
	HTMLOUTPUT 	XMLTYPE;
	DATA_XSL 	LONG;
	MAIN_DATA 	CLOB;
	QUERY_FUNC_ACT  SYS_REFCURSOR;
	DESC_TAB  	dbms_sql.desc_tab2;
	CONTENT 	dbms_xmlgen.ctxhandle;
  BEGIN
    -- TODO: Implementation required for FUNCTION PKG_PCRF_ORA_JSON_EXPORT.CONVERT_INTO_JSON_BASEPKG
        QUERY := 'SELECT ' || QUERY_FUNC || ' FROM DUAL';

        EXECUTE IMMEDIATE QUERY
                INTO QUERY_FUNC_ACT;

   	DATA_HEADER:= '{ "Header" :[';
        CURSOR_DESC   := dbms_sql.to_cursor_number(QUERY_FUNC_ACT);

        dbms_sql.describe_columns2(CURSOR_DESC
                                  ,COL_NUM
                                  ,DESC_TAB);

        FOR i IN 1 .. COL_NUM
        LOOP
                CASE
                        WHEN DESC_TAB(i).col_type IN (2
                                         ,8) THEN
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"number"},';
                        WHEN DESC_TAB(i).col_type = 12 THEN
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"date"},';
                        ELSE
                                ROW_DATA := '{"name":"' || DESC_TAB(i)
                                             .col_name || '","type":"text"},';
                END CASE;
                dbms_lob.writeappend(DATA_HEADER
                                    ,length(ROW_DATA)
                                    ,ROW_DATA);
        END LOOP;
        DATA_HEADER := rtrim(DATA_HEADER
                              ,',') || '],"Data":';

	EXECUTE IMMEDIATE QUERY INTO QUERY_FUNC_ACT;

	DATA_HEADER := ' "SUBSCRIPTION BASE PACKAGE":';

	CONTENT := dbms_xmlgen.newcontext(QUERY_FUNC_ACT);
        dbms_xmlgen.setnullhandling(CONTENT,1);
       	DATA_XML := dbms_xmlgen.getxmltype(CONTENT,dbms_xmlgen.none);

       	DATA_XSL := '<?xml version="1.0" encoding="ISO-8859-1"?>
	<xsl:stylesheet version="1.0"
	  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/><xsl:template match="/">[<xsl:for-each select="/ROWSET/*">{<xsl:for-each select="./*">"<xsl:value-of select="name()"/>":"<xsl:value-of select="text()"/>"<xsl:choose><xsl:when test="position()!= last()">,</xsl:when></xsl:choose></xsl:for-each>}<xsl:choose><xsl:when test="position() != last()">,</xsl:when></xsl:choose></xsl:for-each>]</xsl:template></xsl:stylesheet>';

        HTMLOUTPUT := DATA_XML.transform(XMLTYPE(DATA_XSL));
        MAIN_DATA  := HTMLOUTPUT.getclobval();
        MAIN_DATA  := REPLACE(MAIN_DATA,'_x0020_',' ');
        dbms_lob.writeappend(DATA_HEADER,length(MAIN_DATA),MAIN_DATA);

	RETURN  DATA_HEADER;
   EXCEPTION
        WHEN OTHERS THEN      
                RETURN NULL;
                
  END CONVERT_INTO_JSON_BASEPKG;

  FUNCTION FUNC_GET_DATA_SPR(P_SUBSCRIBERID varchar2) RETURN SYS_REFCURSOR AS
  RESULT_CURSOR SYS_REFCURSOR;
  BEGIN
    -- TODO: Implementation required for FUNCTION PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_SPR
     OPEN RESULT_CURSOR FOR SELECT * FROM TBLNETVERTEXCUSTOMER C WHERE C.SUBSCRIBERIDENTITY = P_SUBSCRIBERID;
     RETURN RESULT_CURSOR;
     CLOSE RESULT_CURSOR;
  END FUNC_GET_DATA_SPR;

  FUNCTION FUNC_GET_DATA_ADDON(P_SUBSCRIBERID varchar2) RETURN SYS_REFCURSOR AS
      RESULT_CURSOR SYS_REFCURSOR;      
  BEGIN    
    -- TODO: Implementation required for FUNCTION PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_ADDON
    --declare v_cnt number;
        
    OPEN RESULT_CURSOR FOR     SELECT * FROM TBLMADDONSUBSCRIBERREL R,(SELECT ADDONID,NAME FROM TBLMADDON)A,TBLMSESSIONUSAGESUMMARY U
    WHERE A.ADDONID = R.ADDONID
    AND R.SUBSCRIBERID = P_SUBSCRIBERID
    AND U.USERID  = R.SUBSCRIBERID
    AND AGGREGATEKEY = A.NAME||'-'||R.ADDONSUBSCRIPTIONID||'-BILLING_CYCLE';
    
    RETURN RESULT_CURSOR;
    CLOSE RESULT_CURSOR;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
  NULL;
  
  END FUNC_GET_DATA_ADDON;

  FUNCTION FUNC_GET_DATA_BASEPKG(P_SUBSCRIBERID varchar2) RETURN SYS_REFCURSOR AS
  RESULT_CURSOR SYS_REFCURSOR;
  BEGIN
    -- TODO: Implementation required for FUNCTION PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_BASEPKG
   
    OPEN RESULT_CURSOR FOR SELECT * FROM (SELECT POLICYGROUPID,NAME FROM TBLMPOLICYGROUP) P,TBLMSESSIONUSAGESUMMARY U
    WHERE U.USERID = P_SUBSCRIBERID
    AND U.AGGREGATEKEY = P.NAME||'-BILLING_CYCLE';
    
   
    RETURN RESULT_CURSOR;
    CLOSE RESULT_CURSOR;
  END FUNC_GET_DATA_BASEPKG;

  FUNCTION FUNC_GET_DATA_MAIN(P_SUBSCRIBERID varchar2) RETURN  clob AS
  Result1  clob;
  BEGIN   
    -- TODO: Implementation required for FUNCTION PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_MAIN
  
   
 SELECT  replace(ltrim(RTRIM(replace('{'
      ||PKG_PCRF_ORA_JSON_EXPORT.CONVERT_INTO_JSON_SPR('PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_SPR('||CONCAT(CONCAT('''',P_SUBSCRIBERID),'''') ||')')
      ||','
      ||PKG_PCRF_ORA_JSON_EXPORT.CONVERT_INTO_JSON_ADDON('PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_ADDON('||CONCAT(CONCAT('''',P_SUBSCRIBERID),'''') ||')')
      ||','
      ||PKG_PCRF_ORA_JSON_EXPORT.CONVERT_INTO_JSON_BASEPKG('PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_BASEPKG('||CONCAT(CONCAT('''',P_SUBSCRIBERID),'''') ||')')
      ||'}', ',,', ','),','),','),',}','}')
      INTO RESULT1
    FROM dual;
   
    return Result1;
END FUNC_GET_DATA_MAIN;
PROCEDURE PROC_LOAD_CID
AS
BEGIN
  FOR REC IN (SELECT SPRID,SUBSCRIBERIDENTITY FROM TBLNETVERTEXCUSTOMER ORDER BY SPRID)
  LOOP
  
    INSERT INTO TBL_CONV_SPR VALUES (REC.SPRID,REC.SUBSCRIBERIDENTITY,'N');
    COMMIT;
  
  END LOOP;
END PROC_LOAD_CID;

PROCEDURE PROC_GEN_JSON_DUMP_P1
   AS
   F UTL_FILE.FILE_TYPE;   
   BEGIN

    --F := UTL_FILE.FOPEN('DB_DUMO','GT_CUSTOMERS.CSV','w',32767);
    F := UTL_FILE.FOPEN('DB_JSON','GT_CUSTOMERS_P1.CSV','A',32767);
    FOR C1_R IN (SELECT SUBSCRIBERID FROM TBL_CONV_SPR PARTITION (P1) WHERE STATUS = 'N' AND ROWNUM <= 2000)
    LOOP
       
        UPDATE TBL_CONV_SPR SET STATUS= 'I' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        COMMIT;
        
        UTL_FILE.PUT(F,PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_MAIN(C1_R.SUBSCRIBERID) );
        
        UTL_FILE.NEW_LINE(F);
        
        
        UPDATE TBL_CONV_SPR SET STATUS= 'Y' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        
        COMMIT;
    END LOOP;
    UTL_FILE.FCLOSE(F);
        
          
   END PROC_GEN_JSON_DUMP_P1;        
   
   PROCEDURE PROC_GEN_JSON_DUMP_P2
   AS
   F UTL_FILE.FILE_TYPE;   
   BEGIN

    --F := UTL_FILE.FOPEN('DB_DUMO','GT_CUSTOMERS.CSV','w',32767);
    F := UTL_FILE.FOPEN('DB_JSON','GT_CUSTOMERS_P2.CSV','A',32767);
    FOR C1_R IN (SELECT SUBSCRIBERID FROM TBL_CONV_SPR PARTITION (P2) WHERE STATUS = 'N' AND ROWNUM <= 2000)
    LOOP
       
        UPDATE TBL_CONV_SPR SET STATUS= 'I' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        COMMIT;
        
        UTL_FILE.PUT(F,PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_MAIN(C1_R.SUBSCRIBERID) );
        
        UTL_FILE.NEW_LINE(F);
        
        
        UPDATE TBL_CONV_SPR SET STATUS= 'Y' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        
        COMMIT;
    END LOOP;
    UTL_FILE.FCLOSE(F);
        
          
   END PROC_GEN_JSON_DUMP_P2;        
   PROCEDURE PROC_GEN_JSON_DUMP_P3
   AS
   F UTL_FILE.FILE_TYPE;   
   BEGIN

    --F := UTL_FILE.FOPEN('DB_DUMO','GT_CUSTOMERS.CSV','w',32767);
    F := UTL_FILE.FOPEN('DB_JSON','GT_CUSTOMERS_P3.CSV','A',32767);
    FOR C1_R IN (SELECT SUBSCRIBERID FROM TBL_CONV_SPR PARTITION (P3) WHERE STATUS = 'N' AND ROWNUM <= 2000)
    LOOP
       
        UPDATE TBL_CONV_SPR SET STATUS= 'I' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        COMMIT;
        
        UTL_FILE.PUT(F,PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_MAIN(C1_R.SUBSCRIBERID) );
        
        UTL_FILE.NEW_LINE(F);
        
        
        UPDATE TBL_CONV_SPR SET STATUS= 'Y' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        
        COMMIT;
    END LOOP;
    UTL_FILE.FCLOSE(F);
        
          
   END PROC_GEN_JSON_DUMP_P3;
   PROCEDURE PROC_GEN_JSON_DUMP_P4
   AS
   F UTL_FILE.FILE_TYPE;   
   BEGIN

    --F := UTL_FILE.FOPEN('DB_DUMO','GT_CUSTOMERS.CSV','w',32767);
    F := UTL_FILE.FOPEN('DB_JSON','GT_CUSTOMERS_P4.CSV','A',32767);
    FOR C1_R IN (SELECT SUBSCRIBERID FROM TBL_CONV_SPR PARTITION (P4) WHERE STATUS = 'N' AND ROWNUM <= 2000)
    LOOP
       
        UPDATE TBL_CONV_SPR SET STATUS= 'I' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        COMMIT;
        
        UTL_FILE.PUT(F,PKG_PCRF_ORA_JSON_EXPORT.FUNC_GET_DATA_MAIN(C1_R.SUBSCRIBERID) );
        
        UTL_FILE.NEW_LINE(F);
        
        
        UPDATE TBL_CONV_SPR SET STATUS= 'Y' WHERE SUBSCRIBERID = C1_R.SUBSCRIBERID;                
        
        COMMIT;
    END LOOP;
    UTL_FILE.FCLOSE(F);
        
          
   END PROC_GEN_JSON_DUMP_P4;
 
END PKG_PCRF_ORA_JSON_EXPORT;
/

Prompt " ----------------------------Start the Migration Process........................................... "

Prompt " Add SPRID COLUMN in SPR Table"
ALTER TABLE TBLNETVERTEXCUSTOMER ADD (SPRID NUMBER);

Prompt " Update the SPRID Column using unique number"
UPDATE TBLNETVERTEXCUSTOMER SET SPRID = rownum;
COMMIT;

begin 
  	  	DBMS_STATS.GATHER_TABLE_STATS (
  	  	  ownname => '"&&USERNAME"',
          tabname => '"TBLNETVERTEXCUSTOMER"',
          cascade => true
          );
          end;
/


Prompt "Dump the Live SPR Table in Staging Table"
EXEC PKG_PCRF_ORA_JSON_EXPORT.PROC_LOAD_CID;

SELECT COUNT(*) CNT_OF_STAGING_TBL FROM TBL_CONV_SPR;

SELECT COUNT(*) CNT_OF_LIVE_SPR FROM TBLNETVERTEXCUSTOMER;

Prompt "Optimize the staging table"
CREATE INDEX IDX_CONV_SPR ON TBL_CONV_SPR(SUBSCRIBERID) INITRANS 169;

begin 
  	  	DBMS_STATS.GATHER_TABLE_STATS (
  	  	  ownname => '"&&USERNAME"',
          tabname => '"TBL_CONV_SPR"',
          cascade => true
          );
          end;
/

Prompt "Creating the Parallel Proceese for perform the Migration P1..P4"
BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   JOB_NAME           =>  'SP_GEN_JSON_DUMP_01',
   JOB_TYPE           =>  'STORED_PROCEDURE',
   JOB_ACTION         =>  'PKG_PCRF_ORA_JSON_EXPORT.PROC_GEN_JSON_DUMP_P1',
   START_DATE         =>  SYSTIMESTAMP,
   REPEAT_INTERVAL    =>  'FREQ=MINUTELY;INTERVAL=1;',   
   END_DATE           =>   NULL,
   ENABLED            =>   TRUE,
   COMMENTS           =>  'SP_GEN_JSON_DUMP_01....');
END;
/

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   JOB_NAME           =>  'SP_GEN_JSON_DUMP_02',
   JOB_TYPE           =>  'STORED_PROCEDURE',
   JOB_ACTION         =>  'PKG_PCRF_ORA_JSON_EXPORT.PROC_GEN_JSON_DUMP_P2',
   START_DATE         =>  SYSTIMESTAMP,
   REPEAT_INTERVAL    =>  'FREQ=MINUTELY;INTERVAL=1;',   
   END_DATE           =>   NULL,
   ENABLED            =>   TRUE,
   COMMENTS           =>  'SP_GEN_JSON_DUMP_02....');
END;
/

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   JOB_NAME           =>  'SP_GEN_JSON_DUMP_03',
   JOB_TYPE           =>  'STORED_PROCEDURE',
   JOB_ACTION         =>  'PKG_PCRF_ORA_JSON_EXPORT.PROC_GEN_JSON_DUMP_P3',
   START_DATE         =>  SYSTIMESTAMP,
   REPEAT_INTERVAL    =>  'FREQ=MINUTELY;INTERVAL=1;',   
   END_DATE           =>   NULL,
   ENABLED            =>   TRUE,
   COMMENTS           =>  'SP_GEN_JSON_DUMP_03....');
END;
/

BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   JOB_NAME           =>  'SP_GEN_JSON_DUMP_04',
   JOB_TYPE           =>  'STORED_PROCEDURE',
   JOB_ACTION         =>  'PKG_PCRF_ORA_JSON_EXPORT.PROC_GEN_JSON_DUMP_P4',
   START_DATE         =>  SYSTIMESTAMP,
   REPEAT_INTERVAL    =>  'FREQ=MINUTELY;INTERVAL=1;',   
   END_DATE           =>   NULL,
   ENABLED            =>   TRUE,
   COMMENTS           =>  'SP_GEN_JSON_DUMP_04....');
END;
/
Prompt ".............................Migration Configuration is Finished ......................................"

Prompt "Migration Monitoring Commands"
create view VW_MGR_STATUS
AS
SELECT DECODE(STATUS,'N','P1_PENDING','Y','P1_DONE') PROCESS ,COUNT(*) CNT FROM TBL_CONV_SPR partition (p1) GROUP BY STATUS
UNION ALL
SELECT DECODE(STATUS,'N','P2_PENDING','Y','P2_DONE') PROCESS ,COUNT(*) CNT FROM TBL_CONV_SPR partition (p2) GROUP BY STATUS
UNION ALL
SELECT DECODE(STATUS,'N','P3_PENDING','Y','P3_DONE') PROCESS ,COUNT(*) CNT FROM TBL_CONV_SPR partition (p3) GROUP BY STATUS
UNION ALL
SELECT DECODE(STATUS,'N','P4_PENDING','Y','P4_DONE') PROCESS ,COUNT(*) CNT FROM TBL_CONV_SPR partition (p4) GROUP BY STATUS;

prompt "SELECT * FROM VW_MGR_STATUS;"
SELECT * FROM VW_MGR_STATUS;

prompt "End of Migration"
/* -- This is comment 
When migration proceessed is completed then please disable the database scheduler using below commands

BEGIN
      DBMS_SCHEDULER.disable(name=>'"RM"."SP_GEN_JSON_DUMP_01"');
      DBMS_SCHEDULER.disable(name=>'"RM"."SP_GEN_JSON_DUMP_02"');
      DBMS_SCHEDULER.disable(name=>'"RM"."SP_GEN_JSON_DUMP_03"');
	  DBMS_SCHEDULER.disable(name=>'"RM"."SP_GEN_JSON_DUMP_04"');
END;
/

SELECT STATUS,COUNT(*) FROM TBL_CONV_SPR GROUP BY STATUS;
*/

spool off;
exit;
