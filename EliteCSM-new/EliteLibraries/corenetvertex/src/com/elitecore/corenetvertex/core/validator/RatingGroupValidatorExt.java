package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides validation for import operation on Rating group from SOAP API
 * Created by Ishani on 22/9/16.
 */
public class RatingGroupValidatorExt implements Validator<RatingGroupDataExt, RatingGroupDataExt, RatingGroupDataExt> {

    private static final String MODULE = "RATING-GROUP-VALIDATOR-EXT";

    @Override
    public List<String> validate(RatingGroupDataExt ratingGroupImported, RatingGroupDataExt parentObject, RatingGroupDataExt pkgData, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {
            String id = ratingGroupImported.getId();
            String name = ratingGroupImported.getName();
            if (Strings.isNullOrBlank(id) && Strings.isNullOrBlank(name)) {
                subReasons.add("Rating group id or name is mandatory");
                return subReasons;
            }
            BasicValidations.validateName(name, "Rating Group", subReasons);
            if (ratingGroupImported.getIdentifier() == null) {
                subReasons.add("Rating Group Identifier is mandatory with Rating Group: " + BasicValidations.printIdAndName(id, name));
                return subReasons;
            }

            List<RatingGroupDataExt> ratingGroupWithSameIdentifier = ImportExportCRUDOperationUtil.filterEntityBasedOnProperty(RatingGroupDataExt.class, "identifier", ratingGroupImported.getIdentifier(), session);
            if (Collectionz.isNullOrEmpty(ratingGroupWithSameIdentifier) == false) {
                RatingGroupDataExt existingRatingGroup = ratingGroupWithSameIdentifier.get(0);
                if (existingRatingGroup.getName().equals(ratingGroupImported.getName()) == false
                        && existingRatingGroup.getId().equals(ratingGroupImported.getId()) == false ) {
                    subReasons.add("Identifier : " + ratingGroupImported.getIdentifier() + " already exists");
                    return subReasons;
                }
            }
            validateWithExistingRatingGroup(ratingGroupImported, action, session, subReasons);
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate rating group with " + BasicValidations.printIdAndName(ratingGroupImported.getId(), ratingGroupImported.getName()));
            LogManager.getLogger().trace(MODULE, e);
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to validate Rating Group with ");
            sb.append(BasicValidations.printIdAndName(ratingGroupImported.getId(), ratingGroupImported.getName()));
            sb.append(". Kindly refer logs for further details");
            subReasons.add(sb.toString());
        }
        return subReasons;
    }


    private void validateWithExistingRatingGroup(RatingGroupDataExt ratingGroupImported, String action, SessionProvider session, List<String> subReasons) throws Exception {
        RatingGroupDataExt existingRatingGroup = null;
        String id = ratingGroupImported.getId();
        String name = ratingGroupImported.getName();
        if (Strings.isNullOrBlank(name) == false) {
            List<RatingGroupDataExt> existingRatingGroups = ImportExportCRUDOperationUtil.getByName(RatingGroupDataExt.class, name, session);
            if (Collectionz.isNullOrEmpty(existingRatingGroups) == false) {
                existingRatingGroup = existingRatingGroups.get(0);
            } else if (Strings.isNullOrBlank(id) == false) {
                existingRatingGroup = ImportExportCRUDOperationUtil.get(RatingGroupDataExt.class, id, session);
            }
        }

        if (existingRatingGroup != null) {
            if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingRatingGroup.getStatus()) == false) {

                if(Strings.isNullOrBlank(id) == false ) {
                    if (existingRatingGroup.getName().equals(name) && (existingRatingGroup.getId().equals(id) == false)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Imported Rating Group with ");
                        sb.append(BasicValidations.printIdAndName(id, name));
                        sb.append(" conflicts with Existing Rating Group ");
                        sb.append(BasicValidations.printIdAndName(existingRatingGroup.getId(), existingRatingGroup.getName()));
                        subReasons.add(sb.toString());
                        return;
                    }
                }

                if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Rating Group ");
                    sb.append(BasicValidations.printIdAndName(id, name));
                    sb.append(" already exists");
                    subReasons.add(sb.toString());
                } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                    ratingGroupImported.setId(existingRatingGroup.getId());
                }
            }
        }
    }

}
