<%@page import="com.elitecore.corenetvertex.pkg.constants.ACLModules"%>
<%@page import="java.util.Map"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.accessgroup.forms.ViewAccessGroupForm"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleActionRelData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleActionRelData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
    List lstRoleActionList = (List)request.getAttribute("lstRoleActionList");
                 
	List lstBusinessModelData = ((ViewAccessGroupForm)request.getAttribute("viewAccessGroupForm")).getListBusinessModel();
	Map<String,String> mapForTreeNodeJson = (Map) ((ViewAccessGroupForm)request.getAttribute("viewAccessGroupForm")).getActionJsonRelationMap();
	HashMap profileMap = (HashMap)request.getAttribute("profileMap");
	int iIndex = 0;
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
</script>

<script language="javascript1.2">
$(document).ready(function(){
	setTitle('<bean:message key="accessgroup.accessgroup" />');
	displayModules();
});
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
	OriginalTree[<%=j%>].ImageStatus=false;
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
// Contents Added
<%//	System.out.println("************** here in the file the size is :"+lstRoleActionList.size());
	for(int k=0;k<lstRoleActionList.size();k++)
	{
		RoleActionRelData roleActionRelData = (RoleActionRelData)lstRoleActionList.get(k);%>
		for(var z=0;z<OriginalTree.length;z++)
		{
			
			if(OriginalTree[z].ChildID == '<%=roleActionRelData.getActionId()%>')
			{
//				alert(OriginalTree[z].ChildID);

				OriginalTree[z].ImageStatus = true;
				TempTree1[z].ImageStatus = true;
				OriginalTree[z].Checked="true";
				break;
			}
		}
<%}%>  
//Contents end
function setFinalCheckedIDs()
{
//	alert("In setfinalcheckedids");
	for(var i = 0;i<OriginalTree.length;i++)
	{
		if(OriginalTree[i].Checked == "true")
		{
			
			strCheckedIDs = strCheckedIDs +OriginalTree[i].ChildID + "$";
			//alert(strCheckedIDs);
		}
	}

	document.forms[0].c_strCheckedIDs.value = strCheckedIDs;

}
</script>
<script language="javascript1.2">
function showAccessRights(childID){
	BOXIDs = "";
	IDLen = 1;
	BOXChecked = false;
	len =3;
	
	BOXIDs = childID;
	BOXChecked = true;
	document.getElementById("showframe").style.display ='';
	document.viewAccessGroupForm.chkID.value=BOXIDs;
	document.viewAccessGroupForm.IDLength.value=IDLen;
	return true;
}
function validateUpdate(){
	document.forms[0].submit();
}
var index = 1;
function displayModules(){

	
	<%for(ACLModules constantVal : ACLModules.values()){
		if(constantVal.getParentModule() == null){%>
		var str = '<%=constantVal.getDisplayLabel().toString()%>';
		var strKey = '"<%=constantVal.getDisplayLabel().toString()%>"';
		var key = '<%= constantVal.name()%>';
				$("#moduleAction").append("<tr><td align='right' class='tblfirstcol' valign='top'>"+index+"&nbsp;&nbsp;</td>"+
						"<td align='left' class='tblrows' valign='top'>&nbsp;&nbsp;"+str+"</td>"+
						"<td align='center' class='tblrows'><a tabindex='3' href='javascript:showAccessRightsForPolicy("+strKey+")'>"+
						"<img src='<%=basePath%>/images/view-info.jpg' alt='Access rights' name='Image52' border=0 id='Image5'  onMouseOut='MM_swapImgRestore()'> </a></td>"+
						"</tr>"); 
				index++;
		
		<%}
	}
	%>
	
	
	
}
function showAccessRightsForPolicy(key){
	document.getElementById("showPolicyFrame").style.display ='';
	parseJsonData(key);
	
}
function parseJsonData(key){
	document.getElementById("showPolicyFrame").innerHTML = "";
	
	<%
	for (Map.Entry<String, String> entry : mapForTreeNodeJson.entrySet()) {
	%>
		
		var keyValue = '<%=(String) entry.getKey()%>';
		if(keyValue ==  key){
			var str = '<%= (String)entry.getValue()%>';
			 $("#showPolicyFrame").treeview({data:str}); 
		}
		
	<%}
	%>
	
	 $("#btnExpandAll").removeAttr("style","display:none");
	$("#btnCollapseAll").removeAttr("style","display:none"); 
}
function expandAll(){
	$('#showPolicyFrame').treeview('expandAll', { silent: true });
}
function collapseAll(){
	
	$('#showPolicyFrame').treeview('collapseAll', { silent: true });
}

