<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

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
			<s:property value="chargingRuleBaseNameData.name"/>
		</h3>
		<div class="nv-btn-group" align="right">

			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
						onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/export?ids=${chargingRuleBaseNameData.id}&${chargingRuleBaseNameData.id}=${chargingRuleBaseNameData.groups}&chargingRuleBaseNameId=${chargingRuleBaseNameData.id}'">
					<span class="glyphicon glyphicon-export" ></span>
				</button>
			</span>

			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn"  data-placement="bottom" data-toggle="modal" data-target="#chargingRuleBaseNameAssociation" title="View Associations">
					<span class="glyphicon glyphicon-th" title="Add"/>
				</button>
			</span>

			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${chargingRuleBaseNameData.id}&auditableId=${chargingRuleBaseNameData.id}&auditPageHeadingName=${chargingRuleBaseNameData.name}&refererUrl=/policydesigner/chargingrulebasename/ChargingRuleBaseName/view?chargingRuleBaseNameId=${chargingRuleBaseNameData.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>

			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/init?chargingRuleBaseNameId=${chargingRuleBaseNameData.id}&groupIds=${chargingRuleBaseNameData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>

			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/delete?ids=${chargingRuleBaseNameData.id}&${chargingRuleBaseNameData.id}=${chargingRuleBaseNameData.groups}&chargingRuleBaseNameId=${chargingRuleBaseNameData.id}">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>

		</div>
	</div>

	<div class="panel-body" >
		<div class="row">
			<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="basic.detail" /></legend>
				<div class="row">
					<div class="col-sm-8">
						<div class="row">
							<s:label key="chargingrulebasename.description" value="%{chargingRuleBaseNameData.description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
						</div>
						<div class="row">
							<s:label key="entity.groups" value="%{chargingRuleBaseNameData.groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
							<s:if test="%{chargingRuleBaseNameData.createdByStaff !=null}">
								<s:hrefLabel key="getText('createdby')" value="%{chargingRuleBaseNameData.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
											 url="/sm/staff/staff/%{chargingRuleBaseNameData.createdByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdby')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{chargingRuleBaseNameData.createdDate != null}">
								<s:set var="createdByDate">
									<s:date name="%{chargingRuleBaseNameData.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdon')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{chargingRuleBaseNameData.modifiedByStaff !=null}">
								<s:hrefLabel key="getText('modifiedby')" value="%{chargingRuleBaseNameData.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
											 url="/sm/staff/staff/%{chargingRuleBaseNameData.modifiedByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="getText('modifiedby')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{chargingRuleBaseNameData.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{chargingRuleBaseNameData.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('lastmodifiedon')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
						</div>
					</div>
				</div>
	       </fieldset>

	       <fieldset class="fieldSet-line">
				<legend align="top"><s:text name="chargingrulebasename.servicetypes"/> </legend>
				<div class="row">
					<div class="col-sm-12">

						<nv:dataTable
							id="crbnServicesDataTable"
							list="${chargingRuleBaseNameData.chargingRuleServiceTypeDataInJsonString}"
							width="100%"
							showPagination="false"
							showInfo="false"
							cssClass="table table-blue" >

							<nv:dataTableColumn title="Monitoring Key" 	beanProperty="monitoringKey" 				tdCssClass="text-left text-middle" 		tdStyle="width:15%"	/>
							<nv:dataTableColumn title="Data Service Type" 	beanProperty="dataServiceTypeData.name" 		tdCssClass="text-left text-middle" 		tdStyle="width:15%"	hrefurl="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=dataServiceTypeData.id"/>
							<nv:dataTableColumn title="Total"   		beanProperty="sliceTotalValue" 	tdCssClass="text-left text-middle" 		tdStyle="width:15%"	/>
							<nv:dataTableColumn title="Download" 		beanProperty="sliceDownloadValue" tdCssClass="text-left text-middle" 	tdStyle="width:15%"	/>
							<nv:dataTableColumn title="Upload" 			beanProperty="sliceUploadValue" 	tdCssClass="text-left text-middle" 		tdStyle="width:15%"	/>
							<nv:dataTableColumn title="Time" 			beanProperty="sliceTimeValue" 		tdCssClass="text-left text-middle" 		tdStyle="width:15%"	/>
						</nv:dataTable>
					</div>
				</div>

       		</fieldset>

	    </div>
    </div>
</div>
