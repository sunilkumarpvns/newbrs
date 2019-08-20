package com.elitecore.corenetvertex.spr.util;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public class SubscriberPkgValidationUtil {

    public static final String SUBSCRIBER_PROFILE_ADD_OPERATION = "add";
    public static final String SUBSCRIBER_PROFILE_UPDATE_OPERATION = "update";

    public static void validateBasePackage (BasePackage basePkg,String baseDataPackageName,String subscriberIdentity,String operationName) throws OperationFailedException{

        if (basePkg.getStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: Base package(" + baseDataPackageName + ")  is failed base package", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (PkgStatus.ACTIVE != basePkg.getAvailabilityStatus()) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: subscribed base package(" + baseDataPackageName + ") found with "
                    + basePkg.getAvailabilityStatus() + " Status", ResultCode.NOT_FOUND);
        }

    }

    public static void validateProductOffer (ProductOffer productOffer, String baseProductOfferName, String subscriberIdentity, String operationName) throws OperationFailedException{

        if (productOffer == null) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: subscribed base product offer(" + baseProductOfferName + ")  not found", ResultCode.NOT_FOUND);
        }

        if (productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: Base product offer(" + baseProductOfferName + ")  is failed base product offer", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (PkgStatus.ACTIVE != productOffer.getStatus()) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: subscribed base product offer(" + baseProductOfferName + ") found with "
                    + productOffer.getStatus() + " Status", ResultCode.NOT_FOUND);
        }

    }

    public static void validateImsPackage(IMSPackage imsPkg, String imsPackageName, String subscriberIdentity, String operationName) throws OperationFailedException {
        if (imsPkg == null) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: subscribed IMS package(" + imsPackageName + ")  not found", ResultCode.NOT_FOUND);
        }

        if (imsPkg.getStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: IMS Base package(" + imsPackageName + ")  is failed base package", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (PkgStatus.ACTIVE != imsPkg.getAvailabilityStatus()) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") "+operationName+" operation failed."
                    + " Reason: subscribed IMS package(" + imsPackageName + ")  found with "
                    + imsPkg.getAvailabilityStatus() + " Status", ResultCode.NOT_FOUND);
        }
    }
}
