<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.UpdateCGPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyDriverRelationData"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData"%>

<% 
	CGPolicyData cgPolicyData = (CGPolicyData)request.getAttribute("cgPolicyData");
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="cgPolicyInstDataBean" name="cgPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData" />
    <tr> 
      <td valign="top" align="right"> 
         <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" > 
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            <bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.details"/></td>
          </tr>           
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="cgPolicyInstDataBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.desp" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="cgPolicyInstDataBean" property="description"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.ruleset"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="cgPolicyInstDataBean" property="ruleSet"/></td>
          </tr>         
          <tr> 
            <td class="tblfirstcol" width="30%" >Drivers</td>
            <td class="tblcol" width="70%" >            
            	<%												
					List<CGPolicyDriverRelationData> driverList = cgPolicyData.getDriverList();												
					if(driverList != null){
						for(int i = 0;i<driverList.size();i++){																									
							CGPolicyDriverRelationData data = driverList.get(i);
							String nm = " ";
								nm = data.getDriverData().getName();										
				%>																												
							<%=i+1%> <% if(nm != null){%>
								<span class="view-details-css" onclick="openViewDetails(this,'<%= data.getDriverData().getDriverInstanceId()%>','<%=nm%>','DRIVERS');">
								   	 <%=nm%>
								</span><br/>
							<%}%>
				<%}
			}else{%>
            	No Drivers Configured
            	<%} %>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.script" /></td>
            <td class="tblcol" width="70%" ><bean:write name="cgPolicyInstDataBean" property="script"/>&nbsp;</td>
          </tr>  
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
 </table>
</table>


