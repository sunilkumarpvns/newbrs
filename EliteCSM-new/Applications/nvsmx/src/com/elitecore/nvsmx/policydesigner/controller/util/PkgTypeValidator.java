package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.validator.BasicValidations;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.interceptor.RequestURI;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

/**
 * Created by aditya on 7/6/17.
 */

public class PkgTypeValidator {




    @Nonnull
    private final ACLModules aclModule;

    public PkgTypeValidator(@Nonnull ACLModules aclModules) {
        this.aclModule = checkNotNull(aclModules, "Acl Module Type can't be null");
    }


    @Nonnull
    public static PkgTypeValidator create(@Nonnull HttpServletRequest request) {
        SingleRequestURI singleRequestURI = new SingleRequestURI(request);
        return new PkgTypeValidator(ACLModules.fromModuleName(singleRequestURI.getModuleName()));
    }

    public List<String> validate(@Nonnull PkgData importedPkgData) {
        List<String> subReasons = Collectionz.newArrayList();
        if (Strings.isNullOrBlank(importedPkgData.getType()) == true) {
            subReasons.add("Package Type must be provided with package " + BasicValidations.printIdAndName(importedPkgData.getId(), importedPkgData.getName()));

        }
        if (ACLModules.DATAPKG.equals(aclModule)) {
            if (PkgType.BASE.name().equalsIgnoreCase(importedPkgData.getType()) == false
                    && PkgType.ADDON.name().equalsIgnoreCase(importedPkgData.getType()) == false) {
                subReasons.add("Invalid Package Type: " + importedPkgData.getType() + " is configured with package " + BasicValidations.printIdAndName(importedPkgData.getId(), importedPkgData.getName()));
            }

        }
        if (ACLModules.PROMOTIONALPKG.equals(aclModule)) {
            if (PkgType.PROMOTIONAL.name().equalsIgnoreCase(importedPkgData.getType()) == false) {
                subReasons.add("Invalid Package Type: " + importedPkgData.getType() + " is configured with package " + BasicValidations.printIdAndName(importedPkgData.getId(), importedPkgData.getName()));
            }
        }
        if (ACLModules.EMERGENCYPKG.equals(aclModule)) {
            if (PkgType.EMERGENCY.name().equalsIgnoreCase(importedPkgData.getType()) == false) {
                subReasons.add("Invalid Package Type: " + importedPkgData.getType() + " is configured with package " + BasicValidations.printIdAndName(importedPkgData.getId(), importedPkgData.getName()));
            }
        }
        subReasons = validateCurrency(importedPkgData,subReasons);

        return subReasons;
    }

    private List<String> validateCurrency(PkgData data,List<String> subReasons){
        if(Objects.isNull(data.getCurrency()) ) {
            subReasons.add("Currency can not be null");

        }else {
            if (!SystemParameterDAO.isMultiCurrencyEnable()) {
                if (!SystemParameterDAO.getCurrency().equals(data.getCurrency())) {
                    subReasons.add("Currency other than system currency is not supported");
                }
            } else {
                if (!SystemParameter.CURRENCY.validate(data.getCurrency())) {
                    subReasons.add("Invalid currency value");
                }
            }
        }
        return subReasons;
    }

    public boolean validateDataPackageDetails(String pkgId){
        Criterion dataPackageFilterCrieteria = Restrictions.eq("dataServicePkgData.id",pkgId);
        List<ProductOfferData> productOfferDataList = CRUDOperationUtil.findAll(ProductOfferData.class, dataPackageFilterCrieteria);
        if(productOfferDataList.size() == 0 || productOfferDataList.isEmpty()) {
            return true;
        }
        return false;
    }


    @Nonnull
    public ACLModules getAclModule() {
        return aclModule;
    }


    private static class SingleRequestURI extends RequestURI {

        public SingleRequestURI(HttpServletRequest request) {
            super(request);
        }

        @Override
        protected boolean isAuthorizedAction(ACLModules module) {
            return false;
        }
    }

}