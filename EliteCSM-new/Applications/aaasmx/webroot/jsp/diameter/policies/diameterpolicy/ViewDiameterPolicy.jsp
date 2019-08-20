<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="diameterPolicyInstDataBean" name="diameterPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="diameterPolicyInstDataBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.description" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="diameterPolicyInstDataBean" property="description"/>&nbsp;</td>
          </tr>           
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message key="general.status" /></td>
			<logic:equal name="diameterPolicyInstDataBean" property="commonStatusId" value="CST01">
			    <td class="tblcol" width="70%" height="20%" colspan="3"><img src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" /></td>
			</logic:equal>
			<logic:equal name="diameterPolicyInstDataBean" property="commonStatusId" value="CST02">
			    <td class="tblcol" width="70%" height="20%" colspan="3"><img src="<%=basePath%>/images/deactive.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" /></td>
			</logic:equal>
          </tr>        
		</table>
		</td>
    </tr>
    <logic:notEqual name="action" value="updateDiameterPolicy" scope="request">
    <tr>
    	<td colspan="3" class="tblheader-bold">
    		<bean:message bundle="radiusResources" key="radiuspolicy.timebasecondition" />
    	</td>
    </tr>
    <tr>
    	<td colspan="3">&nbsp;</td>
    </tr>
    <tr>
      	<td colspan="3" class="diameter-policy-padding">	 
			<table width="90%" cellSpacing="0" cellPadding="0" border="0">
				<tr>																
					<td align="center" class="tblheader" valign="top" width="5%" >Sr No.</td>
					<td align="center" class="tblheader" valign="top" width="15%" >Month of Year</td>
		      		<td align="center" class="tblheader" valign="top" width="15%">Day of Month </td>    
		            <td align="center" class="tblheader" valign="top" width="15%">Day of Week</td>
		            <td align="center" class="tblheader" valign="top" width="15%">Time Period </td>
		        </tr>
		        <logic:iterate id="obj" name="diameterPolicyInstDataBean" property="diameterPolicyTimePeriodList"  indexId="objIndex" type="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyTimePeriod">
            	<tr>
            		<td class="tblfirstcol" align="center" ><%=objIndex+1 %></td>
            		<td class="tblrows" align="center" ><bean:write name="obj" property="monthOfYear"/> &nbsp;</td>
            	    <td class="tblrows" align="center"><bean:write name="obj" property="dayOfMonth"/> &nbsp;</td>
            		<td class="tblrows" align="center" ><bean:write name="obj" property="dayOfWeek"/>&nbsp;</td>
            		<td class="tblrows" align="center"><bean:write name="obj" property="timePeriod"/>&nbsp; </td> 
            	</tr>
            	</logic:iterate>
		      </table>
		    </td>
	</tr>
	</logic:notEqual>
</table>


