package com.elitecore.netvertex.pm.qos.rncbaseqosprofile

import com.elitecore.commons.io.IndentingPrintWriter
import com.elitecore.corenetvertex.pkg.PkgType
import com.elitecore.corenetvertex.pm.constants.SelectionResult
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName
import com.elitecore.corenetvertex.pm.pkg.PCCRule
import com.elitecore.netvertex.pm.*
import spock.lang.Specification

class QuotProfileNotConfiguredTest extends Specification {

    private QoSInformation qoSInformation;
    private PolicyContext policyContext;
    private RnCQoSProfileDetailFactory.RnCQoSProfileDetialBuilder builder;
    def setup() {
        qoSInformation = new QoSInformation();

        BasePackage basePackage = Mock(BasePackage);

        basePackage.getPackageType() >> PkgType.BASE
        qoSInformation.startPackageQoSSelection(basePackage)
        policyContext = Mock(PolicyContext)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        builder = RnCQoSProfileDetailFactory.createQoSProfile().hasRnCQuota()
    }

    def "Set current details as selected QoS profile detail"() {

        given:

        def detail = builder.noPCC().build();

        when:
        detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        qoSInformation.endQoSSelectionProcess()

        then:
        qoSInformation.endProcess().qosProfileDetail  == detail

    }




    def "QoS profile will be fully selected when action is accept"() {


        given:

        def detail = builder.noPCC().build();

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        then:
        result  == SelectionResult.FULLY_APPLIED



    }

    def "QoS profile will be fully selected when action is reject"() {

        given:

        def detail = builder.qosProfileDetailBuilder.rejectAction("test").build();

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        then:
        result  == SelectionResult.FULLY_APPLIED



    }

    def "Select all PCC Rules"() {

        given:

        PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().build();
        def detail = builder.pccRules([pccRule]).build()

        when:
        detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        qoSInformation.endQoSSelectionProcess()
        def selectedPCCRules = qoSInformation.endProcess().pccRules.values()

        then:

        selectedPCCRules.size() == 1
        selectedPCCRules.getAt(0) == pccRule

    }

    def "Select all ChargingRuleBaseName"() {

        given:

        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)
        def detail = builder.withCRBN([chargingRuleBaseName]).build()

        when:
        detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        qoSInformation.endQoSSelectionProcess()
        def selectedCRBNs = qoSInformation.endProcess().chargingRuleBaseNames

        then:

        selectedCRBNs.size() == 1
        selectedCRBNs.getAt(0) == chargingRuleBaseName

    }


    def "Not add ChargingRuleBaseName when any previous qos profile detail has selected from package"() {

        given:

        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)
        def detail = builder.withCRBN([chargingRuleBaseName]).build()
        qoSInformation.setQoSProfileDetail(detail);

        when:
        detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        qoSInformation.endQoSSelectionProcess()
        def selectedCRBNs = qoSInformation.endProcess().chargingRuleBaseNames

        then:
        selectedCRBNs == null



    }
}
