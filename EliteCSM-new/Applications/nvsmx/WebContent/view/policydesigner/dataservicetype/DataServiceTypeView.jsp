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
			<s:property value="dataServiceTypeData.name"/>
		</h3>
		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
						onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/export?ids=${dataServiceTypeData.id}&${dataServiceTypeData.id}=${dataServiceTypeData.groups}'">
					<span class="glyphicon glyphicon-export" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${dataServiceTypeData.id}&auditableId=${dataServiceTypeData.auditableId}&auditPageHeadingName=${dataServiceTypeData.name}&refererUrl=/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=${dataServiceTypeData.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/initUpdate?serviceTypeId=${dataServiceTypeData.id}&groupIds=${dataServiceTypeData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>

			<s:if test="%{dataServiceTypeData.id != @com.elitecore.corenetvertex.constants.CommonConstants@ALL_SERVICE_ID}">
				<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/delete?ids=${dataServiceTypeData.id}&${dataServiceTypeData.id}=${dataServiceTypeData.groups}">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
						<span class="glyphicon glyphicon-trash"></span>
					</button>
				</span>
			</s:if>
			<s:else>
				<span class="btn-group btn-group-xs">
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
							<s:label key="dataservicetype.description" value="%{dataServiceTypeData.description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="dataservicetype.serviceidentifier" value="%{dataServiceTypeData.serviceIdentifier}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine"> 
						<div class="row">
							<s:if test="%{dataServiceTypeData.createdByStaff !=null}">
								<s:label key="getText('createdby')" value="%{dataServiceTypeData.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdby')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{dataServiceTypeData.createdDate != null}">
								<s:set var="createdByDate">
									<s:date name="%{dataServiceTypeData.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdon')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{dataServiceTypeData.modifiedByStaff !=null}">
								<s:label key="getText('modifiedby')" value="%{dataServiceTypeData.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('modifiedby')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{dataServiceTypeData.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{dataServiceTypeData.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
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
				<legend align="top"><s:text name="dataservicetype.servicedataflow"/> </legend>
				<div class="row">
					<div class="col-sm-12">
					<nv:dataTable 							
						id="defaultServiceDataFlow"
						list="${dataServiceTypeData.defaultServiceDataFlowsInJsonString}"
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
				</div>
       		</fieldset>
       		<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="dataservicetype.ratinggroup" /> </legend>
				<div class="row">
					<div class="col-sm-12">
						<nv:dataTable 							
							id="ratingGroupData"
							list="${dataServiceTypeData.ratingDataInJsonString}"
							width="100%" 
							showPagination="false"
							showInfo="false"		
							cssClass="table table-blue">	
							<nv:dataTableColumn title="Name" beanProperty="name" tdCssClass="text-left text-middle" hrefurl="${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/view?ratingGroupId=id"/>
							<nv:dataTableColumn title="Identifier" beanProperty="identifier" tdCssClass="text-left text-middle" />
							<nv:dataTableColumn title="Description" beanProperty="description" tdCssClass="text-left text-middle" />
						</nv:dataTable>
					</div>
				</div>
       		</fieldset>
	    </div>
    </div>
</div>
       