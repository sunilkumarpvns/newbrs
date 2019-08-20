<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.accessgroup.forms.UpdateAccessGroupForm"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData"%>
<%@page import="com.elitecore.corenetvertex.sm.acl.GroupData"%>
<%@page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Iterator"%>


<%
			String localBasePath = request.getContextPath();
            List listAccessGroupList = (List) request.getAttribute("listAccessGroupList");
            UpdateAccessGroupForm updateAccessGroupForm = (UpdateAccessGroupForm) request.getAttribute("updateAccessGroupForm");
%>

<script type="text/javascript" src="<%=localBasePath%>/js/dualList.js">
</script>
<script>

function validate(){

	if (isDuplicateGroup()) {
		return;
	}
	if ($('#roleGroupRelation tr').length <= 1) {
		alert('Resource group relation must be specified');
		$("#btnResourceGroupRelation").focus();
		return;
	}
	document.forms[0].action.value = 'update';
	document.forms[0].submit();

}

function isDuplicateGroup(){
	var selectedGroupDatas=[];
	var isDuplicateGroup = false;
	$("input[name='groupData']").each(function(){
		var group = $(this).val();
		if($.inArray(group, selectedGroupDatas) !== -1){
			var groupName = $(this).parent('td').text();
			alert("Duplicate mapping found for "+groupName);
			isDuplicateGroup = true;
			return true;
		}
		selectedGroupDatas.push(group);
	});
	return isDuplicateGroup;
}

var i = 0;
function addRow(tableId){
	$.fx.speeds._default = 1000;
	document.getElementById("roleGroupRelationPopUp").style.visibility = "visible";		
	$( "#roleGroupRelationPopUp" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 300,
		width: 500,				
		buttons:{		
            'Add': function() {	
            	var accessGroup = $("#roles option:selected").text();
            	var accessGroupId = $("#roles option:selected").val();
            	var selectedGroups = document.getElementsByName('select');
            	for(var i=0;i<selectedGroups.length;i++){
            		if(selectedGroups[i].checked == true){
            			var groupId = $(selectedGroups[i]).val();
                    	var groupData = $.trim($(selectedGroups[i]).parent().next('td').text());
            			$("#"+tableId).append("<tr><td class='tblfirstcol' tabindex='1'>"
    	 					+"<input type='hidden'  name='accessGroupData' value="+accessGroupId+" />"+accessGroup+"</td>"
    	 					+"<td class='tblrows' tabindex='2'><input type='hidden' name='groupData' value="+groupId+" />"+groupData+"</td>"
    	 					+"<td class='tblrows' align='center' style='cursor:default' >"
    	 					+"<img tabindex='3' src='<%=basePath%>/images/minus.jpg'  class='delete' height='15' /></td></tr>");
						$('#'+tableId+' td img.delete').on('click',function() {
							$(this).parent().parent().remove();
						});		
        			}
            	}
 				disableSelect();
 				$("#roles").val(""); 
 				$(this).dialog('close');
         	
            },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
			var selectedGroupDatas=[];
			//Currently selected groups
			$("input[name='groupData']").each(function(){
				selectedGroupDatas.push($(this).val());
			});

			//Groups in popup
			$(".groupCheckBox").each(function(){
				var group = $(this).val();
				if($.inArray(group,selectedGroupDatas) !== -1){
					$(this).attr('disabled', 'true');
					$(this).parent().next().children(0).css("color","gray");
				} else {
					$(this).removeAttr('disabled');
					$(this).parent().next().children(0).css("color","black");
				}
			});
    	},
    	close: function() {
    		$(this).dialog('close');
    	}				
	});
	$( "#roleGroupRelationPopUp" ).dialog("open");
	
}

function  checkAll(){
	var counter=0;
	var selectAllData = document.getElementById('toggleAll');
 	if( selectAllData.checked == true) {
 		var selectVars = document.getElementsByName('select');
	 	for (var i = 0; i < selectVars.length;i++){
	 		if(selectVars[i].disabled == false){
				selectVars[i].checked = true ;
				counter++;
	 		}
	 	}
    } else if (selectAllData.checked == false){
 		var selectVars = document.getElementsByName('select');	    
		for (var i = 0; i < selectVars.length; i++)
			if(selectVars[i].disabled == false){
				selectVars[i].checked = false ;
				counter++;
	 		}
	}
}

function disableSelect(){
	var selectAllData = document.getElementById('toggleAll');
 	if( selectAllData.checked == true) {
 		selectAllData.checked = false;
 	}
 	var selectedGroups = document.getElementsByName('select');
	for (var i = 0; i < selectedGroups.length;i++){
	 	if(selectedGroups[i].checked == true){
	 		selectedGroups[i].checked = false ;
	 	}
	 }
    
}

</script>

