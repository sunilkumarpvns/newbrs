<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<style type="text/css">
	.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
	
}

</style> 
<script type="text/javascript">
/*function submitPage() {
	window.location = "${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${quotaProfile.pkgData.id}";
}*/
</script>
<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title" style="display: inline;">
			<s:property value="quotaProfile.name"/>
		</h3>
		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${quotaProfile.id}&auditableId=${quotaProfile.auditableId}&auditPageHeadingName=${quotaProfile.name}&refererUrl=/policydesigner/quota/QuotaProfile/view?quotaProfileId=${quotaProfile.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<%--checking for package mode--%>
			<s:if test="%{quotaProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
			<span class="btn-group btn-group-xs">
			<%--checking for package type --%>
				<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != quotaProfile.pkgData.type }">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
							onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/init?quotaProfileId=${quotaProfile.id}&groupIds=${quotaProfile.pkgData.groups}'">
						<span class="glyphicon glyphicon-pencil"></span>
					</button>
				</s:if>
				<s:else>
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
							onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/quota/QuotaProfile/PromotionalQuotaProfile/init?quotaProfileId=${quotaProfile.id}&groupIds=${quotaProfile.pkgData.groups}'">
						<span class="glyphicon glyphicon-pencil"></span>
					</button>
				</s:else>
			</span>
				<s:if test="%{quotaProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
					<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != quotaProfile.pkgData.type }">
						<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()"
							  data-href="${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/delete?quotaProfileId=${quotaProfile.id}&groupIds=${quotaProfile.groups}">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
								title="delete">
							<span class="glyphicon glyphicon-trash"></span>
						</button>
						</span>
					</s:if>
					<s:else>
					<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()"
						  data-href="${pageContext.request.contextPath}/promotional/policydesigner/quota/QuotaProfile/PromotionalQuotaProfile/delete?quotaProfileId=${quotaProfile.id}&groupIds=${quotaProfile.groups}">
						<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
								title="delete">
							<span class="glyphicon glyphicon-trash"></span>
						</button>
					</span>
					</s:else>
				</s:if>
				<s:else>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton">
				<button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip"
						data-placement="bottom" title="delete">
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
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton">
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
				<legend align="top"><s:text name="basic.detail" /></legend>
				<div class="row">
					<div class="col-sm-8">
						<div class="row">
							<s:label key="quotaprofile.description" value="%{quotaProfile.description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="quotaprofile.usagepresence" value="%{@com.elitecore.corenetvertex.constants.CounterPresence@fromValue(quotaProfile.usagePresence).getDisplayVal()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="quotaprofile.balance.level" value="%{quotaProfile.balanceLevel.displayVal}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="quotaprofile.renewal.interval" value="%{quotaProfile.renewalInterval}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="quotaprofile.renewal.interval.unit" value="%{quotaProfile.renewalIntervalUnit}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="quotaprofile.groups" value="%{quotaProfile.groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
						    <s:set var="createdByDate">
						      <s:date name="%{quotaProfile.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
						    </s:set>
							<s:if test="%{quotaProfile.createdByStaff !=null}">
							 <s:hrefLabel key="getText('createdby')" value="%{quotaProfile.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
									  url="/sm/staff/staff/%{quotaProfile.createdByStaff.id}"/>
							</s:if>
							<s:else>
							  <s:label key="getText('createdby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{quotaProfile.createdDate !=null}">
							 <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
							<s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/> 
							</s:else>
							
							<s:if test="%{quotaProfile.modifiedByStaff !=null}">
								<s:hrefLabel key="getText('modifiedby')" value="%{quotaProfile.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{quotaProfile.modifiedByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="getText('modifiedby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{quotaProfile.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{quotaProfile.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
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
       <s:iterator value="quotaProfile.fupLevelMap">
       <fieldset class="fieldSet-line">
       		<s:if test="key == 0">
       			<legend align="top"><s:text name="quotaprofile.hs"/> <s:text name="quotaprofile"/></legend>
       		</s:if>
       		<s:else>
				<legend align="top"><s:text name="quotaprofile.fup"/>-<s:property value="key"/> <s:text name="quotaprofile"/></legend>
			</s:else>
			<s:if test="key != 0 && key == quotaProfile.fupLevelMap.size()-1 ">
				<div align="right" class="display-btn" style="margin-bottom: 5px;">
				<s:if test="%{quotaProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
					<div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/deleteFupLevels?fupLevel=${key}&quotaProfileId=${quotaProfile.id}&groupIds=${quotaProfile.groups}">
						<button type="button"  class="btn btn-default">
							<span class="glyphicon glyphicon-trash"></span>
						</button>
					</div>
				</s:if>
				<s:else>
					<div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton" >
						<button type="button" disabled="disabled"  class="btn btn-default" >
							<span class="glyphicon glyphicon-trash"></span>
						</button>
					</div>
				</s:else>
				</div>
			</s:if>
					<div style="margin-bottom: 5px" class="row">
						<div class="col-xs-12">
							<div class="row">
								<div class="col-xs-4 col-sm-2"><s:label value="Total Quota" /></div>
								<div class="col-xs-8 col-sm-8 row">
									<s:if test="value.total == null && value.download == null && value.upload == null">
										<div class="col-sm-4"><s:text name="quotaprofile.unlimited"></s:text> </div>
									</s:if>
									<s:else>
										<s:if test="value.total != null">
											<div class="col-sm-4">
												<s:property value="value.total"/> <s:property value="value.totalUnit"/>
											</div>
										</s:if>
										<s:if test="value.download != null">
											<div class="col-sm-4"><s:property value="value.download"/> <s:property value="value.downloadUnit"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span></div>
										</s:if>
										<s:if test="value.upload != null">
											<div class="col-sm-4"><s:property value="value.upload"/> <s:property value="value.uploadUnit"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span></div>
										</s:if>
									</s:else>
								</div>
							</div>
						</div>
						<div class="col-xs-12">
							<div class="row">
							<div class="col-xs-4 col-sm-2"><s:label value="Total Time" /></div>
							<s:if test="value.time != null">
							<div class="col-xs-8 col-sm-8"><s:property value="value.time"/> <s:property value="value.timeUnit"/></div>
							</s:if>
							</div>
						</div>
					</div>
					<div>

		   <nv:dataTable
							id="quotaProfileDetail-${key}"
							list="${quotaProfileDetailJsonMap[key]}"
							width="100%"
							showPagination="false"
							showInfo="false"		
							cssClass="table table-blue">	
							<nv:dataTableColumn title="Services" 		 beanProperty="serviceName" tdCssClass="text-left" tdStyle="width : 100px" hrefurl="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=serviceId"/>
							<nv:dataTableColumn title="Aggregation Key"  beanProperty="aggregationKey"  sortable="false" tdStyle="width : 125px"/>
							<nv:dataTableColumn title="Total" 	 	     beanProperty="total" cssClass="text-right" tdCssClass="text-right" tdStyle="width : 200px"/>	
							<nv:dataTableColumn title="Download" 	 	 beanProperty="download,downloadUnit" cssClass="text-right" tdCssClass="text-right" tdStyle="width : 200px"/>
							<nv:dataTableColumn title="Upload" 	 	 	 beanProperty="upload,uploadUnit" cssClass="text-right" tdCssClass="text-right" tdStyle="width : 200px"/>
							<nv:dataTableColumn title="Time" 	 	 	 beanProperty="time,timeUnit" cssClass="text-right" tdCssClass="text-right" tdStyle="width : 200px"/>
						</nv:dataTable>


					</div>
       </fieldset>
       </s:iterator>
		<div class="row">
			<div class="col-xs-12" align="center">
				<button class="btn btn-primary btn-sm"  onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${quotaProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"/> </button>
			</div>
		</div>
	</div>
	</div>
</div>

