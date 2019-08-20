<%@ include file="/jsp/core/includes/common/Header.jsp" %>


<%
	String checkItem = (String) request.getAttribute("checkItem");
	if(checkItem==null)
	{
		checkItem = "";
	}
 %>
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<html:form action="/viewRadiusPolicyCheckItem" >

					<html:hidden name="viewRadiusPolicyCheckItemForm" styleId="radiusPolicyId" property="radiusPolicyId" />					
					<tr> 
						<td class="tblheader-bold" colspan="3" ><bean:message bundle="radiusResources" key="radiuspolicy.viewcheckitems" /></td>
					</tr>
					<tr><td colspan="3">&nbsp;</td></tr>
					<tr>	
						<td colspan="3">
							<table width="100%"  align="center" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="left" class="labeltext" valign="top">
										<bean:message bundle="radiusResources" key="radiuspolicy.checkitems.expression" />
									</td>
									<td>
										<textarea rows="4" cols="60" name="checkItem" id ="checkItem"  readonly="readonly"><%=checkItem.trim()%></textarea>
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
