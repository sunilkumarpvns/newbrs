<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<jsp:include page="pcccommon.jsp"/>
<style>
    .small-text-grey {
        font-family: Arial;
        font-size: 11px;
        color: #949494;
    }
</style>
<div class="panel panel-primary">

    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="pccServicePolicy.update" /></h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/sm/pccservicepolicy"
                action="pcc-service-policy"
                id="pccServiceUpdateForm"
                cssClass="form-horizontal"
                validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3"
                elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                theme="bootstrap">

            <s:hidden name="_method" value="put" />
            <s:hidden name="orderNumber"/>

            <s:token/>

            <div class="row">

                <div class="col-xs-12 col-sm-6 col-lg-6">

                    <s:textfield name="name"
                                 key="pccServicePolicy.name"
                                 id="name"
                                 cssClass="form-control focusElement"
                                 onblur="verifyUniqueness('name','update','%{id}','com.elitecore.corenetvertex.sm.pccservicepolicy.PccServicePolicyData','','');"
                                 tabindex="1"/>

                    <s:textarea name="description"
                                key="pccServicePolicy.description"
                                cssClass="form-control"
                                rows="2" tabindex="2"/>

                    <s:select name="status"
                              key="pccServicePolicy.status"
                              cssClass="form-control"
                              list="@com.elitecore.corenetvertex.constants.ServicePolicyStatus@values()"
                              listKey="name()"
                              listValue="getLabel()"
                              tabindex="3"/>

                    <s:textarea name="ruleset"
                                id="ruleset"
                                key="pccServicePolicy.ruleset"
                                cssClass="form-control"
                                onblur="validateRuleset()"
                                rows="2" tabindex="4"/>

                    <s:label cssClass="small-text-grey col-xs-12 col-sm-12"
                             cssStyle="margin-left:125px;margin-top:-10px;"
                             value="Possible Operators: AND , OR , NOT , IN , ( , ) , < , > , =" />

                    <s:select name="action"
                              key="pccServicePolicy.action"
                              cssClass="form-control"
                              list="@com.elitecore.corenetvertex.constants.RequestAction@values()"
                              listKey="name()"
                              listValue="getLabel()"
                              tabindex="5"/>

                    <s:select name="subscriberLookupOn"
                              key="pccServicePolicy.subscriberLookupOn"
                              cssClass="form-control"
                              list="@com.elitecore.corenetvertex.constants.SubscriberLookupOn@values()"
                              listKey="name()"
                              listValue="getLabel()"
                              tabindex="5"/>

                    <s:textfield name="identityAttribute"
                                 key="pccServicePolicy.identity.attribute"
                                 cssClass="form-control identityAttributesSuggestions"
                                 maxlength="100" tabindex="6"/>

                </div>

                <div class="col-xs-12 col-sm-6 col-lg-6">

                    <s:select name="unknownUserAction"
                              id="unknownUserAction"
                              key="pccServicePolicy.unknown.user.action"
                              cssClass="form-control"
                              list="@com.elitecore.corenetvertex.constants.UnknownUserAction@values()"
                              listValue="getName()"
                              listKey="name()"
                              onchange="enableDisablePkgWithUnknownUserAction();"
                              tabindex="7"/>

                    <s:select name="unknownUserPkgId"
                              id="unknownUserPkgId"
                              key="pccServicePolicy.unknown.user.productoffer"
                              cssClass="form-control"
                              headerKey=""
                              headerValue="Select"
                              list="productOffers"
                              listKey="getId()"
                              listValue="getName()"
                              onchange="enableDisablePkgWithUnknownUserAction();"
                              tabindex="8"/>

                    <s:select name="syGatewayId"
                              key="pccServicePolicy.sy.gateway"
                              cssClass="form-control"
                              list="syGateways"
                              listValue="getName()"
                              listKey="getId()"
                              headerKey=""
                              headerValue="Select"
                              tabindex="9"/>

                    <s:select name="syMode"
                              key="pccServicePolicy.sy.mode"
                              cssClass="form-control"
                              list="@com.elitecore.corenetvertex.constants.SyMode@values()"
                              listValue="name()"
                              listKey="name()"
                              tabindex="10"/>

                    <s:select name="policyCdrDriverId"
                              id="policyCdrDriverId"
                              key="pccServicePolicy.policy.cdr"
                              cssClass="form-control"
                              list="policyCDRDrivers"
                              listKey="getId()"
                              listValue="getName()"
                              headerKey=""
                              headerValue="SELECT"
                              tabindex="11"/>

                    <s:select name="chargingCdrDriverId"
                              id="chargingCdrDriverId"
                              key="pccServicePolicy.charging.cdr"
                              cssClass="form-control"
                              list="dbCdrDrivers"
                              listKey="getId()"
                              listValue="getName()"
                              headerValue="SELECT"
                              headerKey=""
                              tabindex="12"/>
                </div>

            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary"
                            type="submit"
                            onclick="return validateOnSubmit()"
                            role="button"
                            formaction="${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/${id}" tabindex="13">
                            <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
                    </button>

                    <button type="button"
                            id="btnCancel"
                            class="btn btn-primary btn-sm"
                            value="Cancel"
                            tabindex="14"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy'">
                            <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>


<script type="text/javascript">

    function validateOnSubmit(){
        if(!validateRuleset()){
            return false;
        }
        if(!checkUnknownUserPackage()){
            return false;
        }
        if(!verifyCdrDrivers()){
            return false;
        }
        return verifyUniquenessOnSubmit('name','update','${id}','com.elitecore.corenetvertex.sm.pccservicepolicy.PccServicePolicyData','','') ;
    }
</script>
