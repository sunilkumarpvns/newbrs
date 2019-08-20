INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (SEQ_TBLMVERSION.nextval,'6.8.0.2','upgrade-csm-to-6.8.0.2','AAASMX',CURRENT_TIMESTAMP);
commit;

-- Introducing Quota Management Plugin in AAA
CREATE SEQUENCE SEQ_MQUOTAMGTPLUGIN
    INCREMENT BY 1
    START WITH 20
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20
    NOORDER; 
    
CREATE TABLE TBLMQUOTAMGTPLUGIN(
  PLUGINID               NUMERIC(10),
  PLUGINDATA             BLOB,
  PLUGININSTANCEID       NUMERIC(20),
  CONSTRAINT PK_MQUOTAMGTPLUGIN PRIMARY KEY (PLUGINID),
  CONSTRAINT FK1_MQUOTAMGTPLUGIN  FOREIGN KEY (PLUGININSTANCEID) REFERENCES TBLMPLUGININSTANCEDATA (PLUGININSTANCEID)
);

INSERT INTO TBLSPLUGINTYPE (PLUGINTYPEID,PLUGINSERVICETYPEID,NAME,DISPLAYNAME,SERIALNO,ALIAS,DESCRIPTION,STATUS)
values (8, 1, 'Quota Management Plugin', 'Quota Management Plugin',1, 'QUOTA_MANAGEMENT_PLUGIN', 'Quota Management Plugin','Y');

COMMIT;