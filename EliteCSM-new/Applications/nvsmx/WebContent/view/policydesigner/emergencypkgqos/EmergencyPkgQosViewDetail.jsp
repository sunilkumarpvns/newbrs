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
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${qosProfile.id}&auditableId=${qosProfile.auditableId}&auditPageHeadingName=${qosProfile.name}&refererUrl=/policydesigner/emergencypkgqos/EmergencyPkgQos/view?qosProfileId=${qosProfile.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/init?qosProfileId=${qosProfile.id}&fromViewPage=true&groupIds=${qosProfile.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span> 
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/delete?qosProfileId=${qosProfile.id}&fromViewPage=true&groupIds=${qosProfile.groups}">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
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
       
         <s:iterator value="qosProfile.qosProfileDetailDataList">
       <fieldset class="fieldSet-line">
       		<s:if test="fupLevel == 0">
       			<legend align="top"><s:text name="qosprofile.hs" /> <s:text name="qosprofile"/></legend>
       		</s:if>
       		<s:else>
       			<legend align="top"><s:text name="qosprofile.fup" />-<s:property value="fupLevel"/> <s:text name="qosprofile"/></legend>
       		</s:else>
	       		<div align="right" class="nv-btn-group">
	       		<s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
					<div class="btn-group btn-group-xs" role="group">
						<button type="button" class="btn btn-default" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/initUpdateHSQDetail?qosProfileDetailId=${id}&groupIds=${qosProfile.groups}'">
							<span class="glyphicon glyphicon-pencil"></span>
						</button>
					</div>
					<s:if test="fupLevel != 0 && fupLevel == qosProfile.qosProfileDetailDataList.size()-1 ">
						<div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/deleteFupLevels?qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&groupIds=${qosProfile.groups}" >
							<button type="button" class="btn btn-default" >
								<span class="glyphicon glyphicon-trash"></span>
							</button>
						</div>
					</s:if>
				</s:if>
				<s:else>
					<div class="btn-group btn-group-xs" role="group">
						<button type="button" disabled="disabled" class="btn btn-default"  >
							<span class="glyphicon glyphicon-pencil"></span>
						</button>
					</div>
					<s:if test="fupLevel != 0 && fupLevel == qosProfile.qosProfileDetailDataList.size()-1 ">
						<div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton"   >
							<button type="button" class="btn btn-default" disabled="disabled" >
								<span class="glyphicon glyphicon-trash"></span>
							</button>
						</div>
					</s:if>
				</s:else>
				</div>
				<div>
					<div class="row">
						<div class="col-sm-4 col-xs-12">
							<div class="row">
								<s:label key="getText('qosprofile.action')" value="%{@com.elitecore.corenetvertex.constants.QoSProfileAction@fromValue(action).getName()}"  cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
								<s:if test="action != 0">
									<s:label key="getText('qosprofile.rejectcause')" value="%{rejectCause}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
									<s:label key="getText('qosprofile.redirecturl')" value="%{redirectUrl}"  cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8 word-break"  />
								</s:if>	
								<s:if test="action == 0">
									<s:label key="getText('qosprofile.detail.qci')" value="%{@com.elitecore.corenetvertex.constants.QCI@fromId(qci).getDisplayValue()}"  cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
							</div>
						</div>
						<div class="col-sm-4 col-xs-12 leftVerticalLine">
							<div class="row">
								<s:if test="mbrdl != null">
									<div class="col-sm-12 row">
										<div class="col-xs-4 col-sm-4" style="font-weight: 700;margin-bottom: 5px"><s:text name="qosprofile.detail.mbr" /> </div>
										<div class="col-xs-8 col-sm-5">
											<s:property value="mbrdl"/> <s:property value="mbrdlUnit"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
										</div>	
									</div>
								</s:if>
								<div class="col-sm-12 row">
									<div class="col-xs-4 col-sm-4" style="font-weight: 700;margin-bottom: 5px">
										<s:if test="mbrdl == null">
											<s:text name="qosprofile.detail.mbr" />
										</s:if>
									</div>
									<div class="col-xs-8 col-sm-5" style="margin-bottom: 5px">
										<s:if test="mbrul != null">
											<s:property value="mbrul"/> <s:property value="mbrulUnit"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
										</s:if>
									</div>
								</div>
								<s:if test="aambrdl != null">
									<div class="col-sm-12 row">
										<div class="col-xs-4 col-sm-4"style="font-weight: 700;margin-bottom: 5px"><s:text name="qosprofile.detail.aambr" /> </div>
										<div class="col-xs-8 col-sm-8">
												<s:property value="aambrdl"/> <s:property value="aambrdlUnit"/> <span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
											
										</div>
									</div>
								</s:if>
								<div class="col-sm-12 row">
									<div class="col-xs-4 col-sm-4" style="font-weight: 700;margin-bottom: 5px">
										<s:if test="aambrdl == null">
											<s:text name="qosprofile.detail.aambr" />
										</s:if>
									</div>
									<div class="col-xs-8 col-sm-8" style="margin-bottom: 5px">
										<s:if test="aambrul != null">
											<s:property value="aambrul"/> <s:property value="aambrulUnit"/> <span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
										</s:if>
									</div>
								</div>
							</div>
						</div>
						<div class="col-sm-4 col-xs-12 leftVerticalLine">
							<div class="row">
								<s:label key="getText('qosprofile.detail.precapability')" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(preCapability).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5" />
								<s:label key="getText('qosprofile.detail.prevulnerability')" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(preVulnerability).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5" />
								<s:label key="getText('qosprofile.detail.prioritylevel')" value="%{priorityLevel}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5" />
							</div>
							<div class="row">
								<s:label key="getText('qosprofile.redirecturl')" value="%{redirectUrl}"  cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5 word-break" />
							</div>
						</div>
					</div>
				<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="qosProfile.detail.usagemonitoringinformation" /> </legend>
				<div class="row">
					<div class="col-sm-4 col-xs-12">
						<div class="row">
							<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px"><s:text name="qosProfile.detail.usagemonitoring" /> </div>
							<div class="col-xs-8 col-sm-7" >
								<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(usageMonitoring).getStringName()}"/>
							</div>
						</div>
					</div>
					<s:if test="usageMonitoring == false">
						<div class="col-sm-4 col-xs-12 leftVerticalLine" style="color:grey">
					</s:if>
					<s:else>
						<div class="col-sm-4 col-xs-12 leftVerticalLine">
					</s:else>
						<div class="row">
							<div class="col-xs-4 col-sm-5" style="margin-bottom: 5px"><s:label value="Slice Quota" /></div>
							<div class="col-xs-8 col-sm-7">
								<s:if test="sliceTotal != null">
									<s:property value="sliceTotal"/> <s:property value="sliceTotalUnit"/>
								</s:if>
							</div>
							<div class="col-xs-4 col-sm-5" ></div>
							<div class="col-xs-8 col-sm-7" >
								<s:if test="sliceDownload != null">
									<s:property value="sliceDownload" /> <s:property value="sliceDownloadUnit" /> <span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
								</s:if>
							</div>
							<div class="col-xs-4 col-sm-5"></div>
							<div class="col-xs-8 col-sm-7">
								<s:if test="sliceUpload != null">
									<s:property value="sliceUpload"/> <s:property value="sliceUploadUnit"/> <span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
								</s:if>
							</div>
						</div>
					</div>
					<s:if test="usageMonitoring == false">
						<div class="col-sm-4 col-xs-12 leftVerticalLine" style="color:grey">
					</s:if>
					<s:else>
						<div class="col-sm-4 col-xs-12 leftVerticalLine">
					</s:else>
						<div class="row">
							<div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px"> <s:text name="Slice Time" /> </div>
							<div class="col-xs-8 col-sm-7">
								<s:if test="sliceTime != null">
									<span><s:property value="sliceTime"/></span>
									<span><s:property value="sliceTimeUnit"/></span>
								</s:if>
							</div>
						</div>
					</div>
				</div> 
		</fieldset>
	
				<div style="text-align: right;">
					<s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
					<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"
						onclick="javascript:location.href='${pageContext.request.contextPath}/emergencypccrule/policydesigner/pccrule/EmergencyPccRule/init?qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&pkgId=${qosProfile.pkgData.id }&pkgType=EMERGENCY&groupIds=${qosProfile.groups}'">
						<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
						<s:text name="qosprofile.addpccrule" />
					</button>
					</s:if>
					<s:else>
					<button class="btn btn-primary btn-xs" disabled="disabled" style="padding-top: 3px; padding-bottom: 3px" >
						<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
						<s:text name="qosprofile.addpccrule" />
					</button>
					</s:else>
				</div>
					<div>
						<nv:dataTable 							
							id="qosProfileDetail-${fupLevel}"
							actionUrl="/searchTable/policydesigner/util/Nvsmx/execute?qosProfileDetailId=${id}"																							
							beanType="com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleWrapper" 						
							width="100%" 
							showPagination="false"
							showInfo="false"		
							cssClass="table table-blue">	
							<nv:dataTableColumn title="PCCRules" 		 beanProperty="name" tdCssClass="text-left text-middle word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/view?pccRuleId=id&requestFromQosView=true&qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}" tdStyle="width:200px" />
							<nv:dataTableColumn title="Service" 		 beanProperty="serviceName" tdCssClass="text-left text-middle" tdStyle="width:100px" hrefurl="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=serviceId"/>
							<nv:dataTableColumn title="Monitoring Key"   beanProperty="monitoringKey" tdCssClass="text-left text-middle word-break" tdStyle="width:200px"/>
							<nv:dataTableColumn title="MBR" 			 beanProperty="mbrdl,mbrul" tdCssClass="text-right" cssClass="text-right" tdStyle="width:180px"/>
							<nv:dataTableColumn title="GBR" 		 	 beanProperty="gbrdl,gbrul" tdCssClass="text-right" cssClass="text-right" tdStyle="width:180px"/>
							<s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
								<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;"  tdCssClass="text-middle" hrefurl="edit:${pageContext.request.contextPath}/emergencypccrule/policydesigner/pccrule/EmergencyPccRule/init?pccRuleId=id&qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&groupIds=groups"/>
			            		<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/emergencypccrule/policydesigner/pccrule/EmergencyPccRule/delete?ids=id&qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}" style="width:20px;" tdStyle="width:20px;"  tdCssClass="text-middle" />
			            	</s:if>
			            	<s:else>
								<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"  tdCssClass="text-middle"  />	
			            		<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"  style="width:20px;" tdStyle="width:20px;opacity:0.50;"  tdCssClass="text-middle" />			            	
			            	</s:else>
						</nv:dataTable>
					</div>
					</s:if>
				</div>
       </fieldset>
       </s:iterator>
      <s:if test="%{qosProfile.qosProfileDetailDataList.size() == 0}">
  			<div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" id="fupbutton">
  					<s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
					<button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/initCreateHSQDetail?qosProfileId=${qosProfile.id}&fuplevel=0&groupIds=${qosProfile.groups}'">
						<span class="glyphicon glyphicon-plus-sign" style="top: 0px"></span>	
						<s:text name="qosprofile.hs"/> <s:text name="qosprofile" />
					</button>
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
					<button id="btnBack" class="btn btn-primary btn-sm"  value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId=${qosProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
				</div>
			</div>
       	</div>
	</div>
</div>     
    