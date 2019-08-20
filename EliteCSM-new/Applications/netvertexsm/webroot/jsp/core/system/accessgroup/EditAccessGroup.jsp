<%@page import="com.elitecore.corenetvertex.pkg.constants.ACLAction"%>
<%@page import="java.util.StringTokenizer"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.accessgroup.forms.ViewAccessGroupForm"%>
<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.google.gson.JsonElement"%>
<%@page import="com.google.gson.JsonObject"%>
<%@page import="com.elitecore.corenetvertex.pkg.constants.ACLModules"%>
<%@page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleActionRelData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleActionRelData"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
    List lstGroupActionList = (List)request.getAttribute("lstRoleActionList");
	RoleData roleData = (RoleData)request.getAttribute("roleData");
	List lstBusinessModelData = ((ViewAccessGroupForm)request.getAttribute("viewAccessGroupForm")).getListBusinessModel();
	HashMap profileMap = (HashMap)request.getAttribute("profileMap");
	Map<String,String> mapForTreeNodeJson = (Map) ((ViewAccessGroupForm)request.getAttribute("viewAccessGroupForm")).getActionJsonRelationMap();
	int iIndex = 0;
	String string = null;
	int index=0;
%>

<style>
.light-btn{
	line-height: normal;
}
BODY {
	background-color: white
}

TD {
	font-size: 12px;
	font-family: arial, verdana, helvetica;
	text-decoration: none;
	white-space: nowrap;
}

A {
	text-decoration: none;
	color: black
}
</style>
<link href="<%=basePath%>/jquery/css/bootstrap/acl/bootstrap.css" rel="stylesheet">
<link href="<%=basePath%>/css/bootstrap-treeview.css" rel="stylesheet">
<script language=javascript src="<%=basePath%>/js/ua.js" type="text/javascript"></script>
<script language=javascript src="<%=basePath%>/js/ftiens4.js" type="text/javascript"></script>
<script language=javascript src="<%=basePath%>/js/TreeNode.js" type="text/javascript"></script>
<script language=javascript" type="text/javascript" src="<%=basePath%>/js/bootstrap-treeview.js"></script>

<script language="javascript">
	var OriginalTree=new Array();
	var TreeNodeArray=new Array();
	var strCheckedIDs = "";
	var initflag = true;
	var strRootID = "";
	var strMode = "EDIT";
	var nodes = new Array();
</script>

<script language="javascript1.2">
<%class CustomeActionData{
		CustomeActionData(String actionid,String name,String alias,String parentactionid){
			this.name = name;
			this.actionid = actionid;
			this.parentactionid = parentactionid;
			this.alias = alias;
		}
		String name = "";
		String actionid = "";
		String parentactionid = "";
		String alias = "";
		
		public String toString(){
			return name+"="+actionid+"="+parentactionid+"="+alias;
		}
	}
	ArrayList actions = new ArrayList();
	CustomeActionData data = new CustomeActionData("1","Root","ROOT","-1");
	actions.add(data);
	ArrayList arr = null;
	
	boolean isDuplicateFound = false;
	String 		   tempId	= null;
	String tempActionName	= null;
	String 	   actionName 	= null;
	String 		 idString 	= "";
	
	HashMap modelMap = (HashMap)profileMap.get("modelMap");
	for(Iterator w=modelMap.keySet().iterator();w.hasNext();){
		String 	       modelId 	= (String) 	w.next();
		HashMap modelObjectMap 	= (HashMap) modelMap.get(modelId);
		String 	   modelStatus	= (String)  modelObjectMap.get("businessModelStatus");
		HashMap      moduleMap	= (HashMap) modelObjectMap.get("moduleMap");
						  data	= 	new CustomeActionData(modelId,(String)modelObjectMap.get("businessModelName"),modelStatus,"-1");
		actions.add(data);
		for(Iterator w1=moduleMap.keySet().iterator();w1.hasNext(); ){
			String 		   moduleId = (String)	w1.next();
			HashMap moduleObjectMap = (HashMap)	moduleMap.get(moduleId);
			String 	   moduleStatus = (String)	moduleObjectMap.get("businessModuleStatus");
			HashMap    subModuleMap = (HashMap)	moduleObjectMap.get("subModuleMap");
			data = new CustomeActionData(moduleId,(String)moduleObjectMap.get("businessModuleName"),moduleStatus,modelId);			
			actions.add(data);			
			for(Iterator w2=subModuleMap.keySet().iterator();w2.hasNext();){				
				String 		   subModuleId	= (String)	w2.next();
				HashMap subModuleObjectMap 	= (HashMap)	subModuleMap.get(subModuleId);
				String 	   subModuleStatus	= (String)	subModuleObjectMap.get("subBusinessModuleStatus");
				HashMap 		 actionMap	= (HashMap)	subModuleObjectMap.get("actionMap");
				data = new CustomeActionData(subModuleId,(String)subModuleObjectMap.get("subBusinessModuleName"),subModuleStatus,moduleId);
				actions.add(data);				
				for(Iterator w3=actionMap.keySet().iterator();w3.hasNext();){					
					String 	       actionId	= (String)	w3.next();
					HashMap actionObjectMap	= (HashMap)	actionMap.get(actionId);					
					String 	   actionStatus	= (String)	actionObjectMap.get("actionStatus");
									   data	= new CustomeActionData(actionId,(String)actionObjectMap.get("actionName"),actionStatus,subModuleId);
					actions.add(data);											
				}
			}
		}
	}%>
