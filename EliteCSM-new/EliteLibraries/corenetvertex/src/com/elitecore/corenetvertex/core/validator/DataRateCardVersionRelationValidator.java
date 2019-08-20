package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DataRateCardVersionRelationValidator implements Validator<DataRateCardVersionRelationData,DataRateCardData,PkgData> {

    private static final String MODULE = "DATA-RC-VERSN-VLDTR";
    private static final String REGEX_PATTERN = "^((\\d{0,9}(\\.\\d{0,6})?))$";


    @Override
    public List<String> validate(DataRateCardVersionRelationData dataRateCardVersionRelationData, DataRateCardData parentObject, PkgData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<>();
        try {
            String id = dataRateCardVersionRelationData.getId();
            if (Strings.isNullOrBlank(id) == false) {
                DataRateCardVersionRelationData  existingVersionDetail = ImportExportCRUDOperationUtil.get(dataRateCardVersionRelationData.getClass(), id,session);
                if (existingVersionDetail != null) {
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("Rate Card version detail  id ( "+id +") already exists for Data Rate Card " + BasicValidations.printIdAndName(parentObject.getId(), parentObject.getName()));
                    }
                }
            }
           validateDataRateCardVersionRelationDetails(dataRateCardVersionRelationData.getDataRateCardVersionDetailDataList(), subReasons);
        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate data card version with id ("+dataRateCardVersionRelationData.getId()+") for data rate card " + BasicValidations.printIdAndName(parentObject.getId(), parentObject.getName()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate data card version with id ("+dataRateCardVersionRelationData.getId()+") for data rate card " + BasicValidations.printIdAndName(parentObject.getId(), parentObject.getName()));
        }
        return subReasons;
    }

    private void validateDataRateCardVersionRelationDetails(List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList, List<String> subReasons) {
         for(DataRateCardVersionDetailData dataRateCardVersionRelationDetailData : dataRateCardVersionDetailDataList){

             Long pulse1 = dataRateCardVersionRelationDetailData.getPulse1();
             BigDecimal rate1 = dataRateCardVersionRelationDetailData.getRate1();
             if(pulse1 == null && rate1 == null){
                 subReasons.add("pulse 1 and rate 1 can't be empty.Provide at least one");
                 return;
             }
             if(pulse1 != null && pulse1 <= 0){
                 subReasons.add("Invalid pulse1 "+pulse1+" configured.Pulse must be greater than zero");
                 return;
             }
              if(rate1!=null) {
                  if (rate1.compareTo(BigDecimal.ZERO) < 0) {
                      subReasons.add("Invalid rate1 " + rate1 + " configured.Rate must be greater than equal to zero");
                      return;
                  }
                  validateRate(rate1, subReasons);
              }

             String rateType = dataRateCardVersionRelationDetailData.getRateType();
             if(Strings.isNullOrBlank(rateType)){
                 subReasons.add("Rate type can't be empty");
                 return;
             }
             if(TierRateType.fromVal(rateType) == null){
                 subReasons.add("Invalid rate type "+rateType+" configured");
                 return;
             }
         }

    }
    private void validateRate(BigDecimal fieldValue, List<String> subReasons) {
          if (Pattern.matches(REGEX_PATTERN,fieldValue.toPlainString()) == false) {
                subReasons.add("It allows maximum 9 numbers and up to 6 decimal");
            }
    }
}
