<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.accessgroup.forms.ViewAccessGroupForm"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupActionRelData"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupActionRelData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%                                 
	List lstGroupActionList = (List)request.getAttribute("lstGroupActionList");
	List lstBusinessModelData = ((ViewAccessGroupForm)request.getAttribute("viewAccessGroupForm")).getListBusinessModel();
	HashMap profileMap = (HashMap)request.getAttribute("profileMap");
	int iIndex = 0;
	IGroupData groupData = (IGroupData)request.getSession().getAttribute("groupData");
%>

<style>
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
<script language=javascript src="<%=basePath%>/js/ua.js"
	type="text/javascript"></script>
<script language=javascript src="<%=basePath%>/js/ftiens4.js"
	type="text/javascript"></script>
<script language=javascript src="<%=basePath%>/js/TreeNode.js"
	type="text/javascript"></script>

<script language="javascript">
var isValidName;

	var OriginalTree=new Array();
	var TreeNodeArray=new Array();
//	var TempTree1 = new Array();
	var strCheckedIDs = "";
	var initflag = true;
	var strRootID = "";
	var strMode = "EDIT";

	setTitle('<bean:message key="accessgroup.accessgroup"/>');
</script>

<script language="javascript1.2">
<%	class CustomeActionData{
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
	}
%>
<%
	int j=0;
	for(Iterator i = actions.iterator();i.hasNext();){
		CustomeActionData actionData = (CustomeActionData)i.next();
%>
	OriginalTree[<%=j%>] = new TreeNode();
//	TempTree1[<%=j%>] = new TreeNode();
<%
	if(actionData.name.equals("Root"))
	{
%>
	OriginalTree[<%=j%>].ParentID="-1";
//	TempTree1[<%=j%>].ParentID="-1";
	strRootID = "<%=actionData.actionid%>";
<% 
	} else {
%>	
	OriginalTree[<%=j%>].ParentID = "<%=actionData.parentactionid%>";
//	TempTree1[<%=j%>].ParentID = "<%=actionData.parentactionid%>";
<% } %>
	OriginalTree[<%=j%>].ChildName='<%=actionData.name%>';
	OriginalTree[<%=j%>].ChildID='<%=actionData.actionid%>';
//	alert('Child ID '+<%=j%>+': '+OriginalTree[<%=j%>].ChildID);
	OriginalTree[<%=j%>].ChildType='';
	OriginalTree[<%=j%>].Root='';
	OriginalTree[<%=j%>].Checked='false';
	<%
		if(actionData.alias.equals("E") || actionData.alias.equals("X")) {
	%>
	OriginalTree[<%=j%>].ImageStatus=true;
	<%
		} else {
	%>
	OriginalTree[<%=j%>].ImageStatus=false;
	<%
		}
	%>
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
<%
	j++;
	}//Class ends Here
%>	

<%
	for(int k=0;k<lstGroupActionList.size();k++)
	{
		GroupActionRelData groupActionRelData = (GroupActionRelData)lstGroupActionList.get(k);
%>

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
//	alert('~~~~~~~~~~~ Value of the strCheckdID value is '+document.forms[0].c_strCheckedIDs.value);
}
</script>
<script language="javascript1.2">
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
function validateUpdate(){
//	alert('Here press the update button');
	if(!isValidName) {
 			alert('Enter Valid Name');
 			document.forms[0].name.focus();
 			return;
 	}
	childFrame.showAdjustment();
	childFrame.setFinalTree();
	setFinalCheckedIDs();
	document.forms[0].action.value='update';
	document.forms[0].submit();
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.ACCESS_GROUP%>',searchName,'update','<%=groupData.getGroupId()%>','verifyNameDiv');
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
	<html:hidden name="viewAccessGroupForm" styleId="groupId"
		property="groupId" />
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
	<input type="hidden" name="mode" value="EDIT" />
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="3"><bean:message
											key="accessgroup.updateaccessgroup" />
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="right" border="0"
											cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message key="accessgroup.name" />
													<ec:elitehelp headerBundle="StaffResources" text="accessgroup.name" header="accessgroup.name"/>	
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" onkeyup="verifyName();"
														property="name" size="30" tabindex="1" maxlength="60"
														style="width:250px" /><font color="#FF0000"> *</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources" key="servermgr.description" />
													<ec:elitehelp headerBundle="StaffResources" text="accessgroup.description" header="accessgroup.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:textarea styleId="description" property="description"
														tabindex="2" cols="50" rows="4" style="width:250px"></html:textarea>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>

									<td valign="top" colspan="3">
										<table width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td class="table-header" colspan="3"><bean:message
														key="accessgroup.accessrights" /></td>
											</tr>
											<tr>
												<td class="btns-td" valign="top">
													<table cellpadding="0" cellspacing="0" border="0"
														width="100%">
														<tr>
															<td align="right" class="tblheader" valign="top"
																width="1%"><bean:message key="general.serialnumber" /></td>
															<td align="left" class="tblheader" valign="top"
																width="20%"><bean:message
																	key="accessgroup.businessmodel" /></td>
															<td align="left" class="tblheader" valign="top"
																width="25%"><bean:message key="accessgroup.edit" /></td>
														</tr>
														<%
			    if(lstBusinessModelData != null && lstBusinessModelData.size() > 0){                    
			%>
														<logic:iterate id="businessModelData"
															name="viewAccessGroupForm" property="listBusinessModel"
															type="com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelData">
															<tr>
																<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
																<td align="left" class="tblrows" valign="top"><bean:write
																		name="businessModelData" property="name" /></td>
																<td align="center" class="tblrows"><a
																	href="<%= "javascript:showAccessRights('"+businessModelData.getBusinessModelId()+"');childFrame.drawTree();"%>"><img
																		src="<%=basePath%>/images/edit.jpg"
																		alt="Access rights" name="Image52" border=0
																		id="Image5"
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
															<td align="left" class="tblheader" valign="top"
																width="1%"><bean:message
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
								<tr>
									<td colspan="3" type="td-smallgap">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td class="btns-td" valign="middle" align="left"><input
										tabindex="3" type="button" name="c_btnCreate"
										onclick="validateUpdate()" id="c_btnCreate2" value="Update"
										class="light-btn" tabindex="6"> <input type="reset"
										name="c_btnDeletePolicy" tabindex="4"
										onclick="javascript:location.href='<%=basePath%>/listAccessGroup.do?/>'"
										value="Cancel" class="light-btn" tabindex="7"></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript1.2">
	function initDrawTree(){
		initflag = false;
	}
</script>

