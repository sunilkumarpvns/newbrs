<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@include file="/view/policydesigner/ratinggroup/RatingGroupsCommonValidation.jsp" %>
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
			<s:property value="ratingGroupData.name"/>
		</h3>
		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
						onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/export?ids=${ratingGroupData.id}&${ratingGroupData.id}=${ratingGroupData.groups}&ratingGroupId=${ratingGroupData.id}'">
					<span class="glyphicon glyphicon-export" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${ratingGroupData.id}&auditableId=${ratingGroupData.auditableId}&auditPageHeadingName=${ratingGroupData.name}&refererUrl=/policydesigner/ratinggroup/RatingGroup/view?ratingGroupId=${ratingGroupData.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/init?ratingGroupId=${ratingGroupData.id}&groupIds=${ratingGroupData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" >
				<button id="deleteButton" type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete" onclick="$('#confirmAlert').modal('show');">
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
							<s:label key="ratinggroup.description" value="%{ratingGroupData.description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="ratinggroup.identifier" value="%{ratingGroupData.identifier}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
							<s:label key="entity.groups" value="%{ratingGroupData.groupNames}" cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine"> 
						<div class="row">
							<s:hrefLabel key="getText('createdby')" value="%{ratingGroupData.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
									 url="/sm/staff/staff/%{ratingGroupData.createdByStaff.id}"/>
							<s:if test="%{ratingGroupData.createdDate != null}">
								<s:set var="createdByDate">
									<s:date name="%{ratingGroupData.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:hrefLabel key="getText('modifiedby')" value="%{ratingGroupData.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
									 url="/sm/staff/staff/%{ratingGroupData.modifiedByStaff.id}"/>
							<s:if test="%{ratingGroupData.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{ratingGroupData.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
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

			<!-- adding Service Type associated with Rating Group -->
			<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="ratinggroup.servicetype"/></legend>
				<div class="row">
					<div class="col-sm-12">
						<s:set value="0" name="index"/>
						<table id="dataServiceType" class="table table-blue table-condensed table-bordered">
							<tbody>
							<s:iterator value="ratingGroupData.dataServiceTypeData" status="dataServiceType" var="dataServiceType">
								<s:if test="%{#index%5==0}">
									<tr>
								</s:if>
								<td>
									<div class="col-sm-5">
										<a href="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=${id}"><s:property value="name" /></a>
									</div>
								</td>
								<s:if test="%{#index%5==4}">
									</tr>
								</s:if>
								<s:set name="index" value="%{#index+1}"/>
							</s:iterator>
							<s:if test="%{#index%5!=0}">
								</tr>
							</s:if>
							</tbody>
						</table>
					</div>
				</div>
			</fieldset>
	    </div>
    </div>
</div>

<script>
	$(function(){
		$("#btnYes").on('click', function(){
			location.href = '${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/delete?ids=${ratingGroupData.id}&${ratingGroupData.id}=${ratingGroupData.groups}&ratingGroupId=${ratingGroupData.id}';
		});
	});

</script>
