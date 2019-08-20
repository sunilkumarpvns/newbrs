<%@page import="com.elitecore.corenetvertex.constants.IMSServiceAction"%>
<%@page import="com.elitecore.corenetvertex.pkg.constants.PCCAttribute"%>
<%@page import="com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>

<%
	String advanceConditions = (String)request.getAttribute(Attributes.ADVANCE_CONDITIONS);
%>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="ims.pkg.service.update" /></h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/imspkgservice/IMSPkgService/update" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token />
			 <s:hidden   name="imsPkgServiceData.id"/>
			<s:hidden name="groupIds" value="%{imsPkgServiceData.imsPkgData.groups}"/>
			<s:hidden name="entityOldGroups" value="%{imsPkgServiceData.imsPkgData.groups}"/>
			<s:hidden id="imsPkgServiceDataListSize" value="%{imsPkgServiceData.imsPkgPCCAttributeDatas.size}" />
			<s:hidden name="pkgId" value="%{imsPkgServiceData.imsPkgData.id}" />
			<div class="row">
				<div class="col-sm-9 col-lg-7">
					<s:textfield  	name="imsPkgServiceData.name" 		key="ims.pkg.service.name" 	id="imspkgServiceName"		cssClass="form-control focusElement" tabindex="1" 
					    onblur=" verifyUniqueness('imspkgServiceName','update','%{imsPkgServiceData.id}','com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData','%{imsPkgServiceData.imsPkgData.id}','');" autofocus="true"  />
					<s:select 		name="imsPkgServiceData.mediaTypeId" key="ims.pkg.service.mediatype"    cssClass="form-control"	list="mediaTypeDatas" id="mediatype" listValue="name" listKey="id" tabindex="2" />
					<s:textfield 	name="imsPkgServiceData.afApplicationId"  key="ims.pkg.service.appfunctionid" 		cssClass="form-control"  id="afApplicationId" tabindex="3" />
					<s:textarea 	name="imsPkgServiceData.expression" 	key="ims.pkg.service.expression"  cssClass="form-control" id="expression" tabindex="4" />
					<s:select       name="imsPkgServiceData.action" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.IMSServiceAction@values()" tabindex="5"  
  									id="action" key="ims.pkg.service.action"  listValue="getName()" onchange="checkAction();" />
				</div>
			</div>
			<div  class="row">
				<fieldset class="fieldSet-line">
					<legend><s:text name="ims.pkg.pcc.attributes" /></legend>
					<div class="row">
					<div id="pccattributes">
							<div class="col-xs-12">
								<table id='PCCAttributesTable'  class="table table-blue table-bordered">
									<caption class="caption-header">
										<s:text name="ims.pkg.pcc.attributes.restriction"/>
										<div align="right" class="display-btn">
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addPCCAttributes();"
												id="addRow"> <span class="glyphicon glyphicon-plus"></span>
											</span>
										</div>
									</caption>
									<thead>
										<th><s:text name="ims.pkg.pcc.attributes"/></th>
										<th><s:text name="ims.pkg.pcc.expression"/></th>
										<th><s:text name="ims.pkg.pcc.action"/></th>
										<th><s:text name="ims.pkg.pcc.value"/></th>
										<th style="width:35px;">&nbsp;</th>
									</thead>
										<s:iterator  value="imsPkgServiceData.imsPkgPCCAttributeDatas" var="imsPkgPCCAttributeData" status="stat">
										<tr name="PCCAttributeRow">
											<td width="20%"> 
											<s:select value="%{#imsPkgPCCAttributeData.attribute}" list="@com.elitecore.corenetvertex.pkg.constants.PCCAttribute@sortedValues()" 
												 name="imsPkgServiceData.imsPkgPCCAttributeDatas[%{#stat.index}].attribute"
												 listValue="getDisplayValue()" onchange="setDefaultAndPossibleValues(%{#stat.index});"
												  cssClass="form-control" elementCssClass="col-xs-12" tabindex="6" 
												  id="pccattribute%{#stat.index}" /></td>
											<td width="30%"><s:textarea value="%{#imsPkgPCCAttributeData.expression}" name="imsPkgServiceData.imsPkgPCCAttributeDatas[%{#stat.index}].expression"
											  cssClass="form-control" rows="1" id="expressionPcc%{#stat.index}" 
											  onfocus="getSuggestions(this)" elementCssClass="col-xs-12" tabindex="7" /></td>
											<td width="20%"><s:select value="%{#imsPkgPCCAttributeData.action}" name="imsPkgServiceData.imsPkgPCCAttributeDatas[%{#stat.index}].action" 
												 list="%{#imsPkgPCCAttributeData.attribute.possibleActions}" id="pccaction%{#stat.index}"
												 listValue="getVal()" elementCssClass="col-xs-12"
												cssClass="form-control" tabindex="8"/></td>
											<td width="19%"><s:textfield value="%{#imsPkgPCCAttributeData.value}" name="imsPkgServiceData.imsPkgPCCAttributeDatas[%{#stat.index}].value" 
											cssClass="form-control" elementCssClass="col-xs-12" tabindex="9" id="pccvalue%{#stat.index}" onfocus="setPossibleValues(%{#stat.index})" /></td>
											<td width="1%"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();" >
										     	<a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a>
										     	</span>
											</td>
										</tr>
										
									</s:iterator>
								</table>
							</div>
					</div>
				</div>
			</fieldset>
			</div>
			<div class="row">
				<div class="col-xs-12" align="center">					
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="10"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"  style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/view?pkgId=${imsPkgServiceData.imsPkgData.id}'" tabindex="11"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.imspkg" /></button>
				</div>
			</div>
		</s:form>
	</div>
</div>

<script type="text/javascript">
var pccAttributeOptions='';

