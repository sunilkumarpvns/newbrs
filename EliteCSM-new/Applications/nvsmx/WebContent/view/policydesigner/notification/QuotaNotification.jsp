<%--
  Created by IntelliJ IDEA.
  User: saloni
  Date: 19/6/18
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.elitecore.corenetvertex.pkg.PkgType" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<script type="text/javascript">

    var action = null;
    $(document).ready(function(){
        clearDialog();
    });

    function updateQuotaNotificationDialog(id){
        clearErrorMessages(quotaNotificationForm);
        var quotaNotificationData = <%=quotaNotificationData%>;
        for(var i in quotaNotificationData)
        {
            var quotaNotificationId = quotaNotificationData[i].id;
            if(quotaNotificationId == id){
                $("#quotaNotificationId").val(quotaNotificationData[i].id);
                $("#quotaProfile").val(quotaNotificationData[i].quotaProfile.id);
                $("#dataServiceTypes").val(quotaNotificationData[i].dataServiceTypeData.id);
                $("#aggregationkey").val(quotaNotificationData[i].aggregationKey);
                $("#fupLevel").val(quotaNotificationData[i].fupLevel);

                if (quotaNotificationData[i].emailTemplateData != null) {
                    $("#emailTemplate").val(quotaNotificationData[i].emailTemplateData.id);
                }
                if (quotaNotificationData[i].smsTemplateData != null) {
                    $("#smsTemplate").val(quotaNotificationData[i].smsTemplateData.id);
                }

                $("#threshold").val(quotaNotificationData[i].threshold);
                $("#quotaNotificationDialog").modal('show');
                action = "update";
            }
        }

    }

    function isUniqueNotification(action)
    {
        var tableData = $('#quotaNotificationData').DataTable().rows().data();
        var quotaProfileId = $('#quotaProfile').val();
        var dataServiceTypes = $('#dataServiceTypes').val();
        var aggregationKey = $('#aggregationkey').val();
        var fupLevel = Number($('#fupLevel').val());
        var threshold = Number($('#threshold').val());
        var checkUniqueness = true;
        var quotaNotificationId = $('#quotaNotificationId').val();

        tableData.each(function (value, index) {
            if(action == 'update') {

                if(checkUniqueness && value.quotaProfile.id == quotaProfileId && value.dataServiceTypeData.id == dataServiceTypes && value.aggregationKey == aggregationKey
                    && value.fupLevel == fupLevel && value.threshold == threshold && value.id != quotaNotificationId) {
                    checkUniqueness = false;
                }
            } else if(action == 'create') {
                if(checkUniqueness && value.quotaProfile.id == quotaProfileId && value.dataServiceTypeData.id == dataServiceTypes && value.aggregationKey == aggregationKey
                    && value.fupLevel == fupLevel && value.threshold == threshold) {
                    checkUniqueness = false;
                }
            }
        })

        if(!checkUniqueness) {
            $(".generalError").addClass("bg-danger");
            $(".generalError").text("Notification already configured for threshold: "+threshold);
        }

        return checkUniqueness;
    }

    function submitForm(){
        if(!isUniqueNotification(action)) {
            return false;
        }

        if($("#emailTemplate").val() == "" && $("#smsTemplate").val() == ""){
            setError('emailTemplate', "Email Template or Sms Template is required");
            setError('smsTemplate', "Email Template or Sms Template is required");
            return false;
        }
        if(action == 'update'){
            if('<%=PkgType.PROMOTIONAL.name()%>'=='<s:property value="pkgData.type"/>'){
                document.quotaNotificationForm.action = "${pageContext.request.contextPath}/promotional/policydesigner/notification/QuotaNotification/PromotionalUsageNotification/update";
            }else{
                document.quotaNotificationForm.action = "${pageContext.request.contextPath}/policydesigner/notification/QuotaNotification/update";
            }
        }else if(isNullOrEmpty(action) || action == 'create'){
            if('<%=PkgType.PROMOTIONAL.name()%>'=='<s:property value="pkgData.type"/>'){
                document.quotaNotificationForm.action = "${pageContext.request.contextPath}/promotional/policydesigner/notification/QuotaNotification/PromotionalUsageNotification/create";
            }else{
                document.quotaNotificationForm.action = "${pageContext.request.contextPath}/policydesigner/notification/QuotaNotification/create";
            }

        }
        $("#quotaNotificationForm").submit();
    }

    function clearQuotaNotificationDialog() {
        $(".generalError").removeClass("bg-danger");
        $(".generalError").text("");
        $("#threshold").val("");
    }

</script>
<div class="modal" id="quotaNotificationDialog" tabindex="-1" role="dialog" aria-labelledby="quotaNotificationDialog1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close" onclick="clearDialog()">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title set-title">
                    <s:text name="quotanotification" />
                </h4>
            </div>
            <s:form id="quotaNotificationForm" namespace="/" action="policydesigner/notification/QuotaNotification/create" validate="true" method="post" cssClass="form-horizontal"  >
                <s:token />
                <s:hidden name="groupIds" value="%{pkgData.groups}" />
                <s:hidden name="entityOldGroups" value="%{pkgData.groups}" />
                <div class="modal-body">
                    <div class="row">
                        <s:hidden name="pkgId" value="%{pkgData.id}" />
                        <s:hidden name="quotaNotificationData.id" id="quotaNotificationId" />
                        <div class="col-xs-12">
                            <s:select 		name="quotaNotificationData.quotaProfile.id"  key="quotanotification.quotaprofile"  list="pkgData.rncProfileDatas" listValue="name" listKey="id"  id="quotaProfile" cssClass="form-control focusElement" />
                            <s:select 		name="quotaNotificationData.dataServiceTypeData.id"		key="quotanotification.servicetype"  list="dataServiceTypeData" listValue="name" listKey="id"  id="dataServiceTypes" cssClass="form-control" />
                            <s:select   	name="quotaNotificationData.aggregationKey"  key="quotanotification.aggregationkey" cssClass="form-control" list="aggregationKeys" id="aggregationkey"  listValue="getVal()" />
                            <s:select       name="quotaNotificationData.fupLevel"   key="quotanotification.fupLevel" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.BalanceLevel@values()" id="fupLevel" listKey="getFupLevel()" listValue="getDisplayVal()"/>
                            <s:select 		name="quotaNotificationData.emailTemplateData.id" key="quotanotification.emailtemplate"  list="emailTemplateDatas" listValue="name" listKey="id"  id="emailTemplate" cssClass="form-control" headerValue="-- Select --" headerKey=""/>
                            <s:select 		name="quotaNotificationData.smsTemplateData.id" 	 key="quotanotification.smstemplate"  list="smsTemplateDatas" listValue="name" listKey="id"  id="smsTemplate" cssClass="form-control" headerValue="-- Select --" headerKey="" />
                            <s:textfield 	name="quotaNotificationData.threshold" 		 key="quotanotification.threshold" 	id="threshold" cssClass="form-control" />
                        </div>
                    </div>
                </div>
                <div class="col-xs-12">
                    <div class="col-xs-12 generalError" ></div>
                </div>
                <div class="modal-footer" style="text-align: center;">
                    <button class="btn btn-sm btn-primary" type="button" onclick="submitForm()"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-sm btn-primary" data-dismiss="modal" onclick="clearQuotaNotificationDialog()"><s:text name="button.cancel"></s:text> </button>
                </div>
            </s:form>
        </div>
    </div>
</div>