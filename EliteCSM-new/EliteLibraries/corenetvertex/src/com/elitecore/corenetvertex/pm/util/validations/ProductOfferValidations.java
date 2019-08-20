package com.elitecore.corenetvertex.pm.util.validations;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public class ProductOfferValidations {

    private ProductOfferValidations() {}
    public static void validateProductOffer(String baseProductOffer,
                                      PolicyRepository policyRepository,
                                      Validation<ProductOffer> productOfferPredicate) throws OperationFailedException {

        if (Strings.isNullOrBlank(baseProductOffer)) {
            throw new OperationFailedException("Reason: product offer can not set to empty", ResultCode.NOT_FOUND);
        }

        ProductOffer productOffer = policyRepository.getProductOffer().base().byName(baseProductOffer);

        productOfferPredicate.validate(productOffer);
    }
}
