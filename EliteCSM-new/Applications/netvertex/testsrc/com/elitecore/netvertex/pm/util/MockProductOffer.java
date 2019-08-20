package com.elitecore.netvertex.pm.util;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferAutoSubscription;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MockProductOffer extends ProductOffer {

    private PolicyRepository policyRepository;

    public MockProductOffer(PolicyRepository policyRepository) {
        super(null,
                null,
                null,
                null,
                null,
                null,
                null,
                0.0,
                0.0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
                , null
                , false
                , null
                , null
                , null
                , null
                , null,new HashMap<>(),null);
        this.policyRepository = policyRepository;
    }

    public static MockProductOffer create(PolicyRepository policyRepository, String id, String name) {
        MockProductOffer mockBasePackage = spy(new MockProductOffer(policyRepository));
        when(mockBasePackage.getId()).thenReturn(id);
        when(mockBasePackage.getName()).thenReturn(name);
        when(mockBasePackage.getType()).thenReturn(PkgType.BASE);
        return mockBasePackage;
    }

    public MockProductOffer addAutoSubscriptions(String id, String name) {

        ProductOfferAutoSubscription productOfferAutoSubscription = new ProductOfferAutoSubscription(UUID.randomUUID().toString(),
                null,
                null,
                id,
                policyRepository,
                name);
        when(this.getProductOfferAutoSubscriptions()).thenReturn(Arrays.asList(productOfferAutoSubscription));

        return this;
    }

    public MockProductOffer addAutoSubscriptions(String advanceCondition, String id, String name) throws InvalidExpressionException {

        ProductOfferAutoSubscription productOfferAutoSubscription = new ProductOfferAutoSubscription(UUID.randomUUID().toString(),
                Compiler.getDefaultCompiler().parseLogicalExpression(advanceCondition),
                advanceCondition,
                id,
                policyRepository,
                name);
        when(this.getProductOfferAutoSubscriptions()).thenReturn(Arrays.asList(productOfferAutoSubscription));

        return this;
    }

    public MockProductOffer addAutoSubscriptions(ProductOfferAutoSubscription productOfferAutoSubscription) {
        when(this.getProductOfferAutoSubscriptions()).thenReturn(Arrays.asList(productOfferAutoSubscription));

        return this;
    }

    public void removeAutoSubscriptions() {
        when(this.getProductOfferAutoSubscriptions()).thenReturn(Collections.emptyList());

    }

    public void setPrice(double price) {
        when(this.getSubscriptionPrice()).thenReturn(price);
    }

    public void addDataPackage(BasePackage basePackage) {
        when(this.getDataServicePkgData()).thenReturn(basePackage);
    }

	public void addRnCPackage(String smsService, String smsServiceId, RnCPackage rnCPackage) {
		ProductOfferServicePkgRel pkgRel = new ProductOfferServicePkgRel(UUID.randomUUID().toString(),
				new Service(smsServiceId, smsService, PkgStatus.ACTIVE), rnCPackage.getId(),  policyRepository);
		when(this.getProductOfferServicePkgRelDataList()).thenReturn(Arrays.asList(pkgRel));
	}

	public static MockProductOffer createAddOn(DummyPolicyRepository policyRepository, String id, String name) {
		MockProductOffer mockBasePackage = spy(new MockProductOffer(policyRepository));
		when(mockBasePackage.getId()).thenReturn(id);
		when(mockBasePackage.getName()).thenReturn(name);
		when(mockBasePackage.getType()).thenReturn(PkgType.ADDON);
		return mockBasePackage;
	}
}
