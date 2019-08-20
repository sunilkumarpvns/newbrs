package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.sm.gateway.ServiceGuidingData;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.core.data.ServiceGuide;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ServiceGuideFactory {

    private static final String MODULE = "SERVICE_GUIDE_FACTORY";

    public List<ServiceGuide> create(List<ServiceGuidingData> serviceGuidingData){
        List serviceGuideList = new ArrayList();

        if(serviceGuidingData==null){
            return serviceGuideList;
        }

        List activeServiceGuides = new ArrayList();
        List inactiveServiceGuides = new ArrayList();

        for(ServiceGuidingData guidingData : serviceGuidingData){
            try {
                String condition = guidingData.getCondition();

                ServiceGuide serviceGuide;
                if(condition==null){
                    serviceGuide = new ServiceGuide(null, guidingData.getCondition(), guidingData.getServiceData().getId());
                } else {
                    LogicalExpression expression = (LogicalExpression) Compiler.getDefaultCompiler().parseExpression(guidingData.getCondition());
                    serviceGuide = new ServiceGuide(expression, guidingData.getCondition(), guidingData.getServiceData().getId());
                }

                if(PkgStatus.ACTIVE.name().equals(guidingData.getServiceData().getStatus())){
                    activeServiceGuides.add(serviceGuide);
                } else {
                    inactiveServiceGuides.add(serviceGuide);
                }

            } catch (InvalidExpressionException | ClassCastException ex) {
                getLogger().error(MODULE, "Skipping service guiding for service:" + guidingData.getServiceData().getId() + ". Reason: Invalid condition = " + guidingData.getCondition());
                getLogger().trace(MODULE, ex);
            }
        }

        serviceGuideList.addAll(activeServiceGuides);
        serviceGuideList.addAll(inactiveServiceGuides);

        return serviceGuideList;
    }

}
