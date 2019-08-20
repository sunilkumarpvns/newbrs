<%@page import="com.elitecore.corenetvertex.pkg.constants.ACLAction"%>
<%@page import="org.codehaus.stax2.validation.Validatable"%>
<%@page import="java.util.Set"%>
<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.google.gson.JsonElement"%>
<%@page import="com.google.gson.JsonObject"%>
<%@page import="com.elitecore.corenetvertex.pkg.constants.ACLModules"%>
<%@page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.accessgroup.forms.CreateAccessGroupForm"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Iterator"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<%
	List lstBusinessModelData = ((CreateAccessGroupForm)request.getAttribute("createAccessGroupForm")).getListBusinessModel();
	String moduleWiseAction = (String)request.getAttribute("jsonData");
	Map<String,String> mapForTreeNodeJson = (Map) ((CreateAccessGroupForm)request.getAttribute("createAccessGroupForm")).getActionJsonRelationMap();
	HashMap profileMap = (HashMap)request.getAttribute("profileMap");
	int iIndex = 0;
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
	var TempTree1 = new Array();
	var strCheckedIDs = "";
	var initflag = true;
	var strRootID = "";
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
	
	HashMap modelMap = (HashMap)profileMap.get("modelMap");
	for(Iterator w=modelMap.keySet().iterator();w.hasNext();){
		String modelId = (String)w.next();
		HashMap modelObjectMap = (HashMap)modelMap.get(modelId);
		String modelStatus = (String) modelObjectMap.get("businessModelStatus");
		HashMap moduleMap = (HashMap)modelObjectMap.get("moduleMap");
		data = new CustomeActionData(modelId,(String)modelObjectMap.get("businessModelName"),modelStatus,"-1");
		actions.add(data);
		for(Iterator w1=moduleMap.keySet().iterator();w1.hasNext(); ){
			String moduleId = (String)w1.next();
			HashMap moduleObjectMap = (HashMap)moduleMap.get(moduleId);
			String moduleStatus = (String)moduleObjectMap.get("businessModuleStatus");
			HashMap subModuleMap = (HashMap)moduleObjectMap.get("subModuleMap");
			data = new CustomeActionData(moduleId,(String)moduleObjectMap.get("businessModuleName"),moduleStatus,modelId);
			actions.add(data);
			for(Iterator w2=subModuleMap.keySet().iterator();w2.hasNext();){
				String subModuleId = (String)w2.next();
				HashMap subModuleObjectMap = (HashMap)subModuleMap.get(subModuleId);
				String subModuleStatus = (String)subModuleObjectMap.get("subBusinessModuleStatus");
				HashMap actionMap = (HashMap)subModuleObjectMap.get("actionMap");
				data = new CustomeActionData(subModuleId,(String)subModuleObjectMap.get("subBusinessModuleName"),subModuleStatus,moduleId);
				actions.add(data);
				for(Iterator w3=actionMap.keySet().iterator();w3.hasNext();){
					String actionId = (String)w3.next();
					HashMap actionObjectMap = (HashMap)actionMap.get(actionId);
					String actionStatus = (String)actionObjectMap.get("actionStatus");
					data = new CustomeActionData(actionId,(String)actionObjectMap.get("actionName"),actionStatus,subModuleId);
					actions.add(data);
				}
			}
		}
	}%>
<%int j=0;
	for(Iterator i = actions.iterator();i.hasNext();){
		CustomeActionData actionData = (CustomeActionData)i.next();%>
	OriginalTree[<%=j%>] = new TreeNode();
	TempTree1[<%=j%>] = new TreeNode();
<%if(actionData.name.equals("Root"))
	{%>
	OriginalTree[<%=j%>].ParentID="-1";
	TempTree1[<%=j%>].ParentID="-1";
	strRootID = "<%=actionData.actionid%>";
<%} else {%>	
	OriginalTree[<%=j%>].ParentID = "<%=actionData.parentactionid%>";
	TempTree1[<%=j%>].ParentID = "<%=actionData.parentactionid%>";
<%}%>
	OriginalTree[<%=j%>].ChildName="<%=actionData.name%>";
	OriginalTree[<%=j%>].ChildID="<%=actionData.actionid%>";
	OriginalTree[<%=j%>].ChildType="";
	OriginalTree[<%=j%>].Root="";
	OriginalTree[<%=j%>].Checked="false";
	<%if(actionData.alias.equals("E") || actionData.alias.equals("X")) {%>
	OriginalTree[<%=j%>].ImageStatus=true;
	<%} else {%>
	OriginalTree[<%=j%>].ImageStatus=false;
	<%}%>
	OriginalTree[<%=j%>].DispStatus=false;
	OriginalTree[<%=j%>].UseChkBox = true;
	TempTree1[<%=j%>].ChildName="<%=actionData.name%>";		
	TempTree1[<%=j%>].ChildID="<%=actionData.actionid%>";
	TempTree1[<%=j%>].ChildType="";
	TempTree1[<%=j%>].Root="";
	TempTree1[<%=j%>].Checked="false";
	TempTree1[<%=j%>].ImageStatus=false;
	TempTree1[<%=j%>].DispStatus = false;
	TempTree1[<%=j%>].UseChkBox = true;
<%j++;
	}%>	
