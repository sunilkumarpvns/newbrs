package com.elitecore.corenetvertex.pm.offer;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferAutoSubscriptionRelData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class BaseProductOfferFactory {
    private static final String MODULE = "PRODUCT_OFFER_FACTORY";
    private PolicyRepository policyRepository;
    public BaseProductOfferFactory(PolicyRepository policyRepository){
        this.policyRepository = policyRepository;
    }

    public ProductOffer createProductOffer(ProductOfferData productOfferData) {
        PkgStatus availabilityStatus;
        if (Strings.isNullOrBlank(productOfferData.getStatus())) {

            getLogger().info(MODULE, "Considering default offer availability status: " + PkgStatus.ACTIVE.val +
                    ". Reason: No status provided");
            availabilityStatus = PkgStatus.ACTIVE;
        } else {

            availabilityStatus = PkgStatus.fromVal(productOfferData.getStatus());
            if (availabilityStatus == null) {

                getLogger().info(MODULE, "Considering default offer availability status: " + PkgStatus.ACTIVE.val +
                        ". Reason: Invalid status(" + productOfferData.getStatus() + ") configured");
                availabilityStatus = PkgStatus.ACTIVE;
            }

        }

        FailReason failReasons = new FailReason("Product Offer");
        FailReason partialFailReasons = new FailReason("Product Offer");

        PolicyStatus policyStatus = null;

        List<ProductOfferServicePkgRel> productOfferServicePkgRels = new ArrayList<>();
        List<ProductOfferAutoSubscription> productOfferAutoSubscriptions = Collectionz.newArrayList();
        List<String> reasons = new ArrayList<>();

        UserPackage dataPackage = null;

        if(Strings.isNullOrBlank(productOfferData.getDataServicePkgId())
                && Collectionz.isNullOrEmpty(productOfferData.getProductOfferServicePkgRelDataList())) {
            policyStatus = PolicyStatus.FAILURE;
            reasons.add( "Neither Data Package nor RnC Package is configured for Product Offer ("+productOfferData.getName()+")");
        }

        if(Strings.isNullOrBlank(productOfferData.getDataServicePkgId()) == false) {
            dataPackage = policyRepository.getPkgDataById(productOfferData.getDataServicePkgId());
            if(dataPackage != null){
                policyStatus = getNewPolicyStatus(policyStatus, dataPackage.getStatus());
                if(policyStatus != PolicyStatus.SUCCESS) {
                    reasons.add( "Data Package("+dataPackage.getName()+") is in "+dataPackage.getStatus().status+" state.");
                }

                List<String> userDataPackageGroups = new ArrayList<>(dataPackage.getGroupIds());
                userDataPackageGroups.retainAll(CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups()));
                if(userDataPackageGroups.isEmpty()) {
                    policyStatus = PolicyStatus.FAILURE;
                    reasons.add("No common group found between product offer and data package: "+dataPackage.getName() +". ");
                }


            } else {
                policyStatus = PolicyStatus.FAILURE;
                reasons.add( "Data Package not found");
            }
        }

        HashSet<String> rncPackageIds = new HashSet<>();
        if(Collectionz.isNullOrEmpty(productOfferData.getProductOfferServicePkgRelDataList()) == false){
            for (ProductOfferServicePkgRelData rel : productOfferData.getProductOfferServicePkgRelDataList()) {
                PolicyStatus rncPackageStatus;
                RnCPackage rncPackage = policyRepository.getRnCPackage().byId(rel.getRncPackageId());
                if(rncPackage != null){
                    rncPackageStatus = rncPackage.getPolicyStatus();
                    if(rncPackageStatus != PolicyStatus.SUCCESS) {
                        reasons.add( "RnC Package("+rncPackage.getName()+") is in "+rncPackage.getPolicyStatus()+" state.");
                    }

                    List<String> rncGroups = CommonConstants.COMMA_SPLITTER.split(rel.getRncPackageData().getGroups());
                    rncGroups.retainAll(CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups()));
                    if(rncGroups.isEmpty()) {
                        rncPackageStatus = PolicyStatus.FAILURE;
                        reasons.add("No common group found between product offer and RnC package: "+rncPackage.getName() +". ");
                    }
                } else {
                    rncPackageStatus = PolicyStatus.FAILURE;
                    reasons.add( "RnC Package not found");
                }

                policyStatus = getNewPolicyStatus(policyStatus, rncPackageStatus);

                if(rncPackageIds.add(rel.getRncPackageId()) == false) {
                    getLogger().debug(MODULE, "RnCPackage with packageId: "+rel.getRncPackageId()+" is already attached to product offer "+productOfferData.getName());
                    continue;
                }

                ProductOfferServicePkgRel productOfferServicePkgRel = new ProductOfferServicePkgRel(
                        rel.getId(),
                        policyRepository.getService().byId(rel.getServiceId()),
                        rel.getRncPackageId(),
                        policyRepository
                );
                productOfferServicePkgRels.add(productOfferServicePkgRel);
            }
        }


        List<FailReason> autoSubscriptionFailReasons = new ArrayList<>();

        if (Collectionz.isNullOrEmpty(productOfferData.getProductOfferAutoSubscriptionRelDatas()) == false && policyStatus != PolicyStatus.FAILURE) {
            for (ProductOfferAutoSubscriptionRelData productOfferAutoSubscriptionRelData : productOfferData.getProductOfferAutoSubscriptionRelDatas()) {
                FailReason autoSubscriptionFailReason = new FailReason(productOfferAutoSubscriptionRelData.getId());
                autoSubscriptionFailReasons.add(autoSubscriptionFailReason);
                PolicyStatus autoSubscriptionStatus;
                ProductOffer addOnProductOffer = policyRepository.getProductOffer().byId(productOfferAutoSubscriptionRelData.getAddOnProductOfferId());
                if (addOnProductOffer != null) {
                    autoSubscriptionStatus = addOnProductOffer.getPolicyStatus();
                    if (PkgType.ADDON != addOnProductOffer.getType()) {
                        autoSubscriptionStatus = PolicyStatus.FAILURE;
                        autoSubscriptionFailReason.add("Product Offer is Of type " + addOnProductOffer.getType() + ". It must be of type " + PkgType.ADDON.name());
                    }
                    if (autoSubscriptionStatus != PolicyStatus.SUCCESS && autoSubscriptionStatus != PolicyStatus.LAST_KNOWN_GOOD) {
                        autoSubscriptionFailReason.add("Product Offer(" + addOnProductOffer.getName() + ") is in "+addOnProductOffer.getPolicyStatus()+" state.");
                    }
                    List<String> autoSubScriptionGroups = CommonConstants.COMMA_SPLITTER.split(productOfferAutoSubscriptionRelData.getAddOnProductOfferData().getGroups());
                    autoSubScriptionGroups.retainAll(CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups()));
                    if(autoSubScriptionGroups.isEmpty()) {
                        autoSubscriptionStatus = PolicyStatus.FAILURE;
                        reasons.add("No common group found between product offer and Auto Subscription package: "+addOnProductOffer.getName() +". ");
                    }
                } else {
                    autoSubscriptionStatus = PolicyStatus.FAILURE;
                    autoSubscriptionFailReason.add("Product Offer not found");
                }
                LogicalExpression logicalExpression = null;
                String advancedCondition = productOfferAutoSubscriptionRelData.getAdvanceCondition();
                try {
                    if (Strings.isNullOrBlank(advancedCondition) == false) {
                        logicalExpression = com.elitecore.exprlib.compiler.Compiler.getDefaultCompiler().parseLogicalExpression(advancedCondition);
                    }
                } catch (InvalidExpressionException e) {
                    autoSubscriptionStatus = PolicyStatus.FAILURE;
                    autoSubscriptionFailReason.add("Invalid condition: " + advancedCondition);
                    ignoreTrace(e);
                }

                policyStatus = getNewPolicyStatus(policyStatus, autoSubscriptionStatus);
                if(autoSubscriptionFailReason.isEmpty()){
                    ProductOfferAutoSubscription productOfferServicePkgRel = new ProductOfferAutoSubscription(
                            productOfferAutoSubscriptionRelData.getId(),
                            logicalExpression,
                            advancedCondition,
                            productOfferAutoSubscriptionRelData.getAddOnProductOfferId(),
                            policyRepository,
                            Objects.nonNull(addOnProductOffer) ? addOnProductOffer.getName() :  null
                    );
                    productOfferAutoSubscriptions.add(productOfferServicePkgRel);
                }
            }
        }

        if (policyStatus == PolicyStatus.FAILURE || policyStatus == PolicyStatus.LAST_KNOWN_GOOD) {
            failReasons.addAll(reasons);
            autoSubscriptionFailReasons.forEach(failReasons::addChildModuleFailReasonIfNotEmpty);
        } else if(policyStatus == PolicyStatus.PARTIAL_SUCCESS) {
            partialFailReasons.addAll(reasons);
            autoSubscriptionFailReasons.forEach(partialFailReasons::addChildModuleFailReasonIfNotEmpty);

        }

        List<String> groupList = CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups());


        return new ProductOffer(productOfferData.getId(),
                productOfferData.getName(),
                productOfferData.getDescription(),
                PkgType.valueOf(productOfferData.getType()),
                PkgMode.valueOf(productOfferData.getPackageMode()),
                productOfferData.getValidityPeriod(),
                ValidityPeriodUnit.valueOf(productOfferData.getValidityPeriodUnit()),
                productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
                availabilityStatus,
                productOfferServicePkgRels,productOfferAutoSubscriptions,
                Objects.nonNull(dataPackage)?dataPackage.getId():null,
                groupList,
                productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(),
                policyStatus,
                failReasons.isEmpty() ? null : failReasons,
                partialFailReasons.isEmpty() ? null : partialFailReasons,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(),
                productOfferData.getParam2(),
                policyRepository,null,null,
                createPackageIdToServiceId(productOfferServicePkgRels),productOfferData.getCurrency());
    }

    private PolicyStatus getNewPolicyStatus(PolicyStatus policyStatus, PolicyStatus productOfferStatus) {

        if(policyStatus == null && productOfferStatus != PolicyStatus.FAILURE) {
            return PolicyStatus.SUCCESS;
        }else if(policyStatus == null){
            return productOfferStatus;
        }

        PolicyStatus newPolicyStatus;
        if(policyStatus == PolicyStatus.FAILURE && productOfferStatus == PolicyStatus.FAILURE ) {
            newPolicyStatus = PolicyStatus.FAILURE;
        } else if(policyStatus == PolicyStatus.SUCCESS && productOfferStatus != PolicyStatus.FAILURE) {
            newPolicyStatus = PolicyStatus.SUCCESS;
        } else {
            newPolicyStatus = PolicyStatus.PARTIAL_SUCCESS;
        }
        return newPolicyStatus;
    }
    private Map<String, Set<String>> createPackageIdToServiceId(List<ProductOfferServicePkgRel> offerServicePkgRel){
        Map<String, Set<String>> packageIdToServiceIds = new HashMap<>();
        for(ProductOfferServicePkgRel productOfferServicePkgRel: offerServicePkgRel){
            if(productOfferServicePkgRel.getServiceData() == null){
                continue;
            }
            packageIdToServiceIds.putIfAbsent(productOfferServicePkgRel.getRncPackageId(), new HashSet<>());
            packageIdToServiceIds.get(productOfferServicePkgRel.getRncPackageId()).add(productOfferServicePkgRel.getServiceData().getId());
        }
        return packageIdToServiceIds;
    }
}
