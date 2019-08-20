<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<%
	String sessionManagerName= (String)request.getAttribute("sessionManagerName"); 
	String viewAdvancedDetails = (String)request.getParameter("viewAdvancedDetails");
	
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="tgppAAAPolicyDataBean" name="tgppAAAPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.name" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="tgppAAAPolicyDataBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.description" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><%=EliteUtility.formatDescription(tgppAAAPolicyDataBean.getDescription()) %>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message key="general.status" /></td>
			<logic:equal name="tgppAAAPolicyDataBean" property="status" value="CST01">
			    <td class="tblcol" width="70%" height="20%" colspan="3"><img src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" /></td>
			</logic:equal>
			<logic:equal name="tgppAAAPolicyDataBean" property="status" value="CST02">
			    <td class="tblcol" width="70%" height="20%" colspan="3"><img src="<%=basePath%>/images/deactive.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" /></td>
			</logic:equal>
          </tr>
        
          <% if( viewAdvancedDetails != null && viewAdvancedDetails.equalsIgnoreCase("true")){ %>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.ruleset" /> </td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="tgppAAAPolicyDataBean" property="ruleset"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.useridentity" />  </td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="tgppAAAPolicyDataBean" property="userIdentity"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.sessionmanagement" />  </td>
            <td class="tblcol" width="70%" height="20%" colspan="3" class="initCapText">
            	<bean:write name="tgppAAAPolicyDataBean" property="sessionManagement"/>&nbsp;</td>

           </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.cui" /> </td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="tgppAAAPolicyDataBean" property="cui"/>&nbsp;</td>
           </tr>
           
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%">  <bean:message bundle="servicePolicyProperties"  key="tgppserviceepolicy.defaultresponsebehavior" /> </td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="tgppAAAPolicyDataBean" property="defaultResponseBehaviour"/></td>
          </tr>
           
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%">  <bean:message bundle="servicePolicyProperties"  key="tgppserviceepolicy.defaultresponsebehaviourargument" /> </td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="tgppAAAPolicyDataBean" property="defaultResponseBehaviorArgument"/></td>
          </tr>
          	
          	<% if("false".equals(request.getAttribute("viewHistory").toString())){ %>
	          	<!-- Command Code wise Response Attribute -->
				<tr>
					<td align="left" class="tblheader-bold" valign="top"  width="10%" colspan="3">
						<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.commandcodewiseresattribute" />
					</td>
				</tr>
				<tr>
					<td style="line-height: 10px">&nbsp;</td>
				</tr>
				<tr>
					<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap" colspan="3">
						<!-- Attributes Table -->
						<table width="76%" cellspacing="0" cellpadding="0" border="0" id="responseAttributeTable" class="responseAttributeTable">
							<tr>
								<td align="left" class="tblheader" valign="top" width="50%">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.commandcode" />
									<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.commandcode" header="servicepolicy.naspolicy.commandcode"/>
								</td>
								<td align="left" class="tblheader" valign="top" width="50%">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattribute" />
									<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.responseattribute" header="servicepolicy.naspolicy.responseattribute"/>
								</td>
							</tr>
							<logic:iterate id="responseAttributes" name="tgppAAAPolicyDataBean" property="commandCodeResponseAttributesList" type="com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.CommandCodeResponseAttribute">
					  			<tr>
									<td class="tblfirstcol">
					         			<bean:write name="responseAttributes" property="commandCodes"/>	&nbsp;
					             	</td>
					         		<td class="tblcol border-right-css">
					         			<bean:write name="responseAttributes" property="responseAttributes"/>	&nbsp;
					         		</td>
					         	</tr>
		  					</logic:iterate>
						</table>
					</td>
				</tr>
				<tr>
					<td style="line-height: 10px">&nbsp;</td>
				</tr>
			 <%} %>
          <%} %>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>