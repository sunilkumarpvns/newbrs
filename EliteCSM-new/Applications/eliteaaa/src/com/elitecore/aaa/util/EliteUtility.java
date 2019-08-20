package com.elitecore.aaa.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.net.Inet4CIDRUtil;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class EliteUtility {

	private static final String MODULE = "ELITE-UTIL";
	public static void addSubscriberProfileRadiusVSA(ServiceRequest serviceRequest, AccountData accountData){
		if (accountData == null) {
			return;
		}
		RadServiceRequest request = (RadServiceRequest) serviceRequest;
		
		addRadiusAuthenticatedIdentityVSA(request);
		
		try {
			IRadiusAttribute radiusAttribute;
			if (accountData.getUserName() != null&& accountData.getUserName().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USERNAME);
				if (radiusAttribute == null) {
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USERNAME);
					if (radiusAttribute != null){
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getUserName());
				}
			}
			if (accountData.getAccountStatus() != null&& accountData.getAccountStatus().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_ACCOUNT_STATUS);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_ACCOUNT_STATUS);
					if (radiusAttribute != null){
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getAccountStatus());
				}
			}
			if (accountData.getImsi() != null&& accountData.getImsi().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_IMSI);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_IMSI);
					if (radiusAttribute != null){
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getImsi());
				}
			}
			if (accountData.getMeid() != null&& accountData.getMeid().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_MEID);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_MEID);
					if (radiusAttribute != null){
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getMeid());
				}
			}

			if (accountData.getMsisdn() != null&& accountData.getMsisdn().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_MSISDN);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_MSISDN);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getMsisdn());
				}
			}

			if (accountData.getMdn() != null&& accountData.getMdn().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_MDN);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_MDN);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getMdn());
				}
			}

			if (accountData.getDeviceVendor() != null&& accountData.getDeviceVendor().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_VENDOR);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_VENDOR);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getDeviceVendor());
				}
			}

			if (accountData.getDevicePort() !=null && accountData.getDevicePort().length()>0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_PORT);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_PORT);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getDevicePort());
				}
			}

			if (accountData.getDeviceName() != null&& accountData.getDeviceName().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_NAME);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_NAME);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getDeviceName());
				}
			}

			if (accountData.getDeviceVLAN() != null&& accountData.getDeviceVLAN().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_VLAN);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_DEVICE_VLAN);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getDeviceVLAN());
				}
			}
			if (accountData.getGeoLocation() != null&& accountData.getGeoLocation().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_GEO_LOCATION);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_GEO_LOCATION);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getGeoLocation());
				}
			}



			if (accountData.getImei() != null&& accountData.getImei().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_IMEI);
				if (radiusAttribute == null) {
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_IMEI);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getImei());
				}
			}

			if (accountData.getCustomerType() != null&& accountData.getCustomerType().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_ACCOUNT_TYPE);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_ACCOUNT_TYPE);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getCustomerType());
				}
			}

			radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_CREDIT_LIMIT);
			if (radiusAttribute == null){
				radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_CREDIT_LIMIT);
				if (radiusAttribute != null) {
					request.addInfoAttribute(radiusAttribute);
				}
			}
			if (radiusAttribute != null) {
				radiusAttribute.setLongValue(accountData.getCreditLimit());
			}

			if (accountData.getGroupName() != null&& accountData.getGroupName().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USER_GROUP);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USER_GROUP);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getGroupName());
				}
			}

			if (accountData.getExpiryDate() != null) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_EXPIRY_DATE);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_EXPIRY_DATE);	
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setLongValue(accountData.getExpiryDate().getTime() / 1000);
				}
			}

			if (accountData.getCUI() != null&& accountData.getCUI().length() > 0) {
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_IDENTITY);
				if (radiusAttribute == null) {
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_IDENTITY);
					if (radiusAttribute != null) {
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if (radiusAttribute != null) {
					radiusAttribute.setStringValue(accountData.getCUI());
				}
			}

			if(accountData.getConcurrentLoginPolicy() != null && accountData.getConcurrentLoginPolicy().length() > 0){
				radiusAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_CONCURRENT_LOGIN_POLICY_NAME);
				if (radiusAttribute == null){
					radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_CONCURRENT_LOGIN_POLICY_NAME);
					if(radiusAttribute != null){
						request.addInfoAttribute(radiusAttribute);
					}
				}
				if(radiusAttribute != null){
					radiusAttribute.setStringValue(accountData.getConcurrentLoginPolicy());
				}
			}

			if ((accountData.getParam1() != null && accountData.getParam1().trim().length() > 0)|| 
					(accountData.getParam2() != null && accountData.getParam2().trim().length() > 0) || 
					(accountData.getParam3() != null && accountData.getParam3().trim().length() > 0) ||
					(accountData.getParam4() != null && accountData.getParam4().trim().length() > 0)||
					(accountData.getParam5() != null && accountData.getParam5().trim().length() > 0)) {

				GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);
				if (profileAVPairGroupedAttr == null){
					profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);
					if (profileAVPairGroupedAttr != null){
						request.addInfoAttribute(profileAVPairGroupedAttr);
					}
				}
				if (profileAVPairGroupedAttr != null) {
					if (accountData.getParam1() != null&& accountData.getParam1().length() > 0) {
						IRadiusAttribute param1Attr = profileAVPairGroupedAttr.getAttribute(RadiusAttributeConstants.ELITE_PARAM1);
						if (param1Attr == null){
							param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
						}
						if (param1Attr != null) {
							profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
							param1Attr.setStringValue(accountData.getParam1());
						}
					}
					if (accountData.getParam2() != null&& accountData.getParam2().length() > 0) {
						IRadiusAttribute param2Attr = profileAVPairGroupedAttr.getAttribute(RadiusAttributeConstants.ELITE_PARAM2);
						if (param2Attr == null){
							param2Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM2);
						}
						if (param2Attr != null) {
							profileAVPairGroupedAttr.addTLVAttribute(param2Attr);
							param2Attr.setStringValue(accountData.getParam2());
						}
					}
					if (accountData.getParam3() != null&& accountData.getParam3().length() > 0) {
						IRadiusAttribute param3Attr = profileAVPairGroupedAttr.getAttribute(RadiusAttributeConstants.ELITE_PARAM3);
						if (param3Attr == null){
							param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
						}
						if (param3Attr != null) {
							profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
							param3Attr.setStringValue(accountData.getParam3());
						}
					}
					if (accountData.getParam4() != null&& accountData.getParam4().length() > 0) {
						IRadiusAttribute param4Attr = profileAVPairGroupedAttr.getAttribute(RadiusAttributeConstants.ELITE_PARAM4);
						if (param4Attr == null){
							param4Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM4);
						}
						if (param4Attr != null) {
							profileAVPairGroupedAttr.addTLVAttribute(param4Attr);
							param4Attr.setStringValue(accountData.getParam4());
						}
					}
					if (accountData.getParam5() != null&& accountData.getParam5().length() > 0) {
						IRadiusAttribute param5Attr = profileAVPairGroupedAttr.getAttribute(RadiusAttributeConstants.ELITE_PARAM5);
						if (param5Attr == null){
							param5Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM5);
						}
						if (param5Attr != null) {
							profileAVPairGroupedAttr.addTLVAttribute(param5Attr);
							param5Attr.setStringValue(accountData.getParam5());
						}
					}
				}
			}
			
			addFramedIPV4AddressAttribute(request, accountData);
			
			addFramedIPV6AddressAttribute(request, accountData);
			
			addFramedPoolAttribute(request, accountData);

		} catch (Exception e) {
			LogManager.getLogger().warn(MODULE, "Error during adding customer VSA for " + accountData.getUserIdentity() + " into radius packet,reason : "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private static void addRadiusAuthenticatedIdentityVSA(RadServiceRequest request) {
		String authenticatedIdentity = (String) request.getParameter(AAAServerConstants.CUI_KEY);
		if (Strings.isNullOrBlank(authenticatedIdentity)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No CUI_KEY found in request parameter, so will not add"
						+ RadiusAttributeConstants.ELITE_AUTHENTICATED_USER_ID_STR + " attribute in request");
			}
			return;
		}
		
		IRadiusAttribute authenticatedIdentityAttribute = request.getRadiusAttribute(
				RadiusAttributeConstants.ELITE_AUTHENTICATED_USER_ID, true);
		if (authenticatedIdentityAttribute == null) {
			authenticatedIdentityAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_AUTHENTICATED_USER_ID);
			if (authenticatedIdentityAttribute == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, RadiusAttributeConstants.ELITE_AUTHENTICATED_USER_ID_STR 
							+ " attribute not found in dictionary, so will not be added in request");
				}
				return;
			}
			request.addInfoAttribute(authenticatedIdentityAttribute);
		}
		authenticatedIdentityAttribute.setStringValue(authenticatedIdentity);
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Added or replaced " + RadiusAttributeConstants.ELITE_AUTHENTICATED_USER_ID_STR 
					+ " attribute in request with value: " + authenticatedIdentity);
		}
	}

	private static void addFramedPoolAttribute(RadServiceRequest request,
			AccountData accountData) {
		String framedPoolValue = accountData.getFramedPool();

		if (Strings.isNullOrEmpty(framedPoolValue)) {
			return;
		}
		
		IRadiusAttribute eliteFramedPoolAttribute = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_FRAMED_POOL_NAME);
		if (eliteFramedPoolAttribute == null) {
			
			eliteFramedPoolAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_FRAMED_POOL_NAME);;
			if (eliteFramedPoolAttribute == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "ELITE-FRAMED-POOL-NAME(21067:145) attribute not found in dictionary.");
				}
				return;
			}
			request.addInfoAttribute(eliteFramedPoolAttribute);
		}
		eliteFramedPoolAttribute.setStringValue(framedPoolValue);
	}

	private static void addFramedIPV6AddressAttribute(RadServiceRequest request,
			AccountData accountData) {
		String value = accountData.getFramedIPv6Prefix();

		if (Strings.isNullOrEmpty(value)) {
			return;
		}
		
		/**
		 * Framed-IPV6-Prefix can be present multiple time in request.
		 * we are appending framed ipv6 prefix in append operation of account 
		 * data using comma as a delimeter.
		 * 
		 * VSA from account data are added before and after append operation.
		 * so, if primary driver has prefix value 1,2,3 it will be added into
		 * request as VSA.
		 * 
		 * And if additional profile contains the 4,5,6 then after append 1,2,3,4,5,6
		 * so here it will be added 9 times with duplication of 1,2,3 
		 * 
		 * so remove all the added prefix as it is already available after append operation.
		 * hence finally prefix attribute is added into request 6 times.
		 *
		 */
		Collection<IRadiusAttribute> ipv6PrefixAttribute = request.getInfoAttributes(RadiusAttributeConstants.FRAMED_IPV6_PREFIX);
		if (Collectionz.isNullOrEmpty(ipv6PrefixAttribute) == false) {
			for (IRadiusAttribute iRadiusAttribute : ipv6PrefixAttribute) {
				request.removeAttribute(iRadiusAttribute, true);
			}
		}
		
		IRadiusAttribute framedIPv6PrefixAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_IPV6_PREFIX);
		if (framedIPv6PrefixAttribute == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-IPv6-Prefix(0:97) attribute not found in dictionary.");
			}
			return;
		}
		
		for (String attributeValue : split(value)) {
			framedIPv6PrefixAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_IPV6_PREFIX);
			framedIPv6PrefixAttribute.setStringValue(attributeValue);
			request.addInfoAttribute(framedIPv6PrefixAttribute);
		}
	}

	private static void addFramedIPV4AddressAttribute(RadServiceRequest request, AccountData accountData) {
		String framedIPv4AddressValue = accountData.getFramedIPv4Address();

		if (Strings.isNullOrEmpty(framedIPv4AddressValue)) {
			return;
		}

		Inet4CIDRUtil util;
		try {
			util = Inet4CIDRUtil.from(framedIPv4AddressValue);
		} catch (IllegalArgumentException ex) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Framed-IP-Address or Framed-IP-Netmask will not be added in request, Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
			return;
		}
	
		if (util.getIPAddress() != null) {
			IRadiusAttribute framedIPv4attribute = request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS, true);

			if (framedIPv4attribute == null){
				framedIPv4attribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS);
				if (framedIPv4attribute == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Framed-IP-Address attribute not found in dictionary, will not be added in request.");
					}	
					return;
				}
				request.addInfoAttribute(framedIPv4attribute);
			}
			framedIPv4attribute.setValueBytes(util.getIPAddress());
		}
		
		if (util.getNetmaskAddress() != null) {
			IRadiusAttribute framedIPNetmask = request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_NETMASK, true);

			if (framedIPNetmask == null){
				framedIPNetmask = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_IP_NETMASK);
				if (framedIPNetmask == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Framed-IP-Netmask attribute not found in dictionary, will not be added in request.");
					}	
					return;
				}
				request.addInfoAttribute(framedIPNetmask);
			}
			framedIPNetmask.setValueBytes(util.getNetmaskAddress());
		}
	}

	private static List<String> split(String value) {
		List<String> valueList = new ArrayList<String>();
		StringTokenizer strTokenizer = new StringTokenizer(value,";,");
		while (strTokenizer.hasMoreTokens()) {
			valueList.add(strTokenizer.nextToken());
		}
		return valueList;
	}

	public static void addSubscriberProfileDiameterVSA(ApplicationRequest serviceRequest, AccountData accountData) {
		if (accountData == null){
			return;
		}
		
		addDiameterAuthenticatedIdentityVSA(serviceRequest);
		
		ApplicationRequest request = (ApplicationRequest) serviceRequest;
		try {
			IDiameterAVP diameterAVP;
			if (accountData.getUserName() != null&& accountData.getUserName().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_USERNAME);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_USERNAME);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getUserName());
				}
			}
			if (accountData.getAccountStatus() != null&& accountData.getAccountStatus().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_ACCOUNT_STATUS);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_ACCOUNT_STATUS);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getAccountStatus());
				}
			}
			if (accountData.getImsi() != null&& accountData.getImsi().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_IMSI);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_IMSI);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getImsi());
				}
			}
			if (accountData.getMeid() != null&& accountData.getMeid().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_MEID);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_MEID);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getMeid());
				}
			}

			if (accountData.getMsisdn() != null&& accountData.getMsisdn().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_MSISDN);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_MSISDN);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getMsisdn());
				}
			}

			if (accountData.getMdn() != null&& accountData.getMdn().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_MDN);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_MDN);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getMdn());
				}
			}

			if (accountData.getImei() != null&& accountData.getImei().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_IMEI);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_IMEI);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getImei());
				}
			}



			if (accountData.getCustomerType() != null&& accountData.getCustomerType().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_ACCOUNT_TYPE);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_ACCOUNT_TYPE);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getCustomerType());
				}
			}

			diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_CREDIT_LIMIT);
			if (diameterAVP == null ) {
				diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_CREDIT_LIMIT);
				if (diameterAVP != null){
					request.addInfoAvp(diameterAVP);
				}
			}
			if (diameterAVP != null) {
				diameterAVP.setInteger(accountData.getCreditLimit());
			}

			if (accountData.getGroupName() != null&& accountData.getGroupName().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_USER_GROUP);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_USER_GROUP);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getGroupName());
				}
			}

			if (accountData.getExpiryDate() != null) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_EXPIRY_DATE);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_EXPIRY_DATE);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setInteger(accountData.getExpiryDate().getTime() / 1000);
				}
			}

			if (accountData.getCUI() != null&& accountData.getCUI().length() > 0) {
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_PROFILE_IDENTITY);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_IDENTITY);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if (diameterAVP != null) {
					diameterAVP.setStringValue(accountData.getCUI());
				}
			}

			if(accountData.getConcurrentLoginPolicy() != null && accountData.getConcurrentLoginPolicy().length() > 0){
				diameterAVP = request.getAVP(DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME);
				if (diameterAVP == null ) {
					diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME);
					if (diameterAVP != null){
						request.addInfoAvp(diameterAVP);
					}
				}
				if(diameterAVP != null){
					diameterAVP.setStringValue(accountData.getConcurrentLoginPolicy());
				}
			}

			if (Strings.isNullOrBlank(accountData.getParam1()) == false 
					|| Strings.isNullOrBlank(accountData.getParam2()) == false  
					|| Strings.isNullOrBlank(accountData.getParam3()) == false
					|| Strings.isNullOrBlank(accountData.getParam4()) == false 
					|| Strings.isNullOrBlank(accountData.getParam5()) == false ) {
				AvpGrouped avpGrouped  = (AvpGrouped) request.getAVP(DiameterAVPConstants.EC_PROFILE_AVPAIR_GROUP);
				if (avpGrouped == null){
					avpGrouped = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_AVPAIR_GROUP);
					if (avpGrouped != null){
						request.addInfoAvp(avpGrouped);
					}
				}
				if (avpGrouped != null) {
					if (accountData.getParam1() != null&& accountData.getParam1().length() > 0) {
						IDiameterAVP param1Avp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_PROFILE_PARAM1);
						if (param1Avp == null){
							param1Avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_PARAM1);
							if (param1Avp != null){
								avpGrouped.addSubAvp(param1Avp);
							}
						}
						if (param1Avp != null) {
							param1Avp.setStringValue(accountData.getParam1());
						}
					}
					if (accountData.getParam2() != null&& accountData.getParam2().length() > 0) {
						IDiameterAVP param2Avp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_PROFILE_PARAM2);
						if (param2Avp == null){
							param2Avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_PARAM2);
							if (param2Avp != null){
								avpGrouped.addSubAvp(param2Avp);
							}
						}
						if (param2Avp != null) {
							param2Avp.setStringValue(accountData.getParam2());
						}
					}
					if (accountData.getParam3() != null&& accountData.getParam3().length() > 0) {
						IDiameterAVP param3Avp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_PROFILE_PARAM3);
						if (param3Avp == null){
							param3Avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_PARAM3);
							if (param3Avp != null){
								avpGrouped.addSubAvp(param3Avp);
							}
						}
						if (param3Avp != null) {
							param3Avp.setStringValue(accountData.getParam3());
						}
					}
					if (Strings.isNullOrBlank(accountData.getParam4()) == false) {
						IDiameterAVP param4Avp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_PROFILE_PARAM4);
						if (param4Avp == null){
							param4Avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_PARAM4);
							if (param4Avp != null){
								avpGrouped.addSubAvp(param4Avp);
							}
						}
						if (param4Avp != null) {
							param4Avp.setStringValue(accountData.getParam4());
						}
					}
					if (Strings.isNullOrBlank(accountData.getParam5()) == false) {
						IDiameterAVP param5Avp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_PROFILE_PARAM5);
						if (param5Avp == null){
							param5Avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROFILE_PARAM5);
							if (param5Avp != null){
								avpGrouped.addSubAvp(param5Avp);
							}
						}
						if (param5Avp != null) {
							param5Avp.setStringValue(accountData.getParam5());
						}
					}
				}
			}
			
			
			addFramedIPv4Avp(accountData, request);
			
			addFrameIPv6Avp(accountData, request);

			addFramedPoolAvp(accountData, request);
			
		} catch (Exception e) {
			LogManager.getLogger().warn(MODULE, "Error during adding customer VSA for " + accountData.getUserIdentity() + " into Diameter packet,reason : "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}


	private static void addFramedPoolAvp(AccountData accountData, ApplicationRequest request) {
		String framedPoolIDValue = accountData.getFramedPool();
		
		if (Strings.isNullOrBlank(framedPoolIDValue)) {
			return;
		}
		
		IDiameterAVP eliteFramedPoolAvp = request.getInfoAvp(DiameterAVPConstants.EC_FRAMED_POOL_NAME);

		if (eliteFramedPoolAvp == null) {
			eliteFramedPoolAvp = createAVP(DiameterAVPConstants.EC_FRAMED_POOL_NAME);
			if (eliteFramedPoolAvp == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "EC-FRAMED-POOL avp not found in dictionary, so will not be added.");
				}
				return;
			} 
			request.addInfoAvp(eliteFramedPoolAvp);
		}
		
		eliteFramedPoolAvp.setStringValue(framedPoolIDValue);
	}


	private static void addFrameIPv6Avp(AccountData accountData, ApplicationRequest request) {
		
		String framedIPv6PrefixValue = accountData.getFramedIPv6Prefix();
		
		if (Strings.isNullOrBlank(framedIPv6PrefixValue)) {
			return;
		}
		
		List<IDiameterAVP> avpList = request.getAVPList(DiameterAVPConstants.FRAMED_IPV6_PREFIX, true);
		/**
		 * Framed-IPV6-Prefix can be present multiple time in request.
		 * we are appending framed ipv6 prefix in append operation of account 
		 * data using comma.
		 * 
		 * VSA from account data are added before append as well as after append
		 * so, if primary driver has prefix value 1,2,3 it will be added into
		 * request as VSA.
		 * 
		 * and additional profile contains the 4,5,6 then after append 1,2,3,4,5,6
		 * so here it will be added 9 times with duplication of 1,2,3 
		 * 
		 * so remove all the added prefix as it is already available after append operation.
		 * hence finally prefix attribute is added into request 6 times.
		 *
		 */
		if (Collectionz.isNullOrEmpty(avpList) == false) {
			for (IDiameterAVP iDiameterAVP : avpList) {
				request.getDiameterRequest().removeAVP(iDiameterAVP, true);
			}
		}
		
		IDiameterAVP eliteFramedIPV6Avp = createAVP(DiameterAVPConstants.FRAMED_IPV6_PREFIX);

		if (eliteFramedIPV6Avp == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-IPV6-Prefix avp not found in dictionary, so will not be added.");
			}
			return;
		}

		for (String ipv6PrefixValue : split(framedIPv6PrefixValue)) {
			eliteFramedIPV6Avp = createAVP(DiameterAVPConstants.FRAMED_IPV6_PREFIX);
			eliteFramedIPV6Avp.setStringValue(ipv6PrefixValue);
			request.addInfoAvp(eliteFramedIPV6Avp);
		}
	}


	private static void addFramedIPv4Avp(AccountData accountData,
			ApplicationRequest request) {

		String framedIPv4AddressValue = accountData.getFramedIPv4Address();
		if (Strings.isNullOrBlank(framedIPv4AddressValue)) {
			return;
		}

		Inet4CIDRUtil util;
		try {
			util = Inet4CIDRUtil.from(framedIPv4AddressValue);
		} catch (IllegalArgumentException ex) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Framed-IP-Address or Framed-IP-Netmask will not be added in request, Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
			return;
		}
		
		if (util.getIPAddress() != null) {
			IDiameterAVP framedIPV4Avp = request.getAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS, true);
			
			if (framedIPV4Avp == null) {
				framedIPV4Avp = createAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS);
				if (framedIPV4Avp == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Framed-IP-Address avp not found in dictionary, wo will not be added.");
					}
					return;
				}
				request.addInfoAvp(framedIPV4Avp);
			}
			framedIPV4Avp.setValueBytes(util.getIPAddress());
		}

		if (util.getNetmaskAddress() != null) {
			IDiameterAVP framedIPNetmaskAvp = request.getAVP(DiameterAVPConstants.FRAMED_IP_NETMASK, true);

			if (framedIPNetmaskAvp == null) {
				framedIPNetmaskAvp = createAVP(DiameterAVPConstants.FRAMED_IP_NETMASK);
				if (framedIPNetmaskAvp == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Framed-IP-Netmask avp not found in dictionary, so will not be added");
					} 
					return;
				}
				request.addInfoAvp(framedIPNetmaskAvp);
			}
			framedIPNetmaskAvp.setValueBytes(util.getNetmaskAddress());
		}
	}
	
	private static IDiameterAVP createAVP(String attribute) {
		return DiameterDictionary.getInstance().getKnownAttribute(attribute);
	}
	
	private static void addDiameterAuthenticatedIdentityVSA(ApplicationRequest request) {
		String authenticatedIdentity = (String) request.getParameter(AAAServerConstants.CUI_KEY);
		if (Strings.isNullOrBlank(authenticatedIdentity)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No CUI_KEY found in request parameter, so will not add"
						+ DiameterAVPConstants.EC_AUTHENTICATED_USER_ID_STR + " avp in request");
			}
			return;
		}
		
		IDiameterAVP authenticatedIdentityAttribute = request.getAVP(
				DiameterAVPConstants.EC_AUTHENTICATED_USER_ID, true);
		if (authenticatedIdentityAttribute == null) {
			authenticatedIdentityAttribute = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_AUTHENTICATED_USER_ID);
			if (authenticatedIdentityAttribute == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, DiameterAVPConstants.EC_AUTHENTICATED_USER_ID_STR 
							+ " avp not found in dictionary, so will not be added in request");
				}
				return;
			}
			request.addInfoAvp(authenticatedIdentityAttribute);
		}
		authenticatedIdentityAttribute.setStringValue(authenticatedIdentity);
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Added or replaced " + DiameterAVPConstants.EC_AUTHENTICATED_USER_ID_STR 
					+ " avp in request with value: " + authenticatedIdentity);
		}
	}

	public static String getServicePolicyOrder(List<String> serviceFlowOrderList) {
		String servicePolicyOrder = "-";
		StringBuilder sb = new StringBuilder();
		if(Collectionz.isNullOrEmpty(serviceFlowOrderList) == false){
			for (String handlerName : serviceFlowOrderList){
				sb.append(handlerName + "<br/>");
			}
			servicePolicyOrder = sb.toString();
		}
		return servicePolicyOrder;
	}
}
