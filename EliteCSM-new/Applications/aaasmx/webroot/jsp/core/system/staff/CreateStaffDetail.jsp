<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>

<%
    String basePath = request.getContextPath();
    List listAccessGroupList = (List)request.getAttribute("listAccessGroupList");
    List listSubscribedGroup = (List)request.getAttribute("listSubscribedGroup");
%>
<link rel='stylesheet' href="<%=basePath%>/css/ui.multiselect.css" type='text/css' media='screen' />
<script type="text/javascript" src="<%=basePath%>/js/dualList.js"></script>
<script type='text/javascript' src="<%=basePath%>/jquery/js/jquery-1.4.2.min.js"></script>
<script type='text/javascript' src="<%=basePath%>/jquery/js/jquery-ui-1.8.5.custom.min.js"></script>
<script type='text/javascript' src="<%=basePath%>/js/ui.multiselect.js"></script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<style type="text/css">
	.add-all{color: white !important;}
	.remove-all{color: white !important;}
</style>

<script> 
	function selectedAllOptions(){
		
		var noOfSelectedRule=parseInt($("#selected option:selected").length);
		if(noOfSelectedRule <= 0){
			alert('Select At least one access group');
			return false;
		}else{
			document.forms[0].submit();
		}
	}

	jQuery(document).ready(function() {
		
		jQuery("#selected").multiselect({sortable: true});
		$('.available').css('width', '245px');
		$('.selected').css('width', '245px');
	
	});

    setTitle('<bean:message bundle="StaffResources" key="staff.staff"/>');
</script>
<html:form action="/createStaffDetail">
	<html:hidden name="createStaffForm" styleId="staffId" property="staffId" />
	<html:hidden name="createStaffForm" styleId="name" property="name" />
	<html:hidden name="createStaffForm" styleId="userName" property="username" />
	<html:hidden name="createStaffForm" styleId="createDate" property="createDate" />
	<html:hidden name="createStaffForm" styleId="password" property="password" />
	<html:hidden name="createStaffForm" styleId="birthDate" property="birthDate" />
	<html:hidden name="createStaffForm" styleId="address1" property="address1" />
	<html:hidden name="createStaffForm" styleId="address2" property="address2" />
	<html:hidden name="createStaffForm" styleId="zip" property="zip" />
	<html:hidden name="createStaffForm" styleId="city" property="city" />
	<html:hidden name="createStaffForm" styleId="status" property="status" />
	<html:hidden name="createStaffForm" styleId="state" property="state" />
	<html:hidden name="createStaffForm" styleId="country" property="country" />
	<html:hidden name="createStaffForm" styleId="emailAddress" property="emailAddress" />
	<html:hidden name="createStaffForm" styleId="mobile" property="mobile" />
	<html:hidden name="createStaffForm" styleId="phone" property="phone" />

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header">
										<bean:message bundle="StaffResources" key="staff.createstaff" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td align="left" class="tblheader-bold" colspan="3">
													<bean:message bundle="StaffResources" key="staff.assignaccessgroup" />
												</td>
											</tr>
											<tr>
												<td>
													<table width="50%" align="center">
														<tr>
														  <td>
													    	<html:select name="createStaffForm"  property="selected" styleId="selected" multiple="multiple" styleClass="multiselect" style="display:none;width: 497px; height: 180px;">
									  							 <logic:iterate id="listAccessGroupBean" name="listAccessGroupList" scope="request"  type="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData">
									     							  <option value="<%= listAccessGroupBean.getGroupId()%>"><%=listAccessGroupBean.getName()%></option>
									  							 </logic:iterate>
															</html:select>
														</td>
														</tr>
														<tr>
															 <td class='small-text-grey'>Note : Drag and Drop to assign Access Group.</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle" align="center">
												<input tabindex="2" type="button" name="c_btnBack" onclick="history.back();" value=" << Back  " class="light-btn"> 
												<input tabindex="3" type="button" name="c_btnCreate" onclick="selectedAllOptions()" id="c_btnCreate2" value="Create" class="light-btn">
												<input tabindex="4" type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchStaff.do?/>'" value="Cancel" class="light-btn"></td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>