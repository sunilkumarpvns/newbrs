package com.elitecore.corenetvertex.spr.util;

import javax.annotation.Nonnull;
import java.sql.SQLException;

public class SPRUtil {

	private static final String ERROR_WHILE = "Error while ";
	private static final String REASON_SQLERRORCODE = ". Reason: SQLErrorCode: ";
	private static final String FOR_SUBSCRIBER = " for subscriber: ";
	private static final String COMMA_SPACE = ", ";
	public static final String SQLERROR_CODE = "SQLErrorCode:";

	private SPRUtil() {}
	/**
	 *
	 * Generates uniform error message for spr operations which contains SQL Error Code
	 *
	 */
	public static @Nonnull
    String toStringSQLException(String operationName, SQLException e) {
		return ERROR_WHILE + operationName + REASON_SQLERRORCODE
				+ e.getErrorCode() + COMMA_SPACE + e.getMessage();
	}

	public static @Nonnull
    String toStringSQLException(String operationName, String subscriberIdentity, SQLException e) {
		return ERROR_WHILE + operationName + FOR_SUBSCRIBER + subscriberIdentity
				+ REASON_SQLERRORCODE + e.getErrorCode() + COMMA_SPACE + e.getMessage();
	}

	public static @Nonnull
    String toStringSQLException(SQLException e) {
		return SQLERROR_CODE + e.getErrorCode() + COMMA_SPACE + e.getMessage();
	}
}
