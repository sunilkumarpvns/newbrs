<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="data.topup.create"/></h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/pd/datatopup" action="data-topup" id="datatopupform" method="post"
                cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                validator="validateForm()">
        <s:token/>
        <div class="row">
            <div class="col-xs-6">
                <s:textfield name="name" key="data.topup.name" id="name" cssClass="form-control focusElement"
                             tabindex="1"/>

                <s:textarea name="description" key="data.topup.description" cssClass="form-control"
                            id="description"
                            value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"
                            tabindex="2"/>

                <s:select name="groups" key="data.topup.groups" cssClass="form-control select2"
                          list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id"
                          listValue="name" cssStyle="width:100%" tabindex="3"/>
                <s:select name="status" key="data.topup.status" cssClass="form-control" tabindex="4"
                          list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="dataTopupStatus"/>
                <s:hidden name="packageMode" value="%{@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}"
                          id="packageMode"
                          tabindex="5"/>
                <s:select name="topupType" key="data.topup.type" cssClass="form-control" listValue="getVal()"
                          list="@com.elitecore.corenetvertex.pd.topup.TopUpType@values()" id="dataTopupType"
                          tabindex="6"/>
                <s:select id="mulitpleSubscription" name="multipleSubscription"
                          key="data.topup.multiple.subscription"
                          list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                          listKey="isBooleanValue()" listValue="getStringName()" cssClass="form-control"
                          value="@com.elitecore.corenetvertex.constants.CommonStatusValues@ENABLE.getStringName()"
                          tabindex="7"/>

            </div>
            <div class="col-xs-6">

                <%--<s:datepicker name="availabilityStartDate" key="data.topup.availability.start.date"
                              parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
                              showAnim="slideDown" duration="fast" showOn="focus"
                              placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true"
                              timepickerFormat="HH:mm:ss"
                              id="availabilityStartDate" readonly="true" tabindex="9"/>


                <s:datepicker name="availabilityEndDate" key="data.topup.availability.end.date"
                              parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
                              showAnim="slideDown" duration="fast" showOn="focus"
                              placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true"
                              timepickerFormat="HH:mm:ss"
                              id="availabilityEndDate" readonly="true" tabindex="10"/>--%>

                 <s:select name="applicablePCCProfiles" key="data.topup.applicable.pcc.profiles"
                              cssClass="form-control select2" tabindex="8"
                              list="pccProfileNames" id="applicablePCCProfiles" multiple="true"/>

                <s:textfield name="validityPeriod" key="data.topup.validity.period" cssClass="form-control"
                             type="number" id="validityPeriod" onkeypress="return isNaturalInteger(event);"
                             value="30" tabindex="10"/>


                <s:select name="validityPeriodUnit" key="data.topup.validity.period.unit" cssClass="form-control"
                          list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                          id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="11"/>

                <s:set var="priceTag">
                    <s:property value="getText('data.topup.price')"/> <s:property
                        value="getText('opening.braces')"/><s:property
                        value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property
                        value="getText('closing.braces')"/>
                </s:set>

                <s:textfield name="price" key="priceTag" type="number" step="any"
                             id="price"
                             cssClass="form-control" tabindex="12"/>

                <s:textfield name="param1" key="data.topup.param1" cssClass="form-control" id="param1"
                             tabindex="14"/>
                <s:textfield name="param2" key="data.topup.param2" cssClass="form-control" id="param2"
                             tabindex="15"/>
            </div>
        </div>
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend><s:text name="data.topup.quota.information"/></legend>
                <div class="row">
                    <div class="col-xs-12 col-sm-6">
                        <s:select key="data.topup.quotatype" name="quotaType" tabindex="16" onchange="setVolumeType()"
                                  list="@com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@values()"
                                  listValue="getValue()" id="quotaType"/>
                        <s:textfield name="volumeBalance" tabindex="17" type="number" id="volumeBalance"
                                     key="data.topup.volume.balance" cssClass="form-control"
                                     onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                                     maxlength="18"/>
                        <s:select cssClass="form-control" name="volumeBalanceUnit" id="volumeBalanceUnit"
                                  key="data.topup.volume.balance.unit" tabindex="18"
                                  list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
                                  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <s:select key="data.topup.unittype" name="unitType" tabindex="19"
                                  list="@com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType@values()"
                                  listValue="getValue()" id="unitType"/>
                        <s:textfield name="timeBalance" tabindex="20" type="number" id="timeBalance"
                                     key="data.topup.time.balance" cssClass="form-control"
                                     onkeypress="return isNaturalInteger(event);" min="1" max="999999999999999999"
                                     maxlength="18"/>
                        <s:select cssClass="form-control" name="timeBalanceUnit" id="timeBalanceUnit"
                                  key="data.topup.time.balance.unit" tabindex="21"
                                  list="@com.elitecore.corenetvertex.constants.TimeUnit@values()"
                                  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND.name()}"/>
                    </div>
                </div>
            </fieldset>
        </div>
        <div class="row">
            <div class="col-xs-12" align="center">
                <s:submit cssClass="btn btn-sm btn-primary" type="button" role="button" tabindex="22">
                        <span class="glyphicon glyphicon-floppy-disk">
                        </span> <s:text name="button.save"/>
                </s:submit>
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="23"
                        style="margin-right:10px;"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/datatopup/data-topup'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                </button>
            </div>
        </div>
    </div>

    </s:form>
</div>
</div>
<script type="text/javascript">

    $(function () {
        $(".select2").select2();
        setVolumeType();
    });

    function validateForm() {
        return verifyUniquenessOnSubmit('name', 'create', '', 'com.elitecore.corenetvertex.pd.topup.DataTopUpData', '', '')
            && validateQuotaVolume()
            && validateValidityPeriod && validateStatus();
    }

    function validateStatus() {
        var status = $("#dataTopupStatus").val();
        if (status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>') {
            setError('dataTopupStatus', "<s:text name='error.status.retired'/>");
            return false;
        }
    }

</script>
<%@include file="data-topup-validation.jsp" %>