</script>
<html:form action="/editAccessGroup">
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
	<input type="hidden" name="mode" value="" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-bottom: -20px">
		<bean:define id="roleBean" name="roleData"
			type="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData" />
		
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="7">&nbsp;</td>
			<td valign="top" align="right" class="box" width="100%">
			<table cellspacing="0" cellpadding="0" height="15%" width="100%">
				<tr>
					<td class="table-header" colspan="2"><bean:message
						key="accessgroup.list" /></td>
				</tr>
				<tr>
					<td class="tblrows" width="20%" height="20%"><bean:message
						key="accessgroup.name" /></td>
					<td class="tblcol" width="30%" height="20%"><bean:write
						name="roleBean" property="name" /></td>
				</tr>
				<tr>
					<td class="tblrows" width="20%" height="20%"><bean:message
						key="accessgroup.description" /></td>
					<td class="tblcol" width="30%" height="20%"><bean:write
						name="roleBean" property="description" /></td>
				</tr>
				<tr>
					<td class="tblrows" width="20%" height="20%"><bean:message
						key="general.createddate" /></td>
					<td class="tblcol" width="30%" height="20%">
					<%=EliteUtility.dateToString(roleBean.getCreateDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblrows" width="20%" height="20%"><bean:message
						key="general.lastmodifieddate" /></td>
					<td class="tblcol" width="30%" height="20%">
					<%=EliteUtility.dateToString(roleBean.getLastModifiedDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
					</td>
				</tr>
				
			</table>
		<!-- </td>
		</tr> -->
		<tr>
			<td width="10">&nbsp;</td>
			<td cellpadding="0" class="box" cellspacing="0" border="0"
				style="min-width: 823px" valign="top">
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
								key="general.serialnumber" /></td>
							<td align="left" class="tblheader" valign="top" width="20%"><bean:message
								key="accessgroup.businessmodel" /></td>
							<td align="left" class="tblheader" valign="top" width="25%"><bean:message
								key="accessgroup.edit" /></td>
						</tr>
						<%
    if(lstBusinessModelData != null && lstBusinessModelData.size() > 0){                    
%>
						<logic:iterate id="businessModelData" name="viewAccessGroupForm"
							property="listBusinessModel"
							type="com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData">
							<tr>
								<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
								<td align="left" class="tblrows" valign="top"><bean:write
									name="businessModelData" property="name" /></td>
								<td align="center" class="tblrows"><a
									href="<%= "javascript:showAccessRights('"+businessModelData.getBusinessModelId()+"');childFrame.drawTree();"%>"><img
									src="<%=basePath%>/images/view-info.jpg" alt="Access rights" name="Image52"
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
					<table cellpadding="0" cellspacing="0" class="box" height="350" width="100%">
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
										name="childFrame" frameborder="0" scrolling="auto" height="320"
										width="100%"> </iframe></td>
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
				<!-- adding Policy Designer Related code -->
				 <tr>
					<table width="100%" class="box">
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
									<td  valign="top">
									<div id="showPolicyFrame" width="100%" style="display:none;height:320;overflow:scroll;overflow-x:hidden;overflow-y:scroll;">
									</div>
									</td>
								</tr>
							</table>
						
						</td>
						</tr>
						
					</table>
				 </tr>
				
				<%--			    <tr>
				  <td colspan="3" type="td-smallgap">&nbsp;</td>
			    </tr>  --%>
				<tr align="center">
					<td colspan="3" class="btns-td" valign="middle"><input
						type="button" name="c_btnCreate"
						onclick="javascript:location.href='<%=basePath%>/editAccessGroup.do?roleId=<bean:write name="roleBean" property="roleId"/>'"
						value=" Edit " class="light-btn"> <input type="reset"
						name="c_btnDeletePolicy"
						onclick="javascript:location.href='<%=basePath%>/listAccessGroup.do?>'"
						value="Cancel" class="light-btn"></td>
				</tr>
			</table>
			</td>
		</tr>
	</td></tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
	
	</table>
</html:form>
<script language="javascript1.2">
	function initDrawTree(){
		initflag = false;
	}
</script>
