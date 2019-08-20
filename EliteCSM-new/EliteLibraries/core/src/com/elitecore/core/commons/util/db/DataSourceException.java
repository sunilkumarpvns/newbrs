package com.elitecore.core.commons.util.db;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.elitecore.core.commons.util.db.errorcodes.EliteErrorCodes;

/**
 * 
 * @author Malav Desai
 *
 */
@SuppressWarnings("serial")
public class DataSourceException extends SQLException{
	
	private final String vendorErrorMessage;

	public DataSourceException(String message, Throwable cause, int errorCode, String sqlState, String vendorErrorMessage){
		super(message, sqlState, errorCode, cause);
		this.vendorErrorMessage  = vendorErrorMessage;
	}
	
	public static DataSourceException newException(Throwable cause, DBVendors vendor){
		if(cause instanceof SQLException) {
			SQLException sqle = ((SQLException)cause);
			return new DataSourceException(
					cause.getMessage(), cause, sqle.getErrorCode(),
					sqle.getSQLState(), 
					vendor.getVendorErrorMessage(sqle));
		}else if(cause instanceof NoSuchElementException) {
			return new DataSourceException(
					EliteErrorCodes.CONNECTION_POOL_FULL_ERROR.getErrorMessage(), 
					cause, EliteErrorCodes.CONNECTION_POOL_FULL_ERROR.getErrorCode(), 
					null,null);
		} else {
			return new DataSourceException(
					cause.getMessage(),
					cause, EliteErrorCodes.UNKNOWN_ERROR.getErrorCode(), 
					null, null);
		}
	}
	
	@Override
	public String getMessage() {
		String message = super.getMessage() + ", Error [Code: " + super.getErrorCode() + ", SQLState: " + super.getSQLState();
		if(vendorErrorMessage != null){
			message += ", Message: " + vendorErrorMessage;
		}
		return message + "]";
	}
	
}