<%int j=0;
	for(Iterator i = actions.iterator();i.hasNext();){
		CustomeActionData actionData = (CustomeActionData)i.next();%>
	OriginalTree[<%=j%>] = new TreeNode();
//	TempTree1[<%=j%>] = new TreeNode();
<%if(actionData.name.equals("Root"))
	{%>
	OriginalTree[<%=j%>].ParentID="-1";
//	TempTree1[<%=j%>].ParentID="-1";
	strRootID = "<%=actionData.actionid%>";
<%} else {%>	
	OriginalTree[<%=j%>].ParentID = "<%=actionData.parentactionid%>";
//	TempTree1[<%=j%>].ParentID = "<%=actionData.parentactionid%>";
<%}%>
	OriginalTree[<%=j%>].ChildName='<%=actionData.name%>';
	OriginalTree[<%=j%>].ChildID='<%=actionData.actionid%>';
//	alert('Child ID '+<%=j%>+': '+OriginalTree[<%=j%>].ChildID);
	OriginalTree[<%=j%>].ChildType='';
	OriginalTree[<%=j%>].Root='';
	OriginalTree[<%=j%>].Checked='false';
	<%if(actionData.alias.equals("E") || actionData.alias.equals("X")) {%>
	OriginalTree[<%=j%>].ImageStatus=true;
	<%} else {%>
	OriginalTree[<%=j%>].ImageStatus=false;
	<%}%>
	OriginalTree[<%=j%>].DispStatus=false;
	OriginalTree[<%=j%>].UseChkBox = true;
//	TempTree1[<%=j%>].ChildName="<%=actionData.name%>";		
//	TempTree1[<%=j%>].ChildID="<%=actionData.actionid%>";
//	TempTree1[<%=j%>].ChildType="";
//	TempTree1[<%=j%>].Root="";
//	TempTree1[<%=j%>].Checked="false";
//	TempTree1[<%=j%>].ImageStatus=false;
//	TempTree1[<%=j%>].DispStatus = false;
//	TempTree1[<%=j%>].UseChkBox = true;
<%j++;
	}//Class ends Here%>	

<%for(int k=0;k<lstGroupActionList.size();k++)
	{
		RoleActionRelData groupActionRelData = (RoleActionRelData)lstGroupActionList.get(k);%>

		for(var z=0;z<OriginalTree.length;z++)
		{
//			alert('value of the childid is '+OriginalTree[z].ChildID);
//			alert('%%%%%%% value of actionid is %%%%%%%%%<%=groupActionRelData.getActionId()%>');
			if(OriginalTree[z].ChildID == '<%= groupActionRelData.getActionId()%>')
			{
				OriginalTree[z].Checked = "true";
//				OriginalTree[z].Checked = "false";
				break;
			}
		}
<%
	}
