<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm" %>
<%@ page import="java.util.List" %>

<%
	String rejectItem = (String) request.getAttribute("rejectItem");
	if(rejectItem==null)
	{
		rejectItem = "";
	}
	
 %>

			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<html:form action="/viewRadiusPolicyRejectItem" >

					<html:hidden name="viewRadiusPolicyRejectItemForm" styleId="radiusPolicyId" property="radiusPolicyId" />					
					<tr> 
						<td class="tblheader-bold" colspan="3" ><bean:message bundle="radiusResources" key="radiuspolicy.viewrejectitems" /></td>
					</tr>
					<tr><td colspan="3">&nbsp;</td></tr>
					<tr>	
						<td colspan="3">
							<table width="100%"  align="center" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="left" class="labeltext" valign="top">
										<bean:message bundle="radiusResources" key="radiuspolicy.rejectitems.expression" />
									</td>
									<td>
										<textarea rows="4" cols="60" name="rejectItem" id ="rejectItem" readonly="readonly" ><%=rejectItem.trim()%></textarea>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr> 
						<td align="left" class="labeltext" valign="top" colspan="3" >&nbsp;</td>
					</tr>	
					
		</html:form>
      </table>
