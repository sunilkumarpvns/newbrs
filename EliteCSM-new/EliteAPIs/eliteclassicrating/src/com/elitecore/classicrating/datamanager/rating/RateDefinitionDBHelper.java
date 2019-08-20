/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Sheetal Soni
 *
 */
package com.elitecore.classicrating.datamanager.rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elitecore.classicrating.commons.util.logger.Log4jLogger;
import com.elitecore.classicrating.datamanager.base.BaseDBHelper;
import com.elitecore.classicrating.datasource.ITransactionContext;

/**
 * @author sheetalsoni
 * 
 */
public class RateDefinitionDBHelper extends BaseDBHelper {

	protected Log4jLogger Logger;
	
	private static final String MODULE = "Rate Definition DB Helper";

	private static final String GET_RATE =" SELECT RATE " +
       					   "    FROM RATEDEFINITION " +
					   "    WHERE PACKAGEID = ? " +
					   "    AND SERVICETYPE = ? " + 
					   "    AND PREFIXGROUP = ? " +
					   "    AND CUSTOMERTYPE = ? " +
					   "    AND EVENTTYPE = ? " +
					   "    AND VERSION = ? ";

	public RateDefinitionDBHelper(ITransactionContext transactionContext) {
		super(transactionContext);
		Logger = Log4jLogger.getInstance();
	}
	
	/**
	 * Finds rate for calculation on the basis of unique Identification of
	 * customerType, Event Type, Version etc. 
	 * If it's defined then it will select rate accordingly other wise it will set these fields as ANY
	 * 
	 * @method getRate()
	 * @param RateDefinition
	 * @return rate
	 * @author sheetalsoni
	 * @throws RateNotFoundException 
	 * @created on 6/10/2008
	 */

	public double getRate(RateDefinitionData rateDefinitionData) throws SQLException, RateNotFoundException {
		Logger.info(MODULE,"Into getRate( " + rateDefinitionData + " )");
		
		// PackageId, ServiceType, Prefix Group, CustomerSubType, EventType, Version
            boolean[][] rateLookupOrder = {
                        {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE},
                        {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE},
                        {Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE}
            };
		
		PreparedStatement pstmtRateSuccess = null;
		ResultSet rsRate = null;

		double rate = -1;
		boolean rateFound = false;
		
		try {
			for(int i=0; i < rateLookupOrder.length; i++){
				RateDefinitionData rateLookupConditions = setRateConditions(rateDefinitionData, rateLookupOrder[i]);
				
				pstmtRateSuccess = super.getTransactionContext().prepareStatement(GET_RATE);
				pstmtRateSuccess.setLong(1, rateLookupConditions.getPackageId());
				pstmtRateSuccess.setString(2, rateLookupConditions.getServiceType());
				pstmtRateSuccess.setString(3, rateLookupConditions.getPrefixGroup());
				pstmtRateSuccess.setString(4, rateLookupConditions.getCustomerSubType());
				pstmtRateSuccess.setString(5, rateLookupConditions.getEventType());
				pstmtRateSuccess.setString(6, rateLookupConditions.getVersion());
				
				rsRate = pstmtRateSuccess.executeQuery();
				
				if (rsRate.next()) {
					rate = rsRate.getDouble("RATE");
					rateFound = true;
					break;
				}
			}
			if (!rateFound)
				throw new RateNotFoundException("Rate Not Found for ANY Value");
		} catch (SQLException e) {
			Logger.trace(MODULE, e);
			Logger.error(MODULE, "Exception caught while reading rate");
			throw e;
		} finally {
			try {
				if (rsRate != null) {
					rsRate.close();
				}
			} catch (SQLException e) {
				Logger.trace(MODULE, e);
				Logger.error(MODULE, "Exception caught while closing Result Set");
			}
			try {
				if (pstmtRateSuccess != null) {
					super.getTransactionContext().closePreparedStatement(pstmtRateSuccess);
				}
			} catch (SQLException e) {
				Logger.trace(MODULE, e);
				Logger.error(MODULE, "Exception caught while closing prepared statement");
			}
		}
		Logger.info(MODULE,"OutOf getRate( " + rateDefinitionData + " ) = " + rate);
		return rate;
	}
	public RateDefinitionData setRateConditions(RateDefinitionData rateDefinitionData, boolean[] rateLookupOrder){
		Logger.info(MODULE,"Into setRateConditions( " + rateDefinitionData + ", RateLookupOrder = [ " + rateLookupOrder + "] )");
		RateDefinitionData rateConditions = new RateDefinitionData();
                rateConditions.setPackageId(rateDefinitionData.getPackageId());
                rateConditions.setServiceType(rateDefinitionData.getServiceType());
                
		/*
		if(rateLookupOrder[0])
			rateConditions.setPackageId(rateDefinitionData.getPackageId());
		else
			rateConditions.setPackageId(-1);

		if(rateLookupOrder[1])
			rateConditions.setServiceType(rateDefinitionData.getServiceType());
		else
			rateConditions.setServiceType("ANY");
                */
		if(rateLookupOrder[2])
			rateConditions.setPrefixGroup(rateDefinitionData.getPrefixGroup());
		else
			rateConditions.setPrefixGroup("ANY");
		
		if(rateLookupOrder[3])
			rateConditions.setCustomerSubType(rateDefinitionData.getCustomerSubType());
		else
			rateConditions.setCustomerSubType("ANY");
		
		if(rateLookupOrder[4])
			rateConditions.setEventType(rateDefinitionData.getEventType());
		else
			rateConditions.setEventType("ANY");
		
		if(rateLookupOrder[5])
			rateConditions.setVersion(rateDefinitionData.getVersion());
		else
			rateConditions.setVersion("ANY");
		
		Logger.info(MODULE,"OutOf setRateConditions( " + rateDefinitionData + "] ) = " + rateConditions);
		return rateConditions;
	}
}