%>  

function setFinalCheckedIDs()
{
//	alert('here the total size is :'+OriginalTree.length);
	for(var i = 0;i<OriginalTree.length;i++)
	{
		if(OriginalTree[i].Checked == "true")
		{
			strCheckedIDs = strCheckedIDs +OriginalTree[i].ChildID + "$";
		}
	}
//	alert('In final cheked id method '+document.forms[0].c_strCheckedIDs.value);
	document.forms[0].c_strCheckedIDs.value = strCheckedIDs;
//	alert('~~~~~~~~~~~ Value of the strCheckdID value is '+document.fopaeserms[0].c_strCheckedIDs.value);
}
</script>
<script language="javascript1.2">
$(document).ready(function(){
	setTitle('<bean:message key="accessgroup.accessgroup" />');
	$("#description").attr('maxlength','255');
	displayModules();
	setNodeJsonArray();
	
});
function setNodeJsonArray(){
	<%
		String str = ((ViewAccessGroupForm)request.getAttribute("viewAccessGroupForm")).getJsonDataArray();
		StringTokenizer st = new StringTokenizer(str,"|");
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			%>
			var json = JSON.parse('<%=token%>');
			for(key in json){
				nodes.push({"name" : key,"value": json[key]});
			}
			<%
		}
	%>
}
function showAccessRights(childID){
	BOXIDs = "";
	IDLen = 1;
//	BOXChecked = false;
	len =3;
	
	BOXIDs = childID;
//	BOXChecked = true;
	
	document.getElementById("showframe").style.display ='';
	document.viewAccessGroupForm.chkID.value=BOXIDs;
	document.viewAccessGroupForm.IDLength.value=IDLen;
//	alert('In show Access Rights chkID'+ document.viewAccessGroupForm.chkID.value);
//	alert('In show Access Rights c_strCheckedIDs'+	document.viewAccessGroupForm.c_strCheckedIDs.value);
	childFrame.showAdjustment();
	childFrame.setFinalTree();
	return true;
}


// Identify whether Update node is selected or not. If Update node is selected and its children are not selected then deselect the update node
function setUpdateCheckBoxProperty(childNodes){
	if(childNodes != undefined){
		for(var i=0;i<childNodes.length;i++){
			if(childNodes[i].text == '<%= ACLAction.UPDATE.getVal() %>'){
				if(childNodes[i].nodes != undefined && childNodes[i].nodes.length > 0){
					var flag = checkForSelectedChildren(childNodes[i].nodes);
					if(flag == false){
						$('#showPolicyFrame').treeview('toggleNodeChecked', [ childNodes[i], { silent: true } ]);
						break;
					}
				}
			}
		}
		
	}
}
//Identify the child nodes for update are checked or not.
function checkForSelectedChildren(nodes){
	var flag = false;
	for(var i=0;i<nodes.length;i++){
		if(nodes[i].state.checked == true){
			flag = true;
			setUpdateCheckBoxProperty(nodes[i].nodes);
		}
	}
	return flag;
}
function selectLeafNode(parentNode,data){
	var flag = false;
	for(var i=0;i<nodes.length;i++){
		var nodeName = nodes[i];
		if(nodeName["name"] == parentNode){
			nodeName["value"] = nodeName["value"]  + data + ",";
			flag = true;
			break;
		}
	
	}
	if(flag == false){
		var strData ="";
		strData = strData + data + ",";
		nodes.push({"name" : parentNode,"value" :  strData});
	}
}

////////////////////////
function selectParent(data){
	var parentNode =  $('#showPolicyFrame').treeview('getParent', data);
	//alert("Parent Data : " + parentNode.text);
	if(parentNode.nodes == undefined){
		return;
	}else{
		selectParent(parentNode);
		parentNode.state.checked = true;
	}
}

