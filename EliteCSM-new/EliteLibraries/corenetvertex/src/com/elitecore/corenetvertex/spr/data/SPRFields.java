package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PasswordEncryptionType;
import com.elitecore.corenetvertex.spr.data.SPRInfo.SubscriberLevelMetering;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

public enum SPRFields {


	SUBSCRIBER_IDENTITY("Subscriber Identity", "SUBSCRIBERIDENTITY", PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setSubscriberIdentity(val);

		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getSubscriberIdentity();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setSubscriberIdentity(val.toString());
			}

		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setSubscriberIdentity(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getSubscriberIdentity());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getSubscriberIdentity());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},

	USERNAME("Username", "USERNAME", PCRFKeyConstants.SUB_USER_NAME, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setUserName(val);

		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getUserName();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setUserName(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setUserName(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getUserName());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getUserName());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PASSWORD("Password", "PASSWORD", null, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setPassword(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getPassword();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setPassword(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setPassword(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getPassword());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getPassword());
		}

		@Override
		public void validateStringValue(String val) {
			//Blank Implementation
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	CUSTOMER_TYPE("Customer Type", "CUSTOMERTYPE", PCRFKeyConstants.SUB_CUSTOMER_TYPE, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setCustomerType(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getCustomerType();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setCustomerType(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setCustomerType(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getCustomerType());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getCustomerType());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	STATUS("Status", "STATUS", PCRFKeyConstants.SUB_SUBSCRIBER_STATUS, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setStatus(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return Strings.isNullOrBlank(sprInfo.getStatus()) ? null : sprInfo.getStatus();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + STATUS);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + STATUS);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + STATUS);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + STATUS);
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},

	PRODUCT_OFFER("Product Offer", "PRODUCT_OFFER", PCRFKeyConstants.SUB_PRODUCT_OFFER, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setProductOffer(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getProductOffer();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setProductOffer(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setProductOffer(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getProductOffer());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getProductOffer());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},

	IMS_PACKAGE("IMS Package", "IMSPACKAGE", PCRFKeyConstants.SUB_IMS_PACKAGE, Types.VARCHAR) {

		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setImsPackage(val);
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setImsPackage(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setImsPackage(val.toString());
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getImsPackage();
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getImsPackage());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getImsPackage());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}

	},

	EXPIRY_DATE("Expiry Date", "EXPIRYDATE", PCRFKeyConstants.SUB_EXPIRY_DATE, Types.TIMESTAMP) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if(val != null){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setExpiryDate(Timestamp.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return toString(sprInfo.getExpiryDate());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setExpiryDate(new Timestamp(val));
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException {
			if (val != null && validate) {
				validateTimeStampValue(val);
			}
			sprInfo.setExpiryDate(val);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return sprInfo.getExpiryDate();
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getExpiryDate());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}
		@Override
		public void validateTimeStampValue(Timestamp val)
				throws OperationFailedException {
			Long currentTime = System.currentTimeMillis();
			if(val.getTime() < currentTime){
				throw new OperationFailedException("Invalid ExpiryDate configured: "+val+". Expiry can not be past Date", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}
	},
	BILLING_DATE("Billing Date", "BILLINGDATE", PCRFKeyConstants.SUB_BILLING_DATE, Types.NUMERIC) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException{
			if (Strings.isNullOrBlank(val) == false) {
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setBillingDate(Integer.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return toString(sprInfo.getBillingDate());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) throws OperationFailedException{
			if(val != null) {
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setBillingDate(val.intValue());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException{
			if(val != null) {
				if (validate) {
					validateTimeStampValue(val);
				}

				sprInfo.setBillingDate(val.toLocalDateTime().getDayOfMonth());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SUBSCRIBER_LEVEL_METERING);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getBillingDate());
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			if(Integer.valueOf(val) < BILING_DAY_START || Integer.valueOf(val) > BILLING_DAY_END){
				throw new OperationFailedException("Invalid Billing Date configured: "+val+". BillingDay must be between 1 to 28", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateNumericValue(Long val) throws OperationFailedException {
			if(val < BILING_DAY_START || val > BILLING_DAY_END){
				throw new OperationFailedException("Invalid Billing Date configured: "+val+". BillingDay must be between 1 to 28", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateTimeStampValue(Timestamp val)
				throws OperationFailedException {
			if(val.toLocalDateTime().getDayOfMonth() < BILING_DAY_START || val.toLocalDateTime().getDayOfMonth() > BILLING_DAY_END){
				throw new OperationFailedException("Invalid Billing Date configured: "+val+". BillingDay must be between 1 to 28", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}
	},
	AREA("Area", "AREA", PCRFKeyConstants.SUB_AREA, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setArea(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getArea();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null) {
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setArea(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null) {
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setArea(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getArea());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getArea());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	CITY("City", "CITY", PCRFKeyConstants.SUB_CITY, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean  validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setCity(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getCity();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setCity(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setCity(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getCity());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getCity());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PARAM1("Param1", "PARAM1", PCRFKeyConstants.SUB_PARAM1, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setParam1(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getParam1();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setParam1(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setParam1(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getParam1());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getParam1());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PARAM2("Param2", "PARAM2", PCRFKeyConstants.SUB_PARAM2, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setParam2(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getParam2();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setParam2(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setParam2(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getParam2());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getParam2());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PARAM3("Param3", "PARAM3", PCRFKeyConstants.SUB_PARAM3, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setParam3(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getParam3();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setParam3(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setParam3(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getParam3());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getParam3());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PARAM4("Param4", "PARAM4", PCRFKeyConstants.SUB_PARAM4, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setParam4(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getParam4();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setParam4(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setParam4(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getParam4());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getParam4());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PARAM5("Param5", "PARAM5", PCRFKeyConstants.SUB_PARAM5, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setParam5(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getParam5();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setParam5(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setParam5(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getParam5());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getParam5());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	ZONE("Zone", "ZONE", PCRFKeyConstants.SUB_ZONE, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setZone(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getZone();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setZone(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setZone(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getZone());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getZone());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	COUNTRY("Country", "COUNTRY", PCRFKeyConstants.SUB_COUNTRY, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);

			}
			sprInfo.setCountry(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getCountry();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setCountry(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setCountry(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getCountry());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getCountry());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	BIRTH_DATE("Birth Date", "BIRTHDATE", PCRFKeyConstants.SUB_BIRTHDATE, Types.TIMESTAMP) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate)throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setBirthdate(Timestamp.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo){
			return toString(sprInfo.getBirthdate());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) throws OperationFailedException{
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setBirthdate(new Timestamp(val));
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException{
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setBirthdate(val);
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo){
			return sprInfo.getBirthdate();
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo){
			return toLong(sprInfo.getBirthdate());
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			Long currentTime = System.currentTimeMillis();
			if(Timestamp.valueOf(val).getTime() > currentTime){
				throw new OperationFailedException("Invalid BirthDate configured: "+val+". BirthDate can not be future Date", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateNumericValue(Long val)
				throws OperationFailedException {
			Long currentTime = System.currentTimeMillis();
			if(val > currentTime){
				throw new OperationFailedException("Invalid BirthDate configured: "+val+". BirthDate can not be future Date", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateTimeStampValue(Timestamp val)
				throws OperationFailedException {
			Long currentTime = System.currentTimeMillis();
			if(val.getTime() > currentTime){
				throw new OperationFailedException("Invalid BirthDate configured: "+val+". BirthDate can not be future Date", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}
	},
	ROLE("Role", "ROLE", PCRFKeyConstants.SUB_ROLE, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setRole(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getRole();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setRole(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setRole(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getRole());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getRole());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	COMPANY("Company", "COMPANY", PCRFKeyConstants.SUB_COMPANY, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setCompany(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getCompany();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setCompany(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setCompany(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getCompany());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getCompany());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	DEPARTMENT("Department", "DEPARTMENT", PCRFKeyConstants.SUB_DEPARTMENT, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setDepartment(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getDepartment();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setDepartment(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setDepartment(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getDepartment());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getDepartment());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	ARPU("ARPU", "ARPU", PCRFKeyConstants.SUB_ARPU, Types.NUMERIC) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false) {
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setArpu(Long.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return toString(sprInfo.getArpu());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if (val != null && validate) {
				validateNumericValue(val);
			}
			sprInfo.setArpu(val);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + ARPU);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + ARPU);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return sprInfo.getArpu();
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	CADRE("Cadre", "CADRE", PCRFKeyConstants.SUB_CADRE, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setCadre(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getCadre();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setCadre(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setCadre(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getCadre());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getCadre());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	EMAIL("Email", "EMAIL", PCRFKeyConstants.SUB_EMAIL, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setEmail(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getEmail();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setEmail(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setEmail(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getEmail());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getEmail());
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			if (Strings.isNullOrBlank(val) == false && EMAIL_REGEX.matcher(val).matches() == false) {
				throw new OperationFailedException("Invalid Email format configured: " + val, ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateNumericValue(Long val)
				throws OperationFailedException {
			if (val != null && EMAIL_REGEX.matcher(val.toString()).matches() == false) {
				throw new OperationFailedException("Invalid Email format configured: " + val, ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateTimeStampValue(Timestamp val)
				throws OperationFailedException {
			if(val != null && EMAIL_REGEX.matcher(val.toString()).matches() == false){
				throw new OperationFailedException("Invalid Email format configured: " + val,ResultCode.INVALID_INPUT_PARAMETER);
			}
		}
	},
	PHONE("Phone", "PHONE", PCRFKeyConstants.SUB_PHONE, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setPhone(val);
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getPhone();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setPhone(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setPhone(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getPhone());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getPhone());
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			if(NUMERIC_REGEX.matcher(val).matches() == false){
				throw new OperationFailedException("Invalid Phone No configured: "+val+". PHONE NO must be numeric", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateNumericValue(Long val)
				throws OperationFailedException {
			if(NUMERIC_REGEX.matcher(val.toString()).matches() == false){
				throw new OperationFailedException("Invalid Phone No configured: "+val+". PHONE NO must be numeric", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateTimeStampValue(Timestamp val)
				throws OperationFailedException {
			if(NUMERIC_REGEX.matcher(val.toString()).matches() == false){
				throw new OperationFailedException("Invalid Phone No configured: "+val+". PHONE NO must numeric", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}
	},
	SIP_URL("SIP URL", "SIPURL", PCRFKeyConstants.SUB_SIP_URL, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setSipURL(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getSipURL();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setSipURL(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setSipURL(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getSipURL());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getSipURL());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	CUI("CUI", "CUI", PCRFKeyConstants.SUB_CUI, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setCui(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getCui();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setCui(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setCui(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getCui());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getCui());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	IMSI("IMSI", "IMSI", PCRFKeyConstants.SUB_IMSI, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setImsi(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getImsi();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setImsi(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setImsi(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getImsi());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getImsi());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	MSISDN("MSISDN", "MSISDN", PCRFKeyConstants.SUB_MSISDN, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setMsisdn(val);
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getMsisdn();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setMsisdn(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setMsisdn(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getMsisdn());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getMsisdn());
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			if(NUMERIC_REGEX.matcher(val).matches()== false){
				throw new OperationFailedException("Invalid value configured for MSISDN: "+val+". MSISDN must be numeric", ResultCode.INVALID_INPUT_PARAMETER);
			}
			if(val.length() < MIN_MSISDN_LENGTH || val.length() > MAX_MSISDN_LENGTH ){
				throw new OperationFailedException("Invalid value configured for MSISDN: "+val+". MSISDN range must be between 5 to 15", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateNumericValue(Long val)
				throws OperationFailedException {
			if(NUMERIC_REGEX.matcher(val.toString()).matches() == false){
				throw new OperationFailedException("Invalid value configured for MSISDN: "+val+". MSISDN must be numeric", ResultCode.INVALID_INPUT_PARAMETER);
			}
			if(val.toString().length() < MIN_MSISDN_LENGTH || val.toString().length() > MAX_MSISDN_LENGTH ){
				throw new OperationFailedException("Invalid value configured for MSISDN: "+val+". MSISDN range must be between 5 to 15", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateTimeStampValue(Timestamp val)
				throws OperationFailedException {
			if(NUMERIC_REGEX.matcher(val.toString()).matches() == false){
				throw new OperationFailedException("Invalid value configured for MSISDN: "+val+". MSISDN must be numeric", ResultCode.INVALID_INPUT_PARAMETER);
			}
			if(val.toString().length() < MIN_MSISDN_LENGTH || val.toString().length() > MAX_MSISDN_LENGTH ){
				throw new OperationFailedException("Invalid value configured for MSISDN: "+val+". MSISDN range must be between 5 to 15", ResultCode.INVALID_INPUT_PARAMETER);
			}

		}
	},
	MAC("MAC", "MAC", PCRFKeyConstants.SUB_MAC, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setMac(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getMac();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setMac(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setMac(val.toString());
			}

		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getMac());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getMac());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	EUI64("EUI64", "EUI64", PCRFKeyConstants.SUB_EUI64, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setEui64(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getEui64();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setEui64(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setEui64(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getEui64());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getEui64());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	MODIFIED_EUI64("Modified EUI64", "MODIFIED_EUI64", PCRFKeyConstants.SUB_MODIFIED_EUI64, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setModifiedEui64(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getModifiedEui64();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setModifiedEui64(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setModifiedEui64(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getModifiedEui64());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getModifiedEui64());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	ENCRYPTION_TYPE("Encryption Type", "ENCRYPTIONTYPE", PCRFKeyConstants.SUB_ENCRYPTION_TYPE, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {

			if(Strings.isNullOrBlank(val) == false){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setEncryptionType(val);
			}else{
				sprInfo.setEncryptionType(PasswordEncryptionType.NONE.strVal);
			}

		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			PasswordEncryptionType passwordEncryptionType = PasswordEncryptionType.fromValue(sprInfo.getEncryptionType());
			if(passwordEncryptionType == null) {
				return PasswordEncryptionType.NONE.strVal;
			} else {
				return passwordEncryptionType.strVal;
			}

		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setEncryptionType(val.toString());
			}else{
				sprInfo.setEncryptionType(PasswordEncryptionType.NONE.strVal);
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) throws OperationFailedException {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setEncryptionType(val.toString());
			}else{
				sprInfo.setEncryptionType(PasswordEncryptionType.NONE.strVal);
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getEncryptionType());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			PasswordEncryptionType passwordEncryptionType = PasswordEncryptionType.fromValue(sprInfo.getEncryptionType());
			if(passwordEncryptionType == null) {
				return PasswordEncryptionType.NONE.val;
			} else {
				return passwordEncryptionType.val;
			}
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			PasswordEncryptionType encryptionType = PasswordEncryptionType.fromValue(val);
			if(encryptionType == null){
				throw new OperationFailedException("Invalid Encryption type configured: "+val, ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateNumericValue(Long val)
				throws OperationFailedException {
			PasswordEncryptionType encryptionType = PasswordEncryptionType.fromValue(val);
			if(encryptionType == null){
				throw new OperationFailedException("Invalid Encryption type configured: "+val, ResultCode.INVALID_INPUT_PARAMETER);
			}

		}

		@Override
		public void validateTimeStampValue(Timestamp val)
				throws OperationFailedException {
			PasswordEncryptionType encryptionType = PasswordEncryptionType.fromValue(val.getTime());
			if(encryptionType == null){
				throw new OperationFailedException("Invalid Encryption type configured: "+val, ResultCode.INVALID_INPUT_PARAMETER);
			}

		}
	},
	ESN("ESN", "ESN", PCRFKeyConstants.SUB_ESN, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setEsn(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getEsn();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setEsn(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setEsn(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getEsn());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getEsn());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	MEID("MEID", "MEID", PCRFKeyConstants.SUB_MEID, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setMeid(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getMeid();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setMeid(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setMeid(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getMeid());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getMeid());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PARENT_ID("Parent ID", "PARENTID", PCRFKeyConstants.SUB_PARENTID, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setParentId(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getParentId();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setParentId(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setParentId(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getParentId());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getParentId());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	GROUP_NAME("Group Name", "GROUPNAME", PCRFKeyConstants.SUB_GROUPNAME, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setGroupName(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getGroupName();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setGroupName(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setGroupName(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getGroupName());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getGroupName());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	IMEI("IMEI", "IMEI", PCRFKeyConstants.SUB_IMEI, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setImei(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getImei();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setImei(val.toString());
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if(val != null){
				if (validate) {
					validateTimeStampValue(val);
				}
				sprInfo.setImei(val.toString());
			}
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getImei());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getImei());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},

	CALLING_STATION_ID("Calling Station ID", "CALLING_STATION_ID", PCRFKeyConstants.SUB_CALLING_STATION_ID, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setCallingStationId(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getCallingStationId();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + CALLING_STATION_ID);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + CALLING_STATION_ID);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + CALLING_STATION_ID);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + CALLING_STATION_ID);
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},


	NAS_PORT_ID("NAS Port ID", "NAS_PORT_ID", PCRFKeyConstants.SUB_NAS_PORT_ID, Types.VARCHAR) {

		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setNasPortId(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getNasPortId();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + NAS_PORT_ID);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + NAS_PORT_ID);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + NAS_PORT_ID);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + NAS_PORT_ID);
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},

	FRAMED_IP("Framed IP", "FRAMED_IP", PCRFKeyConstants.SUB_FRAMED_IP, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setFramedIp(val);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getFramedIp();
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + FRAMED_IP);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + FRAMED_IP);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + FRAMED_IP);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + FRAMED_IP);
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},


	SUBSCRIBER_LEVEL_METERING("Subscriber Level Metering", "SUBSCRIBERLEVELMETERING", PCRFKeyConstants.SUB_IS_UM_ENABLED, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			SubscriberLevelMetering status = SubscriberLevelMetering.fromStatus(val);
			sprInfo.setSubscriberLevelMetering(status == null ? SubscriberLevelMetering.DISABLE : status);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getSubscriberLevelMetering() == null ? null : sprInfo.getSubscriberLevelMetering().status;
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SUBSCRIBER_LEVEL_METERING);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SUBSCRIBER_LEVEL_METERING);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SUBSCRIBER_LEVEL_METERING);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SUBSCRIBER_LEVEL_METERING);
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PASSWORD_CHECK("Password Check", "PASSWORD_CHECK", PCRFKeyConstants.SUB_PASSWORD_CHECK, Types.VARCHAR) {

		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {
			if(Strings.isNullOrBlank(val) == false){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setPasswordCheck(FALSE.equalsIgnoreCase(val) ? false : true);
			}else{
				sprInfo.setPasswordCheck(false);
			}
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PASSWORD_CHECK);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PASSWORD_CHECK);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getPasswordCheck() == true ? TRUE : FALSE;
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PASSWORD_CHECK);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PASSWORD_CHECK);
		}

		@Override
		public void validateStringValue(String val) throws OperationFailedException {
			if(FALSE.equalsIgnoreCase(val) == false && TRUE.equalsIgnoreCase(val) == false){
				throw new OperationFailedException("Invalid Password Check configured: "+val+ ". Possible values are True/False", ResultCode.INVALID_INPUT_PARAMETER);
			}
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},

	SY_INTERFACE("Sy Interface", "SY_INTERFACE", PCRFKeyConstants.SUB_SY_INTERFACE, Types.VARCHAR) {

		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {
			if(Strings.isNullOrBlank(val) == false){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setSyInterface(FALSE.equalsIgnoreCase(val) ? false : true);
			}else{
				sprInfo.setSyInterface(true);
			}

		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SY_INTERFACE);
		}
		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SY_INTERFACE);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getSyInterface() == true ? TRUE : FALSE;
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SY_INTERFACE);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + SY_INTERFACE);
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			if(FALSE.equalsIgnoreCase(val) == false && TRUE.equalsIgnoreCase(val) == false){
				throw new OperationFailedException("Invalid Sy Mode configured: "+val+ ". Possible values are True/False", ResultCode.INVALID_INPUT_PARAMETER);
			}
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	PAYG_INTL_DATA_ROAMING("PAYG International Data Roaming", "PAYG_INTL_DATA_ROAMING", PCRFKeyConstants.SUB_PAYG_INTL_DATA_ROAMING, Types.VARCHAR) {

		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) throws OperationFailedException {
			if(Strings.isNullOrBlank(val) == false){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setPaygInternationalDataRoaming(FALSE.equalsIgnoreCase(val) ? false : true);
			}else{
				sprInfo.setPaygInternationalDataRoaming(true);
			}

		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PAYG_INTL_DATA_ROAMING);
		}
		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PAYG_INTL_DATA_ROAMING);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getPaygInternationalDataRoaming() == true ? TRUE : FALSE;
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PAYG_INTL_DATA_ROAMING);
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			throw new UnsupportedOperationException("Operation not supported for field: " + PAYG_INTL_DATA_ROAMING);
		}

		@Override
		public void validateStringValue(String val)
				throws OperationFailedException {
			if(FALSE.equalsIgnoreCase(val) == false && TRUE.equalsIgnoreCase(val) == false){
				throw new OperationFailedException("Invalid PAYG International Data Roaming configured: "+val+ ". Possible values are True/False", ResultCode.INVALID_INPUT_PARAMETER);
			}
		}

		@Override
		public void validateNumericValue(Long val) {
			//Number validation is not needed for this field
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			//Time stamp validation is not needed for this field
		}
	},
	BILLING_ACCOUNT_ID("Billing Account Id","BILLING_ACCOUNT_ID",PCRFKeyConstants.SUB_BILLING_ACCOUNT_ID, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, @Nullable String val, boolean validate) throws OperationFailedException {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setBillingAccountId(val);
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, @Nullable Long val, boolean validate) throws OperationFailedException {
			throw new UnsupportedOperationException("Operation not supported for field: " + BILLING_ACCOUNT_ID);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, @Nullable Timestamp val, boolean validate) throws OperationFailedException {
			throw new UnsupportedOperationException("Operation not supported for field: " + BILLING_ACCOUNT_ID);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getBillingAccountId();
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getBillingAccountId());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getBillingAccountId());
		}

		@Override
		public void validateStringValue(String val) throws OperationFailedException {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) throws OperationFailedException {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) throws OperationFailedException {
			// NO NEED TO IMPLEMENT
		}
	},
	SERVICE_INSTANCE_ID("Service Instance Id","SERVICE_INSTANCE_ID",PCRFKeyConstants.SUB_SERVICE_INSTANCE_ID, Types.VARCHAR) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, @Nullable String val, boolean validate) throws OperationFailedException {
			if (Strings.isNullOrBlank(val) == false && validate) {
				validateStringValue(val);
			}
			sprInfo.setServiceInstanceId(val);
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, @Nullable Long val, boolean validate) throws OperationFailedException {
			throw new UnsupportedOperationException("Operation not supported for field: " + SERVICE_INSTANCE_ID);
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, @Nullable Timestamp val, boolean validate) throws OperationFailedException {
			throw new UnsupportedOperationException("Operation not supported for field: " + SERVICE_INSTANCE_ID);
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return sprInfo.getServiceInstanceId();
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return Timestamp.valueOf(sprInfo.getBillingAccountId());
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getBillingAccountId());
		}

		@Override
		public void validateStringValue(String val) throws OperationFailedException {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) throws OperationFailedException {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) throws OperationFailedException {
			// NO NEED TO IMPLEMENT
		}
	},
	NEXT_BILL_DATE("Next Bill Date", "NEXTBILLDATE", PCRFKeyConstants.SUB_NEXT_BILL_DATE, Types.TIMESTAMP) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (val != null) {
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setNextBillDate(Timestamp.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return toString(sprInfo.getNextBillDate());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if (val != null) {
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setNextBillDate(new Timestamp(val));
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if (val != null && validate) {
				validateTimeStampValue(val);
			}
			sprInfo.setNextBillDate(val);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return sprInfo.getNextBillDate();
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getNextBillDate());
		}

		@Override
		public void validateStringValue(String val) {

		}

		@Override
		public void validateNumericValue(Long val) {

		}

		@Override
		public void validateTimeStampValue(Timestamp val) {

		}
	},
	BILL_CHANGE_DATE("Bill Change Date", "BILLCHANGEDATE", PCRFKeyConstants.SUB_BILL_CHANGE_DATE, Types.TIMESTAMP) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if (val != null) {
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setBillChangeDate(Timestamp.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return toString(sprInfo.getNextBillDate());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if (val != null) {
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setBillChangeDate(new Timestamp(val));
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if (val != null && validate) {
				validateTimeStampValue(val);
			}
			sprInfo.setBillChangeDate(val);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return sprInfo.getBillChangeDate();
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getBillChangeDate());
		}

		@Override
		public void validateStringValue(String val) {

		}

		@Override
		public void validateNumericValue(Long val) {

		}

		@Override
		public void validateTimeStampValue(Timestamp val) {

		}
	},
	CREATED_DATE("Created Date", "CREATED_DATE", PCRFKeyConstants.SUB_CREATEDDATE, Types.TIMESTAMP) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if(val != null){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setCreatedDate(Timestamp.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return toString(sprInfo.getCreatedDate());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setCreatedDate(new Timestamp(val));
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if (val != null && validate) {
				validateTimeStampValue(val);
			}
			sprInfo.setCreatedDate(val);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return sprInfo.getCreatedDate();
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getCreatedDate());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	},
	MODIFIED_DATE("Modified Date", "MODIFIED_DATE", PCRFKeyConstants.SUB_MODIFIEDDATE, Types.TIMESTAMP) {
		@Override
		public void setStringValue(SPRInfoImpl sprInfo, String val, boolean validate) {
			if(Strings.isNullOrBlank(val) == false){
				if (validate) {
					validateStringValue(val);
				}
				sprInfo.setModifiedDate(Timestamp.valueOf(val));
			}
		}

		@Override
		public String getStringValue(SPRInfo sprInfo) {
			return toString(sprInfo.getModifiedDate());
		}

		@Override
		public void setNumericValue(SPRInfoImpl sprInfo, Long val, boolean validate) {
			if(val != null){
				if (validate) {
					validateNumericValue(val);
				}
				sprInfo.setModifiedDate(new Timestamp(val));
			}
		}

		@Override
		public void setTimestampValue(SPRInfoImpl sprInfo, Timestamp val, boolean validate) {
			if (val != null && validate) {
				validateTimeStampValue(val);
			}
			sprInfo.setModifiedDate(val);
		}

		@Override
		public Timestamp getTimestampValue(SPRInfo sprInfo) {
			return sprInfo.getModifiedDate();
		}

		@Override
		public Long getNumericValue(SPRInfo sprInfo) {
			return toLong(sprInfo.getModifiedDate());
		}

		@Override
		public void validateStringValue(String val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateNumericValue(Long val) {
			// NO NEED TO IMPLEMENT
		}

		@Override
		public void validateTimeStampValue(Timestamp val) {
			// NO NEED TO IMPLEMENT
		}
	}
	;

	private static final int BILLING_DAY_END = 28;
	private static final int BILING_DAY_START = 1;
	private static final int MAX_MSISDN_LENGTH = 15;
	private static final int MIN_MSISDN_LENGTH = 5;
	private static final String FALSE = "FALSE";
	private static final String TRUE = "TRUE";
	private static final Pattern NUMERIC_REGEX = Pattern.compile("^[0-9]*$");
	private static final Pattern EMAIL_REGEX = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	public final String displayName;
	public final String columnName;
	public final PCRFKeyConstants pcrfKey;
	public final int type;
	private static LinkedHashMap<String, SPRFields> dataFieldMap;
	private static LinkedHashMap<String, SPRFields> columnNameToSPRFieldsMap;

	static {
		dataFieldMap = new LinkedHashMap<>(1, 1);
		columnNameToSPRFieldsMap = new LinkedHashMap<>(1,1);
		for (SPRFields sprField : values()) {
			dataFieldMap.put(sprField.name(), sprField);
			columnNameToSPRFieldsMap.put(sprField.columnName, sprField);
		}
	}

	private SPRFields(String displayName, String columnName, PCRFKeyConstants pcrfKey, int dataType) {
		this.displayName = displayName;
		this.columnName = columnName;
		this.pcrfKey = pcrfKey;
		this.type = dataType;
	}

	protected Long toLong(String stringValue) {
		return stringValue == null ? null : Long.valueOf(stringValue);
	}

	protected Long toLong(Timestamp timestamp) {
		return timestamp == null ? null : timestamp.getTime();
	}

	protected Long toLong(Integer integerValue) {
		return integerValue == null ? null : integerValue.longValue();
	}

	protected String toString(Long longvalue) {
		return longvalue == null ? null : longvalue.toString();
	}

	protected String toString(Timestamp timestampValue) {
		return timestampValue == null ? null : timestampValue.toString();
	}

	protected String toString(Integer integerValue) {
		return integerValue == null ? null : integerValue.toString();
	}

	public static Set<Entry<String, SPRFields>> getSPRFieldsEntrySet() {
		return dataFieldMap.entrySet();
	}

	public static SPRFields fromSPRFields(String name) {
		return dataFieldMap.get(name);
	}

	public static SPRFields fromColumnName(String columnName) {
		return columnNameToSPRFieldsMap.get(columnName);
	}

	public abstract void validateStringValue(String val) throws OperationFailedException;
	public abstract void validateNumericValue(Long val) throws OperationFailedException;
	public abstract void validateTimeStampValue(Timestamp val) throws OperationFailedException;

	public abstract void setStringValue(SPRInfoImpl sprInfo, @Nullable String val, boolean validate) throws OperationFailedException;
	public abstract void setNumericValue(SPRInfoImpl sprInfo, @Nullable Long val, boolean validate) throws OperationFailedException;
	public abstract void setTimestampValue(SPRInfoImpl sprInfo, @Nullable Timestamp val, boolean validate) throws OperationFailedException;

	public abstract String getStringValue(SPRInfo sprInfo);
	public abstract Timestamp getTimestampValue(SPRInfo sprInfo);
	public abstract Long getNumericValue(SPRInfo sprInfo);

}