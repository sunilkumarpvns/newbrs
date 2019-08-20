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
		<h3 class="panel-title"><s:text name="qosprofile.update" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/emergencypkgqos/EmergencyPkgQos/update" validate="true" id="qosProfileCreate" method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10" validator="validateForm()">
			<s:hidden name="pkgId" value="%{pkgId}"/>
			<s:hidden name="qosProfile.id" value="%{qosProfile.id}"/>
			<s:hidden name="groupIds" value="%{qosProfile.pkgData.groups}"/>
			<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{qosProfile.pkgData.groups}"/>
			<div class="row">
				<div class="col-sm-8">
					<s:textfield 	name="qosProfile.name" 		key="qosprofile.name" 	id="qosProfileName"		cssClass="form-control" 
					 onblur="verifyUniqueness('qosProfileName','update','%{qosProfile.id}','com.elitecore.corenetvertex.pkg.qos.QosProfileData','%{pkgId}','');"/> 				
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
						<s:select value="showSelectedAccessNetworkForUpdate" multiple="true" name="qosProfile.accessNetwork"  id="accessNetwork"		key="qosprofile.accessnetwork" 		cssClass="form-control select2"  list="pcrfKeyValueConstants" cssStyle="width:100%"/>
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
									<tbody>
									<s:iterator value="qosProfile.timePeriodDataList" status="i" var="timePeriods">
										<tr name='timePeriodRow'>
										  <td><s:textfield value="%{#timePeriods.moy}"	name="timePeriodDataList[%{#i.count - 1}].moy" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
							              <td><s:textfield value="%{#timePeriods.dom}"	name="timePeriodDataList[%{#i.count - 1}].dom" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
							              <td><s:textfield value="%{#timePeriods.dow}"	name="timePeriodDataList[%{#i.count - 1}].dow" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
							              <td><s:textfield value="%{#timePeriods.timePeriod}"	name="timePeriodDataList[%{#i.count - 1}].timePeriod" cssClass="form-control" elementCssClass="col-xs-12" /></td>
							              <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
							            </tr>																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																						
									</s:iterator>
									</tbody>
								</table>																																																																																																																
							</div>
					</div>
				</div>
			</fieldset>
			</div>
		 <div class="row">
				<div class="col-xs-12" align="center">
					<s:submit cssClass="btn btn-primary btn-sm" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>	
					<s:iterator value="qosProfile.qosProfileDetails">
						<button class="btn btn-primary btn-sm" type="button" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/initUpdateHSQDetail?qosProfileDetailId=${id}'">
							<span class="glyphicon glyphicon-pencil"></span>
							<s:if test="fupLevel == 0">
								 <s:text name="qosprofile.hs" /> <s:text name="qosprofile"/>
							</s:if>
							<s:else>
								<s:text name="qosprofile.fup" />-<s:property value="fupLevel"/> <s:text name="qosprofile"/>
							</s:else>
						</button>
					</s:iterator>
					<button id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/view?qosProfileId=${qosProfile.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.qosprofile"></s:text> </button>
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


function addTimeBasedCondition(){
	var i = document.getElementsByName("timePeriodRow").length;
	var tableRow= "<tr name='timePeriodRow'>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].moy'  type='text'></td>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].dom'  type='text'></td>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].dow'  type='text'></td>"+
	              "<td><input class='form-control' name='timePeriodDataList["+i+"].timePeriod'  type='text'></td>"+
	              "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
	              "</tr>";
	$(tableRow).appendTo('#timeBaseConditionTable');
}

function validateForm(){
 return verifyUniquenessOnSubmit('qosProfileName','update','<s:text name="qosProfile.id" />','com.elitecore.corenetvertex.pkg.qos.QosProfileData','<s:text name="pkgId" />','');
}
</script>
