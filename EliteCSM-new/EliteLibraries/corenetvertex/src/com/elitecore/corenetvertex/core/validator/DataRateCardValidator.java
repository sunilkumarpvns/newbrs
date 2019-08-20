package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import org.apache.commons.collections.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class DataRateCardValidator implements Validator<DataRateCardData, PkgData, PkgData> {

    private static final String MODULE = "DATA-RATE-CARD-VLDTR";

    @Override
    public List<String> validate(DataRateCardData rateCardData, PkgData parentObject, PkgData superObject, String action, SessionProvider session) {

        List<String> subReasons = new ArrayList<>();

        if (PkgType.BASE.name().equalsIgnoreCase(superObject.getType()) == false) {
            subReasons.add("Rate cards can only be configured with Base type package");
            return subReasons;
        }
        if (QuotaProfileType.RnC_BASED != superObject.getQuotaProfileType()) {
            subReasons.add("Rate cards only be configured with Base type package and RnC base quota profile");
            return subReasons;
        }

        if (Strings.isNullOrBlank(rateCardData.getId()) && Strings.isNullOrBlank(rateCardData.getName())) {
            subReasons.add("Rate Card id or Name is mandatory");
        }

        BasicValidations.validateName(rateCardData.getName(), "name", subReasons);


        rateCardData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        rateCardData.setCreatedByStaff(superObject.getCreatedByStaff());
        rateCardData.setModifiedDate(rateCardData.getCreatedDate());
        rateCardData.setModifiedByStaff(rateCardData.getModifiedByStaff());

        validateRateCardFields(rateCardData,subReasons);
        //check with existing rate card
        validateWithExistingRateCard(rateCardData, superObject, subReasons, action, session);
        try {
            validateRateCardVersionRelations(rateCardData.getDataRateCardVersionRelationData(),rateCardData,subReasons,session);
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate data rate card version relation data " + BasicValidations.printIdAndName(rateCardData.getId(), rateCardData.getName()));
            LogManager.getLogger().trace(MODULE, e);
        }

        return subReasons;
    }

    private void validateRateCardVersionRelations(List<DataRateCardVersionRelationData> dataRateCardVersionRelations, DataRateCardData rateCardData,List<String> subReasons, SessionProvider session) throws Exception {
        if(Collectionz.isNullOrEmpty(dataRateCardVersionRelations)){
            subReasons.add("At least one version must be configured");
            return;
        }
        Set<String> versionNames = new HashSet<>(dataRateCardVersionRelations.size());
        for(DataRateCardVersionRelationData version : dataRateCardVersionRelations){
            if(versionNames.add(version.getVersionName()) == false) {
                subReasons.add("Version with name " + version.getVersionName() + " already configured in rate card " + rateCardData.getName());
            }
            if(version.getEffectiveFromDate() == null){
                subReasons.add("Effective date is mandatory in "+ version.getVersionName() + " version");
            }

            for (DataRateCardVersionDetailData dataRateCardVersionDetailData : version.getDataRateCardVersionDetailDataList()) {
                if (Objects.nonNull(dataRateCardVersionDetailData.getRevenueDetail()) && Strings.isNullOrEmpty(dataRateCardVersionDetailData.getRevenueDetail().getName()) == false) {
                    if (CollectionUtils.isEmpty(ImportExportCRUDOperationUtil.getAll(RevenueDetailData.class, "name", dataRateCardVersionDetailData.getRevenueDetail().getName(), session))) {
                        subReasons.add("No such revenue code found : " + dataRateCardVersionDetailData.getRevenueDetail());
                    }
                }else{
                    dataRateCardVersionDetailData.setRevenueDetail(null);
                }
            }

        }
    }

    private void validateRateCardFields(DataRateCardData rateCardData, List<String> subReasons) {
        String pulseUnit = rateCardData.getPulseUnit();
        String rateUnit = rateCardData.getRateUnit();
        if(Strings.isNullOrBlank(pulseUnit)){
            subReasons.add("Pulse UOM must be defined");
            return;
        }
        Uom pulseUom = Uom.getUomFromValue(pulseUnit);
        if(pulseUom == null){
            subReasons.add("Invalid Pulse UOM ("+pulseUnit+") configured");
            return ;
        }

        if(Strings.isNullOrBlank(rateUnit)){
            subReasons.add("Rate UOM must be defined");
            return;
        }
        Uom rateUom = Uom.getUomFromValue(rateUnit);

        if(rateUom == null){
            subReasons.add("Invalid Rate UOM ("+rateUnit+") configured");
            return;
        }

        if(Uom.PERPULSE.equals(rateUom)){
            return;
        }

        if(pulseUom.getUnitType().equalsIgnoreCase(rateUom.getUnitType()) == false){
            subReasons.add("Rate & Pulse UOM must be of same type");
        }
    }

    private void validateWithExistingRateCard(DataRateCardData rateCardData, PkgData pkgData, List<String> subReasons, String action, SessionProvider session)  {
        DataRateCardData existingRateCardData = null;
        try {
            existingRateCardData = getExistingDataRateCardData(rateCardData, pkgData, subReasons, action, session, existingRateCardData);
            if (existingRateCardData == null) {
                return;
            }
            if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingRateCardData.getStatus()) == false) {
                validateRateCardBasedOnAction(rateCardData, existingRateCardData, pkgData, action, subReasons);
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Failed to validate rate card with exiting rate card. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate Rate Card " + BasicValidations.printIdAndName(rateCardData.getId(), rateCardData.getName()) + " associated with Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) + ". Kindly refer logs for further details");
        }
    }

    private DataRateCardData getExistingDataRateCardData(DataRateCardData rateCardData, PkgData pkgData, List<String> subReasons, String action, SessionProvider session, DataRateCardData existingRateCardData) throws Exception {
        if (Strings.isNullOrBlank(rateCardData.getId()) == false) {
            existingRateCardData = ImportExportCRUDOperationUtil.get(DataRateCardData.class, rateCardData.getId(), session);
            if ((existingRateCardData != null)) {
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingRateCardData.getStatus())) {
                    ImportExportCRUDOperationUtil.deleteRateCards(rateCardData.getId(), session);
                } else {
                    if(existingRateCardData.getPkgData()!= null){
                        if (Strings.isNullOrBlank(pkgData.getId()) == false
                                && pkgData.getId().equalsIgnoreCase(existingRateCardData.getPkgData().getId()) == false
                                && CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                            subReasons.add("Rate Card " + BasicValidations.printIdAndName(rateCardData.getId(), rateCardData.getName()) + " already exists in different package " + BasicValidations.printIdAndName(existingRateCardData.getPkgData().getId(), existingRateCardData.getPkgData().getName()));
                        }
                    }
                }
            }
        } else {
            existingRateCardData = getExistingDataRateCardDataByName(rateCardData, pkgData, session, existingRateCardData);
        }
        return existingRateCardData;
    }

    private DataRateCardData getExistingDataRateCardDataByName(DataRateCardData rateCardData, PkgData pkgData, SessionProvider session, DataRateCardData existingRateCardData) throws Exception {
        List<DataRateCardData> rateCardDataList = ImportExportCRUDOperationUtil.getNameBasedOnParentId(DataRateCardData.class, rateCardData.getName(), pkgData.getId(), "pkgData.id", session);
        if (Collectionz.isNullOrEmpty(rateCardDataList) == false) {
            existingRateCardData = rateCardDataList.get(0);
        }
        return existingRateCardData;
    }


    private void validateRateCardBasedOnAction(DataRateCardData rateCardData, DataRateCardData existingRateCardData, PkgData pkgData, String action, List<String> subReasons) {
        if (CommonConstants.FAIL.equalsIgnoreCase(action) && pkgData.getName().equalsIgnoreCase(existingRateCardData.getPkgData().getName())) {
            subReasons.add("Rate Card " + BasicValidations.printIdAndName(rateCardData.getId(), rateCardData.getName()) + " already exists in Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
        } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
            rateCardData.setId(existingRateCardData.getId());
        }
    }
}