$(function(){
	<%
	for(PCCAttribute pccAttribute : PCCAttribute.sortedValues()){%>
		pccAttributeOptions += "<option value='<%=pccAttribute.name()%>'><%=pccAttribute.displayValue%></option>";
  	<%}%>
  	
  	$("#expression").autocomplete();
	var advanceConditionsAutoCompleter = createModel(<%=advanceConditions%>);
	expresssionAutoComplete('expression',advanceConditionsAutoCompleter);
	checkAction();
	
	
	
});

function setPossibleValues(index){
	var pccattribute = $("#pccattribute"+index).val();
	var pccPossibleValues = getPossibleValues(pccattribute);
	$("#pccvalue"+index).autocomplete();
	commonAutoComplete('pccvalue'+index,pccPossibleValues);
}

function getDefaultValue(pccAttributeName){
	<%for(PCCAttribute pccAttribute : PCCAttribute.values()){%>
		if(pccAttributeName == '<%=pccAttribute.name()%>'){
			return '<%=pccAttribute.defaultVal%>';
		}		
	<%}%>
	
}

function getDefaultAction(pccAttributeName){
	<%for(PCCAttribute pccAttribute : PCCAttribute.values()){%>
		if(pccAttributeName == '<%=pccAttribute.name()%>'){
			return '<%=pccAttribute.defaultAction.val%>';
		}		
	<%}%>
	
}

function getPossibleValues(pccAttributeName){
	var pccPossibleValues = new Array();
	<%for(PCCAttribute pccAttribute : PCCAttribute.values()){%>
		if(pccAttributeName == '<%=pccAttribute.name()%>'){
			<%for(String string : pccAttribute.getPossibleValues()){%>
				pccPossibleValues.push('<%=string%>');
			<%}%>
			return pccPossibleValues;
		}		
	<%}%>
}

function getPossibleActions(pccAttributeName){
	var pccPossibleActions = new Array();
	
	<%for(PCCAttribute pccAttribute : PCCAttribute.values()){%>
		if(pccAttributeName == '<%=pccAttribute.name()%>'){
			<%for(PCCRuleAttributeAction pccRuleAttributeAction : pccAttribute.getPossibleActions()){%>
				var pccPossibleActionsObj = new Array();
				pccPossibleActionsObj.push('<%=pccRuleAttributeAction.val%>');
				pccPossibleActionsObj.push('<%=pccRuleAttributeAction%>');
				pccPossibleActions.push(pccPossibleActionsObj);
			<%}%>
			return pccPossibleActions;
		}
	<%}%>
}

function setDefaultAndPossibleValues(index){
	var pccattribute = $("#pccattribute"+index).val();
	var defaultPccValue = getDefaultValue(pccattribute);
	$("#pccvalue"+index).val(defaultPccValue);
	
	var possibleActions = getPossibleActions(pccattribute);
	var defaultPccAction = getDefaultAction(pccattribute);
	var pccRuleAttributeActionOptions='';
	for(var i=0; i<possibleActions.length; i++){
		var pccPossibleActionsObj = possibleActions[i];
		if(defaultPccAction == pccPossibleActionsObj[0]){
			pccRuleAttributeActionOptions += "<option value='"+pccPossibleActionsObj[1]+"' SELECTED='SELECTED'>"+pccPossibleActionsObj[0]+"</option>";
		}else{
			pccRuleAttributeActionOptions += "<option value='"+pccPossibleActionsObj[1]+"'>"+pccPossibleActionsObj[0]+"</option>";
		}
	}
	$("#pccaction"+index).html(pccRuleAttributeActionOptions);
	
}

function getSuggestions(obj) {
	var id = $(obj).attr("id");
	$("#"+id).autocomplete();
	var advanceConditionsAutoCompleter = createModel(<%=advanceConditions%>);
	expresssionAutoComplete(id,advanceConditionsAutoCompleter);
	
}
var i=$("#imsPkgServiceDataListSize").val();
function addPCCAttributes(){
	var tableRow= "<tr name='PCCAttributeRow'>"+
	              "<td width='20%'><select name='imsPkgPCCAttributeDatas["+i+"].attribute' id='pccattribute"+i+"' class='form-control' onchange='setDefaultAndPossibleValues("+i+")' tabindex='6'>"+pccAttributeOptions+"</select></td>"+
	              "<td width='30%'><textarea name='imsPkgPCCAttributeDatas["+i+"].expression' class='form-control' rows='1' id='expressionPcc"+i+"' onfocus='getSuggestions(this)' tabindex='7'></textarea></td>"+
	              "<td width='20%'><select name='imsPkgPCCAttributeDatas["+i+"].action' class='form-control' id='pccaction"+i+"' tabindex='8'></select></td>"+
	              "<td width='19%'><input class='form-control' name='imsPkgPCCAttributeDatas["+i+"].value' type='text' id='pccvalue"+i+"' onfocus='setPossibleValues("+i+")' tabindex='9'></td>"+
	              "<td width='1%'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
	              "</tr>";
	$(tableRow).appendTo('#PCCAttributesTable');
	setDefaultAndPossibleValues(i);
	i++;
}
function checkAction(){
	var action = $("#action option:selected").text();
	if(action == '<%= IMSServiceAction.REJECT.getName()%>'){
		$("#pccattributes *").attr('readonly',true);
		$("#addRow").attr('disabled','disabled');
	}else{
		$("#pccattributes *").removeAttr('readonly');
		$("#addRow").removeAttr('disabled');
	}
}

function validateForm(){
	return verifyUniquenessOnSubmit('imspkgServiceName','update','<s:text name="%{imsPkgServiceData.id}" />','com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData','<s:text name="%{imsPkgServiceData.imsPkgData.id}" />','');
}
</script>

</html>