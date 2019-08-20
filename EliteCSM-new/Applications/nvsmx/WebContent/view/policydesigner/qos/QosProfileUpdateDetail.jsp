<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
@media (max-width: 767px) {
	.qos-detail-label{
		width: 22%;
	}
	.qos-detail-element{
		width: 78%;
	}
	
}
@media (min-width: 768px) {
	.qos-detail-label1{
		width: 27.631%
	}
	.qos-detail-element1{
		width: 72.369%
	}
}
</style>
<script src="${pageContext.request.contextPath}/js/UsageValidation.js"></script>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:if test="%{qosProfileDetail.fupLevel == 0}">
				<s:text name="qosprofile.update.hsqlevel"></s:text>
				<s:set var="fuplevelheader">HSQ</s:set>
			</s:if>
			<s:elseif test="%{qosProfileDetail.fupLevel == 1}">
				<s:text name="qosprofile.update.fuplevel1"></s:text>
				<s:set var="fuplevelheader">FUP1</s:set>
			</s:elseif>
			<s:elseif test="%{qosProfileDetail.fupLevel == 2}">
				<s:text name="qosprofile.update.fuplevel2"></s:text>
				<s:set var="fuplevelheader">FUP2</s:set>
			</s:elseif>
		</h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" labelCssClass="col-xs-4 col-sm-5 text-right" elementCssClass="col-xs-8 col-sm-7"
			action="policydesigner/qos/QosProfile/updateQosDetailInformation"
			validate="true" id="qosProfileCreate" method="post"
			cssClass="form-horizontal" onsubmit="return validateForm()">
			<s:hidden name="qosProfileDetail.fupLevel" id="fupLevel" />
			<s:hidden name="groupIds" value="%{qosProfileDetail.qosProfile.pkgData.groups}" />
			<s:hidden name="qosProfileId" value="%{qosProfileDetail.qosProfile.id}" />
			<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{qosProfileDetail.qosProfile.pkgData.groups}"/>

			<div class="row">
  				<div class="col-sm-6">
  					<s:select cssClass="form-control focusElement" tabindex="1" 
  						labelCssClass="qos-detail-label1 qos-detail-label col-xs-3 col-sm-5 text-right" 
  						elementCssClass="qos-detail-element1 col-xs-9 col-sm-7 qos-detail-element" 
						list="@com.elitecore.corenetvertex.constants.QoSProfileAction@values()"
						name="qosProfileDetail.action" id="action" key="qosprofile.action" onchange="disableRestAttributes()"
						listKey="getId()" listValue="getName()" />
  				</div>
  				<div class="col-sm-6">
  					<s:textfield name="qosProfileDetail.rejectCause" id="rejectCause"  tabindex="2"
						labelCssClass="qos-detail-label col-xs-3 col-sm-5 text-right" elementCssClass="col-xs-9 col-sm-7 qos-detail-element"
						key="qosprofile.rejectcause" cssClass="form-control"
						maxlength="255"  />
  				</div>
  			</div>
  			<div class="row">
				<div class="col-sm-6">
					<div class="row">
						<div class="col-xs-8">
							<s:textfield name="qosProfileDetail.mbrdl" tabindex="3"
								key="qosprofile.detail.mbrdl" cssClass="form-control"
								onkeypress="return isNaturalInteger(event);" maxlength="10"
								id="mbrdl" readonly="false" type="number" />
							<s:textfield name="qosProfileDetail.mbrul" tabindex="5"
								key="qosprofile.detail.mbrul" cssClass="form-control"
								onkeypress="return isNaturalInteger(event);" maxlength="10"
								id="mbrul" type="number" />
							<s:textfield name="qosProfileDetail.aambrdl" tabindex="7"
								key="qosprofile.detail.aambrdl" cssClass="form-control"
								onkeypress="return isNaturalInteger(event);" maxlength="10"
								id="aambrdl" type="number" />
							<s:textfield name="qosProfileDetail.aambrul" tabindex="9"
								key="qosprofile.detail.aambrul" cssClass="form-control"
								onkeypress="return isNaturalInteger(event);" maxlength="10"
								id="aambrul" type="number" />
						</div>
						<div class="col-xs-4">
							<s:select cssClass="form-control" elementCssClass="col-xs-12" tabindex="4"
								list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
								name="qosProfileDetail.mbrdlUnit" id="mbrdlUnit" />
							<s:select cssClass="form-control" elementCssClass="col-xs-12" tabindex="6"
								list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
								name="qosProfileDetail.mbrulUnit" id="mbrulUnit"/>
							<s:select cssClass="form-control" elementCssClass="col-xs-12" tabindex="8"
								list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
								name="qosProfileDetail.aambrdlUnit" id="aambrdlUnit" />
							<s:select cssClass="form-control" elementCssClass="col-xs-12" tabindex="10"
								list="@com.elitecore.corenetvertex.constants.QoSUnit@values()"
								name="qosProfileDetail.aambrulUnit" id="aambrulUnit" />
						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<s:select cssClass="form-control" tabindex="11"
						list="@com.elitecore.corenetvertex.constants.QCI@values()"
						labelCssClass="qos-detail-label col-xs-3 col-sm-5 text-right" elementCssClass="col-xs-9 col-sm-7 qos-detail-element"
						listKey="getQci()" listValue="getDisplayValue()"
						name="qosProfileDetail.qci" key="qosprofile.detail.qci" value="qosProfileDetail.qci" />
					<s:select cssClass="form-control" tabindex="12"
						list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
						labelCssClass="qos-detail-label col-xs-3 col-sm-5 text-right" elementCssClass="col-xs-9 col-sm-7 qos-detail-element"
						listKey="isBooleanValue()" listValue="getStringName()"
						name="qosProfileDetail.preCapability"
						key="qosprofile.detail.precapability" />
					<s:select cssClass="form-control" tabindex="13"
						list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
						labelCssClass="qos-detail-label col-xs-3 col-sm-5 text-right" elementCssClass="col-xs-9 col-sm-7 qos-detail-element"
						listKey="isBooleanValue()" listValue="getStringName()"
						name="qosProfileDetail.preVulnerability"
						key="qosprofile.detail.prevulnerability" />
					<s:select cssClass="form-control" tabindex="14"
						list="#{'1':'1', '2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','10':'10','11':'11','12':'12','13':'13','14':'14','15':'15'}"
						labelCssClass="qos-detail-label col-xs-3 col-sm-5 text-right" elementCssClass="col-xs-9 col-sm-7 qos-detail-element"
						name="qosProfileDetail.priorityLevel"
						key="qosprofile.detail.prioritylevel" />
					<s:textfield  id="redirectUrl"	name="qosProfileDetail.redirectUrl" 	key="qosprofile.redirecturl" 	cssClass="form-control" abelCssClass="qos-detail-label col-xs-3 col-sm-5 text-right" elementCssClass="col-xs-9 col-sm-7 qos-detail-element" />
				</div>
			</div>
	<div class="row">
				<fieldset class="fieldSet-line" id="authorizationParameterSet">
					<legend><s:text name="qosProfile.detail.usagemonitoringinformation" /> </legend>
				<div class="row">
				     <div class="col-sm-6">
				     	<div class="row">
				     	<div class="col-xs-8">
							<s:select cssClass="form-control" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" tabindex="15"
								listKey="isBooleanValue()" listValue="getStringName()"
								name="qosProfileDetail.usageMonitoring" key="qosProfile.detail.usagemonitoring" id="usageMonitoring" onchange="disableSliceInformation()" />
						</div>
						<div class="col-xs-8" id="sliceInformation">
							<s:textfield name="qosProfileDetail.sliceTotal" tabindex="16" type="number" id ="slicetotal" 
								key="qosProfile.detail.slicetotal" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" />
							<s:textfield name="qosProfileDetail.sliceDownload" tabindex="18" type="number" id ="slicedownload"
								key="qosProfile.detail.slicedownload" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" />
							<s:textfield name="qosProfileDetail.sliceUpload" tabindex="20" type="number" id ="sliceupload"
								key="qosProfile.detail.sliceupload" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" />
							<s:textfield name="qosProfileDetail.sliceTime" tabindex="22" type="number" id ="slicetime"
								key="qosProfile.detail.slicetime" cssClass="form-control" onkeypress="return isNaturalInteger(event);"
								maxlength="18" />
						</div>
						<div class="col-xs-4" id="sliceInformation">
							<s:select cssClass="form-control" name="qosProfileDetail.sliceTotalUnit" id="sliceTotalUnit" tabindex="17" elementCssClass="col-xs-12"
							list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
							
							<s:select cssClass="form-control" name="qosProfileDetail.sliceDownloadUnit" id="sliceDownloadUnit" tabindex="19" elementCssClass="col-xs-12"   
							list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
							
							<s:select cssClass="form-control" name="qosProfileDetail.sliceUploadUnit" id="sliceUploadUnit" tabindex="21" elementCssClass="col-xs-12"
							list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
							
							<s:select cssClass="form-control" name="qosProfileDetail.sliceTimeUnit" id="sliceTimeUnit" tabindex="23" elementCssClass="col-xs-12"
							list="#{@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR, @com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE ,  @com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND}" 
							/>
							
						</div>
						</div>
					</div>
			</fieldset>
			</div>
			<div class="row">
				<div class="col-xs-12" align="center">
					<s:hidden name="qosProfileDetailId" value="%{qosProfileDetail.id}" />
					<s:submit cssClass="btn btn-primary btn-sm" type="button" role="button" tabindex="24"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>	
					<button tabindex="25" id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/view?qosProfileId=${qosProfileDetail.qosProfile.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.qosprofile"></s:text> </button>
					<button tabindex="26" id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${qosProfileDetail.qosProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
					
				</div>
		</s:form>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function(){
		disableRestAttributes();
	});
</script>
<%@include file="/view/policydesigner/qos/QosProfileDetailValidation.jsp"%>



