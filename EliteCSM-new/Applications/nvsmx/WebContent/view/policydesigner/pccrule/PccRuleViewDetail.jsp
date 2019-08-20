
<%@page import="com.google.gson.JsonArray"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<%
JsonArray serviceDataFlow = (JsonArray)request.getAttribute("serviceDataFlow");
%>
<style type="text/css">
.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}
</style> 
<script type="text/javascript">
function submitPage() {
	if("${pccRule.qosProfileDetails[0].qosProfile.pkgData.type}" == 'EMERGENCY'){
		window.location = "${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/view?qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}";
	}else{
		window.location = "${pageContext.request.contextPath}/policydesigner/qos/QosProfile/view?qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}";
	}
}
</script>

<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title" style="display: inline;">
			<s:property value="pccRule.name"/>
		</h3>
		<div class="nv-btn-group" align="right" style="margin-top: -17px;margin-bottom: 0px;">
        <s:if test="%{pccRule.scope == @com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL}">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
						onclick="javascript:location.href='${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/export?ids=${pccRule.id}&${pccRule.id}=${pccRule.groups}&pccRuleId=${pccRule.id}'">
					<span class="glyphicon glyphicon-export" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn"  data-placement="bottom" data-toggle="modal" data-target="#PCCRuleAssociation" title="View Associations">
					<span class="glyphicon glyphicon-th" title="Add"/>
				</button>
			</span>
         </s:if>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${pccRule.id}&auditableId=${pccRule.auditableId}&auditPageHeadingName=${pccRule.name}&refererUrl=/policydesigner/pccrule/PccRule/view?pccRuleId=${pccRule.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>

			<s:if test="%{pccRule.scope == @com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL}">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
						onclick="javascript:location.href='${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/init?pccRuleId=${pccRule.id}&scope=${pccRule.scope}&groupIds=${pccRule.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/delete?ids=${pccRule.id}&${pccRule.id}=${pccRule.groups}&groupIds=${pccRule.groups}&scope=${pccRule.scope}&pccRuleId=${pccRule.id}">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
			</s:if>
			<s:elseif test="%{pccRule.qosProfileDetails[0].qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
				<s:if test="%{pccRule.qosProfileDetails[0].qosProfile.pkgData.type != @com.elitecore.corenetvertex.pkg.PkgType@EMERGENCY.name()}">
					<span class="btn-group btn-group-xs">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
								onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/init?pccRuleId=${pccRule.id}&qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}&requestFromQosProfileView=${request.requestFromQosView}&groupIds=${pccRule.groups}'">
							<span class="glyphicon glyphicon-pencil"></span>
						</button>
					</span>
					<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/delete?pccRuleId=${pccRule.id}&qosProfileDetailId=${pccRule.qosProfileDetails[0].id}&qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}&groupIds=${pccRule.groups}">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
							<span class="glyphicon glyphicon-trash"></span>
						</button>
					</span>
				</s:if>
				<s:elseif test="%{ @com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() == pccRule.qosProfileDetails[0].qosProfile.pkgData.type }">
					<span class="btn-group btn-group-xs">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
								onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/pccrule/PccRule/PromotionalPCCRule/init?pccRuleId=${pccRule.id}&qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}&requestFromQosProfileView=${request.requestFromQosView}&groupIds=${pccRule.groups}'">
							<span class="glyphicon glyphicon-pencil"></span>
						</button>
					</span>
					<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/promotional/policydesigner/pccrule/PccRule/PROMOTIONALPCCRule/delete?pccRuleId=${pccRule.id}&qosProfileDetailId=${pccRule.qosProfileDetails[0].id}&qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}&groupIds=${pccRule.groups}">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
							<span class="glyphicon glyphicon-trash"></span>
						</button>
					</span>
				</s:elseif>
				<s:else>
					<span class="btn-group btn-group-xs">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
								onclick="javascript:location.href='${pageContext.request.contextPath}/emergencypccrule/policydesigner/pccrule/EmergencyPccRule/init?pccRuleId=${pccRule.id}&qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}&requestFromQosProfileView=${request.requestFromQosView}&groupIds=${pccRule.groups}'">
							<span class="glyphicon glyphicon-pencil"></span>
						</button>
					</span>
					<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/emergencypccrule/policydesigner/pccrule/EmergencyPccRule/delete?pccRuleId=${pccRule.id}&qosProfileDetailId=${pccRule.qosProfileDetails[0].id}&qosProfileId=${pccRule.qosProfileDetails[0].qosProfile.id}&groupIds=${pccRule.groups}">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
							<span class="glyphicon glyphicon-trash"></span>
						</button>
					</span>
				</s:else>

			</s:elseif>
			<s:else>
			<span class="btn-group btn-group-xs">
				<button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span> 
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton"  >
				<button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
			</s:else>
		</div>
	</div>
	<s:if test="%{pccRule.scope == @com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL}">
		<%@include file="PCCRuleAssociation.jsp"%>
	</s:if>
	<div class="panel-body" >
		<div class="row">	
		<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="basic.detail" /> </legend>
				<div class="row">
					<div class="col-sm-4">
						<div class="row">
							<s:label key="pccrule.monitoringkey" value="%{pccRule.monitoringKey}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7 word-break"  />
							<s:label key="pccrule.precedence" value="%{pccRule.precedence}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"  />
							<s:label key="pccrule.type" value="%{pccRule.type}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"  />
							<s:label key="pccrule.groups" value="%{pccRule.groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"  />
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
							<s:label key="pccrule.chargingmode" value="%{@com.elitecore.corenetvertex.core.constant.ChargingModes@fromValue(pccRule.chargingMode.intValue()).getDisplayName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5"  />
							<s:label key="pccrule.sponsoridentity" value="%{pccRule.sponsorIdentity}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5 word-break"  />
							<s:label key="pccrule.appserviceproviderid" value="%{pccRule.appServiceProviderId}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-sm-5 col-xs-8 word-break"  />
						</div>
					</div>
					
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
							<s:if test="%{pccRule.createdByStaff !=null}">
								<s:hrefLabel key="createdby" value="%{pccRule.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{pccRule.createdByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="createdby" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{pccRule.createdDate != null}">
								<s:set var="createdByDate">
									<s:date name="%{pccRule.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="createdon" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:if>
							<s:else>
								<s:label key="createdon" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 col-sm-5" elementCssClass="col-xs-7 col-sm-7"  />
							</s:else>
							
							<s:if test="%{pccRule.modifiedByStaff !=null}">
								<s:hrefLabel key="modifiedby" value="%{pccRule.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
											 url="/sm/staff/staff/%{pccRule.modifiedByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="modifiedby" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{pccRule.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{pccRule.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="lastmodifiedon" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="lastmodifiedon" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							
							
						</div>
					</div>
				</div>
       </fieldset>
       <fieldset class="fieldSet-line">
				<legend align="top"><s:text name="pccrule.serviceinformation" /> </legend>
				<div class="row" style="margin-bottom: 10px">
					<div class="col-sm-4">
						<div class="row">
							<s:hrefLabel key="pccrule.servicetype" id="dataServiceType" value="%{pccRule.dataServiceTypeData.name} (%{pccRule.dataServiceTypeData.serviceIdentifier})" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"
								url="/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=%{pccRule.dataServiceTypeData.id}"/>
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
							<s:hrefLabel key="pccrule.chargingkey" value="%{#request.chargingKey}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7 word-break"
							 url="/policydesigner/ratinggroup/RatingGroup/view?ratingGroupId=%{selectedChargingKeyId}"/>
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
							<s:label key="pccrule.flowstatus" value="%{@com.elitecore.corenetvertex.pm.constants.FlowStatus@fromValue(pccRule.flowStatus).name()}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
						</div>
					</div>
				</div>
				<div class="col-sm-12">
					<nv:dataTable 							
						id="serviceDataFlow"
						list="<%=serviceDataFlow.toString() %>"																							
						width="100%" 
						showPagination="false"
						showInfo="false"		
						cssClass="table table-blue">	
						<nv:dataTableColumn title="Flow Access" beanProperty="Flow Access" tdCssClass="text-left text-middle" tdStyle="width:15%"/>
						<nv:dataTableColumn title="Protocol" beanProperty="Protocol" tdCssClass="text-left text-middle" tdStyle="width:15%"/>
						<nv:dataTableColumn title="Source IP" beanProperty="Source IP" tdCssClass="text-left text-middle" tdStyle="width:20%"/>
						<nv:dataTableColumn title="Source Port" beanProperty="Source Port" tdCssClass="text-left text-middle" tdStyle="width:15%"/>
						<nv:dataTableColumn title="Destination IP" beanProperty="Destination IP" tdCssClass="text-left text-middle" tdStyle="width:20%"/>
						<nv:dataTableColumn title="Destination Port" beanProperty="Destination Port" tdCssClass="text-left text-middle" tdStyle="width:15%"/>
					</nv:dataTable>
				</div>
       </fieldset>
       <fieldset class="fieldSet-line">
				<legend align="top"><s:text name="pccrule.qosparameters" /> </legend>
				<s:set var="uparrow">
					<span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow" />
				</s:set>
				<div class="row">
					<div class="col-sm-4 col-xs-12">
						<div class="row">
							<s:label key="pccrule.qci" value="%{@com.elitecore.corenetvertex.constants.QCI@fromId(pccRule.qci).getDisplayValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
							<s:label key="pccrule.proritylevel" value="%{pccRule.arp}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
						</div>																																																																																																							
					</div>
					<div class="col-sm-4 col-xs-12 leftVerticalLine">
						<div class="row">
							<s:if test="pccRule.gbrdl != null">
								<div class="col-sm-12 row">
									<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px;"><s:text name="pccrule.gbr" /> </div>
									<div class="col-xs-8 col-sm-7" >
										<s:property value="pccRule.gbrdl"/> <s:property value="pccRule.gbrdlUnit"/> <span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
									</div>
								</div>
							</s:if>
							<div class="col-sm-12 row">	
								<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px">
									<s:if test="pccRule.gbrdl == null">
										<s:text name="pccrule.gbr" />
									</s:if>
								</div>
								<div class="col-xs-8 col-sm-7" style="margin-bottom: 5px">
								<s:if test="pccRule.gbrul != null">
									<s:property value="pccRule.gbrul"/> <s:property value="pccRule.gbrulUnit"/> <span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
								</s:if>
								</div>
							</div>
							<s:if test="pccRule.mbrdl != null">
								<div class="col-sm-12 row">
								<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px"><s:text name="pccrule.mbr" /> </div>
								<div class="col-xs-8 col-sm-7">
									<s:property value="pccRule.mbrdl"/> <s:property value="pccRule.mbrdlUnit"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
								</div>
								</div>
							</s:if>
							<div class="col-sm-12 row">	
								<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px">
									<s:if test="pccRule.mbrdl == null">
										<s:text name="pccrule.mbr" />
									</s:if>
								</div>
								<div class="col-xs-8 col-sm-7" style="margin-bottom: 5px">
									<s:if test="pccRule.mbrul != null">
										<s:property value="pccRule.mbrul"/> <s:property value="pccRule.mbrulUnit"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
									</s:if>
								</div>
							</div>
						</div>																																																																																																						
					</div>
					<div class="col-sm-4 col-xs-12 leftVerticalLine">
						<div class="row">
							<s:label key="pccrule.precapability" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(pccRule.preCapability).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5"  />
							<s:label key="pccrule.prevulnerability" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(pccRule.preVulnerability).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5"  />
						</div>
					</div>
				</div>
       </fieldset>
       	<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="pccrule.usagemonitoringinformation" /> </legend>
				<div class="row">
					<div class="col-sm-4 col-xs-12">
						<div class="row">
							<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px"><s:text name="pccrule.usagemonitoring" /> </div>
							<div class="col-xs-8 col-sm-7" >
								<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(pccRule.usageMonitoring).getStringName()}"/>
							</div>
						</div>
					</div>
					<s:if test="pccRule.usageMonitoring == false">
						<div class="col-sm-4 col-xs-12 leftVerticalLine" style="color:grey">
					</s:if>
					<s:else>
						<div class="col-sm-4 col-xs-12 leftVerticalLine">
					</s:else>
						<div class="row">
							<div class="col-xs-4 col-sm-5" style="margin-bottom: 5px"><s:label value="Slice Quota" /></div>
							<div class="col-xs-8 col-sm-7">
								<s:if test="pccRule.sliceTotal != null">
									<s:property value="pccRule.sliceTotal"/> <s:property value="pccRule.sliceTotalUnit"/>
								</s:if>
							</div>
							<div class="col-xs-4 col-sm-5" ></div>
							<div class="col-xs-8 col-sm-7" >
								<s:if test="pccRule.sliceDownload != null">
									<s:property value="pccRule.sliceDownload" /> <s:property value="pccRule.sliceDownloadUnit" /> <span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
								</s:if>
							</div>
							<div class="col-xs-4 col-sm-5"></div>
							<div class="col-xs-8 col-sm-7">
								<s:if test="pccRule.sliceUpload != null">
									<s:property value="pccRule.sliceUpload"/> <s:property value="pccRule.sliceUploadUnit"/> <span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
								</s:if>
							</div>
						</div>
					</div>
					<s:if test="pccRule.usageMonitoring == false">
						<div class="col-sm-4 col-xs-12 leftVerticalLine" style="color:grey">
					</s:if>
					<s:else>
						<div class="col-sm-4 col-xs-12 leftVerticalLine">
					</s:else>
						<div class="row">
							<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px"> <s:text name="Slice Time" /> </div>
							<div class="col-xs-8 col-sm-7">
								<s:if test="pccRule.sliceTime != null">
									<span><s:property value="pccRule.sliceTime"/></span>
									<span><s:property value="pccRule.sliceTimeUnit"/></span>
								</s:if>
							</div>
						</div>
					</div>
				</div> 
		</fieldset>
		<div class="col-xs-12" align="center">
			<s:if test="%{pccRule.scope == @com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@LOCAL}">
			<button class="btn btn-primary btn-sm"  onclick="submitPage()">
			<span class="glyphicon glyphicon-backward" title="Back"></span>
			 <s:text name="button.qosprofile"/>
			 </button>
			 <s:if test="%{#session.pkgType == @com.elitecore.corenetvertex.pkg.PkgType@EMERGENCY.name()}">
			 	<button id="btnCancel" tabindex="39" type="button" class="btn btn-primary btn-sm" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId=${pccRule.qosProfileDetails[0].qosProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"/></button>
			 </s:if>
			 <s:else>
			 	<button id="btnCancel" tabindex="39" type="button" class="btn btn-primary btn-sm" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pccRule.qosProfileDetails[0].qosProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"/></button>
			 </s:else>
			</s:if>
		</div>
</div>
</div>
</div>
