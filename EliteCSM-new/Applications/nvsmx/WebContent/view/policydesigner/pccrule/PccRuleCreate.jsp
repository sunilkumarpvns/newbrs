<%@page import="com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData"%>
<%@page import="com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData"%>
<%@page import="java.util.List"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<style type="text/css">
@media (max-width: 767px) {
	.label-class{
		width: 27.80%;
	}
	.element-class{
		width: 72.20%;
	}
}
</style>
<script src="${pageContext.request.contextPath}/js/UsageValidation.js"></script>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="pccrule.create" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="" validate="true" id="pccRuleCreate" method="post" cssClass="form-horizontal" labelCssClass="col-xs-5 text-right" elementCssClass="col-xs-7" onsubmit="return validateForm()" >
		<s:token />
			<div class="row">
					<div class="col-sm-6">
          		<s:textfield name="pccRule.name" key="pccrule.name" cssClass="form-control focusElement" maxlength="100" id="pccruleName" onkeyup="setMonitoringKey();" tabindex="1" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class"
								 onblur="verifyUniqueness('pccruleName','create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','');
								         verifyUniqueness('monitoringKey','create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','monitoringKey');"/>

								<s:select name="pccRule.serviceTypeId" key="pccrule.servicetype" cssClass="form-control" list="%{serviceTypeDataList}" listKey="id" listValue="%{name}" id="serviceData" onchange="getSDF(this.value)" tabindex="3" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" />
								<s:textfield name="pccRule.precedence" key="pccrule.precednce" cssClass="form-control" maxlength="3" onkeypress="return isNaturalInteger(event);" tabindex="5" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" />
								<s:select name="pccRule.chargingKey" key="pccrule.chargingkey" cssClass="form-control" list="#{'RATING_GROUP_1':'Default_Rating_Group (0)'}"  id="chargingKey" tabindex="7" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class">
									<s:optgroup label="Preferred Charging Keys" list="#{}" />
									<s:optgroup label="Other Charging Keys" list="#{}" />
								</s:select>
								<s:textfield name="pccRule.sponsorIdentity" key="pccrule.sponsoridentity" cssClass="form-control" maxlength="100" tabindex="9" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" />
					</div>
					<div class="col-sm-6">
							   <s:textfield name="pccRule.monitoringKey" key="pccrule.monitoringkey" cssClass="form-control" maxlength="100" id="monitoringKey" tabindex="2" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" onkeyup="isMonitoringKeyChanged();" onfocus="setMonitoringKey();"
							   		onblur="verifyUniqueness('monitoringKey','create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','monitoringKey');
							   		verifyUniqueness('pccruleName','create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','');" />
						     	<s:select name="pccRule.type" key="pccrule.type" cssClass="form-control" list="@com.elitecore.nvsmx.policydesigner.controller.pccrule.PccRuleCTRL$PCCRuleType@values()" listValue="getValue()" listKey="getValue()" id="type" tabindex="4" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" />
						     	<s:select name="pccRule.chargingMode" key="pccrule.chargingmode" cssClass="form-control" list="@com.elitecore.corenetvertex.core.constant.ChargingModes@values()" listKey="getVal()" listValue="getDisplayName()" tabindex="6" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" />
								<s:textfield name="pccRule.appServiceProviderId" key="pccrule.appserviceproviderid" cssClass="form-control" maxlength="100" tabindex="8" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" />
						<s:if test="%{pccRule.scope == @com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL}">
							<s:select 		name="groupIds" 	   		key="pccrule.groups"    cssClass="form-control select2"	list="#session.staffBelongingGroups"  id="groupNames" multiple="true" listKey="id" listValue="name"/>

						</s:if>
						<s:else>
							<s:hidden name="groupIds" value="%{pccRule.qosProfileDetails[0].qosProfile.pkgData.groups}"/>
							<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{pccRule.qosProfileDetails[0].qosProfile.pkgData.groups}"/>
						</s:else>
					</div>

			</div>
			<div class="row">
			<fieldset class="fieldSet-line" id="serviceDataFlowInformationSet">
					<legend>
						<s:text name="pccrule.servicedataflow.information"/>
					</legend>
				<div class="row">
					<div id="serviceDataFlow">
							<div class="col-sm-12">
								<table id='serviceDataFlowTable'  class="table table-blue table-bordered">
									<caption class="caption-header">
										<span class="glyphicon glyphicon-check"></span><s:text name="pccrule.servicedataflow"/>
										<div align="right" class="display-btn" >
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addServiceDataFlow();" tabindex="10"
												id="addRow"> <span class="glyphicon glyphicon-plus" ></span>
											</span>
										</div>
									</caption>
									<thead>
										<th><s:text name="servicedataflow.flowaccess"/></th>
										<th><s:text name="servicedataflow.protocol"/></th>
										<th><s:text name="servicedataflow.sourceip"/></th>
										<th><s:text name="servicedataflow.sourceport"/></th>
										<th><s:text name="servicedataflow.destinationip"/></th>
										<th><s:text name="servicedataflow.destinationport"/></th>
										<th></th>
									</thead>
								</table>
							</div>
							<div class="col-sm-6">
								<s:select  name="pccRule.flowStatus" key="pccrule.flowstatus"  list="@com.elitecore.corenetvertex.pm.constants.FlowStatus@values()" listKey="getVal()" listValue="name()" cssClass="form-control" tabindex="18" labelCssClass="col-xs-5 label-class text-right" elementCssClass="col-xs-7 element-class" value="@com.elitecore.corenetvertex.pm.constants.FlowStatus@ENABLED.getVal()"/>
							</div>
					</div>
				</div>
			</fieldset>
			</div>
			<div class="row">
			<fieldset class="fieldSet-line" id="authorizationParameterSet">
					<legend>
						<s:text name="pccrule.qosparameters"/>
					</legend>
				<div class="row">
				       <div class="col-sm-6">
				       		<div class="row">
								<div class="col-xs-8">
									<s:textfield name="pccRule.gbrdl"
										key="pccrule.gbrdl" cssClass="form-control" tabindex="19" id="gbrdl"
										maxlength="10"  type="number" onkeypress="return isNaturalInteger(event);"/>
									<s:textfield name="pccRule.gbrul" tabindex="21" type="number" id="gbrul"
										key="pccrule.gbrul" cssClass="form-control"
										maxlength="10" onkeypress="return isNaturalInteger(event);" />
									<s:textfield name="pccRule.mbrdl" tabindex="23" type="number" id="mbrdl"
										key="pccrule.mbrdl" cssClass="form-control"
										maxlength="10" onkeypress="return isNaturalInteger(event);" />
									<s:textfield name="pccRule.mbrul" tabindex="25" type="number" id="mbrul"
										key="pccrule.mbrul" cssClass="form-control"
										maxlength="10" onkeypress="return isNaturalInteger(event);" />
								</div>
								<div class="col-xs-4">
									<s:select cssClass="form-control" tabindex="20" elementCssClass="col-xs-12"
										list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
										value="@com.elitecore.corenetvertex.constants.QoSUnit@Kbps.name()"
										name="pccRule.gbrdlUnit" id="gbrdlUnit"/>
									<s:select cssClass="form-control" tabindex="22" elementCssClass="col-xs-12"
										list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
										value="@com.elitecore.corenetvertex.constants.QoSUnit@Kbps.name()"
										name="pccRule.gbrulUnit" id="gbrulUnit"/>
									<s:select cssClass="form-control" tabindex="24" elementCssClass="col-xs-12"
										list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
										value="@com.elitecore.corenetvertex.constants.QoSUnit@Kbps.name()"
										name="pccRule.mbrdlUnit" id="mbrdlUnit" />
									<s:select cssClass="form-control" tabindex="26" elementCssClass="col-xs-12"
										list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
										value="@com.elitecore.corenetvertex.constants.QoSUnit@Kbps.name()"
										name="pccRule.mbrulUnit" id="mbrulUnit"/>
								</div>
							</div>
						</div>
						<div class="col-sm-6">
							<s:select cssClass="form-control" tabindex="27" id="qci"
								labelCssClass="col-xs-4 col-sm-5 text-right label-class" elementCssClass="col-xs-8 col-sm-7 element-class"
								list="@com.elitecore.corenetvertex.constants.QCI@values()" listKey="getQci()" listValue="getDisplayValue()"
								value="@com.elitecore.corenetvertex.constants.QCI@getDefault().getQci()" name="pccRule.qci" key="pccrule.qci" onchange="disableQosInformation()"/>
							<s:select cssClass="form-control" tabindex="28"
								labelCssClass="col-xs-4 col-sm-5 text-right label-class" elementCssClass="col-xs-8 col-sm-7 element-class"
								list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringName()"
								name="pccRule.preCapability"
								key="pccrule.precapability" />
							<s:select cssClass="form-control" tabindex="29"
								labelCssClass="col-xs-4 col-sm-5 text-right label-class" elementCssClass="col-xs-8 col-sm-7 element-class"
								list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringName()"
								name="pccRule.preVulnerability"
								key="pccrule.prevulnerability" />
							<s:select cssClass="form-control"
								list="@com.elitecore.corenetvertex.constants.PriorityLevel@values()"
								name="pccRule.arp" listKey="displayVal" listValue="val"
								key="pccrule.proritylevel" labelCssClass="col-xs-4 col-sm-5 text-right label-class" elementCssClass="col-xs-8 col-sm-7 element-class" />
						</div>
					</div>
			</fieldset>
			</div>
			<div class="row">
				<fieldset class="fieldSet-line" id="authorizationParameterSet">
					<legend><s:text name="pccrule.usagemonitoringinformation" /> </legend>
				<div class="row">
				     <div class="col-sm-6">
				     	<div class="row">
				     	<div class="col-xs-8">
							<s:select cssClass="form-control" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
								listKey="isBooleanValue()" listValue="getStringName()" tabindex="30"
								name="pccRule.usageMonitoring" key="pccrule.usagemonitoring" id="usageMonitoring" onchange="disableSliceInformation()" />
						</div>
						<div class="col-xs-8" id="sliceInformation">
							<s:textfield name="pccRule.sliceTotal" tabindex="31" type="number" id="slicetotal"
								key="pccrule.slicetotal" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" value="25" />
							<s:textfield name="pccRule.sliceDownload" tabindex="33" type="number" id="slicedownload"
								key="pccrule.slicedownload" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" />
							<s:textfield name="pccRule.sliceUpload" tabindex="35" type="number" id="sliceupload"
								key="pccrule.sliceupload" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" />
							<s:textfield name="pccRule.sliceTime" tabindex="37" type="number" id="slicetime"
								key="pccrule.slicetime" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" />
						</div>
						<div class="col-xs-4" id="sliceInformation">
							<s:select cssClass="form-control" name="pccRule.sliceTotalUnit" id="sliceTotalUnit" tabindex="32" elementCssClass="col-xs-12"
							list="@com.elitecore.corenetvertex.constants.DataUnit@values()" value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"/>

							<s:select cssClass="form-control" name="pccRule.sliceDownloadUnit" id="sliceDownloadUnit" tabindex="34" elementCssClass="col-xs-12"
							list="@com.elitecore.corenetvertex.constants.DataUnit@values()" value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" />

							<s:select cssClass="form-control" name="pccRule.sliceUploadUnit" id="sliceUploadUnit" tabindex="36" elementCssClass="col-xs-12"
							list="@com.elitecore.corenetvertex.constants.DataUnit@values()" value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" />

							<s:select cssClass="form-control" name="pccRule.sliceTimeUnit" id="sliceTimeUnit" tabindex="38" elementCssClass="col-xs-12"
							list="#{@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR, @com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE ,  @com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND}"
							value="%{@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE}"
							/>

						</div>
						</div>
					</div>
			</fieldset>
			</div>
			<div class="row">
				<div class="col-xs-12" align="center">
					<s:hidden name="qosProfileDetailId" value="%{pccRule.qosProfileDetails[0].id}"/>
					<s:hidden name="qosProfileId" value="%{pccRule.qosProfileDetails[0].qosProfile.id}"/>
					<s:hidden name="pccRule.scope" value="%{pccRule.scope}"/>
					<button class="btn btn-primary btn-sm" type="submit" role="submit" formaction="create" id="pccRuleCreate_0">
						<span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
					</button>
					<s:if test="%{pccRule.scope == @com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@LOCAL}">
						<s:if test="%{#session.pkgType == @com.elitecore.corenetvertex.pkg.PkgType@EMERGENCY.name()}">
							<button tabindex="40" id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/view?qosProfileId=${request.qosProfileId}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.qosprofile"></s:text> </button>
							<button id="btnCancel" tabindex="41" type="button" class="btn btn-primary btn-sm" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId=${pkgId}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"/></button>
						</s:if>
						<s:else>
							<button tabindex="40" id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/view?qosProfileId=${request.qosProfileId}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.qosprofile"></s:text> </button>
							<button id="btnCancel" tabindex="41" type="button" class="btn btn-primary btn-sm" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pkgId}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"/></button>
						</s:else>
					</s:if>
		   </div>
			</div>
	</div>
	</s:form>
