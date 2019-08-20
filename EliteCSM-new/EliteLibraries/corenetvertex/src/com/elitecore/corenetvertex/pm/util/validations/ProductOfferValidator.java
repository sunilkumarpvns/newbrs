package com.elitecore.corenetvertex.pm.util.validations;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

import java.util.Objects;

public class ProductOfferValidator {

    private ProductOfferValidator() {}
    public static Validation<ProductOffer> nonNull(String message) {
        return productOffer -> {
            if (Objects.isNull(productOffer)) {
                throw new OperationFailedException(message, ResultCode.NOT_FOUND);
            }
        };
    }

    public static Validation<ProductOffer> notFailuer(String message) {
        return productOffer -> {
            if (productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
                throw new OperationFailedException(message, ResultCode.INVALID_INPUT_PARAMETER);
            }
        };
    }

    public static Validation<ProductOffer> statusActive(String message) {
        return productOffer -> {
            if (PkgStatus.ACTIVE != productOffer.getStatus()) {
                throw new OperationFailedException(message, ResultCode.INVALID_INPUT_PARAMETER);
            }
        };
    }

}
