<%@page import="com.google.gson.JsonArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<%
JsonArray timePeriod = (JsonArray)request.getAttribute("timePeriod");
%>
<style type="text/css">
.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}

</style> 
<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title" style="display: inline;">
			<s:property value="qosProfile.name"/>
		</h3>
		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${qosProfile.id}&auditableId=${qosProfile.auditableId}&auditPageHeadingName=${qosProfile.name}&refererUrl=/policydesigner/qos/QosProfile/view?qosProfileId=${qosProfile.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
				<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != qosProfile.pkgData.type }">

			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
						onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/init?qosProfileId=${qosProfile.id}&fromViewPage=true&groupIds=${qosProfile.pkgData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/qos/QosProfile/delete?qosProfileId=${qosProfile.id}&fromViewPage=true&groupIds=${qosProfile.groups}">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
				</s:if>
				<s:else>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
						onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/init?qosProfileId=${qosProfile.id}&fromViewPage=true&groupIds=${qosProfile.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/delete?qosProfileId=${qosProfile.id}&fromViewPage=true&groupIds=${qosProfile.groups}">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
				</s:else>
			</s:if>
			<s:else>
			<span class="btn-group btn-group-xs">
				<button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit" >
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
	<div class="panel-body" >
		<div class="row">
			<s:hidden name="groupIds" value="%{qosProfile.pkgData.groups}" />
		<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="basic.detail" /> </legend>
				<div class="row">
					<div class="col-sm-8">
						<div class="row">
							<s:label key="getText('qosprofile.description')" value="%{qosProfile.description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
						</div>
						<div class="row">
							<s:label key="getText('qosprofile.groups')" value="%{qosProfile.groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
					    	<s:if test="%{qosProfile.createdByStaff !=null}">
								<s:hrefLabel key="getText('createdby')" value="%{qosProfile.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{qosProfile.createdByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{qosProfile.createdDate != null}">
								<s:set var="createdByDate">
									<s:date name="%{qosProfile.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7">
								</s:label>
							</s:if>
							<s:else>
								<s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							
							<s:if test="%{qosProfile.modifiedByStaff !=null}">
								<s:hrefLabel key="getText('modifiedby')" value="%{qosProfile.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{qosProfile.modifiedByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="getText('modifiedby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{qosProfile.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{qosProfile.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							
						</div>
					</div>
				</div>
       </fieldset>
       <fieldset class="fieldSet-line">
				<legend align="top"><s:text name="qosprofile.authorization.parameters" /> </legend>
				<div class="row">
					<s:label key="getText('qosprofile.advancecondition')" value="%{qosProfile.advancedCondition}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-2" elementCssClass="col-xs-8 col-sm-10 word-break"/>
					<s:label key="getText('qosprofile.accessnetwork')" value="%{qosProfile.accessNetwork}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-2" elementCssClass="col-xs-8 col-sm-10" />
					<div class="control-label light-text" >
						<div class="col-xs-4 col-sm-2" style="font-weight:700;margin-bottom: 5px">
							<s:text name="qosprofile.attached.quotaprofile"/>
						</div>
						<div class="col-xs-8 col-sm-10 word-break">
							<s:if test="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@USAGE_METERING_BASED == qosProfile.pkgData.quotaProfileType}">
								<a class="defaultLabel" href="${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/view?quotaProfileId=${qosProfile.quotaProfile.id}">
									<s:property value="%{qosProfile.quotaProfile.name}"/>
								</a>
							</s:if>
							<s:elseif test="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@SY_COUNTER_BASED == qosProfile.pkgData.quotaProfileType}">
								<s:property value="%{qosProfile.syQuotaProfileData.name}"/>
							</s:elseif>
                            <s:elseif test="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@RnC_BASED == qosProfile.pkgData.quotaProfileType}">
								<a class="defaultLabel" href="${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/view?quotaProfileId=${qosProfile.rncProfileData.id}">
                                	<s:property value="%{qosProfile.rncProfileData.name}"/>
								</a>
                            </s:elseif>
						</div>
					</div>
					<s:if test="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@RnC_BASED == qosProfile.pkgData.quotaProfileType}">
						<s:hrefLabel key="getText('qosprofile.ratecard')" value="%{qosProfile.rateCardData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-2" elementCssClass="col-xs-8 col-sm-10"
							url="/pd/dataratecard/data-rate-card/%{qosProfile.rateCardData.id}"/>
					</s:if>
				</div>

       </fieldset>
       <fieldset class="fieldSet-line">
				<legend align="top"><s:text name="qosprofile.timebasedconditions"/></legend>
				<div class="row">
					<s:label key="getText('qosprofile.duration')" value="%{qosProfile.duration}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-2" elementCssClass="col-xs-8 col-sm-10"/>
					<div class="col-sm-12">
						<nv:dataTable 							
							id="timeBaseCondition"
							list="<%=timePeriod.toString()%>"																							
							width="100%" 
							showPagination="false"
							showInfo="false"		
							cssClass="table table-blue">	
							<nv:dataTableColumn title="Month Of Year" beanProperty="Month Of Year" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
							<nv:dataTableColumn title="Day Of Month" beanProperty="Day Of Month" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
							<nv:dataTableColumn title="Day Of Week" beanProperty="Day Of Week" tdCssClass="text-left text-middle" tdStyle="width:20%"/>
							<nv:dataTableColumn title="Time Period" beanProperty="Time Period" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
						</nv:dataTable>
					</div>
				</div>
       </fieldset>
			<%--this included file contain Fup level view detail--%>
			<%@include file="QosProfileFupLevelViewDetail.jsp"%>

			<s:if test="%{qosProfile.qosProfileDetailDataList.size() <= 2}">
				<s:set var="qosFupLevelValue"><s:property value="qosProfile.qosProfileDetailDataList.size"/></s:set>
				<div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" id="fupbutton">
					<s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
						<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != qosProfile.pkgData.type}">
							<s:if test="%{qosProfile.rateCardData == null }" >
								<button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/initCreateHSQDetail?qosProfileId=${qosProfile.id}&fuplevel=${qosFupLevelValue}&groupIds=${qosProfile.pkgData.groups}'">
									<span class="glyphicon glyphicon-plus-sign" style="top: 0px"></span>
									<s:if test="%{#qosFupLevelValue == 0}">
										<s:text name="qosprofile.hs"/>
									</s:if>
									<s:else>
										<s:text name="qosprofile.fup"/>-<s:property value="qosFupLevelValue"/>&nbsp;<s:text name="qosprofile" />
									</s:else>
								</button>
							</s:if>
							<s:else>
								<s:if test="%{#qosFupLevelValue == 0}">
									<button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/initCreateHSQDetail?qosProfileId=${qosProfile.id}&fuplevel=${qosFupLevelValue}&groupIds=${qosProfile.pkgData.groups}'">
										<span class="glyphicon glyphicon-plus-sign" style="top: 0px"></span>
											<s:text name="qosprofile.hs"/>
									</button>
								</s:if>
							</s:else>
						</s:if>
						<s:else>
							<button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/initCreateHSQDetail?qosProfileId=${qosProfile.id}&fuplevel=${qosFupLevelValue}&groupIds=${qosProfile.pkgData.groups}'">
								<span class="glyphicon glyphicon-plus-sign" style="top: 0px"></span>
								<s:if test="%{#qosFupLevelValue == 0}">
									<s:text name="qosprofile.hs"/>
								</s:if>
								<s:else>
									<s:text name="qosprofile.fup"/>-<s:property value="qosFupLevelValue"/>&nbsp;<s:text name="qosprofile" />
								</s:else>
							</button>
						</s:else>
					</s:if>
					<s:else>
						<button type="button" disabled="disabled" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" >
							<span class="glyphicon glyphicon-plus-sign" style="top: 0px"></span>
							<s:text name="qosprofile.hs"/> <s:text name="qosprofile" />
						</button>
					</s:else>
				</div>
			</s:if>
			<div class="row">
				<div class="col-xs-12" align="center">
					<button id="btnBack" class="btn btn-primary btn-sm"  value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${qosProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
				</div>
			</div>
		</div>
	</div>
</div>
