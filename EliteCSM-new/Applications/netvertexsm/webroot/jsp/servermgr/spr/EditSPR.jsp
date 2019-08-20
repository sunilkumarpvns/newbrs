<%@ taglib uri="/WEB-INF/config/tlds/struts-html.tld" prefix="html" %>
<%@page import="com.elitecore.corenetvertex.spr.data.SPRFields"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map.Entry" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.spr.form.SPRForm" %>
<%@ page import="com.elitecore.corenetvertex.sm.acl.GroupInfo" %>
<link href="<%=basePath%>/chosen/css/chosen.css" rel="stylesheet">
<style type="text/css">
	.chzn-results .disabled{color:#CCC;}
</style>
<script src="<%=basePath%>/chosen/js/chosen.jquery.js"></script>
<script src="<%=basePath%>/chosen/js/chosen.order.jquery.js"></script>

<% 
	SPRForm form = (SPRForm)request.getAttribute("sprForm");
%>
<script language = "javascript">

$(document).ready(function(){
	verifyName();
	document.forms[0].sprName.focus();
	<%
		for(String string : form.getGroupNameList()){%>
				$("#groups option").each(function(){
				    var temp = '<%=string%>';
				    if(this.value == temp){
				    	$(this).attr("selected","selected");
				    }
				});
		<%}
	%>


	$(".chosen").children().css('background-color','#FFFACD');
	$(".chosen").chosen();
    var selectedGroup = $($("#groups").get(0));
    selectedGroup.setSelectionOrder("<%= form.getSelectedGroups() %>".split(',') , true);
    $(".chosen").trigger('chosen:updated');;
});

var isValidName;
function validate(){

	var selectedGroup = $($("#groups").get(0));
	var groups = selectedGroup.getSelectionOrder();
	$("input[name='selectedGroups']").val(groups);

	if(isNull(document.forms[0].sprName.value)){
		alert('SPR Name must be specified');
		document.forms[0].sprName.focus();
		return false;
	}else if(!isValidName) {
		alert('Enter Valid SPR Name');
		document.forms[0].sprName.focus();
		return false;
	}else if(document.forms[0].databaseDSId.value == 0){
		alert('DataSource must be specified');
		document.forms[0].databaseDSId.focus();
		return false;
	}else if(isNull(document.forms[0].batchSize.value)){
		alert('Batch Size must be specified');
		document.forms[0].batchSize.focus();
		return false;
	}else if(isEcNaN(document.forms[0].batchSize.value) || document.forms[0].batchSize.value <= 0){
		alert('Batch Size must be Positive Numeric');
		document.forms[0].batchSize.focus();
		return false;
	}else if(document.forms[0].batchSize.value > 2000){
		alert("Batch Size can't be greater than 2000");
		document.forms[0].batchSize.focus();
		return false;
	}else{
		document.forms[0].submit();
	 	return true;
	}
}
function verifyFormat (){
	var searchName = document.getElementById("sprName").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.SPR%>',searchName:searchName,mode:'update',id:'<%=form.getSprId()%>'},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("sprName").value;	
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.SPR%>',searchName:searchName,mode:'update',id:'<%=form.getSprId()%>'},'verifyNameDiv');
}

function getAlternateIdFieldSuggestion(){
	$("#alternateIdField").autocomplete();
	var alternateIdFieldPossibleValues = new Array();
	<%for(SPRFields sprFields : SPRFields.values()){ %>
		alternateIdFieldPossibleValues.push('<%=sprFields.name()%>');
	<%}%>
	commonAutoComplete('alternateIdField',alternateIdFieldPossibleValues);
}
</script>
 
<html:form action="/sprData.do?method=update" onsubmit="return validate();">
	<html:hidden name="sprForm" property="sprId"/>
	<html:hidden name="sprForm" property="selectedGroups" />
	<input type="hidden" name="entityOldGroups" value="${sprForm.selectedGroups}">

	<table cellSpacing="0" cellPadding="0" width="100%" border="0">

   
	<tr> 
		<td colspan="2" align="right"> 
			 	<table cellpadding="0" cellspacing="0" border="0" width="97%">
 				<tr>
					<td class="tblheader-bold"  valign="top" colspan="2"  >	<bean:message  bundle="sprResources" key="spr.update"/> </td>
				</tr>			   
				<tr>
					<td align="left" class="labeltext" valign="top" width="16%">
						<bean:message bundle="sprResources" key="spr.name" />
					</td> 
						<sm:nvNameField size="30" value="${sprForm.sprName}" id="sprName" name="sprName" />
					</tr> 
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="sprResources" key="spr.description" /></td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<html:textarea styleId="description" property="description" cols="40" rows="4" tabindex="2"/></td>							
				</tr>	
				
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="sprResources" key="spr.groups" /></td>
					<td align="left" class="labeltext" valign="top" width="32%">

						<html:select  styleId="groups" property="groups" tabindex="3" styleClass="chosen" multiple="multiple" style="width:340px" >
							<%--<html:optionsCollection name="sprForm" property="groupInfoList" value="id" label="name"/>--%>
							<logic:iterate id="groupInfo" name="sprForm" property="groupInfoList" type="com.elitecore.corenetvertex.sm.acl.GroupInfo">
								<html:option value="${groupInfo.id}"><bean:write name="groupInfo" property="name"/></html:option>
      						</logic:iterate>
						</html:select>
               	</td>
				</tr>					
			 	<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="sprResources" key="spr.datasource" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spr.datasource"/>','<bean:message bundle="sprResources" key="spr.datasource" />')"/>
						</td>
					<td align="left" class="labeltext" valign="top" width="32%">
					<html:select styleId="databaseDSId"  property="databaseDSId" tabindex="4">
							<html:option value="">Select</html:option>
							<html:optionsCollection styleClass="databaseList"  name="sprForm" property="databaseList" label="name" value="databaseId"/>							    	
					</html:select>
					<font color="#FF0000"> *</font>
					</td>
				</tr>	
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="sprResources" key="spr.alternate.id.field" />	
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spr.alternate.id.field"/>','<bean:message bundle="sprResources" key="spr.alternate.id.field" />')"/>					
					</td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<html:text styleId="alternateIdField" property="alternateIdField"  tabindex="5" onfocus="getAlternateIdFieldSuggestion()" maxlength="255" size="30"/>
					</td>							
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="sprResources" key="spr.spinterface" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spr.spinterface"/>','<bean:message bundle="sprResources" key="spr.spinterface" />')"/>
						</td>
					<td align="left" class="labeltext" valign="top" width="32%">
					 <html:select styleId="spInterfaceId"  property="spInterfaceId" tabindex="6">
							<html:option value="0">Select</html:option>
							<html:optionsCollection styleClass="spInterfaceList" name="sprForm" property="spInterfaceList" label="name" value="driverInstanceId" />							    	
					</html:select>
					</td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="sprResources" key="spr.batchsize" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spr.batchsize"/>','<bean:message bundle="sprResources" key="spr.batchsize" />')"/>
					</td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<html:text styleId="batchSize" property="batchSize"  tabindex="7" /></td>							
				</tr>
				<tr>
					<td class="btns-td" valign="middle">&nbsp;</td>
					<td class="btns-td" valign="middle" colspan="2">
						<input type="button" name="c_btnUpdate" value="   Update   " class="light-btn" onclick="return validate();" tabindex="8"> 
						<input type="button" align="left" value=" Cancel " tabindex="8" class="light-btn" onclick="javascript:location.href='<%=basePath%>/sprData.do?method=initSearch'"/>
					</td>
				</tr>				  					 
			   </table>  
			</td>
		  </tr>	 
		 <tr> 
			<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td> 
		  </tr>
           
		</table> 
	  </td> 
	</tr> 
</table> 
</html:form> 
