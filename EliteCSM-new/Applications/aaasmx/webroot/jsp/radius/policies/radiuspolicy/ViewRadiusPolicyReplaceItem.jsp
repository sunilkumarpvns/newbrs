<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.ViewRadiusPolicyReplaceItemForm" %>



			<table cellSpacing="0" cellPadding="0" width="97%" border="0">
				<html:form action="/viewRadiusPolicyReplaceItem" >

					<html:hidden name="viewRadiusPolicyReplaceItemForm" styleId="radiusPolicyId" property="radiusPolicyId" />					
					<tr> 
						<td class="tblheader-bold" colspan="3" ><bean:message bundle="radiusResources" key="radiuspolicy.viewreplaceitems" /></td>
					</tr>
					<tr>	
						<td colspan="3">
							<table width="100%"  align="center" border="0" cellpadding="0" cellspacing="0">
							<%
								int index = 0;
							%>
									<tr>
									  <td align="left" class="tblheader" valign="top" width="5%" ><bean:message key="general.serialnumber" /></td>
									  <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="radiusResources" key="radiuspolicy.replaceitems.parametername" /></td>
									  <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="radiusResources" key="radiuspolicy.replaceitems.operator" /></td>
									  <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="radiusResources" key="radiuspolicy.replaceitems.parametervalue" /></td>
	  								  <td align="left" class="tblheader" valign="top" width="10%" ><bean:message bundle="radiusResources" key="radiuspolicy.replaceitems.status" /></td>
									</tr>
							
						    	<logic:iterate id="replaceItemBean" name="viewRadiusPolicyReplaceItemForm" property="replaceItems" type="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyParamDetailData" >
   							<bean:define id="opeartorBean" name="replaceItemBean" property="radiusOperator" type="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IOperatorData" />					    							    	
									<tr>
									  <td align="left" class="tblfirstcol"><%=(index+1)%></td>
									  <td align="left" class="tblrows"><bean:write name="replaceItemBean" property="dictionaryParameterName"/></td>
								  <td align="left" class="tblrows"><bean:write name="opeartorBean" property="operatorName"/></td>
									  <td align="left" class="tblrows"><bean:write name="replaceItemBean" property="displayValue"/></td>
	
									  <logic:equal name="replaceItemBean" property="status" value="Y">
									    <td align="center" class="tblrows"><img src="<%=basePath%>/images/active.jpg"/></td>
									  </logic:equal>
									  <logic:notEqual name="replaceItemBean" property="status" value="Y">                                                          
									    <td align="center" class="tblrows"><img src="<%=basePath%>/images/deactive.jpg"/></td>
									  </logic:notEqual>
									</tr>
							<% index++; %>
	                        	</logic:iterate>
							</table>
						</td>
					</tr>
					<tr> 
						<td align="left" class="labeltext" valign="top" colspan="3" >&nbsp;</td>
					</tr>	
					
		</html:form>
      </table>
