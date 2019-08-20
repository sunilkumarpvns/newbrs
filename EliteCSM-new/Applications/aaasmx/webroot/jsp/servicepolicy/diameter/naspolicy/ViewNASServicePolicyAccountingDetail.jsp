<%@page import="com.elitecore.elitesm.util.constants.PolicyPluginConstants"%>
<script language = "javascript">

	$(document).ready(function(){
		  $("#acctToggleImageElement").click(function(){
			  var imgElement = document.getElementById("acctToggleImageElement");
			  if ($("#acctToggleDivElement").is(':hidden')) {
		            imgElement.src="<%=basePath%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
		       }
		      $("#acctToggleDivElement").slideToggle("normal");
		  });
	});
	
	$(document).ready(function(){
		  $("#fieldMapingToggleImageElementAccount").click(function(){
			  var imgElement = document.getElementById("fieldMapingToggleImageElementAccount");
			  if ($("#fieldMapingToggleDivElementAccount").is(':hidden')) {
		            imgElement.src="<%=basePath%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
		       }
		      $("#fieldMapingToggleDivElementAccount").slideToggle("normal");
		  });
	});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.accountingparameters"/>
            </td>
             <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="acctToggleImageElement" src="<%=basePath%>/images/bottom-level.jpg"/>
            </td>
          </tr>
        </table>
        <div id="acctToggleDivElement" style="display: none;">  
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblfirstcol" width="30%" >Drivers</td>
            <td class="tblcol" width="70%" >
            	<%int accountDriverIndex = 0; %>
            	<logic:iterate id="obj" name="nasPolicyInstDataBean" property="nasPolicyAcctDriverRelList" type="NASPolicyAcctDriverRelData">
            		<%=++accountDriverIndex%>. 
            		<logic:notEmpty name="obj" property="driverData.name">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="obj" property="driverData.driverInstanceId"/>','<bean:write name="obj" property="driverData.name"/>','DRIVERS');">
					    	<bean:write name="obj" property="driverData.name"/>
					    </span>
					</logic:notEmpty> -W <bean:write name="obj" property="weightage"/>
            		<BR/>
            	</logic:iterate>
            	<%if(accountDriverIndex==0) {%>
            		No Drivers Configured
            	<%} %>
           </td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" /></td>
            <td class="tblcol" width="70%" ><bean:write name="nasPolicyInstDataBean" property="acctScript" />&nbsp;</td>
          </tr> 
            <tr> 
            <td class="tblheader-bold" colspan="2"><bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /></td>
          </tr>
          <tr>
          	<td align="center" colspan="2" style="padding-top: 10px;padding-bottom: 10px;">
          			<table width="80%" cellspacing="0" cellpadding="0" border="0">
          				<tr>
          					<td class="tblheader-bold" width="50%">
          						Plugin Name
          					</td>
          					<td class="tblheader-bold table-border-right" width="50%">
          						Plugin Argument
          					</td>
          				</tr>
          				<logic:iterate id="obj" name="nasPolicyInstDataBean" property="nasPolicyAcctPluginConfigList">
	          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.IN_PLUGIN%>">
		          				<tr>
		          					<td class="tblfirstcol" width="50%">
		          						<bean:write name="obj" property="pluginName"/>
		          					</td>
		          					<td class="tblrows" width="50%">
		          						<bean:write name="obj" property="pluginArgument"/>
		          					</td>
		          				</tr>
	          				</logic:equal>
          				</logic:iterate>
          			</table>
          	</td>
          </tr>
        
       	  <tr> 
            <td class="tblheader-bold" colspan="2"><bean:message bundle="servicePolicyProperties" key="servicepolicy.postplugins" /></td>
          </tr>
          <tr>
          	<td align="center" colspan="2" style="padding-top: 10px;padding-bottom: 10px;">
          			<table width="80%" cellspacing="0" cellpadding="0" border="0">
          				<tr>
          					<td class="tblheader-bold" width="50%">
          						Plugin Name
          					</td>
          					<td class="tblheader-bold table-border-right" width="50%">
          						Plugin Argument
          					</td>
          				</tr>
          				<logic:iterate id="obj" name="nasPolicyInstDataBean" property="nasPolicyAcctPluginConfigList">
	          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.OUT_PLUGIN%>">
		          				<tr>
		          					<td class="tblfirstcol" width="50%">
		          						<bean:write name="obj" property="pluginName"/>
		          					</td>
		          					<td class="tblrows" width="50%">
		          						<bean:write name="obj" property="pluginArgument"/>
		          					</td>
		          				</tr>
	          				</logic:equal>
          				</logic:iterate>
          			</table>
          	</td>
          </tr>
       </table>
       </div>
     </td>
 	</tr>
 </table>         