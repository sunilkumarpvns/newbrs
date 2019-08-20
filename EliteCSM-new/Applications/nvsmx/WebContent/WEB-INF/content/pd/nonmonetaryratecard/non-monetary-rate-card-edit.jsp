<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<style type="text/css">
    .customized-row-margin {
        margin-right: -13px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="nonmonetary.ratecard.update"/></h3>
    </div>
    <div class="panel-body" class="col-sm-6">
        <s:form namespace="/pd/nonmonetaryratecard" action="non-monetary-rate-card" id="nonmonetaryrateCardEdit"
                method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4"
                elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <s:hidden name="rncPkgId" value="%{rncPkgId}"/>
            <s:hidden name="scope" value="LOCAL"/>
            <div class="row">
                <div class="col-xs-12 col-sm-6">
                    <div class="col-xs-12">
                        <strong><s:text name="nonmonetary.ratecard.name"/> </strong>
                    </div>
                    <div class="col-xs-12">
                        <s:textfield name="name" id="nonmonetaryratecardName"
                                     elementCssClass="col-xs-12"
                                     cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
                    </div>
                    <div class="col-xs-12">
                        <strong><s:text name="nonmonetary.ratecard.description"/> </strong>
                    </div>
                    <div class="col-xs-12">
                        <s:textarea name="description" id="nonmonetaryratecardDescription"
                                    elementCssClass="col-xs-12"
                                    cssClass="form-control"
                                    rows="2"
                                    maxlength="2000" tabindex="2"/>
                    </div>
                    <div class="col-xs-12">
                        <strong><s:text name="nonmonetary.ratecard.renewal.interval"/> </strong>
                    </div>
                    <div class="col-xs-12">
                        <div class="col-xs-6">
                            <div class="row customized-row-margin">
                                <s:textfield name="nonMonetaryRateCardData.renewalInterval" id="renewalInterval"
                                             elementCssClass="col-xs-12"
                                             cssClass="form-control" maxlength="3" tabindex="3"
                                             onkeypress="return isNaturalInteger(event);"/>

                            </div>
                        </div>
                        <div class="col-xs-6">
                            <div class="row customized-row-margin">
                                <s:select name="nonMonetaryRateCardData.renewalIntervalUnit"
                                          elementCssClass="col-xs-12" cssClass="form-control" listKey="name()"
                                          listValue="value()"
                                          list="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@values()"
                                          id="renewalIntervalUnit" tabindex="4" onchange="checkForTillBillDate();"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-xs-12 col-sm-6">
                    <div class="col-xs-12">
                        <strong><s:text name="nonmonetary.ratecard.pulse"/> </strong>
                    </div>
                    <div class="col-xs-12">
                        <div class="col-xs-6">
                            <div class="row customized-row-margin">
                                <s:set value="%{chargingType == @com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}" var="isEventBasedPackage"/>
                                <s:if test="%{isEventBasedPackage}">
                                    <s:textfield name="nonMonetaryRateCardData.pulse" id="nonmonetaryratecardPulse"
                                                 elementCssClass="col-xs-12"
                                                 cssClass="form-control" maxlength="18" tabindex="4"
                                                 readonly="true" value="1"/>
                                </s:if>
                                <s:else>
                                    <s:textfield name="nonMonetaryRateCardData.pulse" id="nonmonetaryratecardPulse"
                                                 elementCssClass="col-xs-12"
                                                 cssClass="form-control" maxlength="18" tabindex="4"
                                                 onkeypress="return isNaturalInteger(event);" />
                                </s:else>
                            </div>
                        </div>
                        <div class="col-xs-6">
                            <div class="row customized-row-margin">
                                <s:if test="%{isEventBasedPackage}" >
                                    <s:textfield name="nonMonetaryRateCardData.pulseUom" id="nonmonetaryratecardPulseUom"
                                                 elementCssClass="col-xs-12"
                                                 cssClass="form-control" value="%{@com.elitecore.corenetvertex.constants.Uom@EVENT.name()}" tabindex="5" headerKey=" " readonly="true"/>
                                </s:if>
                                <s:else>
                                    <s:select name="nonMonetaryRateCardData.pulseUom" id="nonmonetaryratecardPulseUom"
                                              elementCssClass="col-xs-12"
                                              cssClass="form-control" list="pulseUnits" tabindex="5" headerKey=" "/>
                                </s:else>
                            </div>
                        </div>
                    </div>
                    <s:if test="%{isEventBasedPackage}" >
                        <div class="col-xs-12">
                            <strong><s:text name="nonmonetary.ratecard.freeunits"/> </strong>
                        </div>
                        <div class="col-xs-12">
                            <div class="col-xs-12">
                                <div class="row customized-row-margin">
                                    <s:textfield name="nonMonetaryRateCardData.event" id="nonmonetaryratecardEvent"
                                                 elementCssClass="col-xs-12"
                                                 cssClass="form-control" maxlength="18" tabindex="6"
                                                 onkeypress="return isNaturalInteger(event);"/>
                                </div>
                            </div>
                        </div>
                    </s:if>

                    <s:else>
                    <div class="col-xs-12">
                        <strong><s:text name="nonmonetary.ratecard.freeunits"/> </strong>
                    </div>
                    <div class="col-xs-12">
                        <div class="col-xs-6">
                            <div class="row customized-row-margin">
                                <s:textfield name="nonMonetaryRateCardData.time" id="nonmonetaryratecardTime"
                                             elementCssClass="col-xs-12"
                                             cssClass="form-control" maxlength="18" tabindex="7"
                                             onkeypress="return isNaturalInteger(event);" placeholder="UNLIMITED"/>
                            </div>
                        </div>
                        <div class="col-xs-6">
                            <div class="row customized-row-margin">
                                <s:select name="nonMonetaryRateCardData.timeUom" id="nonmonetaryratecardtimeUom"
                                          elementCssClass="col-xs-12"
                                          cssClass="form-control" list="{}" tabindex="8" headerKey=" " value="nonMonetaryRateCardData.timeUom"/></div>
                        </div>
                    </div>
                    </s:else>

                    <!-- proration -->
                    <div class="col-xs-12">
                        <strong><s:text name="nonmonetary.ratecard.proration"/> </strong>
                    </div>
                    <div class="col-xs-12">
                        <s:select tabindex="9"
                                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringName()"
                                  name="nonMonetaryRateCardData.proration" id="proration" elementCssClass="col-xs-12"/>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="submit" class="btn btn-primary btn-sm" id="btnSubmit" role="submit"
                            formaction="${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/${id}"
                            tabindex="10"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text
                            name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/${id}'"
                            tabindex="11"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                            name="button.back"/></button>

                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">
    $(function () {
        validateUnits();
        checkForTillBillDate();
    });

    function validateForm() {
        var isEventBasedPackage = <s:property value="%{isEventBasedPackage}"/>;
        if( isEventBasedPackage == true){
            if(validateEvent() == false){
                return false;
            }
        } else {
            if (validatePulseAndTime() == false) {
                return false;
            }
        }

        return verifyUniquenessOnSubmit('nonmonetaryratecardName', 'update', '<s:property value="id"/>', 'com.elitecore.corenetvertex.pd.ratecard.RateCardData', '${rncPkgId}', '') && validateNonNegativeValue();
    }
</script>
<%@include file="non-monetary-rate-card-utility.jsp" %>