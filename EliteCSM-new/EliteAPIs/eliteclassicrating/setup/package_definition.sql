INSERT INTO PackageDefinition (packageid, packagename, packagetype, unittype, mincap, maxcap)
VALUES (PACKAGE_ID_SEQ.NEXTVAL,  'NightPlan001', 'Flat', 'MB','2.00', '600.00');

INSERT INTO PackageDefinition (packageid, packagename, packagetype, unittype, mincap, maxcap)
VALUES (PACKAGE_ID_SEQ.NEXTVAL,  'DayPlan002', 'Stepwise', 'Sec','1.00', '999.00');


INSERT INTO slabdefinition (PACKAGEID, unit1, unit2, unit3)
VALUES (1, 500 ,1000, 2000 );

INSERT INTO slabdefinition (PACKAGEID, unit1, unit2, unit3)
VALUES (10002, 999 ,2000, 5000 );

INSERT INTO slabdefinition (PACKAGEID, unit1, unit2, unit3)
VALUES (10003, 150 ,500, 1000 );


Insert into prefixgroupdefinition (prefixgroupname, prefixgroupnumber) 
values ('Airtel', 9898 );

Insert into prefixgroupdefinition (prefixgroupname, prefixgroupnumber) 
values ('BSNL', 9443 );

Insert into prefixgroupdefinition (prefixgroupname, prefixgroupnumber) 
values ('Vodafone', 9776 );