function selectChild(data){
//alert("Data : " + data.text);
var nodes = data.nodes;
if(nodes == undefined){
	return;
}else{
	$('#showPolicyFrame').treeview('expandNode', [ data.nodeId, { silent: true } ]);
	for(var i=0;i<nodes.length;i++){
		var node = nodes[i];
		//alert("Node is : " + JSON.stringify(node));
		$('#showPolicyFrame').treeview('checkNode', [ node.nodeId, { silent: true } ]);
		//node.state.expanded = true;
		selectChild(nodes[i]);
		
	}
}
}

function unSelectChild(data){
//alert("Data : " + data.text);
var nodes = data.nodes;
if(nodes == undefined){
	return;
}else{
	$('#showPolicyFrame').treeview('expandNode', [ data.nodeId, { silent: true } ]);
	for(var i=0;i<nodes.length;i++){
		var node = nodes[i];
		//alert("Node is : " + JSON.stringify(node));
		$('#showPolicyFrame').treeview('uncheckNode', [ node.nodeId, { silent: true } ]);
		//node.state.expanded = true;
		unSelectChild(nodes[i]);
		
	}
}
}

function showAccessRightsForPolicy(key){
var value = $("#".concat(key)).val();
console.log("Value: " + value);
$("#showPolicyFrame").treeview({data:value,showCheckbox:true, multiSelect: true}); 
		 $('#showPolicyFrame').on('nodeChecked', function(event, data) {
			 selectParent(data);
			 selectChild(data);
			 var curdata = $('#showPolicyFrame').data('treeview').getNode(0);
			 $("#".concat(key)).val("["  + JSON.stringify(curdata) + "]");
		});
		 $('#showPolicyFrame').on('nodeUnchecked', function(event, data) {	
			 unSelectChild(data);
			 var curdata = $('#showPolicyFrame').data('treeview').getNode(0);
			 $("#".concat(key)).val("["  + JSON.stringify(curdata) + "]");
		});
		 
	
	

$("#btnExpandAll").removeAttr("style","display:none");
$("#btnCollapseAll").removeAttr("style","display:none");
}

function selectLeafNode(parentNode,data){
for(var i=0;i<nodes.length;i++){
	var nodeName = nodes[i];
	if(nodeName["name"] == parentNode){
		nodeName["value"] = nodeName["value"]  + data +",";
		break;
	}

}
}




var isValidName;
function verifyName() {
	var searchName = $.trim(document.getElementById("name").value);
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.ACCESS_GROUP%>',isSpaceAllowed:"yes",searchName:searchName,mode:'update',id:'<%=roleData.getRoleId()%>'},'verifyNameDiv');
}


function validateUpdate(){
	verifyName();
	var flag = false;
	if(isNull(document.forms[0].name.value)){
		alert('Name must be specified');		
		document.forms[0].name.focus();
	}else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();		
	}else{
		childFrame.showAdjustment();
		childFrame.setFinalTree();
		setFinalCheckedIDs();
		setJsonStringFromNode();
		document.forms[0].action.value='update';
		flag = true;		
	}
	return flag;
//	alert('Here press the update button');
}

function setJsonStringFromNode()
{
	var jsonStr = "";
	 for(var i=0;i<nodes.length;i++){
		 if($.trim(jsonStr.length) == 0){
			 jsonStr = JSON.stringify(nodes[i]);
		 }else{
		 	jsonStr = jsonStr +"|"+ JSON.stringify(nodes[i]);
		 }
		 
	 }
	document.forms[0].jsonDataArray.value = jsonStr;
	 
}

function expandAll(){
	$('#showPolicyFrame').treeview('expandAll', { silent: true });
}
function collapseAll(){
	
	$('#showPolicyFrame').treeview('collapseAll', { silent: true });
}

</script>
<%
	//out.println(" id and name :"+string);
