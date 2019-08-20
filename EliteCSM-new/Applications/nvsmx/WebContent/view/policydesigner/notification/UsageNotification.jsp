<%@ page import="com.elitecore.corenetvertex.pkg.PkgType" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<script type="text/javascript">

var action = null;
$(document).ready(function(){
	clearDialog();
});

function updateUsageNotificationDialog(id){
	clearErrorMessages(usageNotificationForm);
	var usageNotificationData = <%=usageNotificationData%>;
	for(var i in usageNotificationData)
	{
	     var usageNotificationId = usageNotificationData[i].id;
	     if(usageNotificationId == id){
	    	$("#usageNotificationId").val(usageNotificationData[i].id);
		 	$("#quotaProfile").val(usageNotificationData[i].quotaProfile.id);
		 	$("#dataServiceTypes").val(usageNotificationData[i].dataServiceTypeData.id);
		 	$("#aggregationkey").val(usageNotificationData[i].aggregationKey);
		 	$("#meteringType").val(usageNotificationData[i].meteringType);
		 	
		 	if (usageNotificationData[i].emailTemplateData != null) {
		 		$("#emailTemplate").val(usageNotificationData[i].emailTemplateData.id);
		 	}
		 	if (usageNotificationData[i].smsTemplateData != null) {
		 		$("#smsTemplate").val(usageNotificationData[i].smsTemplateData.id);
		 	}
		 	
		 	$("#threshold").val(usageNotificationData[i].threshold);
	    	$("#usageNotificationDialog").modal('show');
	 		action = "update";
	     }
	}
	
}
function submitForm(){
	if($("#emailTemplate").val() == null && $("#smsTemplate").val() == null){
		setError('emailTemplate', "Email Template Or Sms Template is required");
		setError('smsTemplate', "Email Template Or Sms Template is required");
		return false;
	}
	if(action == 'update'){
		if('<%=PkgType.PROMOTIONAL.name()%>'=='<s:property value="pkgData.type"/>'){
             document.usageNotificationForm.action = "${pageContext.request.contextPath}/promotional/policydesigner/notification/UsageNotification/PromotionalUsageNotification/update";
		}else{
			document.usageNotificationForm.action = "${pageContext.request.contextPath}/policydesigner/notification/UsageNotification/update";
		}
	}else if(isNullOrEmpty(action) || action == 'create'){
		if('<%=PkgType.PROMOTIONAL.name()%>'=='<s:property value="pkgData.type"/>'){
			document.usageNotificationForm.action = "${pageContext.request.contextPath}/promotional/policydesigner/notification/UsageNotification/PromotionalUsageNotification/create";
		}else{
			document.usageNotificationForm.action = "${pageContext.request.contextPath}/policydesigner/notification/UsageNotification/create";
		}

	}
	$("#usageNotificationForm").submit();
} 

function clearDialog() {
	$("#threshold").val("");
}

</script>
<div class="modal" id="usageNotificationDialog" tabindex="-1" role="dialog" aria-labelledby="usageNotificationDialog1" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close" onclick="clearDialog()">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title set-title">
					<s:text name="usagenotification" />
				</h4>
			</div>
			<s:form id="usageNotificationForm" namespace="/" action="policydesigner/notification/UsageNotification/create" validate="true" method="post" cssClass="form-horizontal"  >
			<s:token />
				<s:hidden name="groupIds" value="%{pkgData.groups}" />
				<s:hidden name="entityOldGroups" value="%{pkgData.groups}" />
				<div class="modal-body">
				<div class="row">
					<s:hidden name="pkgId" value="%{pkgData.id}" />
					<s:hidden name="usageNotificationData.id" id="usageNotificationId" />
					<div class="col-xs-12">
						<s:select 		name="usageNotificationData.quotaProfile.id"  key="usagenotification.quotaprofile"  list="pkgData.quotaProfiles" listValue="name" listKey="id"  id="quotaProfile" cssClass="form-control focusElement" />
						<s:select 		name="usageNotificationData.dataServiceTypeData.id"		key="usagenotification.servicetype"  list="dataServiceTypeData" listValue="name" listKey="id"  id="dataServiceTypes" cssClass="form-control" />
						<s:select   	name="usageNotificationData.aggregationKey"  key="usagenotification.aggregationkey" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.AggregationKey@values()" id="aggregationkey"  listValue="getVal()" />
						<s:select   	name="usageNotificationData.meteringType" 	 key="usagenotification.meteringtype" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.MeteringType@values()" id="meteringType" listValue="getVal()" />
						<s:select 		name="usageNotificationData.emailTemplateData.id" key="usagenotification.emailtemplate"  list="emailTemplateDatas" listValue="name" listKey="id"  id="emailTemplate" cssClass="form-control" headerValue="-- Select --" headerKey=""/>
						<s:select 		name="usageNotificationData.smsTemplateData.id" 	 key="usagenotification.smstemplate"  list="smsTemplateDatas" listValue="name" listKey="id"  id="smsTemplate" cssClass="form-control" headerValue="-- Select --" headerKey="" />
						<s:textfield 	name="usageNotificationData.threshold" 		 key="usagenotification.threshold" 	id="threshold" cssClass="form-control" />
					</div>
				</div>
			</div>
			<div class="modal-footer" style="text-align: center;">
				<button class="btn btn-sm btn-primary" type="button" onclick="submitForm()"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
				<button type="button" class="btn btn-sm btn-primary" data-dismiss="modal" onclick="clearDialog()"><s:text name="button.cancel"></s:text> </button>
			</div>
			</s:form>
			</div>
	</div>
</div>