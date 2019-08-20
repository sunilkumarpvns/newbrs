package com.elitecore.nvsmx.policydesigner.controller.ratinggroup;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;

import java.util.List;

/**
 * Created by kirpalsinh on 6/6/17.
 */

public class RatingGroupUtil {

    public static void setChargingKeyName(List<PCCRuleData> pccRuleDatas) throws HibernateDataException {

        for(PCCRuleData pccRuleData: pccRuleDatas){

            if ( Strings.isNullOrBlank(pccRuleData.getChargingKey())==false ) {

                RatingGroupData ratingGroupData = CRUDOperationUtil.getNotDeleted(RatingGroupData.class, pccRuleData.getChargingKey());
                if ( ratingGroupData!=null && Strings.isNullOrBlank(ratingGroupData.getStatus())==false ) {

                    pccRuleData.setChargingKeyName(ratingGroupData.getName());
                }
            }
        }
    }
}
