
 <script language = "javascript">

	$(document).ready(function(){
		  $("#authenticationToggleImageElement").click(function(){
			  var imgElement = document.getElementById("authenticationToggleImageElement");
			  if ($("#authenticationToggleDivElement").is(':hidden')) {
		            imgElement.src="<%=basePath%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
		       }
		      $("#authenticationToggleDivElement").slideToggle("normal");
		  });
	
		  $("#fieldMapingToggleImageElementAuth").click(function(){
			  var imgElement = document.getElementById("fieldMapingToggleImageElementAuth");
			  if ($("#fieldMapingToggleDivElementAuth").is(':hidden')) {
		            imgElement.src="<%=basePath%>/images/top-level.jpg";
		       } else {
		            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
		       }
		      $("#fieldMapingToggleDivElementAuth").slideToggle("normal");
		  });
		 
	});
</script>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
    
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.authenticationdetails"/>
            </td>
            <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="authenticationToggleImageElement" src="<%=basePath%>/images/top-level.jpg"/>
           </td>
          </tr>
        </table> 
        <div id="authenticationToggleDivElement" >
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >  
         <tr>
            <td class="tblfirstcol" width="30%"  align="left" valign="top">
            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.authmethods" />
            </td>
             
             <td class="tblcol" width="70%">
                 <logic:iterate id="obj" name="nasPolicyInstDataBean" type="NASPolicyAuthMethodRelData" property="nasPolicyAuthMethodRelList">
                 	<logic:equal value="1" name="obj" property="authMethodTypeId">
                 		PAP
                 	</logic:equal>
                 	<logic:equal value="2" name="obj" property="authMethodTypeId">
                 		CHAP
                 	</logic:equal>
                             
                 </logic:iterate>                 
                 &nbsp;
              </td>  
         </tr>
        
         <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties"  key="servicepolicy.naspolicy.casesensitiveuid" /></td>
            <td class="tblcol" width="70%" >
            <logic:equal value="1" name="nasPolicyInstDataBean" property="caseSensitiveUserIdentity">
            	No Change
            </logic:equal>
            <logic:equal value="2" name="nasPolicyInstDataBean" property="caseSensitiveUserIdentity">
            	Lower Case
            </logic:equal>
            <logic:equal value="3" name="nasPolicyInstDataBean" property="caseSensitiveUserIdentity">
            	Upper Case
            </logic:equal>
            &nbsp;
            </td>
          </tr>
          
           <tr>
         	<td class="tblfirstcol" width="30%" valign="top"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.acctpolicy.multipleuid" /></td>
         	<td  width="70%" >
         		<table width="100%" border="0" cellspacing="0" cellpadding="0" >
         		<tr>
         			<td class="tblfirstcol" width = 30% >
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.stripuid" />
         				:
         				<bean:write name="nasPolicyInstDataBean" property="stripUserIdentity"/>&nbsp;
         			</td>
         			<td class="tblfirstcol" width=30% >
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.realmseparator" />
         				:
         				<bean:write name="nasPolicyInstDataBean" property="realmSeparator"/>&nbsp;
         			</td>
         			<td class="tblfirstcol" width=30%>
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.realmpattern" />
         				:
         				<bean:write name="nasPolicyInstDataBean" property="realmPattern"/>&nbsp;
         			</td>
         		</tr>
         		<tr>
         			<td class="tblfirstcol"  width=30%>
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.trimuid" />
         				:
         				<bean:write name="nasPolicyInstDataBean" property="trimUserIdentity"/>&nbsp;
         			</td>
         			<td class="tblfirstcol" colspan="3" width=30%>
         				<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.trimpassword" />
         				:
         				<bean:write name="nasPolicyInstDataBean" property="trimPassword"/>&nbsp;
         			</td>
         			
         		</tr>
         		</table>
         		
         	</td>
         </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.username" /></td>
            <td class="tblcol" width="70%" ><bean:write name="nasPolicyInstDataBean" property="userName"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.usernameresattrs" /></td>
            <td class="tblcol" width="70%" ><bean:write name="nasPolicyInstDataBean" property="userNameResonseAttributes"/>&nbsp;</td>
          </tr> 
          <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.anonymousidentity" /> </td>
            <td class="tblcol" width="70%" ><bean:write name="nasPolicyInstDataBean" property="anonymousProfileIdentity"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" >Drivers</td>
            <td class="tblcol" width="70%" >
            	<%int authDriverIndex = 0; %>
            	<logic:iterate id="obj" name="nasPolicyInstDataBean" property="nasPolicyAuthDriverRelList" type="NASPolicyAuthDriverRelData">
            		<%=++authDriverIndex%>. 
            		<logic:notEmpty name="obj" property="driverData.name">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="obj" property="driverData.driverInstanceId"/>','<bean:write name="obj" property="driverData.name"/>','DRIVERS');">
					    	<bean:write name="obj" property="driverData.name"/>
					    </span>
					</logic:notEmpty>
            		-W <bean:write name="obj" property="weightage"/>
            		<BR/>
            	</logic:iterate>
            	<%if(authDriverIndex==0) {%>
            		No Drivers Configured
            	<%} %>
            
            </td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" > Additional Drivers</td>
            <td class="tblcol" width="70%" >
            	<%int additionalDriverIndex = 0; %>
            	<logic:iterate id="obj" name="nasPolicyInstDataBean" property="nasPolicyAdditionalDriverRelDataList" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData">
            		 <%=++additionalDriverIndex%>. 
            		<logic:notEmpty name="obj" property="driverInstanceData.name">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="obj" property="driverInstanceData.driverInstanceId"/>','<bean:write name="obj" property="driverInstanceData.name"/>','DRIVERS');">
					    	<bean:write name="obj" property="driverInstanceData.name"/>
					    </span>
					</logic:notEmpty>
            		<BR/>
            	</logic:iterate>
            	<%if(additionalDriverIndex==0) {%>
            		No Additional Drivers Configured
            	<%} %>
            
            </td>
          </tr> 
          
          <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" /></td>
            <td class="tblcol" width="70%" ><bean:write name="nasPolicyInstDataBean" property="authScript" />&nbsp;</td>
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
          				<logic:iterate id="obj" name="nasPolicyInstDataBean" property="nasPolicyAuthPluginConfigList">
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
          				<logic:iterate id="obj" name="nasPolicyInstDataBean" property="nasPolicyAuthPluginConfigList">
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
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>
