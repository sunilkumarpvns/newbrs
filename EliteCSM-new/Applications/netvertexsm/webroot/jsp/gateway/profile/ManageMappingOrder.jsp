<%@page import="com.elitecore.netvertexsm.web.gateway.profile.form.ManagePacketMapOrderForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="java.util.*" %>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData"%>

<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager,com.elitecore.netvertexsm.datamanager.policymgr.policyrule.data.PolicyRuleData,com.elitecore.netvertexsm.util.constants.*"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>

<%
	List packetMapList = (List)session.getAttribute("packetMapList");
	ManagePacketMapOrderForm packetMapForm = (ManagePacketMapOrderForm)session.getAttribute("packetMapForm");
	String action = packetMapForm.getAction();
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
	setTitle('<bean:message bundle="gatewayResources" key="gateway.profile.summary" />');
	$(".up,.down").click(function(){
		var row = $(this).parents("tr:first");
	    if ($(this).is(".up")) {
	    	row.insertBefore(row.prev());	            
	    }else {
	        row.insertAfter(row.next());	            
	    }
	});
});

</script>

<html:form action="/manageMappingOrder" >

<html:hidden name="manageMappingForm" styleId="action"  property="action" />
			
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
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
				<table id="tblRight2" width="97%" cellpadding="0" cellspacing="0" border="0" >
					<tr>				 
						
						<td width="87%">
							<table id="tblRightheader2" width="100%" cellpadding="0" cellspacing="0" border="0" >								
								<tr>
									<td align="left" class="tblheader" valign="top" width="5%" >Sr.No.</td>
									<td align="left" class="tblheader" valign="top" width="35%">Name</td>								
									<td align="left" class="tblheader" valign="top" width="30%">Condition</td>
									<td align="left" class="tblheader" valign="top" width="25%">Description</td>	
									<td align="left" class="tblheader" valign="top" width="10%">Move</td>	
								</tr>							
							</table>
						</td>																						  								 
					</tr>
				</table>
				<table id="tblRight2" width="97%" cellpadding="0" cellspacing="0" border="0" class="box">
<%		if(packetMapList != null && packetMapList.size() >0){ 					%>
		<tr>
			
			<td width="87%">
			<table id="tblRight1" width="100%" cellpadding="0" cellspacing="0" border="0" >				
				<logic:iterate id="obj" name="manageMappingForm" property="packetMapList" type="GatewayProfilePacketMapData">								
				<tr>
					<td align="left" class="tblrows" width="5%"><%=count%></td>
					<td align="left" class="tblrows" width="35%"><input type="hidden" name="order" value="<bean:write name="obj" property="packetMappingData.packetMapId"/>" /><bean:write name="obj" property="packetMappingData.name"/></td>
					<td align="left" class="tblrows" width="30%"><bean:write name="obj" property="condition"/>&nbsp;</td>
					<td align="left" class="tblrows" width="25%"><%=EliteUtility.formatDescription(obj.getPacketMappingData().getDescription()) %>&nbsp;&nbsp;</td>
					<td width="10%" align="left" class="tblrows">
        				<a href="#" class="up"><img src="<%=basePath%>/images/moveup.JPG" border="0" height="14px"/></a>
        				<a href="#" class="down"><img src="<%=basePath%>/images/movedown.JPG" border="0" height="14px"/></a>            					
    				</td>
    				<%count ++ ;%>																
				</tr>
							
				</logic:iterate>
			</table>
			</td>
		</tr>
<%		}else{				%>
						<tr>
                  			<td align="center" class="tblfirstcol" colspan="8">No Records Found.</td>
                		</tr>
<%		}					%>	
				</table>
				</td>
				
			</tr>	
			<tr>
				<td colspan="5">&nbsp;</td>
				<html:hidden property="profileId" styleId="profileId" name="manageMappingForm" />
			</tr>
			<tr>
				<td class="btns-td" align="left" valign="top"  >
				 	<input type="submit" value=" Save "  class="light-btn" />
				 	<input type="button" value=" Cancel "  class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchProfile.do?/>'"/>
				 	<input type="button" value=" Reset "  class="light-btn" onclick="javascript:location.href='<%=basePath %>/manageMappingOrder.do?profileId=<bean:write name='manageMappingForm' property='profileId'/>'"/>					
				</td>									
			</tr>
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>										
		</table>
		</td>
		</tr>
     <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>

</table> 

</html:form>

