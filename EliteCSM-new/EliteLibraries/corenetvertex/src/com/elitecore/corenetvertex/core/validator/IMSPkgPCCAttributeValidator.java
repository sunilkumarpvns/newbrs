package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgPCCAttributeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A validator class that validates IMSPkgPCCAttributes
 */
public class IMSPkgPCCAttributeValidator implements Validator<IMSPkgPCCAttributeData, IMSPkgServiceData, IMSPkgData> {

    private static final String MODULE = "IMS-PKG-PCC-ATTR-VALIDATION";
    @Override
    public List<String> validate(IMSPkgPCCAttributeData imsPkgPCCAttributeImported, IMSPkgServiceData imsPkgServiceData, IMSPkgData imsPkgData, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {
            String id = imsPkgPCCAttributeImported.getId();
            setDefaultValuesForMandatoryParameters(imsPkgPCCAttributeImported,imsPkgServiceData,subReasons);
            if (Strings.isNullOrBlank(id) == false) {
                IMSPkgPCCAttributeData imsPkgPCCAttributeData = ImportExportCRUDOperationUtil.get(imsPkgPCCAttributeImported.getClass(), id, session);
                if (imsPkgPCCAttributeData != null) {
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("IMS Package PCC Attribute id ( "+id +") already exists for QoS Profile " + BasicValidations.printIdAndName(imsPkgServiceData.getId(), imsPkgServiceData.getName()));
                    }
                }
            }

        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate ims package pcc attribute with id ("+imsPkgPCCAttributeImported.getId() +") for IMS Package Service Data " + BasicValidations.printIdAndName(imsPkgServiceData.getId(), imsPkgServiceData.getName())  +" associated with ims package "+ BasicValidations.printIdAndName(imsPkgData.getId(), imsPkgData.getName()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate ims package pcc attribute with id ("+imsPkgPCCAttributeImported.getId() +") for IMS Package Service Data " + BasicValidations.printIdAndName(imsPkgServiceData.getId(), imsPkgServiceData.getName())  +" associated with ims package "+ BasicValidations.printIdAndName(imsPkgData.getId(), imsPkgData.getName())+ ". Kindly refer logs for further details");

        }
        return subReasons;
    }

    private void setDefaultValuesForMandatoryParameters(IMSPkgPCCAttributeData imsPkgPCCAttributeImported, IMSPkgServiceData imsPkgServiceData, List<String> subReasons) {


        PCCRuleAttributeAction pccRuleAttributeAction = imsPkgPCCAttributeImported.getAction();
        if(pccRuleAttributeAction == null){
            subReasons.add("Invalid Action is configured with IMS Package PCC Attribute " + BasicValidations.printIdAndName(imsPkgPCCAttributeImported.getId(),null) + "associated with IMS Package Service Data" + BasicValidations.printIdAndName(imsPkgServiceData.getId(),imsPkgServiceData.getName()));
        }else{
            PCCAttribute pccAttributeData = imsPkgPCCAttributeImported.getAttribute();
            if(pccAttributeData == null){
                subReasons.add("Invalid PCC Attribute is configured with IMS Package PCC Attribute " + BasicValidations.printIdAndName(imsPkgPCCAttributeImported.getId(),null) + "associated with IMS Package Service Data" + BasicValidations.printIdAndName(imsPkgServiceData.getId(),imsPkgServiceData.getName()));
                return;
            }

            if(pccAttributeData.getPossibleActions().contains(pccRuleAttributeAction) == false) {
                subReasons.add(pccRuleAttributeAction + " Action is not supported with PCC Attribute " + pccAttributeData.name() + " of IMS Package Override PCC Attribute " + BasicValidations.printIdAndName(imsPkgPCCAttributeImported.getId(),null) + "associated with IMS Package Service Data" + BasicValidations.printIdAndName(imsPkgServiceData.getId(),imsPkgServiceData.getName()));
            }
        }
    }
}
