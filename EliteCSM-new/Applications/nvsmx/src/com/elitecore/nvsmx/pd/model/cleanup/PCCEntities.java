package com.elitecore.nvsmx.pd.model.cleanup;

import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;

import java.util.HashMap;
import java.util.Map;

public enum PCCEntities {

    DATA_PACKAGE(com.elitecore.corenetvertex.pkg.PkgData.class),
    IMS_PACKAGE(com.elitecore.corenetvertex.pkg.ims.IMSPkgData.class),
    PCC_RULE(com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData.class),
    CRBN(ChargingRuleBaseNameData.class),
    RATING_GROUP(RatingGroupData.class),
    DATA_SERVICE_TYPE(DataServiceTypeData.class),
    RNC_PACKAGE(RncPackageData.class),
    PRODUCT_OFFER(ProductOfferData.class);


    private Class entityClass;
    private static Map<String, PCCEntities> entityToClassMap;

    PCCEntities(Class imsPkgDataClass) {

        entityClass = imsPkgDataClass;

    }

    static {
        entityToClassMap = new HashMap<>();
        for (PCCEntities pccEntities : PCCEntities.values()) {
            entityToClassMap.put(pccEntities.name(), pccEntities);
        }


    }

    public Class getEntityClass() {
        return entityClass;
    }

    public static PCCEntities fromEntityName(String entityName) {
        return entityToClassMap.get(entityName);
    }

}