package com.elitecore.netvertex.pm;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.NullIndentingPrintWriter;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

public class QoSInformation {
	
	private static final String MODULE = "QoS-INFO";
	@Nullable private OperationFailedException usageException;
	@Nullable private PackageQoSSelectionData currentSelection;
	@Nullable private PackageQoSSelectionData previousSelection;
	private IndentingWriter policyHuntingTrace;
	private StringWriter stringWriter;

	private Map<String, TotalBalance> pccNameToBalanceMap;

	private Map<String, Map<String,TotalBalance>> chargingRuleBaseNameToBalanceMap;

	private Deque<FinalQoSSelectionData> eligibleQoSSelectionDatas;
	
	public QoSInformation() {
		pccNameToBalanceMap = Maps.newHashMap();
		chargingRuleBaseNameToBalanceMap = Maps.newHashMap();
		eligibleQoSSelectionDatas = new ArrayDeque<>(PackageType.values().length);
	}
	
	public void add(PCCRule pccRule) {
		currentSelection.add(pccRule);
	}

	public void add(ChargingRuleBaseName chargingRuleBaseName) {
		currentSelection.add(chargingRuleBaseName);
	}
	
	public void setUsageException(OperationFailedException e) {
		this.usageException = e ;	
	}
	
	public OperationFailedException getUsageException() {
		return usageException;
	}
	
	public Map<Long,PCCRule> getPCCRules() {
		return currentSelection.getPccRules();
	}
	
	public void setPCCRules(List<PCCRule> pccRules) {
		currentSelection.setPCCRules(pccRules);	
	}
	
	public void startPackageQoSSelection(UserPackage userPackage) {
		if(currentSelection != null) {
			swap(userPackage, null);
		} else {
			currentSelection = new PackageQoSSelectionData(userPackage);
		}
		
	}

	private void swap(UserPackage userPackage, String subscriptionId) {
		if(previousSelection != null) {
			PackageQoSSelectionData tempData = previousSelection;
			previousSelection = currentSelection;
			currentSelection = tempData;
			if(subscriptionId != null) {
				currentSelection.reset(userPackage,subscriptionId);
			} else {				
				currentSelection.reset(userPackage);
			}
		} else {
			previousSelection = currentSelection;
			if(subscriptionId != null) {
				currentSelection = new PackageQoSSelectionData(userPackage,subscriptionId);
			} else {					
				currentSelection = new PackageQoSSelectionData(userPackage);
			}
		}
	}
	
	public void startAddOnQoSSelection(UserPackage userPackage, String subscriptionId) {
		if(currentSelection != null) {
			swap(userPackage, subscriptionId);
		} else {
			currentSelection = new PackageQoSSelectionData(userPackage, subscriptionId);
		}
	}
	
	public void endQoSSelectionProcess() {
		
		if(currentSelection.getQosProfileDetail() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping qos selection for package(Name: " + currentSelection.getUserPackage().getName() + ", SubscriptionOrPackageId: "
						+ currentSelection.getSubscriptionIdOrPkgId() + "). Reason: No qos satisfied");
			}
			return;
		}
		
		if (previousSelection != null) {
			if (isSameType()) {
				QoSSelectionWithinGroupStrategy strategy = QoSSelectionStrategyFactory.instance(currentSelection.getPackageType());
				FinalQoSSelectionData finalSelectionOfGroup = eligibleQoSSelectionDatas.pollLast();
				strategy.select(currentSelection, finalSelectionOfGroup);
				eligibleQoSSelectionDatas.offerLast(finalSelectionOfGroup);
			} else {
				QoSSelectionWithinGroupStrategy strategy = QoSSelectionStrategyFactory.instance(currentSelection.getPackageType());
				FinalQoSSelectionData finalSelectionOfGroup = new FinalQoSSelectionData(currentSelection.getPackageType());
				eligibleQoSSelectionDatas.offerLast(finalSelectionOfGroup);
				strategy.select(currentSelection, finalSelectionOfGroup);
			}
		} else {
			QoSSelectionWithinGroupStrategy strategy = QoSSelectionStrategyFactory.instance(currentSelection.getPackageType());
			FinalQoSSelectionData finalSelectionOfGroup = new FinalQoSSelectionData(currentSelection.getPackageType());
			eligibleQoSSelectionDatas.offerLast(finalSelectionOfGroup);
			strategy.select(currentSelection, finalSelectionOfGroup);
		}
	}
	
	public FinalQoSSelectionData endProcess() {
		
		if (eligibleQoSSelectionDatas.isEmpty()) {
			return null;
		}
		
		FinalQoSSelectionData finalQoS = eligibleQoSSelectionDatas.poll();
		while (eligibleQoSSelectionDatas.isEmpty() == false) {
			FinalQoSSelectionData firstQos = eligibleQoSSelectionDatas.poll();
			QoSSelectionStrategyFactory.instance(firstQos.getPackageType(), finalQoS.getPackageType()).select(firstQos, finalQoS);
			finalQoS.setPackageType(firstQos.getPackageType());
		}
		
		return finalQoS;
	}
	
	private boolean isSameType() {
		return eligibleQoSSelectionDatas.peekLast() == null ? false :  eligibleQoSSelectionDatas.peekLast().getPackageType() == currentSelection.getPackageType();
	}

	public QoSProfileDetail getQoSProfileDetail() {
		return currentSelection == null ? null : currentSelection.getQosProfileDetail();
	}

	public void setQoSProfileDetail(QoSProfileDetail qosProfileDetail) {
		this.currentSelection.setQosProfileDetail(qosProfileDetail);
	}

	public IndentingWriter getTraceWriter() {
		if(policyHuntingTrace == null) {
			if(getLogger().isInfoLogLevel()) {
				stringWriter = new StringWriter();
				policyHuntingTrace = new IndentingPrintWriter(new PrintWriter(stringWriter));
			} else {
				policyHuntingTrace = new NullIndentingPrintWriter();
			}
		}
		return policyHuntingTrace;			
	}

	public String getTrace() {
		if(policyHuntingTrace == null) {
			return "";
		}
		return stringWriter.toString();
	}
	
	public Map<String, TotalBalance> getPccBalanceMap() {
		return pccNameToBalanceMap;
	}


	public String getCurrentSubscriptionOrPackageId() {
		return currentSelection.getSubscriptionIdOrPkgId();
	}

	public void setQoSBalance(TotalBalance qosbalance) {
		this.currentSelection.setQoSBalance(qosbalance);
}

	public TotalBalance getQosBalance(){
		return this.currentSelection.getQoSBalance();
	}

	public List<ChargingRuleBaseName> getChargingRuleBaseNames() {
		return this.currentSelection.getChargingRuleBaseNames();
	}

	public void setChargingRuleBaseNames(List<ChargingRuleBaseName> chargingRuleBaseNames) {
		this.currentSelection.setChargingRuleBaseNames(chargingRuleBaseNames);
	}

	public Map<String, Map<String, TotalBalance>> getChargingRuleBaseNameToBalanceMap() {
		return chargingRuleBaseNameToBalanceMap;
	}

	public void startProcessingQoSProfile(QoSProfile qoSProfile) {
		currentSelection.setPccProfile(qoSProfile);
	}

	public void endProcessingPCCProfile() {
		currentSelection.removePCCProfileIfNotSelected();
	}
}

