package com.elitecore.elitesm.ws.rest;

import java.sql.SQLException;
import java.util.Map;

import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.rest.utility.DBFieldList;
import com.elitecore.elitesm.ws.rest.utility.ListWrapper;
import com.elitecore.elitesm.ws.sessionmgmt.SessionMgmtWebServiceBLManager;

public class RestSessionMgmtBlManager extends SessionMgmtWebServiceBLManager {

	public final ListWrapper<DBFieldList> getSessionData(String query, int limit, int offset) throws SQLException, DatabaseConnectionException {
		return getResult(getDbConfiguration(), query, limit, offset);
	}
	
	/**
	 * used for providing the support of 
	 * findbyattributeid web service.
	 */
	public final Map<String, String> getAttributeMap() {
		return getDbConfiguration().getAttributeFieldMap();
	}
}
