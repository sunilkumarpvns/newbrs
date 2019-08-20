<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ParentalControlForm"%>
<%@	page import="com.elitecore.netvertexsm.ws.xsd.SubscriberProfileData"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%
	String basePath = request.getContextPath();
	ParentalControlForm parentalControlForm = (ParentalControlForm) request.getAttribute("parentalControlForm");
	SubscriberProfileData[] subscriberProfileData = null;  
  	if( parentalControlForm != null ){
  		subscriberProfileData = parentalControlForm.getSubscriberProfileData();
  		request.getSession().setAttribute("parentalControlForm",parentalControlForm);
  	}  	  	 
%>
<table width="100%" cellpadding="0">
	<%if(subscriberProfileData!=null && subscriberProfileData.length>0){%>
   <tr>
   		<td  height="20" class="titlebold" width="100%">
   		  &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="parentalcontrol.child.accounts"/>
   		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td align="left" valign="top" >
		<% request.setAttribute("parentalControlForm", parentalControlForm); %>		
		<table width="90%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="tableheader"><bean:message key="general.serialno"/></td>
				<td align="center" class="tableheader"><bean:message key="parentalcontrol.username"/></td>
				<td align="center" class="tableheader"><bean:message key="parentalcontrol.subscriberidentity"/></td>
				<td align="center" class="tableheader"><bean:message key="parentalcontrol.view"/></td>
				<td align="center" class="tableheader"><bean:message key="parentalcontrol.view"/></td>
				<td align="center" class="tableheader"><bean:message key="parentalcontrol.manage"/></td>								
			</tr>						
			<%  int index=1; %>			
			<logic:iterate id="subscriberProfileDataObj" name="parentalControlForm" property="subscriberProfileData" type="SubscriberProfileData">
				<tr>
					<td align="left" class="tblfirstcol"><%=index%></td>
					<td align="left" class="tblrows">&nbsp;&nbsp;<bean:write name="subscriberProfileDataObj" property="userName" /></td>
					<td align="left" class="tblrows">&nbsp;&nbsp;<bean:write name="subscriberProfileDataObj" property="subscriberIdentity" /></td>
					<td align="center" class="tblrows">
						<html:form action="/childAccountInfo">														
							<html:hidden property="subscriberIdentity" value="<%=subscriberProfileDataObj.getSubscriberIdentity()%>" styleId="subscriberIdentity"/>																																			
							<input type="submit" name=View" value="Acc Info" style="margin-top: 4px;margin-bottom:3px">
						</html:form>																																										
					</td>
					<td align="center" class="tblrows">
						<html:form action="/childAccountUsageInfo">
							<html:hidden property="subscriberName" value="<%=subscriberProfileDataObj.getUserName()%>" styleId="subscriberName"/>		
							<html:hidden property="subscriberIdentity" value="<%=subscriberProfileDataObj.getSubscriberIdentity()%>" styleId="subscriberIdentity"/>																																			
							<input type="submit" name="usage"   value="Usage Info" style="margin-top: 4px;margin-bottom:3px">
						</html:form>					
					</td>												
					<td align="center" class="tblrows">
						<html:form action="/childAccountManage">								
							<html:hidden property="subscriberIdentity" value="<%=subscriberProfileDataObj.getSubscriberIdentity()%>" styleId="subscriberIdentity"/>
							<html:hidden property="subscriberName" value="<%=subscriberProfileDataObj.getUserName()%>" styleId="subscriberName"/>
							<input type="submit" name="Manage"   value="Manage" style="margin-top: 4px;margin-bottom:3px">																																										
						</html:form>																																			
					</td>																
				</tr>
					<%index++; %>
			</logic:iterate>
		</table> 
		</td>
	</tr>
	 <%}else{%>
	    <tr>
   			<td  height="20" class="titlebold" width="100%">
   		  		&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="parentalcontrol.child.accounts"/>
   			</td>
		</tr>
	    <tr>
   			<td  height="20" class="tblrows" width="100%">
   		 		 &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="parentalcontrol.norecords"/>
   			</td>
		</tr>
	<%}%>	 
</table>

