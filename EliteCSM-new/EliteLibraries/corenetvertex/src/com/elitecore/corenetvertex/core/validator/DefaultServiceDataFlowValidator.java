package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.dataservicetype.DefaultServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DefaultServiceDataFlowExt;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides validation on Default Service Data Flow imported with Data Service Type
 * Created by Ishani on 15/9/16.
 */
public class DefaultServiceDataFlowValidator implements Validator<DefaultServiceDataFlowExt,DataServiceTypeDataExt,DataServiceTypeDataExt> {

        private static final String MODULE = "DEF-SERVICE-DATA_FLOW-VALIDATOR";
        private static final String PERMIT_IN = "permit in";
        private static final String PERMIT_OUT = "permit out";
        private static final String DENY_IN = "deny in";
        private static final String DENY_OUT = "deny out";
        private static final String TCP = "6";
        private static final String UDP = "17";
        private static final String IP = "IP";

        @Override
        public List<String> validate(DefaultServiceDataFlowExt defaultServiceDataFlowData, DataServiceTypeDataExt serviceType, DataServiceTypeDataExt dataServiceTypeDataExt, String action, SessionProvider session) {
            List<String> subReasons = new ArrayList<String>();
            try {
                String id = defaultServiceDataFlowData.getServiceDataFlowId();
                validateServiceDataFlow(defaultServiceDataFlowData, serviceType);
                if (Strings.isNullOrBlank(id) == false) {
                    DefaultServiceDataFlowData serviceDataFlow = ImportExportCRUDOperationUtil.get(DefaultServiceDataFlowData.class, id, session);
                        if(serviceDataFlow != null) {
                            if(serviceDataFlow.getDataServiceTypeData().getId().equalsIgnoreCase(serviceType.getId()) == false){
                                subReasons.add("Service Data Flow with id(" +id+") already exists in Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(),serviceType.getName()));
                            }else if(serviceDataFlow.getDataServiceTypeData().getName().equalsIgnoreCase(serviceType.getName()) == false){
                                subReasons.add("Service Data Flow with id(" +id+") already exists in Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(),serviceType.getName()));
                            }
                            if (CommonConstants.FAIL.equalsIgnoreCase(action) && CommonConstants.STATUS_DELETED.equalsIgnoreCase(serviceDataFlow.getDataServiceTypeData().getStatus()) == false) {
                                subReasons.add("Default Service Data Flow with id(" + id + ") already exists for Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(), serviceType.getName()));
                            }
                        }
                }
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Failed to validate default service data flow with id (" + defaultServiceDataFlowData.getServiceDataFlowId() + ") for Data Service Type "+BasicValidations.printIdAndName(serviceType.getId(), serviceType.getName()));
                LogManager.getLogger().trace(MODULE, e);
                subReasons.add("Failed to validate Default Service Data Flow with id ("+ defaultServiceDataFlowData.getServiceDataFlowId() +") for Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(), serviceType.getName())+". Kindly refer logs for further details");
            }
            return subReasons;
        }


        private void validateServiceDataFlow(DefaultServiceDataFlowExt sdf, DataServiceTypeDataExt serviceType) {
            String flowAccess = sdf.getFlowAccess();
            if (Strings.isNullOrBlank(flowAccess)) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "Flow Access must be provided with default service data flow associated with Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(), serviceType.getName()) + ". So taking default flow access as PERMIT IN");
                }
                sdf.setFlowAccess(PERMIT_IN);
            } else {
                if (PERMIT_IN.equals(flowAccess) == false && PERMIT_OUT.equals(flowAccess) == false && DENY_IN.equals(flowAccess) == false && DENY_OUT.equals(flowAccess) == false) {
                    if (LogManager.getLogger().isDebugLogLevel()) {
                        LogManager.getLogger().debug(MODULE, "Invalid Flow Access: " + flowAccess + " is configured in service data flow associated with Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(), serviceType.getName()) + ". So taking default flow access as PERMIT IN");
                    }
                    sdf.setFlowAccess(PERMIT_IN);
                }
            }

            String protocol= sdf.getProtocol();
            if(Strings.isNullOrBlank(protocol)){
                if(LogManager.getLogger().isDebugLogLevel()){
                    LogManager.getLogger().debug(MODULE, "Protocol must be provided with default service data flow associated with Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(), serviceType.getName())+". So taking default protocol as IP");
                }
                sdf.setProtocol(IP);
            }else if(IP.equalsIgnoreCase(protocol) == false &&  protocol.equals(TCP) == false && protocol.equals(UDP) == false){
                if(LogManager.getLogger().isDebugLogLevel()){
                    LogManager.getLogger().debug(MODULE, "Invalid protocol: "+protocol +" configured in default service data flow associated with Data Service Type " + BasicValidations.printIdAndName(serviceType.getId(), serviceType.getName())+ ". So taking default protocol as IP");
                }
                sdf.setProtocol(IP);
            }
        }

    }