</script>
<script language="javascript1.2">

</script>
<script language="javascript1.2">
$(document).ready(function(){
	setTitle('<bean:message key="accessgroup.accessgroup" />');
	$("#description").attr('maxlength','255');
	 
});
function showAccessRights(childID){
	BOXIDs = "";
	IDLen = 1;
	len =3;
	
	BOXIDs = childID;
	BOXChecked = true;
	
	document.getElementById("showframe").style.display ='';
	document.createAccessGroupForm.chkID.value=BOXIDs;
	document.createAccessGroupForm.IDLength.value=IDLen;
	childFrame.showAdjustment();
	childFrame.setFinalTree();
	
	return true;
}

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
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.ACCESS_GROUP%>',isSpaceAllowed:"yes",searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function validateCreate(){
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
		flag = true;
	}	
	return flag;
}
function setFinalCheckedIDs()
{
	for(var i = 0;i<OriginalTree.length;i++)
	{
		if(OriginalTree[i].Checked == "true")
		{
			strCheckedIDs = strCheckedIDs +OriginalTree[i].ChildID + "$";
		}
	}
	document.forms[0].c_strCheckedIDs.value = strCheckedIDs;
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

<html:form action="/createAccessGroup" onsubmit="return validateCreate();" >
	<html:hidden name="createAccessGroupForm" styleId="IDLength"
		property="IDLength" />
	<html:hidden name="createAccessGroupForm" styleId="chkID"
		property="chkID" />
	<html:hidden name="createAccessGroupForm" styleId="businessModelId"
		property="businessModelId" />
	<html:hidden name="createAccessGroupForm" styleId="action"
		property="action" />
	<html:hidden name="createAccessGroupForm" styleId="c_strCheckedIDs"
		property="c_strCheckedIDs" />
	<html:hidden name="createAccessGroupForm" styleId="jsonDataArray"
	property="jsonDataArray" />
	<input type="hidden" name="mode" value="CREATE" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">&nbsp;</td>
			<td width="100%" colspan="2" valign="top" class="box">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<tr>
					<td class="table-header" colspan="5"><bean:message
						key="accessgroup.createaccessgroup" /><%-- <img src="<%=basePath%>/images/open.jpg" border="0" name="closeopen"> --%></td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3">
					<table width="97%" name="c_tblCrossProductList"
						id="c_tblCrossProductList" align="right" border="0">
						<tr>
							<td align="left" class="labeltext" valign="top" width="5%"><bean:message
								key="accessgroup.name" /></td>
							<sm:nvNameField maxLength="60" size="30"/>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="5%"><bean:message
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
										key="general.serialnumber" />&nbsp;&nbsp;</td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
										key="accessgroup.businessmodel" /></td>
									<td align="center" class="tblheader" valign="top" width="25%"><bean:message
										key="accessgroup.edit" /></td>
								</tr>
								<%
    if(lstBusinessModelData != null && lstBusinessModelData.size() > 0){                    
%>
								<logic:iterate id="businessModelData"
									name="createAccessGroupForm" property="listBusinessModel"
									type="com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData">
									<tr>
										<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %>&nbsp;&nbsp;</td>
										<td align="left" class="tblrows" valign="top">&nbsp;&nbsp;<bean:write
											name="businessModelData" property="name" /></td>
										<td align="center" class="tblrows"><a tabindex="3"
											href="<%= "javascript:showAccessRights('"+businessModelData.getBusinessModelId()+"');childFrame.drawTree();"%>"><img
											src="<%=basePath%>/images/edit.jpg" alt="Access rights" name="Image52"
											border=0 id="Image5"
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
							<table  class="box" cellpadding="0" cellspacing="0" height="350" width="100%" >
								<tr height="1">
									<td align="left" class="tblheader" valign="top" width="1%"><bean:message
										key="configurationprofile.module" /></td>
								</tr>
								<tr>
									<td class="box" valign="top">
									<div id="showframe" style="display: none">
									<table cellpadding="0" cellspacing="0" width="100%" border="0">
										<tr>
											<td><iframe
												src="<%=basePath%>/jsp/core/system/profilemanagement/DynamicTree.jsp"
												name="childFrame" frameborder="0" scrolling="yes"
												height="320"  width="100%"> </iframe></td>
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
								<logic:iterate id="aclModules" name="createAccessGroupForm" property="actionJsonRelationMap" type="java.util.Map.Entry">
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
									<td class="box" valign="top">
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
						<html:submit styleClass="light-btn" value="Create"  tabindex="4" />
						<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/listAccessGroup.do?/>'" value="Cancel" class="light-btn" tabindex="5">
					</td>
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
