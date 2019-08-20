package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A validator class that validated Media Type components associated with IMS Package Service Data
 */
public class MediaTypeValidator implements Validator<MediaTypeData,IMSPkgServiceData,IMSPkgData> {

    private static final String MODULE = "MEDIA-TYPE-VALIDATOR";
    @Override
    public List<String> validate(MediaTypeData mediaTypeImported, IMSPkgServiceData imsPkgServiceData, IMSPkgData imsPkgData, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {
            if(Strings.isNullOrBlank(mediaTypeImported.getId()) && Strings.isNullOrBlank(mediaTypeImported.getName())){
                subReasons.add("Media type id or Media type name is mandatory");
                return subReasons;
            }
            String id = mediaTypeImported.getId();
            if (Strings.isNullOrBlank(id) == false) {
                isMediaTypeTypeIdExists(mediaTypeImported, session, subReasons, id);
            } else {
                isMediaTypeNameExists(mediaTypeImported, session, subReasons);

            }
        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate media type with " + BasicValidations.printIdAndName(mediaTypeImported.getId(), mediaTypeImported.getName()) +" associated with ims package " + BasicValidations.printIdAndName(imsPkgData.getId(), imsPkgData.getName()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate media type with " + BasicValidations.printIdAndName(mediaTypeImported.getId(), mediaTypeImported.getName()) +" associated with ims Package " + BasicValidations.printIdAndName(imsPkgData.getId(), imsPkgData.getName()) + ". Kindly refer logs for further details");
        }
        return subReasons;
    }


    private void isMediaTypeTypeIdExists(MediaTypeData mediaTypeImported, SessionProvider session, List<String> subReasons, String id) throws Exception {
        if (Strings.isNullOrBlank(id) == false) {
            MediaTypeData existingMediaType = ImportExportCRUDOperationUtil.get(MediaTypeData.class, id, session);
            if (existingMediaType == null) {
                subReasons.add("Media Type with id: " + id + " does not exist");
            } else if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingMediaType.getStatus())) {
                subReasons.add("Media Type with id:" + id + " does not exist");
            } else {
                if (Strings.isNullOrBlank(mediaTypeImported.getName()) == false && mediaTypeImported.getName().equals(existingMediaType.getName()) == false) {
                    subReasons.add("Media Type name: " + mediaTypeImported.getName() + " and media type id:" + id + " are not related");
                }

            }
        }
    }
    private void isMediaTypeNameExists(MediaTypeData mediaTypeImported, SessionProvider session, List<String> subReasons) throws Exception {
        List<MediaTypeData> existingMediaType = ImportExportCRUDOperationUtil.getByName(MediaTypeData.class, mediaTypeImported.getName(),session);
        if (Collectionz.isNullOrEmpty(existingMediaType)) {
            subReasons.add("Media Type with name: " + mediaTypeImported.getName() + " does not exist");
        }else if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingMediaType.get(0).getStatus())){
            subReasons.add("Media Type with name:" + existingMediaType.get(0).getName() + " does not exist");
        }else{
            mediaTypeImported.setId(existingMediaType.get(0).getId());
        }

    }

}
