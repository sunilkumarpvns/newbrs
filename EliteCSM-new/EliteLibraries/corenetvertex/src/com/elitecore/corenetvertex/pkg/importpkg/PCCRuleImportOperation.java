package com.elitecore.corenetvertex.pkg.importpkg;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.util.SessionProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/** This class responsible for performing import operation of PCC Rule Data
 * & hierarchy
 * Created by aditya on 9/29/16.
 */
public class PCCRuleImportOperation implements ImportOperation<PCCRuleData,PCCRuleData,PCCRuleData> {

    @Override
    public void importData(@Nonnull PCCRuleData pccRuleData, @Nullable PCCRuleData parentObject, @Nullable PCCRuleData superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {

        importPCCRule(pccRuleData,session);

    }


    private void importPCCRule(PCCRuleData pccRule,SessionProvider session) {
        if (PCCRuleScope.LOCAL == pccRule.getScope()) {
            return ;
        }
        List<ServiceDataFlowData> serviceDataFlowDatas = pccRule.getServiceDataFlowList();
        pccRule.setServiceDataFlowList(null);
        if (Strings.isNullOrBlank(pccRule.getId())) {
            pccRule.setId(null);
            session.getSession().persist(pccRule);
        } else {
            session.getSession().merge(pccRule);
        }
        for(ServiceDataFlowData sdf : serviceDataFlowDatas){
            sdf.setPccRule(pccRule);
            if(Strings.isNullOrBlank(sdf.getServiceDataFlowId())){
                sdf.setServiceDataFlowId(null);
                session.getSession().persist(sdf);
            }else{
                session.getSession().merge(sdf);
            }
        }
    }
}