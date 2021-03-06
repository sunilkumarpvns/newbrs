INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (SEQ_TBLMVERSION.nextval,'6.6.2.7','upgrade-csm-to-6.6.2.7','AAASMX',CURRENT_TIMESTAMP);
commit;

-- Introducing MSK Revalidation Time in EAP Configuration
ALTER TABLE TBLMEAPCONFIG ADD MSK_REVALIDATION_TIME	NUMERIC(10);

COMMIT;

-- Configuration and implementation of historical password for user
ALTER TABLE TBLMPASSWORDPOLICY ADD MAXHISTORICALPASSWORDS NUMBER(1);
ALTER TABLE TBLMSTAFF ADD HISTORICALPASSWORD VARCHAR2(1550);
UPDATE TBLMPASSWORDPOLICY SET MAXHISTORICALPASSWORDS = 5 WHERE PASSWORDPOLICYID=1;
COMMIT;

--Introducing CDRTIMESTAMPHEADER and CDRTIMESTAMPPOSITION parameter in TBLMCLASSICCSVACCTDRIVER
ALTER TABLE TBLMCLASSICCSVACCTDRIVER
ADD (CDRTIMESTAMPHEADER VARCHAR2(64) DEFAULT 'CDRTimeStamp',CDRTIMESTAMPPOSITION VARCHAR2(64) DEFAULT 'SUFFIX'); 
COMMIT;
