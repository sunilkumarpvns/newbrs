package com.elitecore.aaa.radius.drivers.conf.impl;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;

/**
 * Builds select query to read drivers with specified driver type and having any of
 * the specified driver instance IDs.
 * 
 * @author narendra.pathai
 *
 */
public class DriverSelectQueryBuilder {
	private static final String NO_RESULT_QUERY = "SELECT DRIVERINSTANCEID, DRIVERTYPEID from TBLMDRIVERINSTANCE WHERE 1=2";
	
	@Nonnull private final DriverTypes driverType;
	@Nonnull private final Collection<String> driverIdsToRead;

	/**
	 * 
	 * @param driverType type of driver to be read.
	 * @param driverIdsToRead a non-null and non-empty set of driver IDs to read.
	 * @throws IllegalArgumentException if driverIdsToRead is empty.
	 * @throws NullPointerException if any of the argument is null.
	 */
	public DriverSelectQueryBuilder(DriverTypes driverType, Collection<String> driverIdsToRead) 
	throws NullPointerException {
		this.driverType = checkNotNull(driverType, "driverType is null");
		this.driverIdsToRead = checkNotNull(driverIdsToRead, "driverIdsToRead is null");
	}
	
	/**
	 * @return select query
	 */
	public String build() {
		if (driverIdsToRead.isEmpty()) {
			return NO_RESULT_QUERY;
		}
		
		StringBuilder query = new StringBuilder();
		
		query.append("select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID = ")
			.append("'" + driverType.value + "'")
			.append(" AND DRIVERINSTANCEID IN ")
			.append("(")
			.append(Strings.join(",", addSingleQuoteInDriverInstanceId()))
			.append(")");
		
		return query.toString();
	}
	
	/**
	 * This is used to add single quote in each driver instance id, 
	 * earlier there was numeric type for driver instance id column so SQL statement <b>"select * from table where driverinstanceid in(id1,id2);"</b> was working properly.
	 * Now it has been changed to String so we need to enclose all values with single quote for driver instance id for Ex. <b>"select * from table where driverinstanceid in('id1','id2');"</b> 
	 * @return List of driver instance id with single quote
	 */
	private List<String> addSingleQuoteInDriverInstanceId() {
		List<String> driverInstanceIds = new ArrayList<String>();
		if (Collectionz.isNullOrEmpty(this.driverIdsToRead) == false) {
			for (String driverInstanceId : this.driverIdsToRead) {
				driverInstanceIds.add("'" + driverInstanceId + "'");
			}
		}
		return driverInstanceIds;
	}
}