</div>


<script type="text/javascript">
$(document).ready(function(){
	$( ".select2" ).select2();
	var serviceId = $("#serviceData").val();
	getSDF(serviceId);
	disableQosInformation();
});

var i=0;

function addServiceDataFlow(){
	var tableRow ='<tr>'+
	         '<td style="width:18%"><select tabindex="11" name="pccRule.serviceDataFlowList['+i+'].flowAccess"  class="form-control form-control"><option value="permit in">PERMIT IN</option><option value="permit out">PERMIT OUT</option><option value="deny in">DENY IN</option><option value="deny out">DENY OUT</option></select></td>'+
			'<td style="width:12%"><select tabindex="12" name="pccRule.serviceDataFlowList['+i+'].protocol"  class="form-control form-control"><option value="ip">IP</option><option value="6">TCP</option><option value="17">UDP</option></select></td>'+
			'<td><input tabindex="13" class="form-control" name="pccRule.serviceDataFlowList['+i+'].sourceIP"  type="text"/></td>'+
			'<td><input tabindex="14" class="form-control" name="pccRule.serviceDataFlowList['+i+'].sourcePort"  type="text"/></td>'+
			'<td><input tabindex="15" class="form-control" name="pccRule.serviceDataFlowList['+i+'].destinationIP"  type="text"/></td>'+
			'<td><input tabindex="16" class="form-control" name="pccRule.serviceDataFlowList['+i+'].destinationPort"  type="text"/></td>'+
			'<td><span tabindex="17" class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>'+
			'</tr>';
			$(tableRow).appendTo('#serviceDataFlowTable');
	i++;
}