//out.println(" duplicateStr :"+duplicateStr);
%>
<html:form action="/editAccessGroup" onsubmit="return validateUpdate();" >
	<html:hidden name="viewAccessGroupForm" styleId="IDLength"
		property="IDLength" />
	<html:hidden name="viewAccessGroupForm" styleId="chkID"
		property="chkID" />
	<html:hidden name="viewAccessGroupForm" styleId="businessModelId"
		property="businessModelId" />
	<html:hidden name="viewAccessGroupForm" styleId="action"
		property="action" />
	<html:hidden name="viewAccessGroupForm" styleId="roleId"
		property="roleId" />
	<html:hidden name="viewAccessGroupForm" styleId="createDate"
		property="createDate" />
	<html:hidden name="viewAccessGroupForm" styleId="createdByStaffId"
		property="createdByStaffId" />
	<html:hidden name="viewAccessGroupForm" styleId="lastModifiedDate"
		property="lastModifiedDate" />
	<html:hidden name="viewAccessGroupForm" styleId="lastModifiedByStaffId"
		property="lastModifiedByStaffId" />
	<html:hidden name="viewAccessGroupForm" styleId="c_strCheckedIDs"
		property="c_strCheckedIDs" />
		<html:hidden name="viewAccessGroupForm" styleId="jsonDataArray"
		property="jsonDataArray" />
	<input type="hidden" name="mode" value="EDIT" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">&nbsp;</td>
			<td width="100%" colspan="2" valign="top" class="box">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<tr>
					<td class="table-header" colspan="5"><bean:message
						key="accessgroup.updateaccessgroup" />
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3">
					<table width="97%" name="c_tblCrossProductList"
						id="c_tblCrossProductList" align="right" border="0">
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%"><bean:message
								key="accessgroup.name" /></td>
							<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="name" property="name" size="30" onblur="verifyName();"
								maxlength="100"  tabindex="1" /><font color="#FF0000"> *</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<div id="verifyNameDiv" class="labeltext"></div>								 
							
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.description" /></td>
							<td align="left" class="labeltext" valign="top" width="32%">
							<html:textarea styleId="description" property="description"
								cols="50" rows="4" styleClass="input-textarea" tabindex="2"></html:textarea>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>
				<tr>
						<td cellpadding="0" class="box" cellspacing="0" border="0"
							style="min-width: 823px" valign="top" align="left">
							<table width="100%">
								<tr>
									<td class="table-header" colspan="3"><bean:message
											key="accessgroup.accessrights" /></td>
								</tr>
								<tr>
									<td class="btns-td" valign="top">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="right" class="tblheader" valign="top" width="1%"><bean:message
														key="general.serialnumber" />&nbsp;</td>
												<td align="left" class="tblheader" valign="top" width="20%">&nbsp;<bean:message
														key="accessgroup.businessmodel" /></td>
												<td align="center" class="tblheader" valign="top"
													width="25%"><bean:message key="accessgroup.edit" /></td>
											</tr>
											<%
    if(lstBusinessModelData != null && lstBusinessModelData.size() > 0){                    
%>
											<logic:iterate id="businessModelData"
												name="viewAccessGroupForm" property="listBusinessModel"
												type="com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData">
												<tr>
													<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %>&nbsp;</td>
													<td align="left" class="tblrows" valign="top">&nbsp;<bean:write
															name="businessModelData" property="name" /></td>
													<td align="center" class="tblrows"><a tabindex="3"
														href="<%= "javascript:showAccessRights('"+businessModelData.getBusinessModelId()+"');childFrame.drawTree();"%>"><img
															src="<%=basePath%>/images/edit.jpg" alt="Access rights"
															name="Image52" border=0 id="Image5"
															onMouseOver="MM_swapImage('Image52','','<%=basePath%>/images/lookup-hover.jpg',1)"
															onMouseOut="MM_swapImgRestore()"> </a></td>
												</tr>
												<% iIndex += 1; %>
											</logic:iterate>
											<%
    } else {
%>
											<tr>
												<td align="center" class="tblfirstcol" colspan="8">No
													Records Found.</td>
											</tr>
											<%
    }
