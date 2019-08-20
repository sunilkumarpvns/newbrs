INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (SEQ_TBLMVERSION.nextval,'6.6.2.5','upgrade-csm-to-6.6.2.5','AAASMX',CURRENT_TIMESTAMP);
commit;

-- DIAMETER POLICY TIME PERIOD
CREATE SEQUENCE SEQ_MDIAMETERPOLICYTIMEPERIOD
    INCREMENT BY 1
    START WITH 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20
    NOORDER; 

CREATE TABLE TBLMDIAMETERPOLICYTIMEPERIOD (
  TIMEPERIODID            NUMERIC(12,0),
  MONTHOFYEAR             VARCHAR(100),
  DAYOFMONTH              VARCHAR(100),
  DAYOFWEEK               VARCHAR(100),
  TIMEPERIOD              VARCHAR(100),
  DIAMETERPOLICYID          NUMERIC(10),
  CONSTRAINT PK_MDIAMETERPOLICYTIMEPERIOD PRIMARY KEY (TIMEPERIODID),
  CONSTRAINT FK1_MDIAMETERPOLICYTIMEPERIOD FOREIGN KEY (DIAMETERPOLICYID) REFERENCES TBLMDIAMETERPOLICY(DIAMETERPOLICYID) 
);

COMMIT;


CREATE TABLE TBLMDHCPKEYS (
DHCPIPADDRESS VARCHAR2(30),
RKID NUMBER(10),
GENERATIONTIME NUMBER(20),
DHCPKEY VARCHAR2(3000));

COMMIT;