<%@ page import="com.elitecore.nvsmx.system.util.NVSMXUtil" %>
<div class="modal col-xs-12" id="rncProfileDialogId" tabindex="-1" role="dialog" aria-labelledby="rncProfileDialog"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close" onclick="clearRncProfileDialog()">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title set-title" id="serviceDialogTitle">
                    <s:text name="button.quotaprofile"/>
                </h4>
            </div>
            <s:form action="policydesigner/rnc/RncProfile/create" method="post" cssClass="form-horizontal"
                    validate="true" namespace="/" id="rncProfileForm">
                <div class="modal-body">
                    <s:token/>
                    <div class="row">
                        <div class="col-xs-12 col-sm-12">
                            <s:hidden id="pkgId" value="%{pkgData.id}" name="pkgId"/>
                            <s:hidden id="entityOldGroups" name="entityOldGroups" value="%{pkgData.groups}"/>
                            <s:hidden id="groupIds" name="groupIds" value="%{pkgData.groups}"/>
                            <s:hidden id="rncProfileId" name="rncProfileData.id"/>
                            <s:textfield name="rncProfileData.name" key="rnc.quotaprofile.name" id="rncProfileName"
                                         cssClass="form-control focusElement"
                                         labelCssClass="col-xs-4 col-sm-4 text-right"
                                         elementCssClass="col-xs-8 col-sm-8"/>
                            <s:textarea name="rncProfileData.description" key="rnc.quotaprofile.description"
                                        labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8"
                                        cssClass="form-control" rows="2" id="description"/>
                            <s:select list="@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@values()"
                                      key="rnc.quotaprofile.quota.type" name="rncProfileData.quotaType"
                                      labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8"
                                      cssClass="form-control" id="quotaType" listKey="name()" listValue="getValue()"
                                      onchange="readOnlyVolumeType()"/>
                            <s:select list="@com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType@values()"
                                      key="rnc.quotaprofile.unit.type" name="rncProfileData.unitType"
                                      labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8"
                                      cssClass="form-control" id="unitType" listKey="name()" listValue="getValue()"/>
                            <s:select name="rncProfileData.balanceLevel" key="rnc.quotaprofile.balancelevel"
                                      list="@com.elitecore.corenetvertex.constants.BalanceLevel@values()"
                                      labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8"
                                      listKey="name()" listValue="getDisplayVal()" id="balanceLevel"/>
                            <s:textfield name="rncProfileData.renewalInterval" key="rnc.quotaprofile.renewal.interval"
                                         cssClass="form-control" id="renewalInterval"
                                         labelCssClass="col-xs-4 col-sm-4 text-right"
                                         elementCssClass="col-xs-8 col-sm-8" maxlength="3" onkeypress="return isNaturalInteger(event);" />
                            <s:select list="validRenewalIntervals"
                                      key="rnc.quotaprofile.renewal.interval.unit"
                                      name="rncProfileData.renewalIntervalUnit"
                                      labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8"
                                      cssClass="form-control" id="renewalIntervalUnit" listKey="name()"
                                      listValue="value()" onchange="checkForTillBillDate();" />
                            <s:select cssClass="form-control"
                                      list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringName()"
                                      name="rncProfileData.proration" id="proration"
                                      key="rnc.quotaprofile.proration" labelCssClass="col-xs-4 col-sm-4 text-right"
                                      elementCssClass="col-xs-8 col-sm-8" />
                            <s:select cssClass="form-control"
                                      list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey ="isBooleanValue()" listValue="getStringName()"
                                      name="rncProfileData.carryForward" id="carryForward"
                                      key="rnc.quotaprofile.carryForward" labelCssClass="col-xs-4 col-sm-4 text-right"
                                      elementCssClass="col-xs-8 col-sm-8" />

                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-sm btn-primary" type="button" onclick="verifyForm()"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel"
                            onclick="clearRncProfileDialog()"><s:text name="button.cancel"/></button>
                </div>
            </s:form>
        </div>
    </div>
</div>

<script>

    function clearRncProfileDialog() {
        //TODO
    }


    function checkForTillBillDate() {
        var renewalIntervalUnit = $("#renewalIntervalUnit").val();
        if (renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@TILL_BILL_DATE.name()"/>') {
            $("#renewalInterval").attr('disabled', 'true');
        }else {
            $("#renewalInterval").removeAttr('disabled');
        }
        checkForProration();
        checkForCarryForward();
    }

    function checkForProration(){
        var renewalIntervalUnit = $("#renewalIntervalUnit").val();
        if (renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@TILL_BILL_DATE.name()"/>' ||
            renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@MONTH.name()"/>' ||
            renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@MONTH_END.name()"/>') {
            $("#proration").removeAttr('disabled');
        }else {
            $("#proration").attr('disabled', 'true');
            $("#proration").val('<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()}" />');

        }
    }

    function checkForCarryForward() {
        var renewalIntervalUnit = $("#renewalIntervalUnit").val();
        if (renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@TILL_BILL_DATE.name()"/>' ||
            renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@MONTH.name()"/>' ||
            renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@MONTH_END.name()"/>') {
            $("#carryForward").removeAttr('disabled');
        }else {
            $("#carryForward").attr('disabled', 'true');
            $("#carryForward").val('<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()}" />');
        }
    }

    function addRncProfile() {
        $("#rncProfileName").val("");
        $("#renewalInterval").val("");
        $("#renewalIntervalUnit").val('<s:property value="%{@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@MONTH.name()}" />');
        $("#description").val('<%=NVSMXUtil.getDefaultDescription(request)%>');
        $("#proration").val('<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()}" />');
        $("#carryForward").val('<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()}" />');
        readOnlyVolumeType();
        $("#rncProfileDialogId").modal('show');
    }

    var action = null;

    function updateRncProfile(id) {
        clearErrorMessages(rncProfileForm);
        var rncProfileData = <%=rncProfileData%>;
        for (var i in rncProfileData) {
            var rncProfileId = rncProfileData[i].id;
            if (rncProfileId == id) {
                $("#rncProfileId").val(rncProfileData[i].id);
                $("#rncProfileName").val(rncProfileData[i].name);
                $("#description").val(rncProfileData[i].description);
                $("#quotaType").val(rncProfileData[i].quotaType);
                $("#unitType").val(rncProfileData[i].unitType);
                $("#balanceLevel").val(rncProfileData[i].balanceLevel);
                $("#renewalInterval").val(rncProfileData[i].renewalInterval);
                $("#renewalIntervalUnit").val(rncProfileData[i].renewalIntervalUnit);
                $("#proration").val(""+rncProfileData[i].proration);
                $("#carryForward").val(""+rncProfileData[i].carryForward);

            }
        }
        action = "update";
        readOnlyVolumeType();
        checkForTillBillDate();
        $("#rncProfileDialogId").modal('show');
    }

    function verifyForm() {
        if (action == 'update') {
            document.rncProfileForm.action = "${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/update";
        }
        $("#rncProfileForm").submit();
    }

    function readOnlyVolumeType() {
        var quotaType = $("#quotaType").val();
        if ('<s:property value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.name()}" />' == quotaType) {
            $("#unitType").attr('readOnly', 'true');
        } else {
            $("#unitType").removeAttr('readOnly')
        }

    }
</script>