<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<script src="${pageContext.request.contextPath}/js/RatingGroup.js"></script>
<style>
	.removePadding{
		display: inline;
	}
	.wrap-word{
		word-break: break-all;
		width: 20%;
	}

</style>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="ratinggroup.create" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/ratinggroup/RatingGroup/create" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token/>
			<div class="row">
				<div class="col-xs-6">
					<s:textfield name="ratingGroupData.name" key="ratinggroup.name" id="ratingGroupName"
								 cssClass="form-control focusElement"
								 onblur="verifyUniqueness('ratingGroupName','create','','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','');"
								 tabindex="1"/>
					<s:textarea name="ratingGroupData.description" key="ratinggroup.description" cssClass="form-control"
								rows="2" tabindex="2"/>

				</div>
				<div class="col-xs-6">
					<s:textfield id="identifier" name="ratingGroupData.identifier" key="ratinggroup.identifier"
								 cssClass="form-control" tabindex="3" type="number"
								 onkeypress="return isNaturalInteger(event);"
								 onblur="verifyUniqueness('identifier','create','','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','identifier');"
							/>
					<s:select name="groupIds" key="entity.groups" id="groups" list="#session.staffBelongingGroups"
							  cssClass="form-control select2" listKey="id" listValue="name" multiple="true"
							  cssStyle="width:100%" tabindex="4"/>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<s:set value="0" name="index"/>
					<table id="dataServiceType" class="table table-blue table-condensed table-bordered">
						<caption class="caption-header">

							<s:text name="ratinggroup.servicetype"/>
							<div align="right" class="display-btn">
									<span class="btn btn-group btn-group-xs defaultBtn">
										<span class="glyphicon glyphicon-unchecked" onclick="selectAll()"
											  id="selectAll"></span> Select All</span>
							</div>
						</caption>
						<tbody>
						<s:hidden name="selectedServiceTypes" id="selectedServiceTypes"/>
						<s:iterator value="allServices" status="dataServiceType" var="dataServiceType">
							<s:if test="%{#index%5==0}">
								<tr>
							</s:if>
							<td class="wrap-word">
								<div>
									<s:checkbox key="%{id}"  name="" id="selectedServices" cssClass="removePadding" fieldValue="%{id}" theme="simple"/>
									<s:text name="name" />
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

			<div class="row">
				<div class="col-xs-12" align="center">					
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="5" onclick="getSelectedValuesForServiceType()"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
				</div>
			</div>
		</s:form>
	</div>

</div>
<script type="text/javascript">
$(function(){
	$(".select2").select2();
});

function validateForm(){
	return verifyUniquenessOnSubmit('ratingGroupName','create','','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','') && verifyUniquenessOnSubmit('identifier','create','','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','identifier');
}
</script>