package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide validation on import operation of service type data
 * Created by Ishani on 15/9/16.
 */
public class DataServiceTypeValidatorExt implements Validator<DataServiceTypeDataExt,Object,DataServiceTypeDataExt> {

        private static final String MODULE = "SERVICE-TYPE-VALIDATOR-EXT";
        @Override
        public List<String> validate(DataServiceTypeDataExt serviceTypeImported, Object parentObject, DataServiceTypeDataExt pkgData, String action, SessionProvider session) {
            List<String> subReasons = new ArrayList<String>();
            try {
                String id = serviceTypeImported.getId();
                String name = serviceTypeImported.getName();

                BasicValidations.validateName(name, Discriminators.DATA_SERVICE_TYPE, subReasons);

                if (serviceTypeImported.getServiceIdentifier() == null) {
                    subReasons.add("Service Identifier must be configure with Data Service Type " + BasicValidations.printIdAndName(id, name));
                    return subReasons;
                }

                //Check if id is of all service and name is not All Service
                if (Strings.isNullOrBlank(id) == false && CommonConstants.ALL_SERVICE_ID.equals(id) && CommonConstants.ALL_SERVICE_NAME.equals(name) == false) {
                    subReasons.add("Updating name of All Service is not allowed");
                    return subReasons;
                }

                List<DataServiceTypeDataExt> serviceTypesWithSameIdentifier = ImportExportCRUDOperationUtil.filterEntityBasedOnProperty(DataServiceTypeDataExt.class, "serviceIdentifier", serviceTypeImported.getServiceIdentifier(), session);
                if (Collectionz.isNullOrEmpty(serviceTypesWithSameIdentifier) == false) {
                    DataServiceTypeDataExt existingServiceTypeWithIdentifier = serviceTypesWithSameIdentifier.get(0);
                    if (existingServiceTypeWithIdentifier.getName().equals(name) == false
                            && existingServiceTypeWithIdentifier.getId().equals(id) == false) {
                        subReasons.add("Service Identifier : " + serviceTypeImported.getServiceIdentifier() + " already exists");
                        return subReasons;
                    }
                }

                validateWithExistingServiceTypes(serviceTypeImported, action, session, subReasons);
            }catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Failed to validate data service type with " + BasicValidations.printIdAndName(serviceTypeImported.getId(), serviceTypeImported.getName()));
                LogManager.getLogger().trace(MODULE, e);
                subReasons.add("Failed to validate data service type with " + BasicValidations.printIdAndName(serviceTypeImported.getId(), serviceTypeImported.getName()) +". Kindly refer logs for further details");
            }
            return subReasons;
        }


    private void validateWithExistingServiceTypes(DataServiceTypeDataExt serviceTypeImported, String action, SessionProvider session, List<String> subReasons) throws Exception {
        String id = serviceTypeImported.getId();
        String name = serviceTypeImported.getName();

        if (Strings.isNullOrBlank(id) == false) {
            DataServiceTypeDataExt existingServiceType = ImportExportCRUDOperationUtil.get(DataServiceTypeDataExt.class, id, session);
            if (existingServiceType != null) {
                if (validateWithExistingServiceType(serviceTypeImported, existingServiceType, action, session, subReasons) == false) {
                    return;
                }
            }
        }

        List<DataServiceTypeDataExt> existingServiceTypes = ImportExportCRUDOperationUtil.getAll(DataServiceTypeDataExt.class, "name", name, session);
        if (Collectionz.isNullOrEmpty(existingServiceTypes) == false) {
            for (DataServiceTypeDataExt existingServiceType : existingServiceTypes) {
                if (validateWithExistingServiceType(serviceTypeImported, existingServiceType, action, session, subReasons) == false) {
                    return;
                }
            }

        }

    }

    private boolean validateWithExistingServiceType(DataServiceTypeDataExt serviceTypeImported, DataServiceTypeDataExt existingServiceType, String action, SessionProvider session, List<String> subReasons) throws Exception {
        if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingServiceType.getStatus())) {
            ImportExportCRUDOperationUtil.removeServiceType(existingServiceType, session);
            return true;
        }

        if (Strings.isNullOrBlank(serviceTypeImported.getId()) == false && existingServiceType.getName().equals(serviceTypeImported.getName()) && (existingServiceType.getId().equals(serviceTypeImported.getId()) == false)) {
            subReasons.add("Imported Data Service Type with " + BasicValidations.printIdAndName(serviceTypeImported.getId(), serviceTypeImported.getName()) + " conflicts with Existing Service Type "
                    + BasicValidations.printIdAndName(existingServiceType.getId(), existingServiceType.getName()));
            return false;
        }
        if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
            subReasons.add("Data Service Type " + BasicValidations.printIdAndName(serviceTypeImported.getId(), serviceTypeImported.getName()) + " already exists");
            return false;
        } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
            serviceTypeImported.setId(existingServiceType.getId());
            if (Collectionz.isNullOrEmpty(existingServiceType.getDefaultServiceDataFlows()) == false) {
                ImportExportCRUDOperationUtil.deleteDefaultServiceDataFlowInformation(existingServiceType.getId(), session);
            }
        }
        return true;
    }
}
