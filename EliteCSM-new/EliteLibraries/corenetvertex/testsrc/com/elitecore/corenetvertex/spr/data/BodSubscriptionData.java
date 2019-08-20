package com.elitecore.corenetvertex.spr.data;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.corenetvertex.util.QueryBuilder;

/**
 * Used for DB query generation 
 */
@com.elitecore.corenetvertex.spr.Table(name="TBLBODCUSTOMER")
public class BodSubscriptionData {

    private static final String TABLENAME = "TBLBODCUSTOMER";
    private final String userIdentity;
    private final String subscriberPackage;
    private final String startTime;
    private final String endTime;
    private final String status;

    public BodSubscriptionData(String userIdentity, String subscriberPackage, String startTime, String endTime, String status) {
	this.userIdentity = userIdentity;
	this.subscriberPackage = subscriberPackage;
	this.startTime = startTime;
	this.endTime = endTime;
	this.status = status;
    }

    @Column(name = "USERIDENTITY", type = Types.VARCHAR)
    public String getUserIdentity() {
	return userIdentity;
    }

    @Column(name = "SUBSCRIBERPACKAGE", type = Types.VARCHAR)
    public String getSubscriberPackage() {
	return subscriberPackage;
    }

    @Column(name = "STARTTIME", type = Types.TIMESTAMP)
    public String getStartTime() {
	return startTime;
    }

    @Column(name = "ENDTIME", type = Types.TIMESTAMP)
    public String getEndTime() {
	return endTime;
    }

    @Column(name = "STATUS", type = Types.CHAR)
    public String getStatus() {
	return status;
    }

    @Table(name = "TBLBODCUSTOMER")
    public String getTableName() {
	return TABLENAME;
    }

    public static String createTableQuery() {
	return QueryBuilder.buildCreateQuery(BodSubscriptionData.class);
    }

    public String insertQuery() throws IllegalArgumentException, NullPointerException, IllegalAccessException, InvocationTargetException {
	return QueryBuilder.buildInsertQuery(this);
    }

    public static String dropTableQuery() {
	return QueryBuilder.buildDropQuery(BodSubscriptionData.class);
    }
}