function validateForm(){
	clearErrorMessagesById('pccruleName');
	clearErrorMessagesById('monitoringKey');
	clearErrorMessagesById('gbrul');
	clearErrorMessagesById('mbrul');
	clearErrorMessagesById('gbrdl');
	clearErrorMessagesById('mbrdl');
	clearErrorMessagesById('slicetotal');
	clearErrorMessagesById('slicedownload');
	clearErrorMessagesById('sliceupload');
	clearErrorMessagesById('slicetime');

	var isValidRuleName =  verifyUniquenessOnSubmit('pccruleName','create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','') ;
    var isValidMonitoringKey =  verifyUniquenessOnSubmit('monitoringKey','create','','com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData','','monitoringKey');
    var isValidUsageMonitoring = true;
    var isValidDownloads = true;

    if(isValidRuleName && isValidMonitoringKey ){
       isValidDownloads = verifyDownloads();
       if(isValidDownloads==false){
           return false;
       }

       isValidUsageMonitoring = verifyUsageMonitoring();
       if(isValidUsageMonitoring==false){
           return false;
       }

    }

    var isFormValid = isValidRuleName && isValidMonitoringKey && isValidUsageMonitoring && isValidDownloads;

    return isFormValid;

}
</script>
<%@include file="/view/policydesigner/pccrule/PccRuleValidation.jsp"%>