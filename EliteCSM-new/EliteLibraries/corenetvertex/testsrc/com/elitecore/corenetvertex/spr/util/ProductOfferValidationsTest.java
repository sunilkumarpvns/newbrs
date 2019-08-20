package com.elitecore.corenetvertex.spr.util;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.util.MockProductOffer;
import com.elitecore.corenetvertex.pm.util.validations.ProductOfferValidations;
import com.elitecore.corenetvertex.pm.util.validations.Validation;
import com.elitecore.corenetvertex.pm.util.validations.ProductOfferValidator;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ProductOfferValidationsTest {

    private ProductOfferValidations productOfferValidations;
    private Validation<ProductOffer> validationPredicate;
    private String subscriberId = "1234567890";

    private DummyPolicyRepository policyRepository;
    private SPRInfoImpl sprInfo;
    private String productOfferName = "BaseProductOffer";
    private MockProductOffer productOffer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private String productOfferId = "Base-" + UUID.randomUUID().toString();

    @Before
    public void setUp() throws Exception {
        policyRepository = new DummyPolicyRepository();
        sprInfo = spy(new SPRInfoImpl());
        sprInfo.setProductOffer(productOfferName);
        productOffer = MockProductOffer.create(policyRepository, productOfferId, productOfferName, PolicyStatus.FAILURE, PkgStatus.ACTIVE);
        policyRepository.addProductOffer(productOffer);
    }


    public class ThrowsExceptionWhen {

        @Test
        public void productOfferIsNull() throws OperationFailedException {

            String message = "Reason: product offer can not set to empty";

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage(message);

            String productOffer = "";
            ProductOfferValidations.validateProductOffer(productOffer, policyRepository, validationPredicate);
        }

        @Test
        public void productOfferNotFound() throws OperationFailedException {

            String productOffer = "BaseProductOffer1";
            String message = "Subscriber(" + subscriberId + ") operation failed. Reason: subscribed base product offer(" + productOffer + ") not found";

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage(message);

            when(policyRepository.getProductOffer().base().byName(productOffer)).thenReturn(null);

            validationPredicate = ProductOfferValidator.nonNull(message);
            ProductOfferValidations.validateProductOffer(productOffer, policyRepository, validationPredicate);
        }

        @Test
        public void productOfferIsInFailedStatus() throws OperationFailedException {

            String message = "Subscriber(" + subscriberId + ") update operation failed. Reason: Base product offer(" + productOfferName + ")  is with policy status: " + productOffer.getPolicyStatus();

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage(message);

            validationPredicate = ProductOfferValidator.notFailuer(message);
            ProductOfferValidations.validateProductOffer( productOfferName, policyRepository, validationPredicate);
        }


        @Test
        public void productOfferIsInActive() throws OperationFailedException {

            String message = "Subscriber(" + subscriberId + ") update operation failed."
                    + " Reason: Base product offer(" + productOfferName + ") found with " + productOffer.getStatus() + " Status";

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage(message);

            validationPredicate = ProductOfferValidator.statusActive(message);
            productOffer.setStatus(PkgStatus.INACTIVE);

            ProductOfferValidations.validateProductOffer(productOfferName, policyRepository, validationPredicate);
        }

    }

    @Test
    public void productOfferSucceed() throws OperationFailedException {

        String productOfferSuccess = "BaseProductOffer_SUCCESS";
        productOffer = MockProductOffer.create(policyRepository, productOfferId + "SUCCESS", productOfferSuccess, PolicyStatus.SUCCESS, PkgStatus.ACTIVE);
        policyRepository.addProductOffer(productOffer);

        validationPredicate = ProductOfferValidator.nonNull("").and(ProductOfferValidator.notFailuer("")).and(ProductOfferValidator.statusActive(""));

        ProductOfferValidations.validateProductOffer(productOfferSuccess, policyRepository, validationPredicate);
    }

}