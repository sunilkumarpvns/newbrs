<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="ratinggroup.update" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/ratinggroup/RatingGroup/update" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token/>
			<div class="row">
				<div class="col-sm-9 col-lg-7">
					<s:hidden name="ratingGroupData.id" />
					<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{ratingGroupData.groups}"/>

					<s:textfield name="ratingGroupData.name" key="ratinggroup.name" id="ratingGroupName" cssClass="form-control focusElement"
								 onblur="verifyUniqueness('ratingGroupName','update','%{ratingGroupData.id}','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','');" tabindex="1" />
					<s:textarea name="ratingGroupData.description" 	key="ratinggroup.description" cssClass="form-control" rows="2" tabindex="2" />
					<s:textfield id="identifier" name="ratingGroupData.identifier" key="ratinggroup.identifier" cssClass="form-control" tabindex="3" type="number"  onkeypress="return isNaturalInteger(event);" readonly="true"
						onblur="verifyUniqueness('identifier','update','%{ratingGroupData.id}','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','identifier');"
					/>

					<div class="form-group ">

						<label class="col-xs-12 col-sm-4 col-lg-3 control-label" id="lbl_pkgGroup">Groups </label>

						<div class="col-xs-12 col-sm-8 col-lg-9 controls">
							<select name="groupIds" key="ratingGroupData.groups" class="form-control select2" style="width:100%" multiple="true">
								<s:iterator value="groupInfoList">
									<option locked="<s:property value="locked"/>" <s:property value="selected"/>
											value="<s:property value="id"/>" id="<s:property value="id"/>">
										<s:property value="name"/></option>
								</s:iterator>
							</select>
						</div>

					</div>


				</div>

				<!-- adding service type associated with Rating Group -->
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
						<s:set value="0" name="index"/>
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
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="13" onclick="getSelectedValuesForServiceType()"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/view?ratingGroupId=${ratingGroupData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> RatingGroup</button>
				</div>
			</div>
		</s:form>
	</div>
</div>
</div>

<script src="${pageContext.request.contextPath}/js/RatingGroup.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>

<script type="text/javascript">
$(function(){
	$(".select2").select2();
	getSelectedValuesForServiceTypeUpdate();
});

function validateForm(){
	return verifyUniquenessOnSubmit('ratingGroupName','update','<s:text name="ratingGroupData.id"/>','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','') && verifyUniquenessOnSubmit('identifier','update','<s:text name="ratingGroupData.id"/>','com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData','','identifier');
}
</script>