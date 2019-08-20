<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm" %>
<%@ page import="java.util.List" %>

<%
	String replyItem = (String) request.getAttribute("replyItem");
	if(replyItem==null)
	{
		replyItem = "";
	}
 %>
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<html:form action="/viewRadiusPolicyReplyItem" >

					<html:hidden name="viewRadiusPolicyReplyItemForm" styleId="radiusPolicyId" property="radiusPolicyId" />					
					<tr> 
						<td class="tblheader-bold" colspan="3" ><bean:message bundle="radiusResources" key="radiuspolicy.viewreplyitems" /></td>
					</tr>
					<tr><td colspan="3">&nbsp;</td></tr>
					<tr>	
						<td colspan="3">
							<table width="100%"  align="center" border="0" cellpadding="0" cellspacing="0" border="1">
								<tr>
									<td align="left" class="labeltext" valign="top">
										<bean:message bundle="radiusResources" key="radiuspolicy.replyitems.expression" />
									</td>
									<td>
										<textarea rows="4" cols="60" name="replyItem" id ="replyItem" readonly="readonly"><%=replyItem.trim()%></textarea>
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