%>
										</table>
									</td>
									<td width="57%" rowspan="3" type="tdnormal-left">
										<table class="box" cellpadding="0" cellspacing="0"
											height="350" width="100%">
											<tr height="1">
												<td align="left" class="tblheader" valign="top" width="1%"><bean:message
														key="configurationprofile.module" /></td>
											</tr>
											<tr>
												<td class="box" valign="top">
													<div id="showframe" style="display: none">
														<table cellpadding="0" cellspacing="0" width="100%"
															border="0">
															<tr>
																<td><iframe
																		src="<%=basePath%>/jsp/core/system/profilemanagement/DynamicTree.jsp"
																		name="childFrame" frameborder="0" scrolling="yes"
																		height="320" width="100%"> </iframe></td>
															</tr>
														</table>
													</div>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
				<!-- <tr>
					<td colspan="3" type="td-smallgap">&nbsp;</td>
				</tr> -->
				
				<!-- adding Policy Designer Related code -->
				<tr>
					
					<td cellpadding="0" class="box" cellspacing="0" border="0"
						style="min-width: 823px" valign="top" align="left">
					<table width="100%">
						<tr>
							<td class="table-header" colspan="3"><bean:message
								key="accessgroup.policydesigner" /></td>
						</tr>
						<tr>
							<td class="btns-td" valign="top">
							<table cellpadding="0" cellspacing="0" border="0" width="100%"  id="moduleAction">
								<tr>
									<td align="right" class="tblheader" valign="top" width="1%"><bean:message
										key="general.serialnumber" />&nbsp;&nbsp;</td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
										key="accessgroup.businessmodel" /></td>
									<td align="center" class="tblheader" valign="top" width="25%"><bean:message
										key="accessgroup.edit" /></td>
								</tr>
							<logic:iterate id="aclModules" name="viewAccessGroupForm" property="actionJsonRelationMap" type="java.util.Map.Entry">
									<tr><td align='right' class='tblfirstcol' valign='top'><%=(index+1) %>&nbsp;&nbsp;</td>
									<td align='left' class='tblrows' valign='top'>&nbsp;&nbsp;<bean:write
													name="aclModules" property="key" /></td>
									<td align='center' class='tblrows'><a tabindex='3' 
									href="javascript:showAccessRightsForPolicy('<bean:write
													name="aclModules" property="key" />')">
									
									<img src='<%=basePath%>/images/edit.jpg' alt='Access rights' name='Image52' border=0 id='Image5'  onMouseOut='MM_swapImgRestore()'> 
									</a>
									
									<input type="hidden" name="<%=String.valueOf(aclModules.getKey())%>" id="<%=String.valueOf(aclModules.getKey())%>" 
									value='<%=aclModules.getValue()%>' />
									</td>
									</tr>
									<% index += 1; %>
								</logic:iterate>	
							</table>
							</td>
							<td width="57%" rowspan="3" type="tdnormal-left">
							<table  class="box" cellpadding="0" cellspacing="0" height="350" width="100%">
								<tr height="1">
									<td align="left" class="tblheader" valign="top" width="1%"><bean:message
										key="configurationprofile.module" /></td>
								</tr>
								<tr> 
								<td><input id="btnExpandAll" type=button value="Expand All" onClick="expandAll()" class="light-btn" style="display:none">
    							<input id="btnCollapseAll" type=button value="Collapse All" onClick="collapseAll()" class="light-btn" style="display:none"></td>
								</tr>
								<tr>
									<td valign="top">
									<div id="showPolicyFrame" width="100%" style="height:320;overflow:scroll;overflow-x:hidden;overflow-y:scroll;">
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr align="center">
					<td class="btns-td" valign="middle">				
					<html:submit styleClass="light-btn" value="Update"  tabindex="4" />
					<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/listAccessGroup.do?/>'" value="Cancel" class="light-btn" tabindex="5"></td>
				</tr>
			</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>	
	</table>
	
		

</html:form>
<script language="javascript1.2">
	function initDrawTree(){
		initflag = false;
	}
</script>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
