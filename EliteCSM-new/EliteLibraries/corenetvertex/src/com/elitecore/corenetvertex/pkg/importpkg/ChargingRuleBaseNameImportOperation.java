package com.elitecore.corenetvertex.pkg.importpkg;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.util.SessionProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/** This class responsible for performing import operation for ChargingRuleBaseNameData and hierarchy
 * Created by RAJ KIRPALSINH
 */

public class ChargingRuleBaseNameImportOperation implements ImportOperation<ChargingRuleBaseNameData,   ChargingRuleBaseNameData,   ChargingRuleBaseNameData> {

    @Override
    public void importData(@Nonnull ChargingRuleBaseNameData chargingRuleBaseNameData, @Nullable ChargingRuleBaseNameData parentObject, @Nullable ChargingRuleBaseNameData superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {
        importChargingRuleBaseName(chargingRuleBaseNameData, session);
    }
    
    private void importChargingRuleBaseName(ChargingRuleBaseNameData chargingRuleBaseNameData,SessionProvider session) {

        List<ChargingRuleDataServiceTypeData> serviceTypeDatasForChargingRuleBaseName = chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas();
        chargingRuleBaseNameData.setChargingRuleDataServiceTypeDatas(null);


        if(Strings.isNullOrBlank(chargingRuleBaseNameData.getId())) {
            chargingRuleBaseNameData.setId(null);
            session.getSession().persist(chargingRuleBaseNameData);
        } else {
            session.getSession().merge(chargingRuleBaseNameData);
        }


        for(ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData : serviceTypeDatasForChargingRuleBaseName){
            chargingRuleDataServiceTypeData.setChargingRuleBaseName(chargingRuleBaseNameData);

            if(Strings.isNullOrBlank(chargingRuleDataServiceTypeData.getId())){
                chargingRuleDataServiceTypeData.setId(null);
                session.getSession().persist(chargingRuleDataServiceTypeData);
            }else{
                session.getSession().merge(chargingRuleDataServiceTypeData);
            }
        }
    }
}