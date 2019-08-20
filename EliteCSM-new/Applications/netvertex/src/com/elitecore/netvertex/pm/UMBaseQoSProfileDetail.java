
package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class UMBaseQoSProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.UMBaseQoSProfileDetailImpl implements QoSProfileDetail{

	private static final String MODULE = "UM-QOS-DETAIL";
	private final QoSSelectionWithoutUsage qoSSelectionWithoutUsage;
	private final QoSSelectionWithUsage qoSSelectionWithUsage;
	private UsageProvider usageProvider;

	public UMBaseQoSProfileDetail(String name, String packageName, QoSProfileAction action, String reason,
			@Nullable com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail allServiceQuotaProfileDetail,
			@Nullable Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
			boolean isUsageRequired, int fupLevel, Integer orderNo, boolean applyOnUsageUnavailability, UsageProvider usageProvider,@Nullable String redirectURL) {
		super(name, packageName, action, reason, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail, isUsageRequired, fupLevel, orderNo, applyOnUsageUnavailability,redirectURL);
		this.qoSSelectionWithoutUsage = new QoSSelectionWithoutUsage();
		this.qoSSelectionWithUsage = new QoSSelectionWithUsage();
		this.usageProvider = usageProvider;
	}
	
	public UMBaseQoSProfileDetail(String name, String pkgName, QoSProfileAction action, String reason, int fupLevel,
			@Nullable com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail allServiceQuotaProfileDetail,
			@Nullable Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
			boolean isUsageRequired, IPCANQoS sessionQoS, List<PCCRule> pccRules, boolean usageMonitoring, SliceInformation sliceInformation,
			Integer orderNo, boolean applyOnUsageUnavailability,UsageProvider usageProvider,@Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {
		super(name, pkgName, action, reason, fupLevel, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail, isUsageRequired, sessionQoS, pccRules, usageMonitoring, sliceInformation, orderNo, applyOnUsageUnavailability,redirectURL, chargingRuleBaseNames);
		this.qoSSelectionWithoutUsage = new QoSSelectionWithoutUsage();
		this.qoSSelectionWithUsage = new QoSSelectionWithUsage();
		this.usageProvider = usageProvider;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.netvertex.pm.QoSprofileDetail#apply(com.elitecore.netvertex.pm.PolicyContext, com.elitecore.netvertex.pm.QoSInformation)
	 */
	@Override
	public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult) {

		
		if(getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println("Level:" + getFUPLevel());
			policyContext.getTraceWriter().incrementIndentation();
		}

		try {
			if(getAllServiceQuotaProfileDetail() == null) {
				if(getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Applying qos selection without usage. Reason: Quota profile not attached to qos profile: " + getName());
				}
				return this.qoSSelectionWithoutUsage.apply(policyContext, qosInformation, null, previousQoSResult);
			} else {

				Map<String, SubscriberUsage> currentPackageUsage = null;
				try {
					currentPackageUsage = usageProvider.getCurrentUsage(policyContext, qosInformation);
				} catch (OperationFailedException e) {

					qosInformation.setUsageException(e);
					if (isApplyOnUsageUnavailability() == false) {
						if (getLogger().isErrorLogLevel()) {
							getLogger().error(MODULE, "QoS profile("+ getName() +") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + " on usage unavailability. Reason: Error while fetching usage, Cause: "
									+ qosInformation.getUsageException().getMessage());
						}

						if(getLogger().isInfoLogLevel()) {
							policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue+"(Error while fetching usage. Reason: " +e.getMessage() +")");
						}

						return SelectionResult.NOT_APPLIED;
					}
				}

				if (Maps.isNullOrEmpty(currentPackageUsage)) {
					return onUsageNotFound(policyContext, qosInformation, previousQoSResult, currentPackageUsage);
				} else {
					return this.qoSSelectionWithUsage.apply(policyContext, qosInformation, currentPackageUsage, previousQoSResult);
				}
			}
		} finally {
			if(getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().decrementIndentation();
			}
		}

	}

	private SelectionResult onUsageNotFound(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult, Map<String, SubscriberUsage> currentPackageUsage) {
		if (qosInformation.getUsageException() != null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Applying qos profile without usage for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + ". Reason: " + qosInformation.getUsageException().getMessage());
            }
        } else {

            if (isUsageRequired()) {
                if (getLogger().isErrorLogLevel()) {
                    getLogger().error(MODULE, "QoS profile("+ getName() +") cannot be applied for subscriber: "
                            + policyContext.getSPInfo().getSubscriberIdentity() + ". Reason: Usage is not found from DB");
                }

                if(getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue
                            +". Reason: Usage is required and it is not found from DB");
                }

                return SelectionResult.NOT_APPLIED;
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Applying qos profile without usage. Reason: usage not found for Quota profile: " + getQuotaProfileName());
            }
        }

		return this.qoSSelectionWithoutUsage.apply(policyContext, qosInformation, currentPackageUsage, previousQoSResult);
	}


	private String writeTrace(PCCRule pccRule) {
		return pccRule.getName() + "[Service:" + pccRule.getServiceName()
										+ ",QCI:" + pccRule.getQCI().stringVal 
										+ ",GBRUL:"  + pccRule.getGBRUL() 
										+ ",GBRDL:" + pccRule.getGBRDL() 
										+ ",MBRUL:" +pccRule.getMBRUL() 
										+ ",MBRDL:" + pccRule.getMBRDL() 
										+"]";
	}

	private interface QoSSelection extends Serializable {
		public SelectionResult apply(PolicyContext policyContext,
                                     QoSInformation qosInformation,
                                     Map<String, SubscriberUsage> currentPackageUsage, SelectionResult previousQoSResult);
	}
	
	private class QoSSelectionWithUsage implements QoSSelection{

		@Override
		public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, Map<String, SubscriberUsage> currentPackageUsage, SelectionResult previousQoSResult) {

			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Check default service usage exceeded");

			UMBaseQuotaProfileDetail allServiceQuotaProfileDetail = (UMBaseQuotaProfileDetail) getAllServiceQuotaProfileDetail();
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Subscriber's total usage: " + currentPackageUsage.get(allServiceQuotaProfileDetail .getUsageKey()));
			}
			
			TotalBalance qosbalance = ((UMBaseQuotaProfileDetail)getAllServiceQuotaProfileDetail()).getTotalBalance(currentPackageUsage.get(allServiceQuotaProfileDetail.getUsageKey()), policyContext);
			
			if(qosbalance.isExist() == false) {
				if(getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue+"(All service data exceeded)");
				}
				return SelectionResult.NOT_APPLIED;
			}

			QoSProfileDetail previousQoSProfileDetail  = qosInformation.getQoSProfileDetail();

			qosInformation.setQoSProfileDetail(UMBaseQoSProfileDetail.this);
			if (getUsageMonitoring() == true) {
				qosInformation.setQoSBalance(qosbalance);
			}

			if(getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().print("Session QoS: ");
			}
			if(getAction() == QoSProfileAction.REJECT) {
				if(getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().append("[Action: Reject, RejectCause: " + getRejectCause() + "]");
				}
				return SelectionResult.FULLY_APPLIED;
			} else {
			
				if(getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().append("[QCI:"+ getSessionQoS().getQCI().stringVal
														+ ", AAMBRUL:"+getSessionQoS().getAAMBRULInBytes()
														+", AAMBRDL:"+ getSessionQoS().getAAMBRDLInBytes()
														+ ", MBRUL:"+ getSessionQoS().getMBRULInBytes()
														+", MBRDL:"+ getSessionQoS().getMBRDLInBytes() + "]");
				}
			}

			if(getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().println();
			}

			if(getPccRules() == null && getChargingRuleBaseNames() == null) {
				return SelectionResult.FULLY_APPLIED;
			}

			SelectionResult pccRuleResult = evaluatePCCRules(qosbalance, currentPackageUsage, policyContext, qosInformation);

			if ( Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) == false /*CRBN is configured in this QoSProfileDetail(HSQ/FUP Level)*/
					&& previousQoSProfileDetail == null /*Previous HSQ/FUP level is not satisfied*/ ) {
				addChargingRuleBaseName(qosbalance, currentPackageUsage, policyContext, qosInformation);
			}

			if ( pccRuleResult == SelectionResult.FULLY_APPLIED ){
				return SelectionResult.FULLY_APPLIED;
			}

			if ( pccRuleResult == SelectionResult.NOT_APPLIED && Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) ){
				return SelectionResult.NOT_APPLIED;
			}

			return SelectionResult.PARTIALLY_APPLIED;
		}


		private SelectionResult evaluatePCCRules(TotalBalance allServiceQosbalance,
                                                 Map<String, SubscriberUsage> currentPackageUsage,
                                                 PolicyContext policyContext,
                                                 QoSInformation qosInformation) {

			SelectionResult result = SelectionResult.FULLY_APPLIED;

			List<PCCRule> pccRules = getPccRules();

			if ( Collectionz.isNullOrEmpty(pccRules) ) {
				if ( getLogger().isInfoLogLevel() ) {
					policyContext.getTraceWriter().println("No pcc rule Found");
				}
				return result;
			}

			boolean isAtleastOnePCCRuleSatisfied = false;

			for (int pccRuleIndex = 0; pccRuleIndex < pccRules.size(); pccRuleIndex++) {

					TotalBalance pccBalance = getServiceBalance(pccRules.get(pccRuleIndex).getServiceTypeId(), currentPackageUsage, allServiceQosbalance, policyContext);

					boolean isBalanceExist = pccBalance.isExist();

					if (isBalanceExist) {
						PCCRule satisfiedPCC = pccRules.get(pccRuleIndex);
						if (getLogger().isInfoLogLevel()) {
							policyContext.getTraceWriter().println("Satisfied:" + writeTrace(satisfiedPCC));
						}
						isAtleastOnePCCRuleSatisfied = true;
						qosInformation.add(satisfiedPCC);

						if (satisfiedPCC.getUsageMetering() != UsageMetering.DISABLE_QUOTA) {
							qosInformation.getPccBalanceMap().put(satisfiedPCC.getName(), pccBalance);
						}
					} else {
						if (getLogger().isInfoLogLevel()) {
							policyContext.getTraceWriter().println("Not Satisfied(Quota exceeded):" + writeTrace(pccRules.get(pccRuleIndex)));
						}

						result = SelectionResult.PARTIALLY_APPLIED;
					}
			}

			if (isAtleastOnePCCRuleSatisfied == false) {
				if (getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().println("Note: No pcc rule satisfied");
				}
				result = SelectionResult.NOT_APPLIED;
			}

			return result;

		}

		private void addChargingRuleBaseName(TotalBalance allServiceQosbalance,
											 Map<String, SubscriberUsage> currentPackageUsage,
											 PolicyContext policyContext,
											 QoSInformation qosInformation){

			List<ChargingRuleBaseName> chargingRuleBaseNames = getChargingRuleBaseNames();

			for (int chargingRuleBaseNameIndex = 0; chargingRuleBaseNameIndex < chargingRuleBaseNames.size(); chargingRuleBaseNameIndex++) {

				ChargingRuleBaseName chargingRuleBaseName = chargingRuleBaseNames.get(chargingRuleBaseNameIndex);

				Map<String, TotalBalance> monitoringKeyToBalanceMap = null;
				for (Map.Entry<String, DataServiceType> monitoringKeyServiceTypeEntry : chargingRuleBaseName.getMonitoringKeyServiceTypeMap().entrySet()) {

					String monitoringKey = monitoringKeyServiceTypeEntry.getKey();
					String serviceTypeId = monitoringKeyServiceTypeEntry.getValue().getDataServiceTypeID();

					TotalBalance serviceBalance = getServiceBalance(serviceTypeId, currentPackageUsage, allServiceQosbalance, policyContext);

					if (monitoringKeyToBalanceMap == null) {
						monitoringKeyToBalanceMap = new HashMap<String, TotalBalance>();
					}

					if (chargingRuleBaseName.getUsageMetering(monitoringKey) != UsageMetering.DISABLE_QUOTA) {
						monitoringKeyToBalanceMap.put(monitoringKey, serviceBalance);
					}
				}

				ChargingRuleBaseName tempChargingRuleBaseName = chargingRuleBaseNames.get(chargingRuleBaseNameIndex);
				if (getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().print("Satisfied ChargingRuleBaseName: ");
					chargingRuleBaseName.printToQosSelectionSummary(policyContext.getTraceWriter());
					policyContext.getTraceWriter().println();
				}

				qosInformation.add(tempChargingRuleBaseName);

				if (Maps.isNullOrEmpty(monitoringKeyToBalanceMap) == false) {
					qosInformation.getChargingRuleBaseNameToBalanceMap().put(tempChargingRuleBaseName.getName(), monitoringKeyToBalanceMap);
				}
			}
		}

		private TotalBalance getServiceBalance(String serviceTypeId,
											   Map<String, SubscriberUsage> currentPackageUsage,
											   TotalBalance allServiceQosbalance,
											   PolicyContext policyContext) {

			UMBaseQuotaProfileDetail serviceQuotaProfileDetail = null;
			if (getServiceToQuotaProfileDetail() != null) {
				serviceQuotaProfileDetail = (UMBaseQuotaProfileDetail) getServiceToQuotaProfileDetail().get(serviceTypeId);
			}

			TotalBalance pccBalance;

				/*
				 * when specific service quota not configured than
				 * 	ALL-Service quota will be considered for balance calculation
				 */
			if(serviceQuotaProfileDetail == null) {
				pccBalance = allServiceQosbalance;
			} else {
				pccBalance = serviceQuotaProfileDetail.getTotalBalance(currentPackageUsage.get(serviceQuotaProfileDetail.getUsageKey()), policyContext);
			}

			return pccBalance;
		}
		
	}

	private class QoSSelectionWithoutUsage implements QoSSelection {

		@Override
		public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, Map<String, SubscriberUsage> currentPackageUsage, SelectionResult previousQoSResult) {

			qosInformation.setQoSProfileDetail(UMBaseQoSProfileDetail.this);
			
			if(getAction() == QoSProfileAction.REJECT) {
				if (getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().println("Session QoS: [Action: Reject, RejectCause: " + getRejectCause() + "]");
				}
				return SelectionResult.FULLY_APPLIED;
			}
			
			if (Collectionz.isNullOrEmpty(getPccRules()) == false) {
				qosInformation.setPCCRules(getPccRules());
			}

			if (getLogger().isInfoLogLevel()) {
				IPCANQoS sessionQoS = getSessionQoS();
				policyContext.getTraceWriter().println("Session QoS [QCI:"+ sessionQoS.getQCI().stringVal
						+ ",AAMBRUL:"+ sessionQoS.getAAMBRULInBytes()
						+",AAMBRDL:"+ sessionQoS.getAAMBRDLInBytes() 
						+ ",MBRUL:"+ sessionQoS.getMBRULInBytes()
						+",MBRDL:"+ sessionQoS.getMBRDLInBytes() + "]");
				
				writeTrace(getPccRules(), policyContext.getTraceWriter());
			}


			if (Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) == false && previousQoSResult == SelectionResult.NOT_APPLIED) {
				qosInformation.setChargingRuleBaseNames(getChargingRuleBaseNames());
			}

			if (getLogger().isInfoLogLevel()) {
				writeTraceForChargingRuleBaseName(getChargingRuleBaseNames(), policyContext.getTraceWriter());
			}
			
			return SelectionResult.FULLY_APPLIED;

		}

		private void writeTraceForChargingRuleBaseName(List<ChargingRuleBaseName> chargingRuleBaseNames, IndentingWriter writer) {
			if (Collectionz.isNullOrEmpty(chargingRuleBaseNames) == false) {
				for(int index=0; index < chargingRuleBaseNames.size(); index++) {
					writer.print("Satisfied ChargingRuleBaseName: ");
					chargingRuleBaseNames.get(index).printToQosSelectionSummary(writer);
					writer.println();
				}
			}
		}

		private void writeTrace(List<PCCRule> pccRules, IndentingWriter writer) {
			if (Collectionz.isNullOrEmpty(pccRules) == false) {
				for(int index=0; index < pccRules.size(); index++) {
					writer.println("Satisfied PCC:" + UMBaseQoSProfileDetail.this.writeTrace(pccRules.get(index)));
				}
			}
		}
	}

}
