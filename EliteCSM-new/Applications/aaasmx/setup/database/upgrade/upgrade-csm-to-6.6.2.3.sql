INSERT INTO TBLMVERSION (SERIALNO,VERSION,SETUPMODE,COMPONENT,EXECUTED_ON) VALUES (SEQ_TBLMVERSION.nextval,'6.6.2.3','upgrade-csm-to-6.6.2.3','AAASMX',CURRENT_TIMESTAMP);
commit;

--change status of detail local driver
update tblsdrivertype set status='N' where drivertypeid=5;
update tblsdrivertype set status='N' where drivertypeid=12;
commit;