package com.elitecore.corenetvertex.pm.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferAutoSubscription;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;


import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MockProductOffer extends ProductOffer {


    private PolicyRepository policyRepository;
	private PkgType pkgType;

	public MockProductOffer(PolicyRepository policyRepository, PolicyStatus policyStatus, PkgStatus pkgStatus) {
        super(null,
                null,
                null,
                null,
                null,
                null,
                null,
                0.0,
                0.0,
                pkgStatus,
                null,
                null,
                null,
                null,
                null,
                null,
                policyStatus,
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

    public static MockProductOffer create(PolicyRepository policyRepository, String id, String name,PolicyStatus policyStatus, PkgStatus pkgStatus) {
        MockProductOffer mockBasePackage = spy(new MockProductOffer(policyRepository, policyStatus, pkgStatus));
        when(mockBasePackage.getId()).thenReturn(id);
        when(mockBasePackage.getName()).thenReturn(name);
        when(mockBasePackage.getType()).thenReturn(PkgType.BASE);
        when(mockBasePackage.getPolicyStatus()).thenReturn(policyStatus);
        when(mockBasePackage.getStatus()).thenReturn(pkgStatus);
        when(mockBasePackage.getCurrency()).thenReturn("INR");
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

	public void setStatus(PkgStatus status) {
		when(this.getStatus()).thenReturn(status);
	}

	public void setPkgType(PkgType pkgType) {
		when(this.getType()).thenReturn(pkgType);
	}

    public MockProductOffer addGroups(List<String> groups) {
        when(this.getGroups()).thenReturn(groups);
        return this;
    }

    public MockProductOffer setTypeAddOn() {
	    setPkgType(PkgType.ADDON);
        return this;
    }
}
