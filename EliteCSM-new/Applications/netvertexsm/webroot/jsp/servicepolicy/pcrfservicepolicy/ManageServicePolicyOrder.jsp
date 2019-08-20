<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.ManageServicePolicyOrderForm"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager,com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.*,com.elitecore.netvertexsm.util.constants.*"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>
<%
	List servicePolicyList = (List)session.getAttribute("policyList");
	ManageServicePolicyOrderForm policyForm = (ManageServicePolicyOrderForm)session.getAttribute("policyForm");
	String action = policyForm.getAction();
%>

<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>

<script language = "javascript">

$(document).ready(function(){
	$('#managepolicyorder').tableDnD();
});

</script>

<html:form action="/manageOrderPolicy" >

<html:hidden name="manageServicePolicyForm" styleId="action"  property="action" />
			
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			Manage Order</td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 			
			<tr>
			<%int count = 1; %>
	
				<td width="100%" align="center" valign="middle">
				<table width="97%" cellpadding="0" cellspacing="0" border="0" >
					<tr>				 
						<td width="87%">
							<table id="managepolicyorder" width="100%" cellpadding="0" cellspacing="0" border="0" >
							   <thead>								
								<tr style='cursor:default'>
									<td align="left" class="tblheader" valign="top" width="5%" >Sr.No.</td>
									<td align="left" class="tblheader" valign="top" width="35%">Name</td>								
									<td align="left" class="tblheader" valign="top" width="30%">RuleSet</td>
									<td align="left" class="tblheader" valign="top" width="25%">Description</td>	
								</tr>
								</thead>
		                     <%if(servicePolicyList != null && servicePolicyList.size() >0){%>
								<logic:iterate id="obj" name="manageServicePolicyForm" property="policyList" type="PCRFServicePolicyData">								
								<tr>
										<td align="left" class="tblfirstcol" width="5%"><%=count%></td>
										<td align="left" class="tblrows" width="35%">
										  <input type="hidden" name="order" value="<bean:write name="obj" property="name"/>"/>
										     <bean:write name="obj" property="name"/>
										 </td>
										<td align="left" class="tblrows" width="30%"><bean:write name="obj" property="ruleset"/>&nbsp;</td>
										<td align="left" class="tblrows" width="25%"><%=EliteUtility.formatDescription(obj.getDescription()) %>&nbsp;&nbsp;</td>
										<%count ++ ;%>																
								</tr>
								</logic:iterate>
                        <%}else{%>
						<tr>
                  			<td align="center" class="tblfirstcol" colspan="4" >No Records Found.</td>
                		</tr>
                      <%}%>	
		     		</table>
				  </td>
				</tr>	
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>
			<tr>
				<td class="btns-td" align="left" valign="top"  >
				 	<input type="submit" value=" Save "  class="light-btn" />
				 	<input type="button" value=" Cancel "  class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchPCRFService.do?/>'"/>
				 	<input type="button" value=" Reset "  class="light-btn" onclick="javascript:location.href='<%=basePath %>/manageOrderPolicy.do?/>'"/>					
				</td>									
			</tr>
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>										
		</table>
		</td>
	</tr>
</table>
</td>
</tr>
 		<%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form>

