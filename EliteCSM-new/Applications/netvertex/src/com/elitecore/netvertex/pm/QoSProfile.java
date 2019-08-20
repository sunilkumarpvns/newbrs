package com.elitecore.netvertex.pm;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import javax.annotation.Nullable;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class QoSProfile extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "QOS-PROFILE";	
	
	public QoSProfile(String id, String name, String packageName, String packageId, @Nullable QuotaProfile quotaProfile, DataRateCard dataRateCard, List<String> accessNetwork, int duration,
					  QoSProfileDetail hsqLevelQoSDetail, List<QoSProfileDetail> fupLevelQoSDetails, LogicalExpression logicalExpression,
					  String additionalCondition, AccessTimePolicy accessTimePolicy) {
		super(id, name, packageName, packageId, quotaProfile, dataRateCard, accessNetwork, duration, hsqLevelQoSDetail, fupLevelQoSDetails, logicalExpression, additionalCondition, accessTimePolicy);
	}

	public boolean selectRule(PolicyContext policyContext, UserPackage userPackage, Subscription subscription) {

		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Applying " + getName() + " qos profile");

		if(validate(policyContext,policyContext.getTraceWriter()) == false) {
			return false;
		}


		return policyContext.process(this, userPackage, subscription);
	}

	@Override
	public com.elitecore.netvertex.pm.QoSProfileDetail getHSQLevelQoSDetail() {
		return (com.elitecore.netvertex.pm.QoSProfileDetail) super.getHSQLevelQoSDetail();
	}
	
	private boolean validate(PolicyContext policyContext, IndentingWriter indentingWriter) {

		if(getLogicalExpression() != null && getLogicalExpression().evaluate(policyContext.getValueProvider()) == false){
			if(getLogger().isInfoLogLevel()) {
				indentingWriter.println("Validation fail for additional condition:" + getAdditionalCondition());
			}

			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Validation fail for additional condition:" + getAdditionalCondition() );
			}
			return false;
		}
		
		if(getAccessTimePolicy() != null && getAccessTimePolicy().applyPolicy() == AccessTimePolicy.FAILURE){
			if(getLogger().isInfoLogLevel()) {
				indentingWriter.println("Validation fail for timebase condition");
			}

			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Validation fail for timebase condition");
			}
			return false;
		}
		
		String currentAccessNetwork = policyContext.getPCRFResponse().getAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val);
		List<String> accessNetwork = getAccessNetwork();
		if(accessNetwork != null) { 
				String accessNetworkDisplayVal = getAccessNetworkDisplayVal();
				if(currentAccessNetwork != null) {
					boolean isStatisfied = false;
					for(int index=0; index < accessNetwork.size(); index++) {
						if(accessNetwork.get(index).equalsIgnoreCase(currentAccessNetwork)) {					
							isStatisfied = true;
							break;
						}
					}
					
					if(isStatisfied == false) {						

						if(getLogger().isInfoLogLevel()) {
							indentingWriter.println("Validation fail for access network condition:" + accessNetworkDisplayVal  + ", current access network:" + currentAccessNetwork);
						}


						if(getLogger().isDebugLogLevel()) {
							getLogger().debug(MODULE, "Validation fail for access network condition:" + accessNetworkDisplayVal + ", current access network:" + currentAccessNetwork);
						}
						return false;
					}
					
				} else {
					if(getLogger().isInfoLogLevel()) {
						indentingWriter.println("Validation fail for access network condition:"
								+ accessNetworkDisplayVal + ", no access network received in request");
					}

					if(getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Validation fail for access network condition:" 
								+ accessNetworkDisplayVal + ", no access network received in request");
					}
					return false;	
				}
		}
		
		if (getDuration() > 0) {
			long timeDiff = checkDuration(policyContext.getSessionStartTime(), policyContext.getCurrentTime());
			
			if(timeDiff <= 0){

				if(getLogger().isInfoLogLevel()) {
					indentingWriter.println("Validation fail for duration condition");
				}

				if(getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Validation fail for duration condition. Reason: current time(" 
					+ policyContext.getCurrentTime().getTimeInMillis()+") and session start time(" + policyContext.getSessionStartTime().getTime() + " and duration is:" + getDuration());
				}
				return false;
			}
			
			policyContext.setTimeout(timeDiff);
		}


		return true;

	}

}

