package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.LogicalExpression;
import com.elitecore.core.serverx.sessionx.criterion.SimpleExpression;
import com.elitecore.core.serverx.sessionx.impl.CriteriaImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * @author Prakash Pala
 * @since 7-Mar-2019
 * Criteria Support for TBLT_SESSION table in VoltDB.
 * Only allowed PCRF Keys are supported for Core Session table. Also only "or" logical criteria is supported.
 */
public class VoltDBCoreSessionCriteria extends CriteriaImpl {

    private final List<String> allowedCoreSessionKeys = new ArrayList<>(Arrays.asList(
                PCRFKeyConstants.CS_CORESESSION_ID.val
                , PCRFKeyConstants.CS_SESSION_ID.val
                , PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val
                , PCRFKeyConstants.CS_SESSION_IPV4.val
                , PCRFKeyConstants.CS_SESSION_IPV6.val
                , PCRFKeyConstants.CS_SY_SESSION_ID.val
                , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val)
    );

    private List<String> coreSessionIdValues;
    private List<String> sessionIdValues;
    private List<String> subscriberIdValues;
    private List<String> sessionIpV4Values;
    private List<String> sessionIpV6Values;
    private List<String> sySessionIdValues;
    private List<String> gatewayAddressValues;

    public VoltDBCoreSessionCriteria(String schemaName) {
        super(schemaName);
        coreSessionIdValues = new ArrayList<>();
        sessionIdValues = new ArrayList<>();
        subscriberIdValues = new ArrayList<>();
        sessionIpV4Values = new ArrayList<>();
        sessionIpV6Values = new ArrayList<>();
        sySessionIdValues = new ArrayList<>();
        gatewayAddressValues = new ArrayList<>();
    }

    public List<String> getCoreSessionIdValues() {
        return coreSessionIdValues;
    }

    public List<String> getSessionIdValues() {
        return sessionIdValues;
    }

    public List<String> getSubscriberIdValues() {
        return subscriberIdValues;
    }

    public List<String> getSessionIpV4Values() {
        return sessionIpV4Values;
    }

    public List<String> getSessionIpV6Values() {
        return sessionIpV6Values;
    }

    public List<String> getSySessionIdValues() {
        return sySessionIdValues;
    }

    public List<String> getGatewayAddressValues() {
        return gatewayAddressValues;
    }

    @Override
    public void add(Criterion criterion) {
        if(isNotEmpty(super.getCriterions())){
            throw new UnsupportedOperationException("Only one criterion is supported in VoltDB");
        }
        if (checkForValidCriterion(criterion)) {
            super.add(criterion);
        }
    }

    private boolean checkForValidCriterion(Criterion criterion){
        boolean isValid = false;
        switch(criterion.getExpressionType()){
            case Criterion.SIMPLE_EXPRESSION:
                isValid = checkForSimpleExpression(criterion);
                break;
            case Criterion.LOGICAL_EXPRESSION:
                isValid = checkForLogicalExpression(criterion);
                break;
            case Criterion.NOT_EXPRESSION:
                throw new UnsupportedOperationException("NOT Expression is not supported in VoltDB");
        }
        return isValid;
    }

    private boolean checkForSimpleExpression(Criterion criterion) {
        SimpleExpression simpleExpression = (SimpleExpression)criterion;
        String key = simpleExpression.getPropertyName();
        String value = simpleExpression.getValue();

        if(allowedCoreSessionKeys.contains(key) == false){
            throw new UnsupportedOperationException("Only following keys are supported in Criterion for VoltDB: "+allowedCoreSessionKeys);
        } else if(coreSessionIdValues.isEmpty() == false
                && PCRFKeyConstants.CS_CORESESSION_ID.val.equals(key) == false){
            throw new UnsupportedOperationException("Other Criterion cannot be combined with CS_CORESESSION_ID.");
        }
        addToCoreSessionLists(key, value);
        return true;
    }

    private void addToCoreSessionLists(String key, String value){
        if(PCRFKeyConstants.CS_CORESESSION_ID.val.equals(key)){
            coreSessionIdValues.add(value);
        } else if (PCRFKeyConstants.CS_SESSION_ID.val.equals(key)){
            sessionIdValues.add(value);
        } else if (PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val.equals(key)){
            subscriberIdValues.add(value);
        } else if (PCRFKeyConstants.CS_SESSION_IPV4.val.equals(key)){
            sessionIpV4Values.add(value);
        } else if (PCRFKeyConstants.CS_SESSION_IPV6.val.equals(key)){
            sessionIpV6Values.add(value);
        } else if (PCRFKeyConstants.CS_SY_SESSION_ID.val.equals(key)){
            sySessionIdValues.add(value);
        } else if (PCRFKeyConstants.CS_GATEWAY_ADDRESS.val.equals(key)){
            gatewayAddressValues.add(value);
        }
    }

    private boolean checkForLogicalExpression(Criterion criterion) {
        LogicalExpression logicalExpression = (LogicalExpression) criterion;
        return "or".equalsIgnoreCase(logicalExpression.getOp())
                && checkForValidCriterion(logicalExpression.getLhs())
                && checkForValidCriterion(logicalExpression.getRhs());
    }
}