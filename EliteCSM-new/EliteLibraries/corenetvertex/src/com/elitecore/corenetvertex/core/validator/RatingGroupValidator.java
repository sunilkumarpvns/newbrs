package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Rating group validator that will provide validation on Rating Group Data
 * Created by Ishani on 15/9/16.
 */
public class RatingGroupValidator implements Validator<RatingGroupData,DataServiceTypeDataExt,DataServiceTypeDataExt> {


    private static final String MODULE = "RATING-GROUP-VALIDATOR";
    @Override
    public List<String> validate(RatingGroupData ratingGroupImported, DataServiceTypeDataExt dataServiceTypeDataExt, DataServiceTypeDataExt pkgData, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {
            if(Strings.isNullOrBlank(ratingGroupImported.getId()) && Strings.isNullOrBlank(ratingGroupImported.getName())){
                subReasons.add("Rating Group id or name is mandatory");
                return subReasons;
            }
            String id = ratingGroupImported.getId();
            if (Strings.isNullOrBlank(id) == false) {
                isRatingGroupIdExists(ratingGroupImported, dataServiceTypeDataExt, session, subReasons, id);
            } else {
                isRatingGroupNameExists(ratingGroupImported, dataServiceTypeDataExt, session, subReasons);

            }
        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate rating group with " + BasicValidations.printIdAndName(ratingGroupImported.getId(), ratingGroupImported.getName()) +" associated with package " + BasicValidations.printIdAndName(pkgData.getId(),pkgData.getName()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate rating group with " + BasicValidations.printIdAndName(ratingGroupImported.getId(), ratingGroupImported.getName()) +" associated with Package " + BasicValidations.printIdAndName(pkgData.getId(),pkgData.getName()) + ". Kindly refer logs for further details");
        }
        return subReasons;
    }


    private void isRatingGroupIdExists(RatingGroupData ratingGroupImported, DataServiceTypeDataExt dataServiceTypeDataExt, SessionProvider session, List<String> subReasons, String id) throws Exception {
        if (Strings.isNullOrBlank(id) == false) {
            RatingGroupData existingRatingGroup = ImportExportCRUDOperationUtil.get(RatingGroupData.class, id, session);
            if (existingRatingGroup == null) {
                subReasons.add("Rating Group with id: " + id + " does not exist");
            } else if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingRatingGroup.getStatus())) {
                subReasons.add("Rating Group with id:" + id + " does not exist");
            } else {
                if (Strings.isNullOrBlank(ratingGroupImported.getName()) == false && ratingGroupImported.getName().equals(existingRatingGroup.getName()) == false) {
                    subReasons.add("Rating Group name: " + ratingGroupImported.getName() + " and Rating Group id:" + id + " are not related");
                }
                ratingGroupImported.setIdentifier(existingRatingGroup.getIdentifier());
                ratingGroupImported.setName(existingRatingGroup.getName());
            }
        }
    }
    private void isRatingGroupNameExists(RatingGroupData ratingGroupImported, DataServiceTypeDataExt dataServiceTypeDataExt, SessionProvider session, List<String> subReasons) throws Exception {
        List<RatingGroupData> existingRatingGroups = ImportExportCRUDOperationUtil.getByName(RatingGroupData.class, ratingGroupImported.getName(),session);
        if (Collectionz.isNullOrEmpty(existingRatingGroups)) {
            subReasons.add("Rating Group with name: " + ratingGroupImported.getName() + " does not exist");
        }else{
            ratingGroupImported.setId(existingRatingGroups.get(0).getId());
            ratingGroupImported.setIdentifier(existingRatingGroups.get(0).getIdentifier());
        }

    }

}
