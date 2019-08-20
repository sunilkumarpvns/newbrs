 <%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.ViewRadiusPolicyForm" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<%
	String localBasePath = request.getContextPath();
%>
   <bean:define id="radiusPolicyBean" name="radiusPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData" />
    <tr> 
	    <td class="table-header" >
	    	
    		 <logic:equal name="action" value="updateRadiusPolicy" scope="request">
				<bean:message bundle="radiusResources" key="radiuspolicy.update" />
      	    </logic:equal> 
			<logic:notEqual name="action" value="updateRadiusPolicy" scope="request">
				<bean:message bundle="radiusResources" key="radiuspolicy.view" />
      	    </logic:notEqual>
      		
      				
      </td>
    </tr>
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.view.information" /></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="radiusPolicyBean" property="name"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.description" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusPolicyBean" property="description"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.status" /></td>
			<logic:equal name="radiusPolicyBean" property="commonStatusId" value="CST01">
			    <td class="tblcol" width="70%" height="20%"><img src="<%=localBasePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="radiusResources" key="radiuspolicy.active" /></td>
			</logic:equal>
			<logic:equal name="radiusPolicyBean" property="commonStatusId" value="CST02">
			    <td class="tblcol" width="70%" height="20%"><img src="<%=localBasePath%>/images/deactive.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="radiusResources" key="radiuspolicy.inactive" /></td>
			</logic:equal>
          </tr>
          
          <!--  For Update case hide extra details -->
          <logic:notEqual name="action" value="updateRadiusPolicy" scope="request">
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.checkitems.expression" /></td>
			<td class="tblcol" width="70%" height="20%"><bean:write name="radiusPolicyBean" property="checkItem"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.additems.expression" /></td>
			<td class="tblcol" width="70%" height="20%"><bean:write name="radiusPolicyBean" property="addItem"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.rejectitems.expression" /></td>
			<td class="tblcol" width="70%" height="20%"><bean:write name="radiusPolicyBean" property="rejectItem"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radiuspolicy.replyitems.expression" /></td>
			<td class="tblcol" width="70%" height="20%"><bean:write name="radiusPolicyBean" property="replyItem"/>&nbsp;</td>
          </tr>
          
         
         <tr><td colspan="3">&nbsp;</td></tr>
         <tr><td colspan="3" class="tblheader-bold"><bean:message bundle="radiusResources" key="radiuspolicy.timebasecondition" /></td></tr>
         <tr><td colspan="3">&nbsp;</td></tr>
         <tr>
         	<td colspan="3" >	 
				<table width="90%" cellSpacing="0" cellPadding="0" border="0">
					<tr>																
						<td align="center" class="tblheader" valign="top" width="5%" >Sr No.</td>
						<td align="center" class="tblheader" valign="top" width="15%" >Month of Year</td>
		      			<td align="center" class="tblheader" valign="top" width="15%">Day of Month </td>    
		                <td align="center" class="tblheader" valign="top" width="15%">Day of Week</td>
		                <td align="center" class="tblheader" valign="top" width="15%">Time Period </td>
		            </tr>
		            <logic:iterate id="obj" name="radiusPolicyBean" property="radiusPolicyTimePeriodList"  indexId="objIndex" type="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyTimePeriod">
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
		
		 <tr><td colspan="3">&nbsp;</td></tr>	
					
        </table>
		</td>
    </tr>

	     	
          
</table>
