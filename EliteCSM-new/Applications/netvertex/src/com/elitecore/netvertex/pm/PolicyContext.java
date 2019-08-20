package com.elitecore.netvertex.pm;

import com.elitecore.acesstime.TimePeriod;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.util.PCRFValueProvider;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public interface PolicyContext {

    PCRFValueProvider getValueProvider();
    
	boolean haltProcess();

	void resetHaltPrecess();

	void setHaltPrecess();
	
	SPRInfo getSPInfo();

	Calendar getCurrentTime();

	TimePeriod getCurrentTimePeriod();

	void setTimeout(long timeOutInSec);

	BasePackage getBasePackage();

	ServiceUsage getCurrentUsage() throws OperationFailedException;

    SubscriberNonMonitoryBalance getCurrentBalance() throws OperationFailedException;

	SubscriberMonetaryBalance getCurrentMonetaryBalance() throws OperationFailedException;

    SubscriberRnCNonMonetaryBalance getCurrentRnCBalance() throws OperationFailedException;
	
	LinkedHashMap<String, Subscription> getSubscriptions() throws OperationFailedException;

    Collection<Subscription> getPreTopUpSubscriptions();
    Collection<Subscription> getSpareTopUpSubscriptions();
    Collection<Subscription> getBoDPkgSubscriptions();
	
	long getTimeout();
	
	PCRFRequest getPCRFRequest();
	PCRFResponse getPCRFResponse();

	Date getSessionStartTime();

	void setBasePackage(BasePackage basePackage);

	void setRevalidationTime(Timestamp revalidationTime);

	Timestamp getRevalidationTime();

	boolean process(QoSProfile qoSProfile, UserPackage subscriptionPackage, Subscription subscription);

    IndentingPrintWriter getTraceWriter();

    String getTrace();

    void append(String trace);

    PolicyRepository getPolicyRepository();

    void preTopUpChecked();

    boolean isPreTopUpChecked();

    String getProductOfferId();

    default String writeTrace(PCCRule pccRule) {
        return pccRule.getName() + "[Service:" + pccRule.getServiceName()
                + ",QCI:" + pccRule.getQCI().stringVal
                + ",GBRUL:" + pccRule.getGBRUL()
                + ",GBRDL:" + pccRule.getGBRDL()
                + ",MBRUL:" + pccRule.getMBRUL()
                + ",MBRDL:" + pccRule.getMBRDL()
                + "]";
    }

    default void writeTrace(List<PCCRule> pccRules) {
        if (Collectionz.isNullOrEmpty(pccRules) == false) {
            for(int index=0; index < pccRules.size(); index++) {
                getTraceWriter().println("Satisfied PCC:" + writeTrace(pccRules.get(index)));
            }
        }
    }

    default void writeTraceForChargingRuleBaseName(List<ChargingRuleBaseName> chargingRuleBaseNames) {
        if (Collectionz.isNullOrEmpty(chargingRuleBaseNames) == false) {
            for (int index = 0; index < chargingRuleBaseNames.size(); index++) {
                getTraceWriter().print("Satisfied ChargingRuleBaseName: ");
                chargingRuleBaseNames.get(index).printToQosSelectionSummary(getTraceWriter());
                getTraceWriter().println();
            }
        }
    }
}
