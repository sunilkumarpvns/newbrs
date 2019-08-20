package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.spr.data.ProfileFieldMapping;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.LDAPUtility;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv2;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ExternalLDAPSPInterface implements SPInterface {

	private static final String MODULE = "EXT-LDAP-SP-INTERFACE";
	private final LDAPSPInterfaceConfiguration ldapspInterfaceConfiguration;
	private final AlertListener alertListener;
	private final LDAPConnectionProvider connectionProvider;

	public ExternalLDAPSPInterface(LDAPSPInterfaceConfiguration ldapspInterfaceConfiguration, AlertListener alertListener,
			LDAPConnectionProvider connectionProvider) {
		this.ldapspInterfaceConfiguration = ldapspInterfaceConfiguration;
		this.alertListener = alertListener;
		this.connectionProvider = connectionProvider;
	}

	public void init() throws InitializationFailedException {

		List<String> attributeList = ldapspInterfaceConfiguration.getProfileFieldMapping().getAttributeList();
		if (attributeList.isEmpty()) {
			throw new InitializationFailedException("Missing Field Mapping");
		}
	}

	/**
	 * This method returns the filter according to RFC2254 for LDAP
	 * 
	 * @param strSearchString
	 * @return String
	 */
	private String getRFC2254LDAPFilter(String strSearchString) {

		StringBuilder  buffer = new StringBuilder(strSearchString.length() + 16);
		for (int cnt = 0; cnt < strSearchString.length(); cnt++) {
			char ch = strSearchString.charAt(cnt);
			if (LDAPUtility.isRFC2254LDAPFilter(ch) || (ch >= CommonConstants.SPACE && ch <= CommonConstants.TILDE) == false) {
				buffer.append(CommonConstants.BACKSLASH);
				buffer.append(Integer.toHexString(ch));
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}

	@Override
	public SPRInfo getProfile(String userIdentity) throws OperationFailedException {
		if (getLogger().isLogLevel(LogLevel.INFO)) {
			getLogger().info(MODULE, "Trying to get account data for user identity: " + userIdentity);
		}
		ArrayList<String> ldapEntryAttrs = null;
		LDAPSearchResults ldapResults = null;


		LDAPConnection ldapConnection = null;
		try {
			ldapConnection = connectionProvider.getConnection();

			if (ldapConnection == null) {
				alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE, "Unable to get subscriber profile from "
						+ getDSName() + " database. Reason: LDAP connection is not available");

				throw new OperationFailedException("LDAP connection is not available", ResultCode.SERVICE_UNAVAILABLE);
			}
			String strSearchString = "(" + getUserPrefix() + getRFC2254LDAPFilter(userIdentity) + ")";
			ArrayList<String> searchBaseDNs = getSearchBaseDnList();
			ldapEntryAttrs = getLdapEntryAttributes();
			String[] strLdapEntryAttributes = ldapEntryAttrs.toArray(new String[ldapEntryAttrs.size()]);

			long totalQueryExecutionTime = System.currentTimeMillis();
			for (String searchBaseDN : searchBaseDNs) {
				long queryExecutionTime = System.currentTimeMillis();
				ldapResults = ldapConnection.search(searchBaseDN, LDAPv2.SCOPE_SUB, strSearchString, strLdapEntryAttributes, false);
				queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;

				if (queryExecutionTime > AlertConstants.LDAP_HIGH_QUERY_RESPONSE_TIME_MS) {
					alertListener.generateSystemAlert("", Alerts. HIGH_QUERY_RESPONSE_TIME, MODULE, "LDAP query execution time is high for SPR: "
							+ MODULE +
							". Last query execution time: " + queryExecutionTime + " ms");

					if (getLogger().isLogLevel(LogLevel.WARN)) {
						getLogger().warn(MODULE, "LDAP query execution time getting high, - Last query execution time = "
								+ queryExecutionTime + " ms.");
					}
				}
				if (ldapResults != null && ldapResults.hasMoreElements()) {
					break;
				}
			}

			if (ldapResults != null && ldapResults.hasMoreElements()) {
				totalQueryExecutionTime = System.currentTimeMillis() - totalQueryExecutionTime;
				SPRInfo sprInfo = createProfileData(ldapResults);
				sprInfo.setSprLoadTime(totalQueryExecutionTime);
				return sprInfo;
			}
		} catch (LDAPException e) {
			handleError(e, userIdentity);
		} catch (Exception e) {
			handleError(e, userIdentity);
		} finally {
			connectionProvider.close(ldapConnection);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Subscriber profile is not exist for user ID: " + userIdentity);
		}

		return null;
	}

	private void handleError(Exception e, String userIdentity) throws OperationFailedException {

		getLogger().error(MODULE, "Error while feaching profile for user ID: " + userIdentity
				+ ". Reason: " + e.getMessage());
		getLogger().trace(MODULE, e);
		throw new OperationFailedException("Error while fetching profile data. Reason: " + e.getMessage(), e);
	}

	private void handleError(LDAPException e, String userIdentity) throws OperationFailedException {
		if (LDAPUtility.isLDAPDownException(e)) {
			alertListener.generateSystemAlert("", Alerts.SPRDOWN, MODULE, "LDAP with data source name "
					+ getDSName() + " is down, System marked as dead. Reason: " + e.getMessage(), 0, getDSName());

			getLogger().error(MODULE, "LDAP with data source name " + getDSName() + " is down, System marked as dead. Reason: "
					+ e.getMessage());
			getLogger().trace(MODULE, e);
		} else {
			getLogger().error(MODULE, "Unknown LDAP Error, Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		handleError((Exception) e, userIdentity);
	}

	/**
	 * This method returns the datasource name
	 * 
	 * @return String
	 */

	private String getDSName() {
		return ldapspInterfaceConfiguration.getLdapDataSource().getDataSourceName();
	}

	/**
	 * This method returns the field mapping for subscriber profile
	 * 
	 * @return AccountDataFieldMapping
	 */
	private ProfileFieldMapping getAccountDataFieldMapping() {
		return ldapspInterfaceConfiguration.getProfileFieldMapping();
	}

	/**
	 * This method returns the user distinguished name
	 * 
	 * @return String
	 */
	private String getUserPrefix() {
		return ldapspInterfaceConfiguration.getUserPrefix();
	}

	/**
	 * This method returns the List of distinguished name
	 * 
	 * @return ArrayList
	 */
	private ArrayList<String> getSearchBaseDnList() {
		return ldapspInterfaceConfiguration.getSearchBaseDnList();
	}

	/**
	 * This method returns the data format
	 * 
	 * @return SimpleDateFormat
	 */

	private SimpleDateFormat getExpiryDatePatterns() {
		return new SimpleDateFormat(ldapspInterfaceConfiguration.getExpiryDatePattern());
	}

	private ArrayList<String> getLdapEntryAttributes() {
		return ldapspInterfaceConfiguration.getProfileFieldMapping().getAttributeList();
	}

	private SPRInfo createProfileData(LDAPSearchResults ldapResultSet) throws LDAPException {
		ProfileFieldMapping accountDataFieldMapping = getAccountDataFieldMapping();
		LDAPEntry ldapEntry = ldapResultSet.next();

		SPRInfoImpl sprInfo = new SPRInfoImpl();
		boolean validate = false;
		for (SPRFields sprField : SPRFields.values()) {
			String value = fetchValue(sprField, ldapEntry, accountDataFieldMapping);

			if (value == null) {
				continue;
			}

			try {
				if (SPRFields.EXPIRY_DATE == sprField) {
					setExpiryDate(sprInfo, value);
				} else {
					sprField.setStringValue(sprInfo, value, validate);
				}
			} catch (Exception e) {
				getLogger().error(MODULE, "Invalid value: " + value + " for field: " + sprField.columnName);
				getLogger().trace(MODULE, e);
			}
		}

		return sprInfo;
	}

	private void setExpiryDate(SPRInfoImpl sprInfo, String value) {
		SimpleDateFormat expiryDatePattern = getExpiryDatePatterns();

		try {
			sprInfo.setExpiryDate(new Timestamp(expiryDatePattern.parse(value).getTime()));
		} catch (ParseException e) {
			getLogger().error(MODULE, "Error while parsing expiry date: " + value + ", date format should be in "
					+ expiryDatePattern.toPattern());
			getLogger().trace(MODULE, e);
		}
	}

	private String fetchValue(SPRFields sprField, LDAPEntry ldapEntry, ProfileFieldMapping accountDataFieldMapping) {
		String logicalField = accountDataFieldMapping.getFieldMappingForKey(sprField);

		if (Strings.isNullOrBlank(logicalField)) {
			return null;
		}

		LDAPAttribute attribute = ldapEntry.getAttribute(logicalField);
		if (attribute == null) {
			return null;
		}

		String[] values = attribute.getStringValueArray();
		if (Arrayz.isNullOrEmpty(values)) {
			return null;
		}

		return values[0];
	}

	@Override
	public void addProfile(SPRInfo info) throws OperationFailedException {
		throw new OperationFailedException("Add profile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException {
		throw new OperationFailedException("Update profile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int changeIMSpackage(String subscriberIdentity, String packageName) throws OperationFailedException {
		throw new OperationFailedException("change IMS package operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int purgeProfile(String subscriberIdentity) throws OperationFailedException {
		throw new OperationFailedException("purge profile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int markForDeleteProfile(String subscriberIdentity) throws OperationFailedException {
		throw new OperationFailedException("markForDeleteProfile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {
		throw new OperationFailedException("getDeleteMarkedProfiles operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int purgeProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {
		throw new OperationFailedException("purgeProfile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int restoreProfile(String subscriberIdentity) throws OperationFailedException {
		throw new OperationFailedException("restoreProfile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
		throw new OperationFailedException("restoreProfile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public Transaction createTransaction() throws OperationFailedException {
		throw new OperationFailedException("getTransaction operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public void addProfile(SPRInfo info, Transaction transaction) throws OperationFailedException {
		throw new OperationFailedException("addProfile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int updateProfile(String subscriberIdentity,
			EnumMap<SPRFields, String> updatedProfile,
			Transaction transaction) throws OperationFailedException,
			TransactionException {
		throw new OperationFailedException("updateProfile operation is not supported in external LDAP SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
	}
}