<html:form action="/updateAccessGroup">
	<html:hidden name="updateAccessGroupForm" styleId="action" 	property="action" />
	<html:hidden name="updateAccessGroupForm" styleId="staffId" property="staffId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right" colspan="3">
				<table cellpadding="0" cellspacing="0" border="0" width="97%" height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="10">
							<bean:message key="staff.assignaccessgroup" />
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="10">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td>
							<table width="100%" align="center">
								<tr> 
								        <td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold" >
								        <input type="button" id="btnResourceGroupRelation" name="add" value="Add Resource-Group" class="light-btn" onclick="addRow('roleGroupRelation')"></td>
								</tr>
								<tr>
									<td width="10" class="small-gap">&nbsp;</td>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>

								<tr width="100%">
									<td colspan="4">
										<table cellpadding="0" id="roleGroupRelation" cellspacing="0" border="0"
											width="97%" >
											<thead>
												<tr>
													<td align="center" class="tblheader" valign="top"
														width="45%"><bean:message key="staff.role" /></td>
													<td align="center" class="tblheader" valign="top"
														width="50%"><bean:message key="staff.group" /></td>
													<td align="center" class="tblheader" valign="top"
														width="5%"><bean:message key="staff.remove" /></td>
												</tr>
											</thead>
											<tbody>
											<%
												if(Collectionz.isNullOrEmpty(updateAccessGroupForm.getStaffGroupRoleRelDatas()) == false) {
												
											%>
											<logic:iterate id="staffGroupRoleRelationData" name="updateAccessGroupForm" property="staffGroupRoleRelDatas" type="com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData">
											<tr>
															<td align="left" class="tblfirstcol" valign="top">
																<input type='hidden' name='accessGroupData' value='<bean:write name="staffGroupRoleRelationData" property="roleData.roleId" />' />
																<bean:write name="staffGroupRoleRelationData" property="roleData.name" />
															</td>
															<td align="left" class="tblrows" valign="top">
																<input type='hidden' name='groupData' value='<bean:write name="staffGroupRoleRelationData" property="groupData.id" />' />
																<bean:write name="staffGroupRoleRelationData" property="groupData.name" />
															</td>
															
												<%if(updateAccessGroupForm.getGroupDatas().contains(staffGroupRoleRelationData.getGroupData())){
													%>
															<td class="tblrows" align="center" valign="middle">
													<img tabindex="4" src="<%=basePath%>/images/minus.jpg" style="cursor:pointer" class="delete" height="14" onclick="$(this).parent().parent().remove();"/>
															</td>
												<%} else{%>
												<td class="tblrows" align="center" valign="middle">
													<img tabindex="4" src="<%=basePath%>/images/minus.jpg" style="cursor:not-allowed" class="delete" height="14"/>
												</td>
												<%}%>

											</tr>
												</logic:iterate>
												<%}else{ %>
												<div>No Data Found</div>
												<%} %>
											</tbody>
										</table>
									</td> 
									</tr>	
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle" align="center">
				<input type="button" name="c_btnCreate"
					onclick="return validate();"
					value="Update" class="light-btn">
				<input type="reset" name="c_btnDeletePolicy"
					onclick="javascript:location.href='<%=localBasePath%>/searchStaff.do?/>'"
					value="Cancel" class="light-btn">
			</td>
		</tr>
	</table>
	
	<!-- Pop Up code -->
<div id="roleGroupRelationPopUp" title="Add Group Role Relation" style="display: none;" class="labeltext">
	<table id="roleGroupRelationPopUpTable" width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="30%" class="labeltext"><bean:message key="staff.role" /></td>
			<td class="labeltext">
			<html:select styleId="roles" property="available" tabindex="1" >
				<html:options collection="listAccessGroupList"	property="roleId" labelProperty="name" />
			</html:select>
			</td>
		</tr>
		<tr>	
			<td>&nbsp;</td>
		</tr>
		<tr><td valign="top" width="100%"  class="tblheader-bold" colspan="6"><bean:message key="staff.group" /></td></tr>
		<tr>
								<td align="center" class="labeltext" width="3%" >
									<input type="checkbox" name="toggleAll" id="toggleAll" onclick="checkAll()" tabindex="8"/>
								</td>
								<td align="left" class="labeltext" width="30%">
									<b>Select All</b>
								</td>
							</tr>
							<tr>	
								<td>&nbsp;</td>
							</tr>	
		<tr>
			
			<td class="labeltext">
			<%	
							if(Collectionz.isNullOrEmpty(updateAccessGroupForm.getGroupDatas()) == false) {
								
								int index=0;
								
						%>		
									<logic:iterate id="groupDataBean" name="updateAccessGroupForm" property="groupDatas" type="com.elitecore.corenetvertex.sm.acl.GroupData">
									<%if(index%3==0){ %>	
									<tr>			
									<%}%>						

											<td align="center" valign="top" class="labeltext" width="3%" >
												<input type="checkbox" tabindex="9" class="groupCheckBox" name="select" value="<bean:write name="groupDataBean" property="id"/>"  />
											</td>
											<td align="left" valign="top" class="labeltext" width="30%">
													<label><bean:write name="groupDataBean" property="name" />&nbsp;</label>
											</td>
									<%if(index%3==2){ %>		
									</tr>
									<tr>	
										<td>&nbsp;</td>
									</tr>				
									<%}%>
									<%index++;%>																																																																																																																																																																										
									</logic:iterate>
									<%if(updateAccessGroupForm.getGroupDatas().size()%3!=0){%>
									</tr>
									<%}%>
											
																				
					<%}else{%>
						<tr>
							<td align="center" class="tblfirstcol" colspan="7">No Records Found.</td>
						</tr>
						
					<%}%>
			
			</td>
		</tr>
	</table>
</div>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>



