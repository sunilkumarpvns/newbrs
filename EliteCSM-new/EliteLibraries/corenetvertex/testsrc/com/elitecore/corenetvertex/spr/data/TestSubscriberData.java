package com.elitecore.corenetvertex.spr.data;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.corenetvertex.spr.Table;
import com.elitecore.corenetvertex.util.QueryBuilder;

@Table(name = "TBLM_TEST_SUBSCRIBER")
public class TestSubscriberData implements DMLProvider {

	private String subscriberId;

	public TestSubscriberData(String subscriberId) {
		this.subscriberId = subscriberId;

	}

	@Column(name = "SUBSCRIBER_IDENTITY", type = Types.VARCHAR)
	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	@Override
	public String insertQuery() throws IllegalArgumentException, NullPointerException, IllegalAccessException, InvocationTargetException {
		return QueryBuilder.buildInsertQuery(this);
	}

	public static String createTableQuery() {
		return QueryBuilder.buildCreateQuery(TestSubscriberData.class);
	}

	public static String dropTableQuery() {
		return QueryBuilder.buildDropQuery(TestSubscriberData.class);
	}
}
