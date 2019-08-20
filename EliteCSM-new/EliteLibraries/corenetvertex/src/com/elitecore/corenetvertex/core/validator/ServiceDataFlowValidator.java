package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ishani on 3/8/16.
 */
public class ServiceDataFlowValidator implements Validator<ServiceDataFlowData,PCCRuleData,ResourceData> {

    private static final String MODULE = "SERVICE-DATA_FLOW-VALIDATOR";
    private static final String PERMIT_IN = "permit in";
    private static final String PERMIT_OUT = "permit out";
    private static final String DENY_IN = "deny in";
    private static final String DENY_OUT = "deny out";
    private static final String TCP = "6";
    private static final String UDP = "17";
    private static final String IP = "IP";

    @Override
    public List<String> validate(ServiceDataFlowData serviceDataFlowData, PCCRuleData pccRuleData, ResourceData pkgData, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {
            String id = serviceDataFlowData.getServiceDataFlowId();

            validateServiceDataFlow(serviceDataFlowData, pccRuleData);
            if (Strings.isNullOrBlank(id) == false) {
                ServiceDataFlowData serviceDataFlow = ImportExportCRUDOperationUtil.get(ServiceDataFlowData.class, id, session);
                if (serviceDataFlow != null) {
                    if(serviceDataFlow.getPccRule().getId().equalsIgnoreCase(pccRuleData.getId()) == false){
                        subReasons.add("Service Data Flow with id(" +id+") already exists in pcc rule " + BasicValidations.printIdAndName(pccRuleData.getId(),pccRuleData.getName()));
                    }else if(serviceDataFlow.getPccRule().getName().equalsIgnoreCase(pccRuleData.getName()) == false){
                        subReasons.add("Service Data Flow with id(" +id+") already exists in pcc rule " + BasicValidations.printIdAndName(pccRuleData.getId(),pccRuleData.getName()));
                    }
                    if (CommonConstants.FAIL.equalsIgnoreCase(action) && CommonConstants.STATUS_DELETED.equalsIgnoreCase(pccRuleData.getStatus()) == false) {
                        subReasons.add("Service Data Flow with id(" + id + ") already exists for PCC Rule " + BasicValidations.printIdAndName(pccRuleData.getId(), pccRuleData.getName()));
                    }
                }
            }
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate service data flow with id (" + serviceDataFlowData.getServiceDataFlowId() + ") for pcc rule "+BasicValidations.printIdAndName(pccRuleData.getId(),pccRuleData.getName()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate Service Data Flow with id ("+ serviceDataFlowData.getServiceDataFlowId() +") for PCC Rule " + BasicValidations.printIdAndName(pccRuleData.getId(),pccRuleData.getName()));
        }
        return subReasons;
    }


    private void validateServiceDataFlow(ServiceDataFlowData sdf, PCCRuleData pccRuleData) {
        String flowAccess = sdf.getFlowAccess();
        if (Strings.isNullOrBlank(flowAccess)) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Flow Access must be provided with service data flow associated with PCC Rule " + BasicValidations.printIdAndName(pccRuleData.getId(), pccRuleData.getName()) + ". So taking default flow access as PERMIT IN");
            }
            sdf.setFlowAccess(PERMIT_IN);
        } else {
            if (PERMIT_IN.equals(flowAccess) == false && PERMIT_OUT.equals(flowAccess) == false && DENY_IN.equals(flowAccess) == false && DENY_OUT.equals(flowAccess) == false) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "Invalid Flow Access: " + flowAccess + " is configured in service data flow associated with PCC Rule " + BasicValidations.printIdAndName(pccRuleData.getId(), pccRuleData.getName()) + ". So taking default flow access as PERMIT IN");
                }
                sdf.setFlowAccess(PERMIT_IN);
            }
        }

        String protocol= sdf.getProtocol();
        if(Strings.isNullOrBlank(protocol)){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Protocol must be provided with service data flow associated with PCC Rule " + BasicValidations.printIdAndName(pccRuleData.getId(),pccRuleData.getName())+". So taking default protocol as IP");
            }
            sdf.setProtocol(IP);
        }else if(IP.equalsIgnoreCase(protocol) == false &&  protocol.equals(TCP) == false && protocol.equals(UDP) == false){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Invalid protocol: "+protocol +" configured in service data flow associated with PCC Rule " + BasicValidations.printIdAndName(pccRuleData.getId(),pccRuleData.getName())+ ". So taking default protocol as IP");
            }
            sdf.setProtocol(IP);
        }
    }

}
