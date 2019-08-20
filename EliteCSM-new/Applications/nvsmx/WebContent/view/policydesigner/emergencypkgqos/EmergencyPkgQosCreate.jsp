<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<%
	String advanceConditions = (String)request.getAttribute(Attributes.ADVANCE_CONDITIONS);
%>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="qosprofile.create" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/emergencypkgqos/EmergencyPkgQos/create" validate="true" id="qosProfileCreate" method="post" cssClass="form-horizontal"  labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10" validator="validateForm()">
		<s:token />
			<s:hidden name="pkgId" value="%{pkgId}"/>
			<s:hidden name="groupIds" value="%{qosProfile.pkgData.groups}"/>
			<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{qosProfile.pkgData.groups}"/>
			<div class="row">
				<div class="col-sm-8">
					<s:textfield 	name="qosProfile.name" 		key="qosprofile.name" 	id="qosProfileName"		cssClass="form-control focusElement" 
					 onblur="verifyUniqueness('qosProfileName','create','','com.elitecore.corenetvertex.pkg.qos.QosProfileData','%{pkgId}','');" />   				
					<s:textarea 	name="qosProfile.description" 	key="qosprofile.description" 				cssClass="form-control"  />
				</div>
			</div>
			<div class="row" >
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="qosprofile.authorization.parameters"/>
					</legend>
					<div class="row">
						<div class="col-sm-8">							
							<s:textarea 	name="qosProfile.advancedCondition" 	key="qosprofile.advancecondition"  cssClass="form-control" id="advanceCondition" />
							<s:select       multiple="true" list="pcrfKeyValueConstants"	name="qosProfile.accessNetwork" id="accessNetwork"		key="qosprofile.accessnetwork" 		cssClass="form-control select2" cssStyle="width:100%"/>
						</div>
					</div>
				</fieldset>
			</div>
			<div  class="row">
				<fieldset class="fieldSet-line" id="timeBaseCondtionSet">
					<legend><s:text name="qosprofile.timebasedconditions" /></legend>
					<div class="row">
						<div class="col-sm-8">
						<s:textfield 	name="qosProfile.duration" 	key="qosprofile.duration" cssClass="form-control" type="number"	 onkeypress="return isNaturalInteger(event);" maxlength="18" />
						</div>
					</div>
				<div class="row">
					<div id="timeBaseConditionDiv">
							<div class="col-xs-12">
								<table id='timeBaseConditionTable'  class="table table-blue table-bordered">
									<caption class="caption-header">
										<span class="glyphicon glyphicon-check"></span><s:text name="timeperiod.restricions"/>
										<div align="right" class="display-btn">
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addTimeBasedCondition();"
												id="addRow"> <span class="glyphicon glyphicon-plus"></span>
											</span>
										</div>
									</caption>
									<thead>
										<th><s:text name="timeperiod.moy"/></th>
										<th><s:text name="timeperiod.dom"/></th>
										<th><s:text name="timeperiod.dow"/></th>
										<th><s:text name="timeperiod.timeperiod"/></th>
										<th style="width:35px;">&nbsp;</th>
									</thead>
								</table>
							</div>
					</div>
				</div>
			</fieldset>
			</div>
		 <div class="row">
				<div class="col-xs-12" align="center">
					<s:submit cssClass="btn btn-primary btn-sm" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId=${qosProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
				</div>
			</div>
		</s:form>
	</div>	
</div>



<script type="text/javascript">

$(function(){
	$(".select2").select2();
	$(".focusElement").focus();
	$("#advanceCondition").autocomplete();
	var advanceConditionsAutoCompleter = createModel(<%=advanceConditions%>);
	expresssionAutoComplete('advanceCondition',advanceConditionsAutoCompleter);
	
});

var i=0;
function addTimeBasedCondition(){
	var tableRow= "<tr name='timePeriodRow'>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].moy'  type='text'></td>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].dom'  type='text'></td>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].dow'  type='text'></td>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].timePeriod'  type='text'></td>"+
	              "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
	              "</tr>";
	$(tableRow).appendTo('#timeBaseConditionTable');
	i++;
}
function validateForm(){
 return verifyUniquenessOnSubmit('qosProfileName','create','','com.elitecore.corenetvertex.pkg.qos.QosProfileData','<s:text name="pkgId" />','');
 
}
</script>
