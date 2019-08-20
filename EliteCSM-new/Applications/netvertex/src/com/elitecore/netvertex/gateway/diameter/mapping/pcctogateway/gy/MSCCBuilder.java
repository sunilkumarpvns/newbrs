package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gy;

import java.util.List;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitAction;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitIndication;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.RedirectAddressType;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMapping;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class MSCCBuilder {

	private static final String MODULE = "MSCC-Builder";

	public void addMSCCAVPs(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {


		PCRFResponse response = valueProvider.getPcrfResponse();
		DiameterPacket diameterPacket = valueProvider.getDiameterPacket();
		List<MSCC> grantedMSCCs = response.getGrantedMSCCs();

		if (Collectionz.isNullOrEmpty(grantedMSCCs)) {
			return;
		}

		for (int i = 0; i < grantedMSCCs.size(); i++) {
			applyMSCC(grantedMSCCs.get(i), diameterPacket, accumalator, valueProvider);
		}
	}

	/**
	 *  Multiple-Services-Credit-Control ::= < AVP Header: 456 >
                                           [ Granted-Service-Unit ]
                                          *[ Service-Identifier ]
                                           [ Rating-Group ]
                                          *[ G-S-U-Pool-Reference ]
                                           [ Validity-Time ]
                                           [ Result-Code ]
                                           [ Final-Unit-Indication ]
                                          *[ AVP ]
	 * @param accumalator
	 */
	private void applyMSCC(MSCC mscc, DiameterPacket diameterPacket, AvpAccumalator accumalator, DiameterPacketMappingValueProvider valueProvider) {

		AvpGrouped msccAVP = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);

		msccAVP.addSubAvp(DiameterAVPConstants.RATING_GROUP, mscc.getRatingGroup());

		if(mscc.getValidityTime() > 0) {
			msccAVP.addSubAvp(DiameterAVPConstants.VALIDITY_TIME, mscc.getValidityTime());
		}

		if (Collectionz.isNullOrEmpty(mscc.getServiceIdentifiers()) == false) {
			for (long serviceIdentifier : mscc.getServiceIdentifiers()) {
				msccAVP.addSubAvp(DiameterAVPConstants.SERVICE_IDENTIFIER, serviceIdentifier);
			}
		}

		msccAVP.addSubAvp(ResultCodeMapping.getInstance().getResultCodeAVP(mscc.getResultCode().getVal(), (int) diameterPacket.getApplicationID()));

		if (mscc.getTimeQuotaThreshold() > 0) {
			msccAVP.addSubAvp(DiameterAVPConstants.TIME_QUOTA_THRESHOLD, mscc.getTimeQuotaThreshold());
		}

		if (mscc.getVolumeQuotaThreshold() > 0) {
			msccAVP.addSubAvp(DiameterAVPConstants.VOLUME_QUOTA_THRESHOLD, mscc.getVolumeQuotaThreshold());
		}

		if (mscc.getGrantedServiceUnits() != null) {
			IDiameterAVP grantedServiceUnitsAVP = getGrantedServiceUnitAVP(mscc.getGrantedServiceUnits(), valueProvider);
			if (grantedServiceUnitsAVP != null) {
				msccAVP.addSubAvp(grantedServiceUnitsAVP);
			}
		}

		IDiameterAVP finalUnitindicationAVP = getFinalUnitIndicationAVP(mscc.getFinalUnitIndiacation());
		if (finalUnitindicationAVP != null) {
			msccAVP.addSubAvp(finalUnitindicationAVP);
		}

		accumalator.add(msccAVP);
	}

	private IDiameterAVP getFinalUnitIndicationAVP(FinalUnitIndication finalUnitIndiacation) {

		if (finalUnitIndiacation == null) {
			return null;
		}

		AvpGrouped finalUnitIndicationAVP = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.FINAL_UNIT_INDICATION);
		FinalUnitAction action = finalUnitIndiacation.getAction();

		IDiameterAVP finalUnitActionAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.FINAL_UNIT_ACTION);
		finalUnitActionAVP.setInteger(action.val);
		finalUnitIndicationAVP.addSubAvp(finalUnitActionAVP);

		if (action == FinalUnitAction.TERMINATE) {
			return finalUnitIndicationAVP;
		}

		if (action == FinalUnitAction.REDIRECT) {
			RedirectAddressType redirectAddressType = finalUnitIndiacation.getRedirectAddressType();
			AvpGrouped redirectServerAVP = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.REDIRECT_SERVER);
			redirectServerAVP.addSubAvp(DiameterAVPConstants.REDIRECT_ADDRESS_TYPE, redirectAddressType.val);
			String redirectServerAddress = finalUnitIndiacation.getRedirectServerAddress();
			redirectServerAVP.addSubAvp(DiameterAVPConstants.REDIRECT_SERVER_ADDRESS, redirectServerAddress);
			finalUnitIndicationAVP.addSubAvp(redirectServerAVP);
			return finalUnitIndicationAVP;
		}

		if (action == FinalUnitAction.RESTRICT_ACCESS) {
			List<String> restrictionFilterRules = finalUnitIndiacation.getRestrictionFilterRules();

			if (Collectionz.isNullOrEmpty(restrictionFilterRules) == false) {

				for (int i = 0; i < restrictionFilterRules.size(); i++) {
					IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.RESTRICTION_FILTER_RULE);
					diameterAVP.setStringValue(restrictionFilterRules.get(i));
					finalUnitIndicationAVP.addSubAvp(diameterAVP);
				}
			}
			return finalUnitIndicationAVP;
		}

		List<String> filterIds = finalUnitIndiacation.getFilterIds();
		if (Collectionz.isNullOrEmpty(filterIds) == false) {
			for (int index = 0; index < filterIds.size(); index++) {
				finalUnitIndicationAVP.addSubAvp(DiameterAVPConstants.FILTER_ID, filterIds.get(index));
			}
		}

		return null;
	}

	private IDiameterAVP getGrantedServiceUnitAVP(GyServiceUnits grantedServiceUnits, DiameterPacketMappingValueProvider valueProvider) {

		if (grantedServiceUnits == null) {
			return null;
		}

		AvpGrouped grantedServiceUnitAVP = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GRANTED_SERVICE_UNIT);

		boolean isUsageGranted = false;
		if (grantedServiceUnits.getVolume() > 0) {

			VolumeUnitType volumeUnitType = getVolumeUnitType(grantedServiceUnits, valueProvider);

			if(VolumeUnitType.TOTAL == volumeUnitType) {
				grantedServiceUnitAVP.addSubAvp(DiameterAVPConstants.CC_TOTAL_OCTETS, grantedServiceUnits.getVolume());
                isUsageGranted = true;
			}else if(VolumeUnitType.DOWNLOAD == volumeUnitType){
				grantedServiceUnitAVP.addSubAvp(DiameterAVPConstants.CC_OUTPUT_OCTETS, grantedServiceUnits.getVolume());
                isUsageGranted = true;
			}else if(VolumeUnitType.UPLOAD == volumeUnitType){
				grantedServiceUnitAVP.addSubAvp(DiameterAVPConstants.CC_INPUT_OCTETS, grantedServiceUnits.getVolume());
                isUsageGranted = true;
			}
		}

		if (grantedServiceUnits.getTime() > 0) {
			grantedServiceUnitAVP.addSubAvp(DiameterAVPConstants.CC_TIME, grantedServiceUnits.getTime());
			isUsageGranted = true;
		}

		if (isUsageGranted == true) {
			return grantedServiceUnitAVP;
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Skipped granted service unit. Reason: Granted Service Unit not found in MSCC");
		}

		return null;
	}

	private VolumeUnitType getVolumeUnitType(GyServiceUnits grantedServiceUnits, DiameterPacketMappingValueProvider valueProvider) {
        QuotaProfile quotaProfile = valueProvider.getControllerContext()
                .getPolicyManager().getQuotaProfile(grantedServiceUnits.getPackageId(), grantedServiceUnits.getQuotaProfileIdOrRateCardId());

        if (quotaProfile != null) {
            RnCQuotaProfileDetail rnCQuotaProfileDetail = (RnCQuotaProfileDetail) quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values().iterator().next();
            return rnCQuotaProfileDetail.getUnitType();
        }

        DataRateCard dataRateCard = valueProvider.getControllerContext()
                .getPolicyManager().getDataRateCard(grantedServiceUnits.getPackageId(), grantedServiceUnits.getQuotaProfileIdOrRateCardId());

        if (dataRateCard != null) {
            return VolumeUnitType.TOTAL;
        }

        return null;
	}
}
